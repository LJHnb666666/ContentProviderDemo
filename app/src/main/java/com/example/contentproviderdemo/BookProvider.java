package com.example.contentproviderdemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/**
 * todo query 参数相对应的 sql语句
 *
 * String[] projection = {
 *     ContactsContract.Contacts._ID,
 *     ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
 *     ContactsContract.CommonDataKinds.Phone.NUMBER
 * };
 *
 * String selectionClause = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
 *
 * String[] selectionArgs = {"123456"};
 *
 * getContentResolver().query(uri, projection, selectionClause, selectionArgs, "sort_key COLLATE LOCALIZED asc");
 *
 * todo -----------------------------------------
 *
 * 上面的代码 类似于下面的sql语句
 * SELECT _ID, displayName, number FROM uri WHERE number = "123456" ORDER BY sort_key COLLATE LOCALIZED asc
 */

/**
 * Created by didiwei on 2022/5/15
 * desc: ContentProvider实体类
 *
 * todo 其实ContentProvider不一定操作数据库，还可以是文件，网络等。它只暴露一套CRUD的接口给外界，至于数据源具体从哪来，外界不需要管
 * 只是在这里为了方便演示，就以ContentProvider操作数据库为例。
 */
public class BookProvider extends ContentProvider {
    SQLiteDatabase mDb;//数据库

    public static final String AUTHORITY = "com.example.provider";//ContentProvider唯一标识

    //分别操作Book表和User表的uri
    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");
    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;

    //UriMatcher
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        uriMatcher.addURI(AUTHORITY,"user",USER_URI_CODE);
    }

    @Override
    public boolean onCreate() {
        Log.v("ljh","onCreate,current thread:" + Thread.currentThread().getName());
        //实际项目中，不建议在onCreate中进行耗时的数据库操作
        initProviderData();
        return false;
    }

    public void initProviderData(){
        mDb = new DatabaseHelper(getContext()).getWritableDatabase();
        mDb.execSQL("delete from " + DatabaseHelper.BOOK_TABLE_NAME);
        mDb.execSQL("delete from " + DatabaseHelper.USER_TABLE_NAME);
        mDb.execSQL("insert into book values(3,'Android');");
        mDb.execSQL("insert into book values(4,'Ios');");
        mDb.execSQL("insert into book values(5,'Html');");
        mDb.execSQL("insert into user values(1,'jake',1);");
        mDb.execSQL("insert into user values(2,'Android',0);");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.v("ljh","query,current thread:" + Thread.currentThread().getName());

        //首先根据uri得到表的名称
        String tableName = getTableName(uri);
        if(tableName.equals("")){
           throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        return mDb.query(tableName,projection,selection,selectionArgs,null,null,sortOrder,null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.v("ljh","getType,current thread:" + Thread.currentThread().getName());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.v("ljh","insert,current thread:" + Thread.currentThread().getName());
        String tableName = getTableName(uri);
        if(tableName.equals("")){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        mDb.insert(tableName,null,values);

        getContext().getContentResolver().notifyChange(uri,null);//通知外界当前ContentProvider已经改变
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v("ljh","delete,current thread:" + Thread.currentThread().getName());

        String tableName = getTableName(uri);
        if(tableName.equals("")){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        int count = mDb.delete(tableName,selection,selectionArgs);
        if(count > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v("ljh","update,current thread:" + Thread.currentThread().getName());

        String tableName = getTableName(uri);
        if(tableName.equals("")){
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        int row = mDb.update(tableName,values,selection,selectionArgs);
        if(row > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return row;
    }

    //得到要操作的表的名字
    private String getTableName(Uri uri){
        String tableName = "";
        switch (uriMatcher.match(uri)){
            case BOOK_URI_CODE:
                //操作的是Book表
                tableName = DatabaseHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                //操作的是User表
                tableName = DatabaseHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }

        return tableName;
    }

}
