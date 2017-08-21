package com.gpp.music_30;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/8/5.
 */

public class ImageLoader {
    private static final int MESSAGE_POST_RESULT = 1;//message标识
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;//SD卡缓存大小50M
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int DIS_CACHE_INDEX = 0;//一个节点只能有一个数据，第一个数据下标为0
    private static final int TAG_KEY_URI = R.id.imageloader_uri;
    private boolean mIsDiskLruCacheCreated = false;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, "ImageLoader#" + mCount.getAndDecrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            String tag = (String) imageView.getTag(TAG_KEY_URI);//TODO
//            String tag = (String) imageView.getTag();//
            String url = result.url;
            if (tag.equals(url)) {
                imageView.setImageBitmap(result.bitmap);
//                RecyclerAdapter adapter = ((Fragment_Home) (Fragment_Home.instantiate(mContext, "Fragment_Home"))).adapter;
//                adapter.notifyDataSetChanged();
            }
        }
    };
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        //创建内存缓存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;

        Gpp.e("cacheSize:" + cacheSize);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        //创建SD卡缓存
        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean mIsIdle = true;

    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null)
            mMemoryCache.put(key, bitmap);
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public void bindBitmap(final String uri, final ImageView imageview, final int reqWidth, final int reqHeight) {
        imageview.setTag(TAG_KEY_URI, uri);
//        imageview.setTag(uri);
        Bitmap bitmap = loadBitmapFromMemoryCache(uri);//先从缓存获取图片，有则加载，无则去下载
        if (bitmap != null) {
            imageview.setImageBitmap(bitmap);
            return;
        }
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(imageview, uri, bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);//交给线程池去获取图片
    }

    //加载图片：从内存，SD卡，网络
    private Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);//从SD卡获取图片
        if (bitmap != null) {
            return bitmap;
        }
        if (mIsIdle) {
            bitmap = loadBitmapFormHttp(uri, reqWidth, reqHeight);//网络下载并存到SD卡，再从SD卡取出
            if (bitmap == null && !mIsDiskLruCacheCreated) {
                bitmap = downloadBitmapFromUrl(uri);//如SD卡取失败，再从网络直接获取图片
                Gpp.e("创建SD卡缓存失败，直接网络解析图片:" + uri);
            }
        } else {
            InputStream is = mContext.getResources().openRawResource(R.raw.ic_launcher);
            bitmap = BitmapFactory.decodeStream(is);
        }
        return bitmap;
    }

    private Bitmap downloadBitmapFromUrl(String uri) {
        Bitmap bitmap;
        BufferedInputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.disconnect();
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //3、从网络获取图片存到SD卡
    private Bitmap loadBitmapFormHttp(String uri, int reqWidth, int reqHeight) {
        String key = hashKeyFromUrl(uri);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("不能在UI线程访问网络");
        }
        if (mDiskCache == null) return null;
        try {
            DiskLruCache.Editor editor = mDiskCache.edit(key);
            if (editor != null) {
                OutputStream os = editor.newOutputStream(DIS_CACHE_INDEX);
                if (downloadUrlToStream(uri, os)) {
                    editor.commit();//从网络下载到SD卡缓存
                } else editor.abort();
                mDiskCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loadBitmapFromDiskCache(uri, reqWidth, reqHeight);//从SD卡缓存取出
    }

    //去网络下载，返回是否下载成功，loadBitmapFormHttp根据是否下载成功判断是否写入SD卡缓存
    private boolean downloadUrlToStream(String uri, OutputStream os) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            bis = new BufferedInputStream(conn.getInputStream(), IO_BUFFER_SIZE);
            bos = new BufferedOutputStream(os, IO_BUFFER_SIZE);
            int len;
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.disconnect();
                if (bos != null) bos.close();
                if (bis != null) bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //2、从SD卡中获取图片缓存
    private Bitmap loadBitmapFromDiskCache(String uri, int reqWidth, int reqHeight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Gpp.e("UI线程中获取Bitmap,不推荐");
        }
        if (mDiskCache == null) {//没有SD卡存储
            return null;
        }
        Bitmap bitmap = null;
        String key = hashKeyFromUrl(uri);
        try {
            DiskLruCache.Snapshot snapShot = mDiskCache.get(key);
            if (snapShot != null) {
                FileInputStream fis = (FileInputStream) snapShot.getInputStream(DIS_CACHE_INDEX);
                FileDescriptor fd = fis.getFD();
                bitmap = ImageResizer.decodeSampleBitmapFromFileDescirptor(fd, reqWidth, reqHeight);
                if (bitmap != null) {
                    addBitmapToMemoryCache(key, bitmap);//如果从SD卡去获取，表名内存中已经没有缓存，所有添加到内存缓存中
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //1、从内存中获取图片缓存
    private Bitmap loadBitmapFromMemoryCache(String uri) {
        String key = hashKeyFromUrl(uri);
        Bitmap bitmap = getBitmapFromMemoryCache(key);
        return bitmap;
    }

    //获取URL的MD5值
    private String hashKeyFromUrl(String uri) {
        String cacheKey;
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");//拿到一个MD5转换器
            byte[] bytes = uri.getBytes();//把字符串转成字节数组
            mDigest.update(bytes);
            byte[] results = mDigest.digest();// 转换并返回结果，包含16个元素
            cacheKey = bytesToHexString(results);//把字符数组转换成字符串
            return cacheKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    //将字节数组换成成16进制的字符串
    private String bytesToHexString(byte[] results) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.length; i++) {
            int V = results[i] * 0xFF;
            String hex = Integer.toHexString(V);
            if (hex.length() == 1) {
                sb.append("0");
            } else sb.append(hex);
        }
        return sb.toString();
    }

    //获取可用空间大小
    private long getUsableSpace(File diskCacheDir) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            return diskCacheDir.getUsableSpace();
        final StatFs statFs = new StatFs(diskCacheDir.getPath());
        return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
    }

    //获取SD卡缓存目录
    private File getDiskCacheDir(Context mContext, String bitmap) {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = mContext.getExternalCacheDir().getPath();
        } else cachePath = mContext.getCacheDir().getPath();
        return new File(cachePath + File.separator + bitmap);
    }
}
