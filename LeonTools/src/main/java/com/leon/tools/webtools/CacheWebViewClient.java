package com.leon.tools.webtools;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.leon.tools.utils.JavaUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by leon.zhang on 2016/3/28.
 * 用于缓存静态文件的WebVeiwClient
 */
public class CacheWebViewClient extends ForwardingWebViewClient {

    private AbsResourceCache mResourceCache;

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (null != mResourceCache) {
            return mResourceCache.shouldInterceptRequest(view, url);
        }
        return super.shouldInterceptRequest(view, url);
    }

    /**
     * 设置缓存策略
     *
     * @param cache
     */
    public final void setResourceCache(AbsResourceCache cache) {
        mResourceCache = cache;
    }

    /**
     * 缓存方案基类
     */
    public static abstract class AbsResourceCache {
        private final static String IS_CACHE = "isCache";
        private final static String TRUE = "true";
        private final static String TRUE_NUM = "1";
        protected ArrayList<String> iFilters = new ArrayList<String>(10);
        protected String iRootDir;

        public AbsResourceCache(String dir) {
            iRootDir = Environment.getExternalStorageDirectory() + dir;
            initFilters();
        }

        //添加需要缓存的文件后缀
        private void initFilters() {
            iFilters.add("bmp");
            iFilters.add("png");
            iFilters.add("jpg");
            iFilters.add("gif");
            iFilters.add("css");
            iFilters.add("js");
        }

        private String getFileNameByUrl(String url, String fileExtension) {
            final StringBuilder path = new StringBuilder(iRootDir);
            path.append("/");
            path.append(JavaUtils.createFileNameByUrl(url));
            if (!TextUtils.isEmpty(fileExtension)) {
                path.append(".");
                path.append(fileExtension);
            }
            return path.toString();
        }


        private WebResourceResponse loadCacheResource(WebView view, String url) {
            final String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
            File file = new File(getFileNameByUrl(url, fileExtension));
            if (file.exists()) {//如果文件存在则加载文件
                try {
                    InputStream is = new FileInputStream(file);
                    return new WebResourceResponse("", "", is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                onRequestResouce(view, url, file.getAbsolutePath());
            }
            return null;
        }

        final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            String isCache = "";
            try {
                isCache = Uri.parse(url).getQueryParameter(IS_CACHE);
            } catch (Exception e) {
                isCache = "";
            }

            if (!TextUtils.isEmpty(isCache)) {
                if (TRUE.equals(isCache) || TRUE_NUM.equals(isCache)) {//如果可以取,则从缓存中取出
                    return loadCacheResource(view, url);
                }
                return null;
            }

            final String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (iFilters.contains(fileExtension)) {//如果在缓存过滤器中,则启用缓存
                return loadCacheResource(view, url);
            }
            return null;
        }

        /**
         * 需要网络请求缓存到本地的方案
         *
         * @param view         webView
         * @param url          需要缓存的文件url
         * @param filePathName 缓存目标文件路径名
         */
        protected abstract void onRequestResouce(WebView view, String url, String filePathName);
    }

//    /**
//     * {@link IHttpFileGetter} 缓存策略实现体
//     */
//    public static class FileGetterResourceCache extends AbsResourceCache {
//
//        private IHttpFileGetter iFileGetter;
//
//
//        public FileGetterResourceCache(IHttpFileGetter getter, String dir) {
//            super(dir);
//            iFileGetter = getter;
//        }
//
//        @Override
//        protected void onRequestResouce(WebView view, String url, String filePathName) {
//            HttpFileGetParams params = new HttpFileGetParams(url);
//            params.setFilePath(filePathName);
//            iFileGetter.httpFileGet(params);
//        }
//    }
}