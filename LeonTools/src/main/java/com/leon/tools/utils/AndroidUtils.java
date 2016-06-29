package com.leon.tools.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 集成Android中相关的工具
 *
 * @author zhanghong
 * @time 2015-6-8
 */
public class AndroidUtils {
    private final static int BMP_SIZE = 256 * 1024;
    private final static int BMP_MAX_HEIGHT = 1920;
    private final static int BMP_MAX_WIDTH = 1080;

    private final static String TITLE = "AndroidUtils";
    private static boolean IsDebug = true;

    /**
     * 设置debug 模式
     *
     * @param isDebuging true 需要Debug, false 不需要打印 debug
     */
    public static void setDebugModel(boolean isDebuging) {
        IsDebug = isDebuging;
    }

    /**
     * 打印 debug text
     *
     * @param tag
     * @param msgs
     */
    public static void DEBUG(String tag, Object... msgs) {
        if (IsDebug) {
            StringBuilder builder = new StringBuilder();
            if (null != msgs) {
                for (Object msg : msgs) {
                    builder.append(msg.toString());
                }
            }
            Log.d(tag, builder.toString());
        }
    }

    /**
     * 打印错误信息
     *
     * @param tag
     * @param msgs
     */
    public static void ERROR(String tag, Object... msgs) {
        if (IsDebug) {
            StringBuilder builder = new StringBuilder();
            if (null != msgs) {
                for (Object msg : msgs) {
                    builder.append(msg.toString());
                }
            }
            Log.e(tag, builder.toString());
        }
    }

    /**
     * 直接打印错误信息
     *
     * @param tag
     * @param msgs
     */
    public static void ERROR_DIRECT(String tag, Object... msgs) {
        StringBuilder builder = new StringBuilder();
        if (null != msgs) {
            for (Object msg : msgs) {
                builder.append(msg.toString());
            }
        }
        Log.e(tag, builder.toString());
    }

