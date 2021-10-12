package com.primayasa.logintodoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "com.primayasa.logintodoapp.database";
    public static final int DB_VERSION = 1;
    public static final String TABLE_ACCOUNT = "account";
    public static final String TABLE_TODO = "todo";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ACCOUNT_USERNAME = "account_username";

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableAccount =
                String.format("CREATE TABLE %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT, %s TEXT)", TABLE_ACCOUNT,
                        COLUMN_USERNAME, COLUMN_PASSWORD);

        String createTableTodo =
                String.format("CREATE TABLE %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT, %s TEXT)", TABLE_TODO,
                        COLUMN_TITLE, COLUMN_ACCOUNT_USERNAME);

        sqLiteDatabase.execSQL(createTableAccount);
        sqLiteDatabase.execSQL(createTableTodo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_ACCOUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_TODO);
        onCreate(sqLiteDatabase);
    }
}
