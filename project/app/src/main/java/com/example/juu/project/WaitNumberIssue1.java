package com.example.juu.project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.juu.project.MainActivity1.isValidCellPhoneNumber;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class WaitNumberIssue1 extends Dialog {
    InputMethodManager imm;
    TextInputLayout layout;
    EditText iss;

    public WaitNumberIssue1(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_issue1);

        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);

        layout = findViewById(R.id.IssLayout1);
        // 연락처 입력시 하이픈(-) 자동 입력
        iss = findViewById(R.id.IssPhoneNum1);
        iss.addTextChangedListener(new PhoneNumberFormattingTextWatcher("KR"));
        iss.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    iss.setHint("");
                else {
                    iss.setHint("010-0000-0000");
                    imm.hideSoftInputFromWindow(iss.getWindowToken(), 0);
                }
            }
        });

        Button ok = (Button)findViewById(R.id.btnIssOk1);
        Button close = (Button)findViewById(R.id.btnIssClose1);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidCellPhoneNumber(iss.getText().toString())) { //전화번호 형식이 옳은 경우
                    seatVariable.setPhoneNumber(iss.getText().toString()); //전화번호 저장
                    if(managerDB.findSeat(seatVariable.getPhoneNumber())
                            || managerDB.findWait(seatVariable.getPhoneNumber())) { //SeatDB 또는 WaitDB에 존재하는 전화번호일 경우
                        layout.setError("현재 좌석을 발급중이거나\n대기번호가 발급된 전화번호 입니다");
                        iss.setText(null);
                        return;
                    }
                    else { //신규 전화번호인 경우
                        new WaitNumberIssue2(context).show();
                    }
                }
                else { //전화번호 형식이 옳지 않은 경우
                    layout.setError("올바른 전화번호가 아닙니다.");
                    iss.setText(null);
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
