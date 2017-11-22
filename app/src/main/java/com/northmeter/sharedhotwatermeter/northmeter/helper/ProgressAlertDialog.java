package com.northmeter.sharedhotwatermeter.northmeter.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.sharedhotwatermeter.R;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by dyd on 2017/8/18.
 */
public class ProgressAlertDialog extends AlertDialog {

    private ImageView progressImg;
    private TextView message;
    //旋转动画
    private Animation animation;

    public ProgressAlertDialog(Context context) {
        super(context, R.style.LoadDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress_layout);

        //点击imageview外侧区域，动画不会消失
        setCanceledOnTouchOutside(false);

        progressImg = (ImageView) findViewById(R.id.refreshing_img);
        message = (TextView) findViewById(R.id.message);

        //加载动画资源
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_progress_rotate);
        //动画完成后，是否保留动画最后的状态，设为true
        animation.setFillAfter(true);

//        LinearInterpolator lir = new LinearInterpolator();
//        animation.setInterpolator(lir);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                progressImg.clearAnimation();
//            }
//        },1000);

    }

    public void show(String msg) {
        super.show();
        if (message != null) {
            message.setText(msg);
        }
    }

    /**
     * 在AlertDialog的 onStart() 生命周期里面执行开始动画
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (animation != null) {
            progressImg.startAnimation(animation);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressImg.clearAnimation();
    }
}
