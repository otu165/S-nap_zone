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

public class Return3 extends Dialog {
    public Return3(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_main_return2); //레이아웃

        TextView textView = (TextView)findViewById(R.id.btnReTextView2);
        textView.setText("잔여 시간 : " + seatVariable.getLeftTimeNowFromEndDateTime());

        Button ok = (Button)findViewById(R.id.btnReOk2);
        Button close = (Button)findViewById(R.id.btnReClose2);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //좌석 활성화 및 삭제(반드시 버튼이 위치하는 액티비티가 붙어야 버튼설정이 완료됨)
                activity1.findViewById(seatVariable.getButton().getId()).setEnabled(true); //좌석 활성화
                managerDB.deleteFromSeatDB(); //SeatDB에서 제거
                seatVariable.showRemainSeat(); //잔여석 표기

                Toast.makeText(context, "반환이 완료되었습니다", Toast.LENGTH_SHORT).show();

                //대기자가 있고 메인2에서 좌석이 반환된 경우
                if(managerDB.isNowActivity2() && (managerDB.getWaitSize() != 0)) { //대기자가 0명이 아닐때
                    if(managerDB.getRemainSeat() > managerDB.getWaitSize()) { //잔여석 > 대기자 수
                        managerDB.updateActivity(); //메인2 = false, 메인1 = true 설정
                        context.startActivity(new Intent(context, MainActivity1.class)); //메인1로 전환
                        activity2.finish();
                    }
                    else { //단순히 메인2에서 좌석이 반환되고 메인1로 전환되면 안됨
                        System.out.println("CanIssue가 증가되었습니다.");
                        managerDB.updateCanIssue(); //발급받을 수 있는 대기번호 증가
                        TextView textView = activity2.findViewById(R.id.textView3);
                        String str = managerDB.getCanIssue() + " 번 까지의 대기자는 좌석을 발급받을 수 있습니다\n(아래 버튼을 눌러 좌석을 발급받으십시오)";
                        SpannableStringBuilder ssb = new SpannableStringBuilder(str); //글자색 포인트 지정
                        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textView.setText(ssb);
                    }
                }

                if(managerDB.getWaitSize() != 0) { //대기자 리스트의 대기시간 재할당
                    managerDB.updateWaitTime();
                }

                //반환 후 잔여석이 1개 이상 + 대기 인원이 0명 + 현재 액티비티가 메인2라면 메인2 -> 메인1 전환(현재 메인2 = true, 메인1 = false)
                if((managerDB.getRemainSeat() > 0) && (managerDB.getWaitSize() == 0) && managerDB.isNowActivity2()) {
                    managerDB.updateActivity(); //메인2 = false, 메인1 = true 설정
                    context.startActivity(new Intent(context, MainActivity1.class));
                    activity2.finish(); //메인2 종료
                }

                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"반환이 취소되었습니다",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
