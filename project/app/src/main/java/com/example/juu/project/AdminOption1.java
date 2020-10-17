package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.juu.project.MainActivity1.managerDB;


public class AdminOption1 extends Dialog {
    InputMethodManager imm;
    CustomEditText layout;
    EditText pw;

    public AdminOption1(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_setting_option1); //레이아웃 설정

        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);

        layout = findViewById(R.id.setLayout1);
        pw = findViewById(R.id.pwTextView);
        pw.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD ); //비밀번호 입력시 *표기
        pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
            }
        });

        Button ok = (Button)findViewById(R.id.btnSetOk1);
        Button close = (Button)findViewById(R.id.btnSetClose1);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(managerDB.getSystemPassword().equals(pw.getText().toString())) { //입력한 비밀번호 = 관리자 비밀번호
                    new AdminOption2(context).show();
                }
                else {
                    layout.setError("비밀번호가 일치하지 않습니다.");
                    pw.setText(null);
                    return;
                }
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
