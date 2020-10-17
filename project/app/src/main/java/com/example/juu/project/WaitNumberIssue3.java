package com.example.juu.project;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.juu.project.MainActivity1.activity1;
import static com.example.juu.project.MainActivity1.managerDB;
import static com.example.juu.project.MainActivity1.seatVariable;
import static com.example.juu.project.MainActivity2.activity2;

public class WaitNumberIssue3 extends Dialog {
    InputMethodManager imm;
    PendingIntent sentPI;
    Activity act = activity2;
    Context con;
    String message;

    public WaitNumberIssue3(Context context) {
        super(context);

        con = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_issue3);

        TextView textView = (TextView)findViewById(R.id.btnIssTextView3_1);
        textView.setText("대기번호 : " + managerDB.getNextWaitNumber() + "번, 예상 대기 시간 : " + managerDB.getWaitTime());
        message = "[S-nap zone]\n대기번호 : " + managerDB.getNextWaitNumber() + "번\n예상 대기 시간 : " + managerDB.getWaitTime() + "\n정상적으로 대기열에 등록되었습니다.";

        Button ok = (Button)findViewById(R.id.btnIssOk3);
        Button close = (Button)findViewById(R.id.btnIssClose3);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //WaitDB에 insert
                managerDB.insertToWaitDB(seatVariable.getPhoneNumber());

                //WaitHelperDB에 대기번호 증가
                managerDB.updateNextWaitNumber();

                //메인2의 대기자 수 업데이트
                TextView textView = activity2.findViewById(R.id.btnMainTextView2);
                String str = "대기 인원 : " + managerDB.getWaitSize() + " 명";
                SpannableStringBuilder ssb = new SpannableStringBuilder(str); //글자색 포인트 지정
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#B3FF0000")), 8, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(ssb);

                Toast.makeText(context,"대기번호 발급 완료", Toast.LENGTH_SHORT).show();
                sendIssueSMS(); //대기번호 발급 완료 문자 전송
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"대기번호 발급이 취소되었습니다",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void sendIssueSMS() {
        int permissionCheck = ContextCompat.checkSelfPermission(act, Manifest.permission.SEND_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //SMS 전송 권한 확인
            ActivityCompat.requestPermissions(act, new String[] {Manifest.permission.SEND_SMS},1);
//            Toast.makeText(con, "권한 허용 후 확인을 누르십시오", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SmsManager sms = SmsManager.getDefault();

            // 아래 구문으로 지정된 핸드폰으로 문자 메시지를 보낸다
            sms.sendTextMessage(seatVariable.getPhoneNumber(), null, message, sentPI, null);
            dismiss();
        }
    }
}
