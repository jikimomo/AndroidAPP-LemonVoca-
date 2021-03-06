package com.vocabulary.LemonVoca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //데이터베이스 관련 변수
    private DBHelper helper;
    private SQLiteDatabase db = null;

    //List 관련 변수
    MainListviewAdapter adapter;
    ListView listView;
    public static ArrayList<String> dupliCheck;

    //delete 관련 변수
    Button comButton;

    //Fab 관련 변수
    private Context mContext;
    private FloatingActionButton fab_menu, fab_plus, fab_del;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loading 화면
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        try {
            //Toast.makeText(getApplicationContext(), "create db", Toast.LENGTH_SHORT).show();
            helper = new DBHelper(this, "Test", null, 1);
            db = helper.getWritableDatabase();
        } catch(SQLiteException se){
            //Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("", se.getMessage());
        }

        //List 관련 변수 설정
        dupliCheck = new ArrayList<String>();
        dupliCheck.add("기본 단어장");
        adapter = new MainListviewAdapter(helper, db);
        listView = (ListView) findViewById(R.id.dictList);
        listView.setAdapter(adapter);

        //Fab 관련 변수 설정
        mContext = getApplicationContext();
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);

        fab_menu = (FloatingActionButton) findViewById(R.id.menuFab);
        fab_plus = (FloatingActionButton) findViewById(R.id.plusFab);
        fab_del = (FloatingActionButton) findViewById(R.id.delFab);
        fab_menu.setOnClickListener(this);
        fab_plus.setOnClickListener(this);
        fab_del.setOnClickListener(this);

        //delete 관련 변수 설정
        comButton = (Button)findViewById(R.id.completeButton);

        String CREATE_SQL = "create table if not exists Dictionary("
                + "_id integer PRIMARY KEY autoincrement, "
                + "dictName text unique)";
        String insert_sql = "insert into Dictionary (dictName) values ('기본 단어장')";
        try {
            //Toast.makeText(getApplicationContext(), "create 기본 단어장", Toast.LENGTH_SHORT).show();
            db.execSQL(CREATE_SQL);
            db.execSQL(insert_sql);
            db.execSQL("create table if not exists `기본 단어장`(_id integer PRIMARY KEY autoincrement, word text, korean text);");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('patron', '손님, 후원자');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('appeal', '호소하다, 매력');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('clarity', '선명도');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('meet', '달성하다');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('fuel', '연료');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('past', '지난');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('allocate', '할당하다');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('steel', '철강');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('besides', '게다가');");
            db.execSQL("insert into `기본 단어장`(word, korean) values ('record', '기록하다');");
        } catch(SQLiteException se){
            //Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("", se.getMessage());
        }
        showList(); //화면 켰을 때 db에 있는 단어장 목록 출력
    }

    @Override
    public void onClick(View v) {
        //Fab 선택
        switch (v.getId()) {
            case R.id.menuFab:
                toggleFab();
                break;
            case R.id.plusFab:
                toggleFab();
                //단어장 추가 다이얼로그
                final EditText editText = new EditText(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("새로운 단어장 추가");
                builder.setMessage("새로운 단어장의 이름을 입력하세요.");
                builder.setView(editText);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String newDict = editText.getText().toString();
                                if(newDict.length() != 0 && !dupliCheck.contains(newDict)){
                                    dupliCheck.add(newDict);
                                    createTable(newDict);
                                    editText.setText("");
                                    adapter.addItem(newDict);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), newDict+" 추가", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "이름을 입력하지 않거나 중복된 이름일 경우 단어장이 생성되지 않습니다.", Toast.LENGTH_SHORT).show();
                                comButton.setVisibility(View.GONE);
                                adapter.setDeleteState(false);
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
                break;
            case R.id.delFab:
                toggleFab();
                comButton.setVisibility(View.VISIBLE);
                adapter.setDeleteState(true);
                break;
        }
    }

    //Fab 효과 메소드
    private void toggleFab() {
        if (isFabOpen) {
            fab_menu.setImageResource(R.drawable.fabmenu);
            fab_plus.startAnimation(fab_close);
            fab_del.startAnimation(fab_close);
            fab_plus.setClickable(false);
            fab_del.setClickable(false);
            isFabOpen = false;
        } else {
            fab_menu.setImageResource(R.drawable.fabcancle);
            fab_plus.startAnimation(fab_open);
            fab_del.startAnimation(fab_open);
            fab_plus.setClickable(true);
            fab_del.setClickable(true);
            isFabOpen = true;

        }
    }

    public void onClickComButton(View view) {
        comButton.setVisibility(View.GONE);
        adapter.setDeleteState(false);
    }

    private void createTable(String name){
        try {
            //Toast.makeText(getApplicationContext(), "creating table ["+ name + "].", Toast.LENGTH_SHORT).show();
            String sql1 = "insert into Dictionary (dictName) values ('"+name+"')";
            db.execSQL(sql1);
            String sql2 = "create table if not exists `"+name+"`(_id integer PRIMARY KEY autoincrement, word text, korean text)";
            db.execSQL(sql2);

        } catch(SQLiteException se){
            //Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("테이블 생성 안됨", se.getMessage());
        }
    }

    private void showList(){
        try{
            Cursor c = db.rawQuery("select * from Dictionary", null);
            if(c != null){
                if(c.moveToFirst()) {
                    do {
                        String name = c.getString(c.getColumnIndex("dictName"));
                        adapter.addItem(name);
                        if(!dupliCheck.contains(name))
                            dupliCheck.add(name);
                    } while (c.moveToNext());
                }
            }
            c.close();
            adapter.notifyDataSetChanged();
        } catch(SQLiteException se){
            //Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


