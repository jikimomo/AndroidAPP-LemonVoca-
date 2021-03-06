package com.vocabulary.LemonVoca;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MemoryActivity extends AppCompatActivity {
    private com.vocabulary.LemonVoca.DBHelper helper;
    private SQLiteDatabase db = null;
    String DictionaryName;

    Button closeButton;
    TextView wordList1;
    StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        Intent intent = getIntent();
        DictionaryName = intent.getExtras().getString("dictName");
        //Toast.makeText(getApplicationContext(), DictionaryName+" 선택됨", Toast.LENGTH_LONG).show();

        try {
            //Toast.makeText(getApplicationContext(), "db", Toast.LENGTH_SHORT).show();
            helper = new com.vocabulary.LemonVoca.DBHelper(this, "Test", null, 1);
            db = helper.getWritableDatabase();
        } catch(SQLiteException se){
            //Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("", se.getMessage());
        }

        wordList1 = (TextView) findViewById(R.id.wordList1);
        sb = new StringBuilder();

        showList3();
        wordList1.setText(sb);

        closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               finish();
           }
        });
    }

    private void showList3(){
        try{
            Cursor c = db.rawQuery("select * from `"+DictionaryName+"`", null);
            if(c != null){
                if(c.moveToFirst()) {
                    do {
                        String english = c.getString(c.getColumnIndex("word"));
                        sb.append("       "+english+"       ");
                    } while (c.moveToNext());
                }
            }
            c.close();
        } catch(SQLiteException se){
            //Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
