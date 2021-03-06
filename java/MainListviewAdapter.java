package com.vocabulary.LemonVoca;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.vocabulary.LemonVoca.MainActivity.dupliCheck;


//단어장으로 이동 시 완료버튼 안보이게 하는 것
public class MainListviewAdapter extends BaseAdapter {
    DBHelper helper;
    SQLiteDatabase db;

    private ArrayList<MainListview> dictionary = new ArrayList<MainListview>();
    static Button delete;
    View body;
    private boolean deleteState = false;

    Context context;

    public MainListviewAdapter(DBHelper helper, SQLiteDatabase db){
        this.helper = helper;
        this.db = db;
    }
    @Override
    public int getCount() {
        return dictionary.size();
    }

    @Override
    public Object getItem(int position) {
        return dictionary.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_dictlist, parent, false);
        }

        TextView dictName = (TextView) convertView.findViewById(R.id.dictName);
        body = (View) convertView.findViewById(R.id.listBody);
        body.setTag(position);
        delete = (Button) convertView.findViewById(R.id.deleteButton);
        delete.setTag(position); //delete버튼에 tag달기

        MainListview listview = dictionary.get(position);
        dictName.setText(listview.getName());

        if (deleteState) {
            delete.setVisibility(convertView.VISIBLE);
            body.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event){
                    return true;
                }
            });
        }
        else {
            delete.setVisibility(convertView.INVISIBLE);
            body.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event){
                    return false;
                }
            });
        }

        //단어장 이동 시 db.close해줘야 할 수도 있음...
        body.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Integer index1 = (Integer) v.getTag();
                MainListview tmp1 = dictionary.get(index1.intValue());
                Intent intent = new Intent(context, DictActivity.class);
                intent.putExtra("dictName", tmp1.getName());
                context.startActivity(intent);
            }
        });

        delete.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Integer index2 = (Integer) v.getTag(); //버튼의 tag값 받아오기
                MainListview tmp2 = dictionary.get(index2.intValue());
                dupliCheck.remove(tmp2.getName());
                deleteTable(tmp2.getName()); //해당되는 테이블 삭제
                dictionary.remove(index2.intValue()); //arraylist인 dictionary에서 tag에 해당하는 항목 삭제
                notifyDataSetChanged();
                Toast.makeText(context, tmp2.getName()+" 삭제", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    public void addItem(String dictName){
        MainListview item = new MainListview();
        item.setName(dictName);
        dictionary.add(item);
    }

    public void setDeleteState(boolean state){
        deleteState = state;
        notifyDataSetChanged();
    }

    private void deleteTable(String name){
        try {
            String delete_sql = "delete from Dictionary where dictName = '" + name + "'";
            String drop_sql = "drop table if exists `"+name +"`";
            db.execSQL(delete_sql);
            db.execSQL(drop_sql);
        } catch (SQLiteException se){
            //Toast.makeText(context, se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("delete 안됨 ", se.getMessage());
        }
    }
}
