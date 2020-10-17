package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
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

public class Extension1 extends Dialog {
    InputMethodManager imm;
    TextInputLayout layout;
    EditText ex;

    public Extension1(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_extension1);

        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);

        layout = findViewById(R.id.ExLayout1);
        //전화번호 자동 하이픈(-) 입력 구현
        ex = findViewById(R.id.ExPhoneNum);
        ex.addTextChangedListener(new PhoneNumberFormattingTextWatcher("KR"));
        ex.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    ex.setHint("");
                else {
                    ex.setHint("010-0000-0000");
                    imm.hideSoftInputFromWindow(ex.getWindowToken(), 0);
                }
            }
        });

        Button ok = (Button)findViewById(R.id.btnExOk1);
        Button close = (Button)findViewById(R.id.btnExClose1);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidCellPhoneNumber(ex.getText().toString())) { //전화번호 형식이 옳은 경우
                    seatVariable.setPhoneNumber(ex.getText().toString()); //전화번호 저장
                    if(managerDB.findSeat(seatVariable.getPhoneNumber())) { //일치하는 전화번호가 있는 경우
                        if(!managerDB.getFlagFromSeatDB()) { //flag = false인 경우(연장 가능)
                            new Extension2_accept(context).show();
                        }
                        else { //flag = true(연장 불가)
                            Toast.makeText(context, "연장 가능 횟수를 초과했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else { //전화번호 일치 X
                        new Extension2_deny(context).show();
                    }
                }
                else { //전화번호 형식이 옳지 않은 경우
                    layout.setError("올바른 전화번호가 아닙니다.");
                    ex.setText(null);
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
