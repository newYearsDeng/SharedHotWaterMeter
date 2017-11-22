package com.northmeter.sharedhotwatermeter.northmeter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.entity.GetChargeRecordModel;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;


/**
 * Created by dyd on 2017/8/25.
 */
public class RVAdapter_GetChargeRecord extends BaseAdapter {
    private List<GetChargeRecordModel> models;

    public RVAdapter_GetChargeRecord(List<GetChargeRecordModel> models) {
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(view==null){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_getchargerecord,viewGroup,false);
            viewHolder=new ViewHolder();
            viewHolder.tv1= (TextView) view.findViewById(R.id.tv_1);
            viewHolder.tv2= (TextView) view.findViewById(R.id.tv_2);
            viewHolder.tv3= (TextView) view.findViewById(R.id.tv_3);
            viewHolder.tv4= (TextView) view.findViewById(R.id.tv_4);
            view.setTag(viewHolder);
            AutoUtils.auto(view);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.tv1.setText(models.get(i).getChargeRecordName());
        viewHolder.tv2.setText(String.format("%.2f",Float.parseFloat(models.get(i).getChargeAmount()))+"元");
        viewHolder.tv3.setText(models.get(i).getChargeTime());
        viewHolder.tv4.setText(models.get(i).getChargeWay());
        return view;
    }



    private class ViewHolder{
        TextView tv1,tv2,tv3,tv4;
    }

    public void update(List<GetChargeRecordModel> newDatas){
        models.clear();
        models.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void add(List<GetChargeRecordModel> newData){
        if(models.contains(newData)){
            return;
        }
        models.addAll(newData);
        notifyDataSetChanged();
    }
}
