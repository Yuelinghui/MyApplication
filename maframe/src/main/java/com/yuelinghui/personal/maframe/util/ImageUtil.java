package com.yuelinghui.personal.maframe.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class ImageUtil {
    /**
     * 图像缩放类型 - 适合
     */
    public static final int SCALE_FIT = 0;
    /**
     * 图像缩放类型 - 裁剪
     */
    public static final int SCALE_CROP = 1;

    /**
     * Get rounded corner images
     *
     * @param bitmap
     * @param roundPx 5 10
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * @param drawable drawable图片
     * @param roundPx  角度
     * @return
     * @Description:// 获得圆角图片的方法
     */
    public static Bitmap getRoundedCornerBitmap(Drawable drawable, float roundPx) {
        Bitmap bitmap = drawableToBitmap(drawable);
        return getRoundedCornerBitmap(bitmap, roundPx);
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 创建指定缩放类型的图片
     *
     * @param unscaledBitmap
     * @param dstWidth
     * @param dstHeight
     * @param scaleType
     * @return
     */
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap,
                                            int dstWidth, int dstHeight, int scaleType) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(),
                unscaledBitmap.getHeight(), dstWidth, dstHeight, scaleType);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(),
                unscaledBitmap.getHeight(), dstWidth, dstHeight, scaleType);

        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(),
                dstRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(
                Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    private static Rect calculateSrcRect(int srcWidth, int srcHeight,
                                         int dstWidth, int dstHeight, int scaleType) {
        if (scaleType == SCALE_CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth,
                        srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop
                        + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    private static Rect calculateDstRect(int srcWidth, int srcHeight,
                                         int dstWidth, int dstHeight, int scaleType) {
        if (scaleType == SCALE_FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;
            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    /**
     * @param drawable
     * @return
     * @Description:将Drawable转化为Bitmap
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 图片base64字符串数据解码成图片
     *
     * @param imgBase64Str 图片Base64编码数据
     * @return
     */
    public static Bitmap str2Image(String imgBase64Str) {
        if (imgBase64Str == null) { // 图像数据为空
            return null;
        }
        try {
            // 剔除换行等特殊字符
            imgBase64Str = imgBase64Str.replaceAll("\n", "");
            imgBase64Str = imgBase64Str.replaceAll("\r", "");
            // Base64解码
            byte[] data = Base64.decode(imgBase64Str);
            for (int i = 0; i < data.length; ++i) {
                if (data[i] < 0) {// 调整异常数据
                    data[i] += 256;
                }
            }

            return bytes2Bimap(data);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 字节数组转化成图片
     *
     * @param imgBytes
     * @return
     */
    public static Bitmap bytes2Bimap(byte[] imgBytes) {
        if (imgBytes.length != 0) {
            try {
                return BitmapFactory.decodeByteArray(imgBytes, 0,
                        imgBytes.length);
            } catch (Exception e) {
            }
        }
        return null;
    }
}
