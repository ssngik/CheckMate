package kr.co.company.capstone.util.adapter;

import android.app.Activity;
import android.widget.Toast;

public class BackPressHandler {
    private long backPressedTime;
    private final Activity activity;
    private Toast toast;


    public void onBackPressed(){
        if(System.currentTimeMillis() > backPressedTime + 2000){
            backPressedTime = System.currentTimeMillis();
            showToast();
            return;
        }
        if(System.currentTimeMillis() <= backPressedTime + 2000){
            activity.finish();
            toast.cancel();
        }
    }


    public BackPressHandler(Activity activity){
        this.activity = activity;
    }

    public void showToast(){
        toast = Toast.makeText(activity, "앱을 종료 하시겠습니까 ? ", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showToast(String message){
        toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showToast(Activity activity, String message){
        toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
