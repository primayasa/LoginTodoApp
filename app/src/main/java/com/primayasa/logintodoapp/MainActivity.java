package com.primayasa.logintodoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String username;
    private String password;
    private DatabaseHelper helper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        sharedPreferences.contains("username");
    }

    public void loginAccount(View view) {
        getUserInput();
        helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT  * FROM account", null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(1).equals(username) && cursor.getString(2).equals(password)){
                    Toast.makeText(this, "Login Successfull",
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            } while (cursor.moveToNext());
        }
        Toast.makeText(this, "Username / Password invalid", Toast.LENGTH_SHORT).show();
    }

    public void registerAccount(View view) {
        getUserInput();
        helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT  * FROM account", null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(1).equals(username)){
                    Toast.makeText(this, "User Already Exist, Please Login",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } while (cursor.moveToNext());
        }

        String sql = String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s');",
                helper.TABLE_ACCOUNT, helper.COLUMN_USERNAME, helper.COLUMN_PASSWORD,
                username, password);
        sqlDB.execSQL(sql);
        Toast.makeText(this, "Account Successfully Cretaed", Toast.LENGTH_SHORT).show();
    }

    private void getUserInput(){
        EditText usernameET = findViewById(R.id.usernameET);
        EditText passwordET = findViewById(R.id.passwordET);

        username = String.valueOf(usernameET.getText());
        password = String.valueOf(passwordET.getText());
    }
}