package com.example.roomapp;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public class MyApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
