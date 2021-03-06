package com.yuelinghui.personal.network.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuelinghui on 16/9/26.
 */
public class RequestParams {
    private static final String ENCODING = "UTF-8";

    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;
    protected ConcurrentHashMap<String, ArrayList<String>> urlParamsWithArray;

    public RequestParams() {
        init();
    }

    public RequestParams(Map<String, String> source) {
        init();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public RequestParams(String key, String value) {
        init();
        put(key, value);
    }

    public RequestParams(Object... keysAndValues) {
        init();
        int length = keysAndValues.length;
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        }
        for (int i = 0; i < length; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String value = String.valueOf(keysAndValues[i + 1]);
            put(key, value);
        }
    }

    private void init() {
        urlParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
        urlParamsWithArray = new ConcurrentHashMap<String, ArrayList<String>>();
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    public void put(String key, ArrayList<String> value) {
        if (key != null && value != null) {
            urlParamsWithArray.put(key, value);
        }
    }

    public void put(String key, FileInputStream fileInputStream) {
        put(key, fileInputStream, null);
    }

    public void put(String key, FileInputStream fileInputStream, String name) {
        put(key, fileInputStream, name, null);
    }

    public void put(String key, FileInputStream fileInputStream, String fileName, String contentType) {
        if (key != null && fileInputStream != null) {
            fileParams.put(key, new FileWrapper(fileInputStream, fileName, contentType));
        }
    }

    public void remove(String key) {
        urlParams.remove(key);
        fileParams.remove(key);
        urlParamsWithArray.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        for (ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            ArrayList<String> values = entry.getValue();
            for (String value : values) {
                if (values.indexOf(value) != 0)
                    result.append("&");
                result.append(entry.getKey());
                result.append("=");
                result.append(value);
            }
        }

        return result.toString();
    }

    public HttpEntity getEntity() {
        HttpEntity entity = null;
        if (!fileParams.isEmpty()) {
            SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();
            // add String params
            for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }
            // add dupe params
            for (ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
                ArrayList<String> values = entry.getValue();
                for (String value : values) {
                    multipartEntity.addPart(entry.getKey(), value);
                }
            }

            int currentIndex = 0;
            int lastIndex = fileParams.entrySet().size() - 1;
            for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
                FileWrapper file = entry.getValue();
                if (file.inputStream != null) {
                    boolean isLast = currentIndex == lastIndex;
                    if (file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, file.contentType, isLast);
                    } else {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }
            entity = multipartEntity;
        } else {
            try {
                entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return entity;
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lpParams = new LinkedList<>();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lpParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        for (ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (String value : values) {
                lpParams.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }
        return lpParams;
    }

    protected String getParamString() {
        return URLEncodedUtils.format(getParamsList(),ENCODING);
    }

    private static class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName, String contentType) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public String getFileName() {
            if (fileName != null) {
                return fileName;
            } else {
                return "nofilename";
            }
        }
    }
}
