package com.vocabulary.LemonVoca;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddWordDialog extends Dialog implements  View.OnClickListener{
    private Context mContext;
    private Button addButton;
    private Button closeButton;
    private EditText eng;
    private EditText kor;

    private CustomDialogListener customDialogListener;
    public AddWordDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    interface  CustomDialogListener{
        void onPositiveClicked(String english, String korean);
        void onNegativeClicked();
    }

    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_addword);

        //final Dialog dialog = new Dialog(mContext);
        addButton = (Button) findViewById(R.id.addButton);
        closeButton = (Button) findViewById(R.id.closeButton);
        eng = (EditText) findViewById(R.id.engWord);
        kor = (EditText) findViewById(R.id.korWord);

        addButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addButton:
                String english = eng.getText().toString();
                String korean = kor.getText().toString();
                customDialogListener.onPositiveClicked(english, korean);
                dismiss();
                break;
            case R.id.closeButton:
                cancel();
                break;
        }
    }
}

