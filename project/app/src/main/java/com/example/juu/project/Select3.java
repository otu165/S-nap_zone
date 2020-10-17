package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;


public class Select3 extends Dialog {
    public Select3(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_main_select3); //레이아웃 설정

        //전화번호 TextView 나타내기
        TextView btnSelectedPhoneNum = (TextView)findViewById(R.id.btnWaitPhoneNumber4);
        btnSelectedPhoneNum.setText(seatVariable.getPhoneNumber());

        //발급 시간 TextView 나타내기
        TextView btnSelectedTime = (TextView)findViewById(R.id.btnSeSelectedTime3);
        if(seatVariable.getSelectedHour() == 0) {
            btnSelectedTime.setText(seatVariable.getSelectedMin() + "분");
        }
        else {
            btnSelectedTime.setText(seatVariable.getSelectedHour() + "시간 " + seatVariable.getSelectedMin() + "분");
        }

        Button ok = (Button)findViewById(R.id.button2);
        Button close = (Button)findViewById(R.id.button3);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managerDB.insertToSeatDB(seatVariable.getPhoneNumber(), seatVariable.getEndDateTime(), seatVariable.getButton()); //SeatDB 저장
                seatVariable.showRemainSeat(); //잔여석 갱신

                new Select4(context).show();
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"발급이 취소되었습니다",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }


}
