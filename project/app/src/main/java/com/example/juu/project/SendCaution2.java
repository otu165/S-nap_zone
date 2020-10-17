package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;

public class SendCaution2 extends Dialog implements View.OnClickListener {
    public static SendCaution2 sendCaution2;
    private ArrayList<Button> buttonList1 = new ArrayList<>();
    Context context;

    public SendCaution2(Context context) {
        super(context);
        sendCaution2 = this;
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_send_caution2); //레이아웃 설정

        //모든 버튼 객체가 저장된 리스트 생성
        int[] array = {R.id.Button1, R.id.Button2, R.id.Button3,R.id.Button4,R.id.Button5,R.id.Button6,R.id.Button7,R.id.Button8,R.id.Button9,
                R.id.Button10,R.id.Button11,R.id.Button12,R.id.Button13,R.id.Button14,R.id.Button15,R.id.Button16,
                R.id.Button17,R.id.Button18,R.id.Button19,R.id.Button20,R.id.Button21,R.id.Button22,R.id.Button23,R.id.Button24,
                R.id.Button25,R.id.Button26,R.id.Button27,R.id.Button28,R.id.Button29,R.id.Button30,R.id.Button31,R.id.Button32,
                R.id.Button33, R.id.Button34, R.id.Button35, R.id.Button36};

        for(int i=0;i<36;i++) {
            Button b = findViewById(array[i]);
            b.setOnClickListener(this);
            buttonList1.add(b);
        }

        //사용중인 좌석의 선택 활성화
        managerDB.setSelectedSeatEnabled(buttonList1);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            default:
                Button button = findViewById(v.getId()); //문자를 전송할 좌석
                seatVariable.setButton(button); //SendCaution2에 존재하는 좌석 저장
                managerDB.findUser(button.getText().toString()); //해당 좌석 사용자의 전화번호 seatVariable에 저장
                new SendCaution3(context).show();
                dismiss();
                break;
        }
    }
}
