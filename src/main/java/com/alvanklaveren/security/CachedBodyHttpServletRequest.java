package com.alvanklaveren.security;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private ByteArrayOutputStream cachedBytes;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {

        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        if (cachedBytes == null) {
            cacheInputStream();
        }

        return new CachedServletInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {

        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private void cacheInputStream() throws IOException {

        // Cache the input stream in order to read it multiple times.
        cachedBytes = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cachedBytes);
    }

    /* An inputstream which reads the cached request body */
    public class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream input;

        public CachedServletInputStream() {

            /* create a new input stream from the cached request body */
            input = new ByteArrayInputStream(cachedBytes.toByteArray());
        }

        @Override public boolean isFinished() {

            return input.available() == 0;
        }

        @Override public boolean isReady() {

            return true;
        }

        @Override public void setReadListener(ReadListener readListener) {

            throw new RuntimeException("Not implemented");
        }

        @Override
        public int read() throws IOException {

            return input.read();
        }
    }

}

