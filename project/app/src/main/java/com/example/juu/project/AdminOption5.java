package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.juu.project.MainActivity1.managerDB;


public class AdminOption5 extends Dialog {
    InputMethodManager imm;
    CustomEditText setting1, setting2;
    EditText pw1, pw2;

    public AdminOption5(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_setting_option5); //레이아웃 설정

        //텍스트 효과위한 레이아웃 설정
        setting1 = findViewById(R.id.settingPW1);
        setting2 = findViewById(R.id.settingPW2);
        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);


        //비밀번호 입력박스
        pw1 = findViewById(R.id.pwEditText1);
        pw2 = findViewById(R.id.pwEditText2);
        pw1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    imm.hideSoftInputFromWindow(pw1.getWindowToken(), 0);
            }
        });

        pw2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    imm.hideSoftInputFromWindow(pw2.getWindowToken(), 0);
            }
        });

        //텍스트 효과 설정
        settingEditText();

        Button ok = (Button)findViewById(R.id.btnSetOk5);
        Button close = (Button)findViewById(R.id.btnSetClose5);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPassword(pw1.getText().toString())) { //비밀번호 형식 검사
                    if (pw1.getText().toString().equals(pw2.getText().toString())) { //위아래 비밀번호가 일치
                        if (pw1.getText().toString().equals(managerDB.getSystemPassword())) { //현재 비밀번호와 같으면
                            setting2.setError("현재 사용중인 비밀번호와 동일합니다.");
                            pw1.setText(null);
                            pw2.setText(null);
                            return;
                        } else { //새로운 비밀번호면
                            managerDB.setSystemPassword(pw1.getText().toString()); //관리자 비밀번호 변경
                            Toast.makeText(context, "관리자 비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        setting2.setError("비밀번호가 일치하지 않습니다.");
                        pw2.setText(null); //2번째 텍스트박스만 바꿈
                        return;
                    }
                    dismiss();
                }
                else { //비밀번호 형식이 옳지 않은 경우
                    setting1.setError("8~16자 영문 대소문자, 숫자, 특수문자를 사용하세요.");
                    pw1.setText(null);
                    pw2.setText(null);
                    return;
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void settingEditText() { //텍스트 박스 설정
        //에러 메세지 출력
        setting1.setPasswordVisibilityToggleEnabled(true);
        setting1.setCounterEnabled(true);
        setting1.setCounterMaxLength(16);
        setting2.setPasswordVisibilityToggleEnabled(true);
        setting2.setCounterEnabled(true);
        setting2.setCounterMaxLength(16);
    }

    public boolean isValidPassword(String pw) {
        if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,16}$", pw)) {//비밀번호 형식 검사
//        if(!Pattern.matches("^(?=.*\\d)(?=.*[a-zA-Z]).{8,16}$", pw)) {
            return false;
        }
        else
            return true;
    }
}
