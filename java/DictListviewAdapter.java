package com.vocabulary.LemonVoca;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.vocabulary.LemonVoca.DictActivity.DictionaryName;
import static com.vocabulary.LemonVoca.DictActivity.dupliCheck2;

public class DictListviewAdapter extends BaseAdapter {
    DBHelper helper;
    SQLiteDatabase db;

    private ArrayList<DictListview> vocabulary = new ArrayList<DictListview>();
    static Button delete2;
    private boolean deleteState = false;
    Context context;

    public DictListviewAdapter(DBHelper helper, SQLiteDatabase db){
        this.helper = helper;
        this.db = db;
    }
    @Override
    public int getCount() {
        return vocabulary.size();
    }

    @Override
    public Object getItem(int position) {
        return vocabulary.get(position);
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
            convertView = layoutInflater.inflate(R.layout.listview_vocalist, parent, false);
        }
        TextView english = (TextView) convertView.findViewById(R.id.english);
        TextView korean = (TextView) convertView.findViewById(R.id.korean);
        delete2 = (Button) convertView.findViewById(R.id.deleteButton2);
        delete2.setTag(position); //delete버튼에 tag달기

        DictListview listview = vocabulary.get(position);
        english.setText(listview.getEnglish());
        korean.setText(listview.getKorean());

        if (deleteState) {
            delete2.setVisibility(convertView.VISIBLE);
        }
        else {
            delete2.setVisibility(convertView.INVISIBLE);
        }

        delete2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Integer index = (Integer) v.getTag(); //버튼의 tag값 받아오기
                DictListview tmp = vocabulary.get(index.intValue());
                dupliCheck2.remove(tmp.getEnglish()); //중복 확인 위해 저장해둔 단어 삭제
                deleteWord(tmp.getEnglish(), tmp.getKorean()); //해당되는 테이블에서 단어 삭제
                vocabulary.remove(index.intValue()); //arraylist인 dictionary에서 tag에 해당하는 항목 삭제
                notifyDataSetChanged();
                Toast.makeText(context, tmp.getEnglish()+" 삭제", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    public void addItem(String english, String korean){
        DictListview item = new DictListview();
        item.setEnglish(english);
        item.setKorean(korean);
        vocabulary.add(item);
    }

    public void setDeleteState(boolean state){
        deleteState = state;
        notifyDataSetChanged();
    }

    public void deleteWord(String english, String korean){
        try {
            String delete_sql = "delete from `"+DictionaryName+"` where word = '" + english + "' AND korean = '" + korean + "'";
            db.execSQL(delete_sql);
        } catch (SQLiteException se){
            //Toast.makeText(context, se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("delete 안됨 ", se.getMessage());
        }
    }
}
