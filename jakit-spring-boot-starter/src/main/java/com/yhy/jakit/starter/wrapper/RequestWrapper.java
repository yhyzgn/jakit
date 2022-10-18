package com.yhy.jakit.starter.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * HttpServletRequest 的包装模型，支持多次读取 RequestBody
 * <p>
 * <a href="https://www.baeldung.com/spring-reading-httpservletrequest-multiple-times">解决方案</a>
 * <p>
 * Created on 2022-07-26 16:44
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {
    private ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();

    /**
     * 包装
     *
     * @param request 原 HttpServletRequest
     * @throws IOException 可能出现的异常
     */
    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        // 只处理 POST 请求
        if (HttpMethod.POST.matches(request.getMethod())) {
            log.debug("Wrapping the request which url is {} ...", request.getRequestURL().toString());
            int contentLength = getContentLength();
            cachedContent = new ByteArrayOutputStream(Math.max(contentLength, 0));

            if (isApplicationJson()) {
                byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
                cachedContent.write(body);
            } else {
                // 其他类型的，只是把参数取出重新组装成 body 的样子，否则 getParameter() 等系列方法会获取不到参数
                cacheRequestParameters();
            }
        }
    }

    /**
     * 包装
     *
     * @param request 原 ServletRequest
     * @return 包装实例
     * @throws IOException 可能出现的异常
     */
    public static RequestWrapper wrap(ServletRequest request) throws IOException {
        return wrap((HttpServletRequest) request);
    }

    /**
     * 包装
     *
     * @param request 原 HttpServletRequest
     * @return 包装实例
     * @throws IOException 可能出现的异常
     */
    public static RequestWrapper wrap(HttpServletRequest request) throws IOException {
        return new RequestWrapper(request);
    }

    /**
     * RequestBody
     *
     * @return RequestBody
     */
    public byte[] body() {
        return cachedContent.toByteArray();
    }

    /**
     * RequestBody
     *
     * @return RequestBody
     */
    public String string() {
        return string(Charset.defaultCharset());
    }

    /**
     * RequestBody
     *
     * @param charset 编码集
     * @return RequestBody
     */
    public String string(Charset charset) {
        if (null == body()) {
            return null;
        }
        return new String(body(), charset);
    }

    /**
     * ServletInputStream
     *
     * @return ServletInputStream
     */
    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(body());
    }

    /**
     * BufferedReader
     *
     * @return BufferedReader
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
    }

    @Override
    public String getParameter(String name) {
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return super.getParameterValues(name);
    }

    private boolean isApplicationJson() {
        // application/json
        String contentType = getContentType();
        if (!StringUtils.hasText(contentType)) {
            return false;
        }
        log.info("RequestWrapper POST method Content-Type = {}", contentType);
        MediaType mediaType = MediaType.parseMediaType(contentType);
        return MediaType.APPLICATION_JSON.includes(mediaType);
    }

    private void cacheRequestParameters() {
        // application/x-www-form-urlencoded，multipart/form-data
        try {
            if (cachedContent.size() == 0) {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = super.getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
                        String value = valueIterator.next();
                        cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value != null) {
                            cachedContent.write('=');
                            cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                cachedContent.write('&');
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        cachedContent.write('&');
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    /**
     * 核心灵魂
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final InputStream stream;

        CachedBodyServletInputStream(byte[] buffer) {
            stream = new ByteArrayInputStream(buffer);
        }

        @Override
        public boolean isFinished() {
            try {
                // 必须检查 stream 中有效值
                return stream.available() == 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public int read(byte @NotNull [] bs) throws IOException {
            return stream.read(bs);
        }
    }
}
