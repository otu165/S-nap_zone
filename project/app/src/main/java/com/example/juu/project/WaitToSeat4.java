package com.example.juu.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.juu.project.MainActivity1.activity1;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;
import static com.example.juu.project.MainActivity2.activity2;

public class WaitToSeat4 extends Dialog {
    public WaitToSeat4(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_wait_issue4);

        //전화번호 TextView 나타내기
        TextView btnSelectedPhoneNumber = (TextView)findViewById(R.id.btnWaitPhoneNumber4);
        btnSelectedPhoneNumber.setText(seatVariable.getPhoneNumber());

        //사용자가 선택한 이용 시간 TextView 나타내기
        TextView btnSelectedTime = (TextView)findViewById(R.id.btnWaitSelectedTime4);
        if(seatVariable.getSelectedHour() == 0) {
            btnSelectedTime.setText(seatVariable.getSelectedMin() + "분");
        }
        else {
            btnSelectedTime.setText(seatVariable.getSelectedHour() + "시간 " + seatVariable.getSelectedMin() + "분");
        }

        Button ok = (Button)findViewById(R.id.btnWaitOk4);
        Button close = (Button)findViewById(R.id.btnWaitClose4);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //전화번호에 해당하는 객체 WaitDB에서 제거 + 대기인원 감소
                    managerDB.deleteFromWaitDB(seatVariable.getPhoneNumber()); //WaitDB에서 제거
                    managerDB.insertToSeatDB(seatVariable.getPhoneNumber(), seatVariable.getEndDateTime(), seatVariable.getButton()); //SeatDB에 삽입

                    //좌석처리
                    seatVariable.showRemainSeat();

                    //발급이 완료됨과 동시에 WaitDB의 개수 -1, SeatDB의 개수 +1, 대기인원의 개수 -1
                    TextView textView = activity2.findViewById(R.id.btnMainTextView2);
                    String str = "대기 인원 : " + managerDB.getWaitSize() + " 명 ";
                    SpannableStringBuilder ssb = new SpannableStringBuilder(str); //글자색 포인트 지정
                    ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 8, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(str);

                    //대기인원 < 잔여석이라 메인1로 전환된상태에서, 대기인원 전부가 좌석을 발급받은 경우 textView없애기
                    if (managerDB.getWaitSize() == 0) {
                        TextView textView1 = activity1.findViewById(R.id.notice);
                        textView1.setText(null);
                        TextView textView2 = activity1.findViewById(R.id.notice1);
                        textView2.setText(null);
                    }

                    new WaitToSeat5(context).show();
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "발급가능 시간이 지났습니다.", Toast.LENGTH_SHORT).show(); //좌석 발급하는 도중에 데이터가 사라졌을 경우 대비
                    dismiss();
                }
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
