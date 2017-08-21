package com.gpp.music_30;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/8/5.
 */

class LoaderResult {
    public ImageView imageView;
    public String url,title,content;
    public Bitmap bitmap;

    public LoaderResult(ImageView imageView, String url, Bitmap bitmap) {
        this.imageView = imageView;
        this.url = url;
        this.bitmap = bitmap;
    }
    public LoaderResult(ImageView imageView, String url, String title, String content , Bitmap bitmap) {
        this.imageView = imageView;
        this.url = url;
        this.bitmap = bitmap;
        this.content=content;
        this.title=title;
    }
}
