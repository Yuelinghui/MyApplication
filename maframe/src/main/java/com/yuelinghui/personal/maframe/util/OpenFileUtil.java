package com.yuelinghui.personal.maframe.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yuelinghui.personal.maframe.exception.FileTypeNotSupportedException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class OpenFileUtil {


    private Context context;

    public OpenFileUtil(Context context) {
        this.context = context;
    }

    // android获取一个用于打开HTML文件的intent
    public static Intent getHtmlFileIntent(File file) {
        Uri uri = Uri.parse(file.toString()).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    // android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    // android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // android获取一个用于打开apk文件的intent
    public static Intent getApkFileIntent(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        return intent;
    }

    // 3、定义用于检查要打开的文件的后缀是否在遍历后缀数组中
    @SuppressLint("DefaultLocale")
    private boolean checkEndsWithInStringArray(String checkItsEnd,
                                               String[] fileEndings) {
        for (String aEnd : fileEndings) {
            checkItsEnd = checkItsEnd.toLowerCase();
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    /**
     * @param currentPath
     * @return
     * @throws FileNotFoundException
     */
    public void open(final File currentPath) throws FileNotFoundException,
            FileTypeNotSupportedException {
        if (currentPath == null || !currentPath.exists()
                || !currentPath.isFile()) {
            throw new FileNotFoundException("FileName：" + currentPath.getName());
        }
        String fileName = currentPath.toString();
        Intent intent;
        if (checkEndsWithInStringArray(fileName, FileEnding.Image)) {
            intent = OpenFileUtil.getImageFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Web)) {
            intent = OpenFileUtil.getHtmlFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Package)) {
            intent = OpenFileUtil.getApkFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Audio)) {
            intent = OpenFileUtil.getAudioFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Video)) {
            intent = OpenFileUtil.getVideoFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Text)) {
            intent = OpenFileUtil.getTextFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Pdf)) {
            intent = OpenFileUtil.getPdfFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Word)) {
            intent = OpenFileUtil.getWordFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.Excel)) {
            intent = OpenFileUtil.getExcelFileIntent(currentPath);
            context.startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, FileEnding.PPT)) {
            intent = OpenFileUtil.getPPTFileIntent(currentPath);
            context.startActivity(intent);
        } else {
            throw new FileTypeNotSupportedException(currentPath.getName());
        }
    }

    /**
     * 文件后缀名
     *
     * @author sunxiao5
     */
    public static class FileEnding {
        public static String[] Image = {".png", ".gif", ".jpg", ".jpeg",
                ".bmp"};
        public static String[] Audio = {".mp3", ".wav", ".ogg", ".midi"};
        public static String[] Video = {".mp4", ".rmvb", ".avi", ".flv"};
        public static String[] Package = {".jar", ".zip", ".rar", ".gz",
                ".apk"};
        public static String[] Web = {".htm", ".html", ".php", ".jsp"};
        public static String[] Text = {".txt", ".java", ".c", ".cpp", ".py",
                ".xml", ".json", ".log"};
        public static String[] Word = {".doc", ".docx"};
        public static String[] Excel = {".xls", ".xlsx"};
        public static String[] PPT = {".ppt", ".pptx"};
        public static String[] Pdf = {".pdf"};
    }
}
