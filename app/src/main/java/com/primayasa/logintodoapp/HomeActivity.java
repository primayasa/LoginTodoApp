package com.primayasa.logintodoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String username;
    private Todo[] todos;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView textViewTV = findViewById(R.id.textView);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        textViewTV.setText(username + "'s Task");

        updateUI();
    }

    public void logoutAccount(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void addTask(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Task");
        builder.setMessage("Task Title");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String task = inputField.getText().toString();

                helper = new DatabaseHelper(HomeActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.clear();
                values.put(helper.COLUMN_TITLE, task);
                values.put(helper.COLUMN_ACCOUNT_USERNAME, username);

                db.insertWithOnConflict(helper.TABLE_TODO, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                updateUI();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    public void onEditButtonClick (View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.todoTV);
        String task = taskTextView.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");
        builder.setMessage("Task Title");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String update = inputField.getText().toString();

                String sql = String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s' AND %s = '%s'",
                        helper.TABLE_TODO, helper.COLUMN_TITLE,
                        update,helper.COLUMN_TITLE, task, helper.COLUMN_ACCOUNT_USERNAME, username);

                helper = new DatabaseHelper(HomeActivity.this);
                SQLiteDatabase sqlDB = helper.getWritableDatabase();
                sqlDB.execSQL(sql);
                updateUI();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    public void onDeleteButtonClick(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.todoTV);
        String task = taskTextView.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Text");
        builder.setMessage("Are you sure to delete this task?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String sql = String.format("DELETE FROM %s WHERE %s = '%s' AND %s = '%s'",
                        helper.TABLE_TODO, helper.COLUMN_TITLE, task, helper.COLUMN_ACCOUNT_USERNAME, username);

                helper = new DatabaseHelper(HomeActivity.this);
                SQLiteDatabase sqlDB = helper.getWritableDatabase();
                sqlDB.execSQL(sql);
                updateUI();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void updateUI() {
        helper = new DatabaseHelper(HomeActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();

        String sql = String.format("SELECT * FROM '%s' WHERE %s = '%s'",
                helper.TABLE_TODO, helper.COLUMN_ACCOUNT_USERNAME, username);

        Cursor cursor = sqlDB.rawQuery(sql, null);
        int length = 0;
        if (cursor.moveToFirst()) {
            do {
                length++;
            } while (cursor.moveToNext());
        }

        todos = new Todo[length];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                todos[i] = new Todo(cursor.getString(1));
                i++;
            } while (cursor.moveToNext());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.todo_recycler_view);
        TodoAdapter adapter = new TodoAdapter(todos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}