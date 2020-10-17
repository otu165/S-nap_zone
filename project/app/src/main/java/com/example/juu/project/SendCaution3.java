package com.example.juu.project;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.juu.project.MainActivity1.activity1;
import static com.example.juu.project.MainActivity1.seatVariable;

public class SendCaution3 extends Dialog {
    InputMethodManager imm;
    PendingIntent sentPI;
    Activity act = activity1;
    Context con;
    String message = "[S-nap zone]\n이용에 불편을 겪는 사용자가 있습니다.\n다른 사람을 위해 수면실 이용 규칙을 준수해주시기 바랍니다.";

    public SendCaution3(Context context) {
        super(context);
        con = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        setContentView(R.layout.activity_send_caution3); //레이아웃 설정

        imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

        TextView textView = findViewById(R.id.SendTextView3);
        textView.setText("[ " + seatVariable.getButton().getText() + " ] 좌석 사용자에게\n메시지를 전송하시겠습니까?");

        Button ok = (Button)findViewById(R.id.btnSendOk3);
        Button close = (Button)findViewById(R.id.btnSendClose3);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCautionSMS();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        System.out.println("SendCaution3이 실행되었습니다.");
    }

    private void sendCautionSMS() {
        int permissionCheck = ContextCompat.checkSelfPermission(act, Manifest.permission.SEND_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //SMS 전송 권한 확인
            ActivityCompat.requestPermissions(act, new String[] {Manifest.permission.SEND_SMS},1);
            Toast.makeText(con, "권한 허용 후 확인을 누르십시오", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SmsManager sms = SmsManager.getDefault();

            // 아래 구문으로 지정된 핸드폰으로 문자 메시지를 보낸다
            sms.sendTextMessage(seatVariable.getPhoneNumber(), null, message, sentPI, null);
            Toast.makeText(con,"전송을 완료하였습니다",Toast.LENGTH_LONG).show();
            dismiss();
        }
    }
}
