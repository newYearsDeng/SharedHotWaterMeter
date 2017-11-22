package com.northmeter.sharedhotwatermeter.northmeter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.BaseActivity.BaseActivity;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowGetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowUseWater;
import com.northmeter.sharedhotwatermeter.northmeter.adapter.RVAdapter_GetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.adapter.RVAdapter_UseWater;
import com.northmeter.sharedhotwatermeter.northmeter.entity.GetChargeRecordModel;
import com.northmeter.sharedhotwatermeter.northmeter.entity.UseWaterModel;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.GetChargeRecordPresenter;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.GetUseWaterPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dyd on 2017/8/25.
 * 用水记录
 */
public class UseWaterActivity extends BaseActivity implements IShowUseWater, XRefreshView.XRefreshViewListener {
    @BindView(R.id.button_back)
    ImageView buttonBack;
    @BindView(R.id.xRefreshView)
    XRefreshView xRefreshView;
    @BindView(R.id.listview_chage)
    ListView listview_chage;

    private RVAdapter_UseWater adapter;
    private GetUseWaterPresenter getUseWaterPresenter;
    private String telNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_usewater;
    }

    @Override
    protected void inteView(Bundle savedInstanceState) {
        try{
            Intent intent = getIntent();
            telNum = intent.getStringExtra("TEL");
            getUseWaterPresenter = new GetUseWaterPresenter(this);

            // 设置是否可以下拉刷新
            xRefreshView.setPullRefreshEnable(true);
            // 设置是否可以上拉加载
            xRefreshView.setPullLoadEnable(false);
            // 设置上次刷新的时间
            xRefreshView.restoreLastRefreshTime(xRefreshView.getLastRefreshTime());
            // 设置时候可以自动刷新
            xRefreshView.setAutoRefresh(false);
            xRefreshView.setXRefreshViewListener(this);

            adapter = new RVAdapter_UseWater(new ArrayList<UseWaterModel>());
            listview_chage.setAdapter(adapter);

            xRefreshView.startRefresh();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick(R.id.button_back)
    public void onViewClicked() {
        this.finish();
    }


    @Override
    public void onRefresh() {
        getUseWaterPresenter.GetUseWaterPresenter(0,telNum);
    }

    @Override
    public void onLoadMore(boolean isSilence) {
        getUseWaterPresenter.GetUseWaterPresenter(0,telNum);
    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }

    @Override
    public void showSuccessMessage(int state, List<UseWaterModel> models) {
        if(!this.isFinishing()){
            adapter.update(models);
        }

    }

    @Override
    public void showFailMessage(String message) {
        Toast.makeText(UseWaterActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopRefresh() {
        if(!this.isFinishing()){
            xRefreshView.stopRefresh();
        }
    }

}
