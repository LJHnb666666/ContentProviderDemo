package com.example.contentproviderdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    //ContentObserver
    MyContentObserver myContentObserver;

    //Handler
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MyContentObserver.CONTENTPROVIDER_ONCHANGE:
                    Log.v("ljh","这里是MainActivity里面的Handler，传来的Msg的obj为" + (String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //得到book表
        Uri bookUri = BookProvider.BOOK_CONTENT_URI;
        //创建监听
        myContentObserver = new MyContentObserver(this,handler);
        //为book表注册监听
        //notifyForDescendants 为false 表示精确匹配，即只匹配该Uri 为true 表示可以同时匹配其派生的Uri
        getContentResolver().registerContentObserver(bookUri,false,myContentObserver.mContentObserver);

        //todo -----------------对book表的insert-----------------
        //为book表insert一条数据
        ContentValues values = new ContentValues();
        values.put("_id",6);
        values.put("name","在MainActivity新增的书");

        getContentResolver().insert(bookUri,values);

        //todo -----------------对book表的query-----------------
        Cursor cursor = getContentResolver().query(bookUri, new String[]{"_id", "name"}, null, null, null);
        while(cursor.moveToNext()){
            Book book = new Book();
            book.setBookId(cursor.getInt(0));
            book.setBookName(cursor.getString(1));
            Log.v("ljh","query book:" + book);
        }
        cursor.close();


        //为book表解除监听
        getContentResolver().unregisterContentObserver(myContentObserver.mContentObserver);
    }
}