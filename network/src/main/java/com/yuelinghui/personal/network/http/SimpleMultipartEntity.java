package com.yuelinghui.personal.network.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by yuelinghui on 16/9/27.
 */
public class SimpleMultipartEntity implements HttpEntity {

    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private String mBoundary = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    boolean isSetLast = false;
    boolean isSetFirst = false;

    public SimpleMultipartEntity() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        this.mBoundary = buf.toString();
    }

    public void writeFirstBoundaryIfNeeds() {
        if (!isSetFirst) {
            try {
                out.write(("--" + mBoundary + "\r\n").getBytes());
            } catch (Exception e) {
            }
        }
        isSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (isSetLast) {
            return;
        }
        try {
            out.write(("\r\n--" + mBoundary + "--\r\n").getBytes());
        } catch (Exception e) {
        }
        isSetLast = true;
    }

    public void addPart(String key, String value) {
        writeFirstBoundaryIfNeeds();
        try {
            out.write(("Content-Disposition:form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
            out.write(value.getBytes());
            out.write(("\r\n--" + mBoundary + "\r\n").getBytes());
        } catch (Exception e) {
        }
    }

    public void addPart(String key, String fileName, InputStream fileInputStream, boolean isLast) {
        addPart(key, fileName, fileInputStream, "application/octet-stream", isLast);
    }

    public void addPart(String key, String fileName, InputStream fileInputStream, String type, boolean isLast) {
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-type" + type + "\r\n";
            out.write(("Content-Disposition: form-data; name=\"" + key + "\";fileName=\"" + fileName + "\"\r\n").getBytes());
            out.write(type.getBytes());
            out.write(("Content-Transfer-Encoding:binary\r\n\r\n").getBytes());

            byte[] temp = new byte[4096];
            int l = 0;
            while ((l = fileInputStream.read(temp)) != -1) {
                out.write(temp, 0, l);
            }
            if (!isLast) {
                out.write(("\r\n--" + mBoundary + "\r\n").getBytes());
            }
            out.flush();
        } catch (Exception e) {
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public void addPart(String key, File value, boolean isLast) {
        try {
            addPart(key, value.getName(), new FileInputStream(value), isLast);
        } catch (FileNotFoundException e) {
        }
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return out.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + mBoundary);
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(out.toByteArray());
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {
        if (isStreaming()) {
            throw new UnsupportedOperationException("Stream entity does not implement #consumeContent()");
        }
    }
}
