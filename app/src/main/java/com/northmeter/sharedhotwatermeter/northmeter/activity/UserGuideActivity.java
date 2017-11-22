package com.northmeter.sharedhotwatermeter.northmeter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.BaseActivity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dyd on 2017/9/28.
 * 用户指南
 */
public class UserGuideActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.button_back)
    ImageView buttonBack;
    @BindView(R.id.listview_guide)
    ListView listviewGuide;
    @BindView(R.id.all_ques)
    RelativeLayout allQues;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_useguide;
    }

    @Override
    protected void inteView(Bundle savedInstanceState) {
        List<String> model = new ArrayList<String>();
        for (String name : getResources().getStringArray(R.array.user_guide_string)) {
            model.add(name);
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.userguide_list_item, R.id.text_name, model);
        listviewGuide.setAdapter(adapter);
        listviewGuide.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @OnClick({R.id.button_back, R.id.all_ques})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                finish();
                break;
            case R.id.all_ques:
                break;
        }
    }


}
