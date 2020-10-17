package com.example.juu.project;

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

public class Return1 extends Dialog {
    InputMethodManager imm;
    TextInputLayout layout;
    EditText re;

    public Return1(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_main_return1); //레이아웃

        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);

        layout = findViewById(R.id.ReLayout1);
        // 연락처 입력시 하이픈(-) 자동 입력
        re = findViewById(R.id.RePhoneNum);
        re.addTextChangedListener(new PhoneNumberFormattingTextWatcher("KR"));
        re.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    re.setHint("");
                else {
                    re.setHint("010-0000-0000");
                    imm.hideSoftInputFromWindow(re.getWindowToken(), 0);
                }
            }
        });

        Button ok = (Button)findViewById(R.id.btnReOk1);
        Button close = (Button)findViewById(R.id.btnReClose1);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidCellPhoneNumber(re.getText().toString())) { //전화번호가 옳은 경우
                    seatVariable.setPhoneNumber(re.getText().toString()); //전화번호 임시 저장
                    if(managerDB.findSeat(seatVariable.getPhoneNumber())) { //SeatDB에 일치하는 전화번호가 있는 경우
                        new Return2_accept(context).show();
                    }
                    else { //없는 경우
                        new Extension2_deny(context).show();
                    }
                }
                else { //전화번호가 옳지 않은 경우
                    layout.setError("올바른 전화번호가 아닙니다.");
                    re.setText(null);
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
