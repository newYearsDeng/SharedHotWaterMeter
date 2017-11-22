package com.northmeter.sharedhotwatermeter.northmeter.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.northmeter.sharedhotwatermeter.R;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 *欢迎页
 */
public class WelcomeActivity extends AutoLayoutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we);
//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	try{
            		startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                    finish();
            	}catch(Exception e){
            		startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            		e.printStackTrace();
            	}
               
            }
        }, 2000);
        
    }
    

}
