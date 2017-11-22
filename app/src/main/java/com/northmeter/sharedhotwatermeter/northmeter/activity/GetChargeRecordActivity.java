package com.northmeter.sharedhotwatermeter.northmeter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.BaseActivity.BaseActivity;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowGetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.adapter.RVAdapter_GetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.entity.GetChargeRecordModel;
import com.northmeter.sharedhotwatermeter.northmeter.helper.DividerItemDecoration;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.GetChargeRecordPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dyd on 2017/8/25.
 * 查看充值流水
 */
public class GetChargeRecordActivity extends BaseActivity implements IShowGetChargeRecord, XRefreshView.XRefreshViewListener {
    @BindView(R.id.button_back)
    ImageView buttonBack;
    @BindView(R.id.xRefreshView)
    XRefreshView xRefreshView;
    @BindView(R.id.listview_chage)
    ListView listview_chage;

    private RVAdapter_GetChargeRecord adapter;
    private GetChargeRecordPresenter getChargeRecordPresenter;
    private String telNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_getchargerecord;
    }

    @Override
    protected void inteView(Bundle savedInstanceState) {
        try{
            Intent intent = getIntent();
            telNum = intent.getStringExtra("TEL");
            getChargeRecordPresenter = new GetChargeRecordPresenter(this);

            // 设置是否可以下拉刷新
            xRefreshView.setPullRefreshEnable(true);
            // 设置是否可以上拉加载
            xRefreshView.setPullLoadEnable(false);
            // 设置上次刷新的时间
            xRefreshView.restoreLastRefreshTime(xRefreshView.getLastRefreshTime());
            // 设置时候可以自动刷新
            xRefreshView.setAutoRefresh(false);
            xRefreshView.setXRefreshViewListener(this);

            adapter = new RVAdapter_GetChargeRecord(new ArrayList<GetChargeRecordModel>());
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
        getChargeRecordPresenter.GetChargeRecord(0,telNum);
    }

    @Override
    public void onLoadMore(boolean isSilence) {
        getChargeRecordPresenter.GetChargeRecord(0,telNum);
    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }

    @Override
    public void showSuccessMessage(int state, List<GetChargeRecordModel> models) {
        if(!this.isFinishing()){
            adapter.update(models);
        }

    }

    @Override
    public void showFailMessage(String message) {
        Toast.makeText(GetChargeRecordActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopRefresh() {
        if(!this.isFinishing()){
            xRefreshView.stopRefresh();
        }
    }

}