    public static void LOG(String text) {
        Log.d(TITLE, text);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 缩放图片
     *
     * @param bitmap
     * @param zf
     * @return
     */
    public static Bitmap zoom(Bitmap bitmap, float zf) {
        Matrix matrix = new Matrix();
        matrix.postScale(zf, zf);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 缩放图片
     *
     * @param bitmap
     * @param zf
     * @return
     */
    public static Bitmap zoom(Bitmap bitmap, float wf, float hf) {
        Matrix matrix = new Matrix();
        matrix.postScale(wf, hf);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 图片圆角处理
     *
     * @param bitmap
     * @param roundPX
     * @return
     */
    public static Bitmap getRCB(Bitmap bitmap, float roundPX) {
        // RCB means
        // Rounded
        // Corner Bitmap
        Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(dstbmp);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return dstbmp;
    }

    /**
     * 控制图片大小
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的宽度
        final int width = options.outWidth;
        final int height = options.outHeight;

        if (0 == reqWidth || reqWidth > BMP_MAX_WIDTH) {
            reqWidth = BMP_MAX_WIDTH;
        }

        if (0 == reqHeight || reqHeight > BMP_MAX_HEIGHT) {
            reqHeight = BMP_MAX_HEIGHT;
        }

        int inSampleSizeW = 1;
        int inSampleSizeH = 1;
        if (width > reqWidth && reqWidth > 0) {
            // 计算出实际宽度和目标宽度的比率
            inSampleSizeW = Math.round((float) width / (float) reqWidth);
        }

        if (height > reqHeight && reqHeight > 0) {
            inSampleSizeH = Math.round((float) height / (float) reqHeight);
        }
        final int size = inSampleSizeW > inSampleSizeH ? inSampleSizeW : inSampleSizeH;
        AndroidUtils.DEBUG("calculateInSampleSize", " size: " + size);
        // 图片压缩后,对于渐变过于难看,故传诵
        // final int cSize = (width / size) * (height / size) / BMP_SIZE;
        // AndroidUtils.DEBUG("calculateInSampleSize", " bmp size: " + (width *
        // height));
        // if (cSize > 0) {
        // options.inPreferredConfig = Bitmap.Config.RGB_565;
        // }

        return size;
    }

    /**
     * 获取图片所占内存
     *
     * @param bitmap
     * @return
     */
    public static int getBmpSize(Bitmap bitmap) {
        if (null == bitmap) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {// API
            // 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static Bitmap decodeSampledBitmapFromResource(Context context, int resId, int reqWidth, int reqHeight) {
        return decodeSampledBitmapFromResource(context.getResources(), resId, reqWidth, reqHeight);
    }

    /**
     * 从资源获取图片
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeBitmap(Resources res, int resId) {
        return decodeSampledBitmapFromResource(res, resId, 0, 0);
    }

    /**
     * 从路径获取图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromPath(String pathName, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 文件压缩
     *
     * @param srcPath 目标文件
     * @param dstPath 生成文件
     * @param quality 生成图片质量
     * @return true 生成成功, false 生成失败
     */
    public static boolean compressPictureFile(String srcPath, String dstPath, int quality) {
        Bitmap bmp = decodeSampledBitmapFromPath(srcPath, 0, 0);
        if (null != bmp) {
            FileOutputStream fos = null;
            try {
                File file = JavaUtils.createFile(dstPath + ".temp");
                fos = new FileOutputStream(file);
                bmp.compress(CompressFormat.JPEG, quality, fos);
                file.renameTo(JavaUtils.createFile(dstPath));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fos) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                bmp.recycle();
                bmp = null;
            }
        }
        return false;
    }

    /**
     * 图片裁剪
     *
     * @param act
     * @param uri
     * @return
     */
    public static boolean cropPhoto(Activity act, Uri uri, String outFilePath, int requestCode, int sx, int sy) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", sx);
        intent.putExtra("aspectY", sy);
        intent.putExtra("return-data", false);
        Uri outputUri = Uri.fromFile(new File(outFilePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", CompressFormat.JPEG.toString());
        try {
            act.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String signatureParams(List<NameValuePair> params, String signKey) {
        return signatureParamsList(params, signKey);
    }

    public static String signatureParams(HashMap<String, String> params, String signKey) {
        List<NameValuePair> pairs = new LinkedList<NameValuePair>();
        for (String key : params.keySet()) {
            pairs.add(new BasicNameValuePair(key, params.get(key)));
        }
        return signatureParams(pairs, signKey);
    }

    private static String signatureParamsList(List<NameValuePair> params, String signKey) {
        Collections.sort(params, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair lhs, NameValuePair rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        String sign = JavaUtils.createUrlGetParams(params) + signKey;
        return JavaUtils.getStringMD5(JavaUtils.getStringMD5(sign));
    }

    public static String getApkPath(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        return info.publicSourceDir;
    }

    public static String getApplicationMetaData(Context context, String key) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return info.metaData.getString(key);
        } catch (Exception e) {
        }
        return null;
    }

    public static String readAssetFile(Context context, String fileName) {
        return readAssetFile(context, fileName, "utf-8");
    }

    /**
     * 读取asset文件中的数据文件
     *
     * @param context
     * @param fileName
     * @param encode
     * @return
     */
    public static String readAssetFile(Context context, String fileName, String encode) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(context.getAssets().open(fileName));
            byte[] data = new byte[bis.available()];
            bis.read(data);
            return EncodingUtils.getString(data, encode);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bis) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static float dip2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return dpValue * density;
    }

    public static float px2dip(Context context, float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return pxValue / density;
    }

    public static float px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale;
    }

    public static float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale;
    }

    public static void hideInputSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showInputSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 打电话接口
     *
     * @param act
     * @param phoneNumber
     * @return
     */
    public static boolean startPhoneCall(Activity act, String phoneNumber) {
        try {
            Intent intent;
            if (phoneNumber.startsWith("tel:")) {
                intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
            } else {
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            }
            act.startActivity(intent);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 解压Zip
     *
     * @param archive
     * @param decompressDir
     * @return
     */
    public static boolean unzipFile(String archive, String decompressDir) {
        ZipInputStream inZip = null;
        try {
            inZip = new ZipInputStream(new FileInputStream(archive));
            ZipEntry zipEntry;
            while ((zipEntry = inZip.getNextEntry()) != null) {
                unzipFile(inZip, zipEntry, decompressDir);
            }
            return true;
        } catch (Exception e) {
        } finally {
            if (null != inZip) {
                try {
                    inZip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 解压文件内容
     *
     * @param inZip
     * @param entry
     * @param decompressDir
     */
    private static void unzipFile(ZipInputStream inZip, ZipEntry entry, String decompressDir) {
        String name = entry.getName();
        name = decompressDir + File.separator + name;
        if (entry.isDirectory()) {//建立目录文件
            new File(name).mkdirs();
        } else {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(JavaUtils.createFile(name));
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
