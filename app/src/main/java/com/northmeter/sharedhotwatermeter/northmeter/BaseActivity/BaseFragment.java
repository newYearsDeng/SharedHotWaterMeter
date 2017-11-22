package com.northmeter.sharedhotwatermeter.northmeter.BaseActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.northmeter.sharedhotwatermeter.northmeter.helper.ProgressAlertDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *@author  dyd
 *@time    2017/08/03
 *@des:    baseFragment
 */
public abstract  class BaseFragment extends Fragment {
    private Unbinder unbinder;
    ProgressAlertDialog mProgress;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startGetArgument(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView=inflater.inflate(getLayoutResId(),container,false);
        return parentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         unbinder = ButterKnife.bind(this, view);
        //初始化控件
        finishCreateView(savedInstanceState);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected abstract int getLayoutResId();

    /**
     * 初始化传递的参数
     */
    protected abstract void startGetArgument(Bundle savedInstanceState);

    /**
     * 初始化控件
     * @param savedInstanceState
     */
    protected abstract void finishCreateView(Bundle savedInstanceState);


    /**开始网络加载动画*/
    protected  void showDialog(){
        mProgress = new ProgressAlertDialog(getActivity());
        mProgress.show();
    }

    /**停止网络加载动画*/
    protected  void stopDialog(){
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }

    }


}
