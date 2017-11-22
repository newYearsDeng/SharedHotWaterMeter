package com.northmeter.sharedhotwatermeter.northmeter.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowUserCenterMessage;
import com.northmeter.sharedhotwatermeter.northmeter.I.IUserCenterPresenter;
import com.northmeter.sharedhotwatermeter.northmeter.helper.WebServiceUtils;

/**
 * Created by dyd on 2017/8/24.
 */
public class UserCenterPresenter implements IUserCenterPresenter{

    IShowUserCenterMessage showUserCenterMessage;
    public UserCenterPresenter(IShowUserCenterMessage showUserCenterMessage){
        this.showUserCenterMessage = showUserCenterMessage;
    }

    @Override
    public void getUserCenterData(String customer_telphone,String comAddress) {
        String para = "<customer_telphone>"+customer_telphone+"</customer_telphone>\n" +
                "<comAddress>"+comAddress+"</comAddress>\n ";

        WebServiceUtils.getWebServiceInfo("LoginCheck", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        showUserCenterMessage.showFailMessage("连接超时");
                        break;
                    case "ERROR":
                        showUserCenterMessage.showFailMessage("数据异常");
                        break;
                    default:
                        JSONObject json = JSON.parseObject(result);
                        String RESCODE = json.get("RESCODE").toString();
                        if(RESCODE.equals("1")){
                            String RESPONSEXML = json.get("RESPONSEXML").toString();
                            JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                            JSONObject jsondata = JSON.parseObject(josnArray.get(0).toString()) ;
                            String oper_LeftMoney = jsondata.get("oper_LeftMoney").toString();
                            String OPER_Use = jsondata.get("oper_Use").toString();
                            String OPER_UseMoney = jsondata.get("oper_UseMoney").toString();
                            String OPER_Times = jsondata.get("oper_Times").toString();
                            showUserCenterMessage.showSuccessMessage(oper_LeftMoney,OPER_Use, OPER_UseMoney, OPER_Times);
                        }else{
                            String RESMSG = json.get("RESMSG").toString();
                            showUserCenterMessage.showFailMessage(RESMSG);
                        }


                        break;
                }
                return null;
            }
        });

    }
}
