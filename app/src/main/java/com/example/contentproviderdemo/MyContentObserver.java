package com.example.contentproviderdemo;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Created by didiwei on 2022/5/15
 * desc: 监听ContentProvider中指定Uri标识数据的变化
 *
 * https://www.cnblogs.com/longjunhao/p/8926858.html
 */
public class MyContentObserver {
    public static final int CONTENTPROVIDER_ONCHANGE = 1;
    Handler handler;
    Context context;

    MyContentObserver(Context context,Handler handler){
        this.handler = handler;
        this.context = context;
    }

    public final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, @Nullable Uri uri) {
            //selfChange一般为false
            Log.v("ljh","MyContentObserver");

            handler.obtainMessage(CONTENTPROVIDER_ONCHANGE,"这里是来自onChange方法的Message").sendToTarget();
        }
    };
}
