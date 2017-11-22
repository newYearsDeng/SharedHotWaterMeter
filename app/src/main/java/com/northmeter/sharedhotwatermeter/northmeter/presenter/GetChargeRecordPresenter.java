package com.northmeter.sharedhotwatermeter.northmeter.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.sharedhotwatermeter.northmeter.I.IGetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowGetChargeRecord;
import com.northmeter.sharedhotwatermeter.northmeter.entity.GetChargeRecordModel;
import com.northmeter.sharedhotwatermeter.northmeter.helper.WebServiceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dyd on 2017/8/25.
 */
public class GetChargeRecordPresenter implements IGetChargeRecord{
    public IShowGetChargeRecord iShowGetChargeRecord;

    public GetChargeRecordPresenter(IShowGetChargeRecord iShowGetChargeRecord){
        this.iShowGetChargeRecord = iShowGetChargeRecord;
    }
    @Override
    public void GetChargeRecord(final int state, String customer_telphone) {
        String para = "<customer_telphone>"+customer_telphone+"</customer_telphone>\n";

        WebServiceUtils.getWebServiceInfo("GetChargeRecord", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        iShowGetChargeRecord.showFailMessage("连接超时");
                        break;
                    case "ERROR":
                        iShowGetChargeRecord.showFailMessage("数据异常");
                        break;
                    default:
                        JSONObject json = JSON.parseObject(result);
                        String RESCODE = json.get("RESCODE").toString();
                        if(RESCODE.equals("1")){
                            try{
                                List<GetChargeRecordModel> models = new ArrayList<GetChargeRecordModel>();
                                String RESPONSEXML = json.get("RESPONSEXML").toString();
                                JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                                for(Object listItem:josnArray){
                                    GetChargeRecordModel model = new GetChargeRecordModel();
                                    JSONObject dataItem = JSON.parseObject(listItem.toString());
                                    String ChargeRecordName = dataItem.get("ChargeRecordName").toString();
                                    String ChargeWay = dataItem.get("ChargeWay").toString();
                                    String ChargeAmount = dataItem.get("ChargeAmount").toString();
                                    String ChargeTime = dataItem.get("ChargeTime").toString();
                                    model.setChargeRecordName(ChargeRecordName);
                                    model.setChargeWay(ChargeWay);
                                    model.setChargeAmount(ChargeAmount);
                                    model.setChargeTime(ChargeTime);
                                    models.add(model);
                                }
                                iShowGetChargeRecord.showSuccessMessage(state,models);
                            }catch(Exception e){
                                iShowGetChargeRecord.showFailMessage("未查询到充值记录");
                                e.printStackTrace();
                            }
                        }else{
                            String RESMSG = json.get("RESMSG").toString();
                            iShowGetChargeRecord.showFailMessage(RESMSG);
                        }
                        break;
                }
                iShowGetChargeRecord.stopRefresh();
                return null;
            }
        });
    }
}
