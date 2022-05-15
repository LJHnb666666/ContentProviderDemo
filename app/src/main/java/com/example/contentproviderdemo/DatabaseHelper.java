package com.example.contentproviderdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Created by didiwei on 2022/5/15
 * desc:  数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;//数据库版本号
    private static final String DB_NAME = "book_provider.db";//数据库名称
    public static final String BOOK_TABLE_NAME = "book";//book表
    public static final String USER_TABLE_NAME = "user";//user表

    //创建book表和user表
    private String CREATE_BOOK_TABLE = "create table if not exists "+ BOOK_TABLE_NAME +
            "(_id integer primary key," + "name text)";
    private String CREATE_USER_TABLE = "create table if not exists " + USER_TABLE_NAME +
            "(_id integer primary key," + "name text," + "sex int)";

    //创建默认构造方法
    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建book表和user表
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
