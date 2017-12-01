package com.northmeter.sharedhotwatermeter.northmeter.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.BaseActivity.BaseActivity;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowUserCenterMessage;
import com.northmeter.sharedhotwatermeter.northmeter.helper.ObservableScrollView;
import com.northmeter.sharedhotwatermeter.northmeter.helper.StatusBarCompat;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.UserCenterPresenter;
import com.northmeter.sharedhotwatermeter.northmeter.updata.Download;
import com.northmeter.sharedhotwatermeter.northmeter.updata.NetworkUtil;
import com.northmeter.sharedhotwatermeter.northmeter.updata.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dyd on 2017/8/23.
 * 用户中心
 */
public class UserCenter extends BaseActivity implements AdapterView.OnItemClickListener, IShowUserCenterMessage, ObservableScrollView.ScrollViewListener {
    @BindView(R.id.button_back)
    ImageView buttonBack;
    @BindView(R.id.text_total_water)
    TextView textTotalWater;
    @BindView(R.id.text_total_money)
    TextView textTotalMoney;
    @BindView(R.id.text_total_count)
    TextView textTotalCount;
    @BindView(R.id.text_username)
    TextView textUsername;
    @BindView(R.id.text_oper_leftmoney)
    TextView textOperLeftmoney;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.realative_title)
    Toolbar realativeTitle;
    @BindView(R.id.scrollview)
    ObservableScrollView scrollview;
    @BindView(R.id.linear_heigh)
    LinearLayout linearHeigh;

    private int height;
    private String telNum;
    private UserCenterPresenter userCenterPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_usercenter;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void inteView(Bundle savedInstanceState) {
        setSupportActionBar(realativeTitle);
        getSupportActionBar().setTitle("");
        //底部导航栏
        //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        Intent intent = getIntent();
        telNum = intent.getStringExtra("TEL");
        textUsername.setText(telNum);
        userCenterPresenter = new UserCenterPresenter(this);
        userCenterPresenter.getUserCenterData(telNum, "000000000000");

        String[] user_center_string = getResources().getStringArray(R.array.user_center_string);
        List<String> model = new ArrayList<String>();
        for (String name : user_center_string) {
            model.add(name);
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.usercenter_list_item, R.id.text_name, model);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        ViewTreeObserver vto = linearHeigh.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearHeigh.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height = linearHeigh.getHeight();
                linearHeigh.getWidth();

                scrollview.setScrollViewListener(UserCenter.this);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void dialog_show() {
        final AlertDialog dialogSex = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog)).create();
        dialogSex.show();
        Window window = dialogSex.getWindow();
        window.setContentView(R.layout.dialog_layout);

        //DisplayMetrics metrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);

        dialogSex.setCanceledOnTouchOutside(true);
        dialogSex.setCancelable(true);
        window.setWindowAnimations(R.style.AnimBottom_Dialog);

        window.findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("LoginFlag", false);
                editor.commit();

                Intent loginIntent = new Intent(UserCenter.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_left,
                        R.anim.slide_right);
                finish();
            }
        });

        window.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSex.cancel();
            }
        });

        window.findViewById(R.id.relativeLayout2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialogSex.cancel();
            }
        });
    }

    @OnClick({R.id.button_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                finish();
                break;
        }
    }


    @Override
    public void showFailMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessMessage(String oper_LeftMoney, String OPER_Use, String OPER_UseMoney, String OPER_Times) {
        if (!this.isFinishing()) {
            textOperLeftmoney.setText(oper_LeftMoney);
            textTotalWater.setText(OPER_Use);
            textTotalMoney.setText(OPER_UseMoney);
            textTotalCount.setText(OPER_Times);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0://我的用水
                Intent intent_0 = new Intent(UserCenter.this, UseWaterActivity.class);
                intent_0.putExtra("TEL", telNum);
                startActivity(intent_0);
                break;
            case 1://我的消息
                break;
            case 2://用户指南
                startActivity(new Intent(this, UserGuideActivity.class));
                break;
            case 3://用户指南
                break;
            case 4://建议
                break;
            case 5://版本更新
                if(true){
                    int i = NetworkUtil.checkedNetWorkType(this);
                    if(i == NetworkUtil.NOWIFI){
                        ToastUtils.showToast(this,"没有WIFI");
                    }else if(i == NetworkUtil.WIFI){
                        ToastUtils.showToast(this,"有WIFI");
                        final ProgressDialog pd; // 进度条对话框
                        pd = new ProgressDialog(this);
                        pd.setCancelable(true);// 必须一直下载完，不可取消
                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pd.setMessage("正在下载安装包，请稍后");
                        pd.setTitle("版本升级");
                        pd.show();
                        new Download(pd).start();
                    }
                }else{
                    //无新本
                    ToastUtils.showToast(this,"该版本已经是最新版本");
                }
                break;
            case 6://二维码下载
                break;
            case 7://关于我们
                break;
            case 8://注销
                dialog_show();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {
            realativeTitle.setBackgroundColor(Color.argb((int) 0, 68, 98, 164));
        } else if (y > 0 && y <= height) {
            float scale = (float) y / height;
            float alpha = (255 * scale);
            realativeTitle.setBackgroundColor(Color.argb((int) alpha, 68, 98, 164));
        } else {
            realativeTitle.setBackgroundColor(Color.argb((int) 255, 68, 98, 164));
        }
    }
}
