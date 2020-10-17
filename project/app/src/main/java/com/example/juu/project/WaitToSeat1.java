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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.juu.project.MainActivity1.isValidCellPhoneNumber;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class WaitToSeat1 extends Dialog {
    InputMethodManager imm;
    TextInputLayout layout;
    EditText wait;

    public WaitToSeat1(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_wait_issue1);

        imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);


        layout = (TextInputLayout)findViewById(R.id.IssLayout1);
        //입력시 자동 하이픈(-) 입력
        wait = findViewById(R.id.IssTextView1);
        wait.addTextChangedListener(new PhoneNumberFormattingTextWatcher("KR"));
        wait.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    wait.setHint("");
                else {
                    wait.setHint("010-0000-0000");
                    imm.hideSoftInputFromWindow(wait.getWindowToken(), 0);
                }
            }
        });

        Button ok = (Button)findViewById(R.id.btnWaitOk1);
        Button close = (Button)findViewById(R.id.btnWaitClose1);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidCellPhoneNumber(wait.getText().toString())) { //입력한 전화번호가 옳은 경우
                    seatVariable.setPhoneNumber(wait.getText().toString()); //전화번호 저장
                    if(managerDB.findWait(seatVariable.getPhoneNumber())) { //대기자 리스트에 존재하는 전화번호
                        if(seatVariable.getWaitNumber() <= managerDB.getCanIssue()) { //대기번호 <= 발급가능대기번호이면
                            Toast.makeText(context, seatVariable.getWaitNumber() + "번 대기자님 확인되었습니다", Toast.LENGTH_SHORT).show();
                            new WaitToSeat2(context).show();
                        }
                        else {
                            layout.setError("발급 대상이 아닌 대기자입니다.");
                            return;
                        }
                    }
                    else { //대기자 리스트에 존재하지 않는 전화번호 -> 거절
                        layout.setError("대기열에 없는 전화번호 입니다.");
                        return;
                    }
                }
                else { //전화번호 형식이 옳지 않은 경우
                    layout.setError("올바른 전화번호가 아닙니다.");
                    wait.setText(null);
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
