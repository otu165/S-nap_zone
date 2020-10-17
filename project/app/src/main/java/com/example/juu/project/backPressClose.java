package com.example.juu.project;

import android.app.Activity;
import android.widget.Toast;

//뒤로가기 두번 = 종료
public class backPressClose {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public backPressClose(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        else {
            activity.finish();
            toast.cancel();
        }
    }
    private void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
