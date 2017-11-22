package com.northmeter.sharedhotwatermeter.northmeter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.mob.MobSDK;
import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.bt_bluetooth.BTMainActivity;
import com.northmeter.sharedhotwatermeter.northmeter.BaseActivity.BaseActivity;
import com.northmeter.sharedhotwatermeter.northmeter.helper.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by dyd on 2017/8/18.
 */
public class LoginActivity extends BaseActivity {
    private String TGA = "northmeter.activity.LoginActivity";

    @BindView(R.id.et_telnumber)
    EditText etTelnumber;
    @BindView(R.id.et_login_code)
    EditText etLoginCode;
    @BindView(R.id.to_get_code)
    Button toGetCode;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;


    private int i = 60;
    private SharedPreferences sp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void inteView(Bundle savedInstanceState) {
        reg_smssdk();
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        boolean login_flag = sp.getBoolean("LoginFlag", false);//判断是否登录
        if (login_flag) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            LoginActivity.this.finish();
        } else {
            String telNum = sp.getString("TEL", null);
            if (telNum != null) {
                etTelnumber.setText(telNum);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 短信验证注册
     */
    private void reg_smssdk() {
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        // SMSSDK.setAskPermisionOnReadContact(boolShowInDialog)
        MobSDK.init(this, Constants.MOB_APP_KEY, Constants.MOB_secret);
        // 创建EventHandler对象
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };

        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    @OnClick({R.id.to_get_code, R.id.btn_login,R.id.iv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_delete:
                etTelnumber.setText("");
                break;
            case R.id.to_get_code:
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(etTelnumber.getText().toString())) {
                    return;
                } // 2. 通过sdk发送短信验证
                SMSSDK.getVerificationCode("86", etTelnumber.getText().toString());

                // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                toGetCode.setClickable(false);
                toGetCode.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-9);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
                break;
            case R.id.btn_login:
                showDialog("");
                SMSSDK.submitVerificationCode("86", etTelnumber.getText().toString(), etLoginCode
                        .getText().toString());
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                if (msg.what == -9) {
                    toGetCode.setText("重新发送(" + i + ")");
                } else if (msg.what == -8) {
                    toGetCode.setText("获取验证码");
                    toGetCode.setClickable(true);
                    i = 60;
                } else {
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    Log.e(TGA, "event==== " + event);
                    Log.e(TGA, "result==== " + result);
                    Log.e(TGA, "data==== " + data.toString());
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // 短信注册成功后，返回MainActivity
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                            stopDialog();
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("TEL", etTelnumber.getText().toString());
                            editor.putBoolean("LoginFlag", true);
                            editor.commit();
                            i = 0;

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                            LoginActivity.this.finish();

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            Toast.makeText(LoginActivity.this, "验证码已发出",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ((Throwable) data).printStackTrace();
                            Toast.makeText(LoginActivity.this, data.toString(),
                                    Toast.LENGTH_SHORT).show();
                            stopDialog();
                        }
                    } else {
                        int state = data.toString().indexOf("{");
                        String json = data.toString().substring(state, data.toString().length());

                        JSONObject jsonobject = JSONObject.parseObject(json);
                        Object detail = jsonobject.get("detail");
                        Toast.makeText(LoginActivity.this, detail.toString(),
                                Toast.LENGTH_SHORT).show();
                        stopDialog();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * progressbar
     */
    private void createProgressBar() {
        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ProgressBar mProBar = new ProgressBar(this);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.VISIBLE);
        layout.addView(mProBar);
    }
}
