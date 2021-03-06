package com.vocabulary.LemonVoca;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DictActivity extends AppCompatActivity implements View.OnClickListener{
    TextView nameText;

    //데이터베이스 관련 변수
    private com.vocabulary.LemonVoca.DBHelper helper;
    private SQLiteDatabase db = null;
    public static String DictionaryName;

    //List 관련 변수
    DictListviewAdapter adapter;
    ListView listView;
    public static ArrayList<String> dupliCheck2;

    String english;
    String korean;

    //delete 관련 변수
    Button comButton2;

    //Fab 관련 변수
    private Context mContext;
    private FloatingActionButton fab_menu, fab_plus, fab_mem, fab_del;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        Intent intent = getIntent();
        DictionaryName = intent.getExtras().getString("dictName");
        nameText = (TextView) findViewById(R.id.nameText);
        nameText.setText(DictionaryName);
        //Toast.makeText(getApplicationContext(), DictionaryName+" 선택됨", Toast.LENGTH_LONG).show();

        try {
            //Toast.makeText(getApplicationContext(), "db", Toast.LENGTH_SHORT).show();
            helper = new com.vocabulary.LemonVoca.DBHelper(this, "Test", null, 1);
            db = helper.getWritableDatabase();
        } catch(SQLiteException se){
            //Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("", se.getMessage());
        }

        //List 관련 변수 설정
        dupliCheck2 = new ArrayList<String>();
        adapter = new DictListviewAdapter(helper, db);
        listView = (ListView) findViewById(R.id.vocaList);
        listView.setAdapter(adapter);

        //Fab 관련 변수 설정
        mContext = getApplicationContext();
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);

        fab_menu = (FloatingActionButton) findViewById(R.id.menuFab);
        fab_plus = (FloatingActionButton) findViewById(R.id.plusFab);
        fab_del = (FloatingActionButton) findViewById(R.id.delFab);
        fab_mem = (FloatingActionButton) findViewById(R.id.memFab);

        fab_menu.setOnClickListener(this);
        fab_plus.setOnClickListener(this);
        fab_del.setOnClickListener(this);
        fab_mem.setOnClickListener(this);

        //delete 관련 변수 설정
        comButton2 = (Button)findViewById(R.id.completeButton2);

        showList2();
    }

    @Override
    public void onClick(View v) {

        //Fab 선택
        switch(v.getId()) {
            case R.id.menuFab:
                toggleFab();
                break;
            case R.id.plusFab:
                toggleFab();
                AddWordDialog dialog = new AddWordDialog(DictActivity.this);
                dialog.setDialogListener(new AddWordDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String eng, String kor) {
                        english = eng;
                        korean = kor;
                        if(english.length() != 0 && korean.length() != 0 && !dupliCheck2.contains(english)) {
                            dupliCheck2.add(english);
                            insertWord(english, korean);
                            adapter.addItem(english, korean);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), english+" 추가", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(mContext, "단어를 입력하지 않거나 같은 단어인 경우 단어가 추가되지 않습니다.", Toast.LENGTH_LONG).show();
                        comButton2.setVisibility(View.GONE);
                        adapter.setDeleteState(false);
                    }
                    @Override
                    public void onNegativeClicked() {
                        Log.d("DialogListener","onNegativeClicked");
                    }
                });
                dialog.show();
                break;
            case R.id.delFab:
                toggleFab();
                comButton2.setVisibility(View.VISIBLE);
                adapter.setDeleteState(true);
                break;
            case R.id.memFab:
                toggleFab();
                Intent intent = new Intent(getApplicationContext(), MemoryActivity.class);
                intent.putExtra("dictName", DictionaryName);
                startActivity(intent);
                break;
        }
    }

    //Fab 효과 메소드
    private void toggleFab(){
        if(isFabOpen){
            fab_menu.setImageResource(R.drawable.fabmenu);
            fab_plus.startAnimation(fab_close);
            fab_del.startAnimation(fab_close);
            fab_mem.startAnimation(fab_close);
            fab_plus.setClickable(false);
            fab_del.setClickable(false);
            fab_mem.setClickable(false);
            isFabOpen = false;
        } else {
            fab_menu.setImageResource(R.drawable.fabcancle);
            fab_plus.startAnimation(fab_open);
            fab_del.startAnimation(fab_open);
            fab_mem.startAnimation(fab_open);
            fab_plus.setClickable(true);
            fab_del.setClickable(true);
            fab_mem.setClickable(true);
            isFabOpen = true;

        }
    }
    public void onClickComButton2(View view) {
        comButton2.setVisibility(View.GONE);
        adapter.setDeleteState(false);
    }

    private void insertWord(String english, String korean){
        try {
            //Toast.makeText(mContext, "inserting word ["+ DictionaryName + "].", Toast.LENGTH_SHORT).show();
            String sql1 = "insert into `"+DictionaryName+"`(word, korean) values ('"+english+"', '"+korean+"')";
            db.execSQL(sql1);
        } catch(SQLiteException se){
            //Toast.makeText(mContext, se.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("테이블 생성 안됨", se.getMessage());
        }
    }

    private void showList2(){
        try{
            Cursor c = db.rawQuery("select * from `"+DictionaryName+"`", null);
            if(c != null){
                if(c.moveToFirst()) {
                    do {
                        String english = c.getString(c.getColumnIndex("word"));
                        String korean = c.getString(c.getColumnIndex("korean"));
                        adapter.addItem(english, korean);
                        if(!dupliCheck2.contains(english))
                            dupliCheck2.add(english);
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
