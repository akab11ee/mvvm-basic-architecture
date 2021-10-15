package example.com.myapplication.interceptor

import okhttp3.Interceptor

import example.com.myapplication.BuildConfig
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import timber.log.Timber
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit


class LoggerInterceptor : Interceptor {
    private val UTF8 = Charset.forName("UTF-8")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (BuildConfig.DEBUG) {
            Timber.v(makeCURL(request))

            val requestBody = request.body()
            val hasRequestBody = requestBody != null

            val connection = chain.connection()
            val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
            var requestStartMessage =
                "-----> " + request.method() + ' '.toString() + request.url() + ' '.toString() + protocol
            if (hasRequestBody)
                requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"

            Timber.d(requestStartMessage)

            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody!!.contentType() != null) {
                    Timber.d("Content-Type: %s", requestBody.contentType())
                }
                if (requestBody.contentLength() != -1L) {
                    Timber.d("Content-Length: %s", requestBody.contentLength())
                }
            }

            val headers = request.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(
                        name,
                        ignoreCase = true
                    ) && !"Content-Length".equals(name, ignoreCase = true)
                )
                    Timber.d("%s: %s", name, headers.value(i))
                i++
            }

            if (!hasRequestBody) {
                Timber.d("-----> END %s", request.method())
            } else if (bodyEncoded(request.headers())) {
                Timber.d("-----> END %s %s", request.method(), " (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)

                var charset: Charset? = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }

                Timber.d("")
                if (isPlaintext(buffer)) {
                    Timber.d(buffer.readString(charset!!))
                    Timber.d(
                        "-----> END %s %s %s %s",
                        request.method(),
                        " (",
                        requestBody.contentLength(),
                        "-byte body)"
                    )
                } else {
                    Timber.d(
                        "-----> END %s %s %s %s",
                        request.method(),
                        " (binary ",
                        requestBody.contentLength(),
                        "-byte body omitted)"
                    )
                }
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e, "<----- HTTP FAILED:")
            throw e
        }

        if (BuildConfig.DEBUG) {
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

            val responseBody = response.body()
            val contentLength = responseBody!!.contentLength()
            Timber.d(
                "<----- %s %s %s %s %s%s%s",
                response.code(),
                response.message(),
                response.request().url(),
                "(",
                tookMs,
                "ms",
                ")"
            )

            val responseHeaders = response.headers()
            var i = 0
            val count = responseHeaders.size()
            while (i < count) {
                Timber.d("%s %s %s", responseHeaders.name(i), ": ", responseHeaders.value(i))
                i++
            }

            if (response.cacheResponse() != null) {
                Timber.d("CacheType : Cache")
            }

            if (response.networkResponse() != null) {
                Timber.d("CacheType : Network")
            }


            if (!HttpHeaders.hasBody(response))
                Timber.d("<----- END HTTP")
            else if (bodyEncoded(response.headers()))
                Timber.d("<----- END HTTP (encoded body omitted)")
            else {
                val source = responseBody.source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()

                var charset: Charset? = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        Timber.d("")
                        Timber.e("Couldn't decode the jawalPackagesMessageResponse body; charset is likely malformed.")
                        Timber.d("<----- END HTTP")

                        return response
                    }

                }

                if (!isPlaintext(buffer)) {
                    Timber.d("")
                    Timber.d("<----- END HTTP (binary  %s %s", buffer.size(), "-byte body omitted)")
                    return response
                }

                if (contentLength != 0L) {
                    Timber.d("")
                    Timber.i(buffer.clone().readString(charset!!))
                }

                Timber.d("<----- END HTTP ( %s %s", buffer.size(), "-byte body)")
            }
        }
        return response
    }

    @Throws(IOException::class)
    private fun makeCURL(request: Request): String {
        val curlCommandBuilder = StringBuilder("")
            .append("cURL ")
            .append("-X ")
            .append(request.method().toUpperCase())
            .append(" ")

        for (headerName in request.headers().names())
            curlCommandBuilder.append("-H " + "\"")
                .append(headerName)
                .append(": ")
                .append(request.headers().get(headerName))
                .append("\" ")

        val requestBody = request.body()
        if (request.body() != null) {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)
            val contentType = requestBody.contentType()
            if (contentType != null) {
                curlCommandBuilder.append("-H " + "\"" + "Content-Type" + ": ")
                    .append(request.body()!!.contentType()!!.toString())
                    .append("\" ")

                val charset = contentType.charset(UTF8)
                curlCommandBuilder.append(" -d '")
                    .append(buffer.readString(charset!!))
                    .append("'")
            }
        }

        // add request URL
        curlCommandBuilder.append(" \"")
            .append(request.url().toString())
            .append("\"")

        return curlCommandBuilder.toString()
    }

    private fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = if (buffer.size() < 64) buffer.size() else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false // Truncated UTF-8 sequence.
        }

    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }
}