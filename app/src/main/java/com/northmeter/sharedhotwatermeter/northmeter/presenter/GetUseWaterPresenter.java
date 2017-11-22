package com.northmeter.sharedhotwatermeter.northmeter.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.sharedhotwatermeter.northmeter.I.IGetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.I.IGetUseWaterPresenter;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowGetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowUseWater;
import com.northmeter.sharedhotwatermeter.northmeter.entity.GetChargeRecordModel;
import com.northmeter.sharedhotwatermeter.northmeter.entity.UseWaterModel;
import com.northmeter.sharedhotwatermeter.northmeter.helper.WebServiceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dyd on 2017/8/25.
 */
public class GetUseWaterPresenter implements IGetUseWaterPresenter {
    public IShowUseWater iShowUseWater;

    public GetUseWaterPresenter(IShowUseWater iShowUseWater){
        this.iShowUseWater = iShowUseWater;
    }
    @Override
    public void GetUseWaterPresenter(final int state, String customer_telphone) {
        String para = "<customer_telphone>"+customer_telphone+"</customer_telphone>\n";

        WebServiceUtils.getWebServiceInfo("UsedRecord", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        iShowUseWater.showFailMessage("连接超时");
                        break;
                    case "ERROR":
                        iShowUseWater.showFailMessage("数据异常");
                        break;
                    default:
                        JSONObject json = JSON.parseObject(result);
                        String RESCODE = json.get("RESCODE").toString();
                        if(RESCODE.equals("1")){
                            try{
                                List<UseWaterModel> models = new ArrayList<UseWaterModel>();
                                String RESPONSEXML = json.get("RESPONSEXML").toString();
                                JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                                for(Object listItem:josnArray){
                                    UseWaterModel model = new UseWaterModel();
                                    JSONObject dataItem = JSON.parseObject(listItem.toString());
                                    String comaddress = dataItem.get("comaddress").toString();
                                    String BuyTimes = dataItem.get("BuyTimes").toString();
                                    String UseMoney = dataItem.get("UseMoney").toString();
                                    String UsingTime = dataItem.get("UsingTime").toString();
                                    model.setComaddress(comaddress);
                                    model.setBuyTimes(BuyTimes);
                                    model.setUseMoney(UseMoney);
                                    model.setUsingTime(UsingTime);
                                    models.add(model);
                                }
                                iShowUseWater.showSuccessMessage(state,models);
                            }catch(Exception e){
                                iShowUseWater.showFailMessage("未查询到用水记录");
                                e.printStackTrace();
                            }
                        }else{
                            String RESMSG = json.get("RESMSG").toString();
                            iShowUseWater.showFailMessage(RESMSG);
                        }
                        break;
                }
                iShowUseWater.stopRefresh();
                return null;
            }
        });
    }
}
