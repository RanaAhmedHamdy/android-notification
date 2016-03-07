package com.bigr.notificationservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rana on 6/3/2015.
 */
public class MyDatabase extends SQLiteOpenHelper{

    public final static String DB_NAME = "iddatabase.db";
    public final static int DB_VERSION = 1;

    public final static String TABLE_NAME = "idtable";
    public final static String TABLE_ID = "id";

    public final static String CREATE_NAME = "create table " + TABLE_NAME + " (" + TABLE_ID + " INTEGER)";

    public MyDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
    }
}
