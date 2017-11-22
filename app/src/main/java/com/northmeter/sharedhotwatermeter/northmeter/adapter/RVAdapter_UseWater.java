package com.northmeter.sharedhotwatermeter.northmeter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.entity.UseWaterModel;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;


/**
 * Created by dyd on 2017/8/25.
 */
public class RVAdapter_UseWater extends BaseAdapter {
    private List<UseWaterModel> models;

    public RVAdapter_UseWater(List<UseWaterModel> models) {
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
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_usewater,viewGroup,false);
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
        viewHolder.tv1.setText(models.get(i).getBuyTimes());
        viewHolder.tv2.setText(models.get(i).getComaddress());
        viewHolder.tv3.setText(models.get(i).getUsingTime()+"分钟");

        String money = models.get(i).getUseMoney();
        if(money.equals("")){
            viewHolder.tv4.setText(money+"元");
        }else{
            viewHolder.tv4.setText(String.format("%.2f",Float.parseFloat(money))+"元");
        }
        return view;
    }



    private class ViewHolder{
        TextView tv1,tv2,tv3,tv4;
    }

    public void update(List<UseWaterModel> newDatas){
        models.clear();
        models.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void add(List<UseWaterModel> newData){
        if(models.contains(newData)){
            return;
        }
        models.addAll(newData);
        notifyDataSetChanged();
    }
}
