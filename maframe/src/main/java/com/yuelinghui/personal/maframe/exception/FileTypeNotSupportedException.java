package com.yuelinghui.personal.maframe.exception;

import java.io.IOException;

/**
 * Created by yuelinghui on 16/8/30.
 * 文件类型不支持异常
 */
public class FileTypeNotSupportedException extends IOException {
    private static final long serialVersionUID = 1L;

    public String fileName;

    public FileTypeNotSupportedException(String name) {
        super(name);
        fileName = name;
    }
}
