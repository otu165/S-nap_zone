package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.InputMismatchException;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.juu.project.MainActivity1.activity1;
import static com.example.juu.project.MainActivity1.isValidCellPhoneNumber;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class Select1 extends Dialog {
    InputMethodManager imm;
    CustomEditText layout;
    EditText se;

    public Select1(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_main_select1); //레이아웃 설정

        imm = (InputMethodManager)activity1.getSystemService(INPUT_METHOD_SERVICE);

        //텍스트 레이아웃 설정
        layout = findViewById(R.id.SeLayout1);
        layout.setErrorEnabled(true);

        // 연락처 입력시 하이픈(-) 자동 입력
        se = findViewById(R.id.SePhoneNum);
        se.addTextChangedListener(new PhoneNumberFormattingTextWatcher("KR"));
        se.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    se.setHint("");
                else {
                    se.setHint("010-0000-0000");
                    imm.hideSoftInputFromWindow(se.getWindowToken(), 0);
                }
            }
        });

        Button ok = (Button)findViewById(R.id.btnSeOk1);
        Button close = (Button)findViewById(R.id.btnSeClose1);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidCellPhoneNumber(se.getText().toString())) { //전화번호 형식이 옳은 경우
                    seatVariable.setPhoneNumber(se.getText().toString()); //전화번호 저장
                    if(managerDB.findSeat(seatVariable.getPhoneNumber())) { //발급 리스트에 이미 존재하는 전화번호일 경우
                        layout.setError("이미 발급중인 전화번호 입니다.");
                        se.setText(null);
                        return;
                    }
                    else if(managerDB.findWait(seatVariable.getPhoneNumber())) { //대기자 리스트에 존재하는 전화번호인 경우
                        if(seatVariable.getWaitNumber() <= managerDB.getCanIssue()) { //대기번호가 좌석발급가능대기번호보다 같거나 작으면
                            Toast.makeText(context,
                                    seatVariable.getWaitNumber() + "번 대기자님 확인되었습니다", Toast.LENGTH_SHORT).show();
                            new WaitToSeat3(context).show();
                        }
                        else {
                            layout.setError("발급 대상이 아닌 대기자입니다.");
                            se.setText(null);
                            return;
                        }
                    }
                    else { //새로운 전화번호인 경우
                        new Select2(context).show();
                    }
                }
                else { //전화번호 형식이 옳지 않은 경우
                    layout.setError("올바른 전화번호가 아닙니다.");

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
