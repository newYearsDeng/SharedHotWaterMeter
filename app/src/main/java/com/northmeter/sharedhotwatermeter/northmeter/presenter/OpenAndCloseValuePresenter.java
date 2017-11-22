package com.northmeter.sharedhotwatermeter.northmeter.presenter;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.sharedhotwatermeter.northmeter.I.IOpenAndCloseValuePresenter;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowMainMessage;

/**
 * Created by dyd on 2017/8/24.
 */
public class OpenAndCloseValuePresenter implements IOpenAndCloseValuePresenter {
    public IShowMainMessage iShowMainMessage;
    public RequestUserData requestUserData;

    public OpenAndCloseValuePresenter(IShowMainMessage iShowMainMessage){
        this.iShowMainMessage = iShowMainMessage;
        requestUserData = new RequestUserData();
    }

    /**登录和扫描时查询用户账户状态*/
    @Override
    public void DoRequestLoginData(final int state, String CUSTOMER_TELPHONE, String COMADDRESS) {
        requestUserData.LoginCheck(state,CUSTOMER_TELPHONE,COMADDRESS,new RequestUserData.RequestUsersCallback() {
            @Override
            public void successData(String data) {
                JSONObject json = JSON.parseObject(data);
                String RESCODE = json.get("RESCODE").toString();
                if(RESCODE.equals("1")){
                    String RESPONSEXML = json.get("RESPONSEXML").toString();
                    JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                    JSONObject jsondata = JSON.parseObject(josnArray.get(0).toString()) ;
                    String oper_LeftMoney = jsondata.get("oper_LeftMoney").toString();
                    String oper_Use = jsondata.get("oper_Use").toString();
                    String oper_UseMoney = jsondata.get("oper_UseMoney").toString();
                    String oper_Times = jsondata.get("oper_Times").toString();
                    String oper_UserStatus = jsondata.get("oper_UserStatus").toString();
                    String oper_MeterStatus = jsondata.get("oper_MeterStatus").toString();
                    iShowMainMessage.showLoginMessage(state ,oper_LeftMoney, oper_Use, oper_UseMoney,
                             oper_Times, oper_UserStatus, oper_MeterStatus);
                }else{
                    String RESMSG = json.get("RESMSG").toString();
                    iShowMainMessage.showFailMessage(RESMSG);
                }
            }

            @Override
            public void failData(String data) {
                iShowMainMessage.showFailMessage(data);
            }
        });
    }


    /***/
    @Override
    public void DoSafetyCertificate(final int state, String COMADDRESS) {
        requestUserData.SafetyCertificate(state, COMADDRESS,new RequestUserData.RequestUsersCallback() {
            @Override
            public void successData(String data) {
                JSONObject json = JSON.parseObject(data);
                String RESCODE = json.get("RESCODE").toString();
                if(RESCODE.equals("1")){
                    String RESPONSEXML = json.get("RESPONSEXML").toString();
                    JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                    JSONObject jsondata = JSON.parseObject(josnArray.get(0).toString()) ;
                    String Status = jsondata.get("Status").toString();
                    String GroupNo = jsondata.get("GroupNo").toString();
                    String ReturnData = jsondata.get("ReturnData").toString();
                    iShowMainMessage.showSafetyCertificateMessage(state ,Status,GroupNo,ReturnData);
                }else{
                    String RESMSG = json.get("RESMSG").toString();
                    iShowMainMessage.showFailMessage(RESMSG);
                }
            }

            @Override
            public void failData(String data) {
                iShowMainMessage.showFailMessage(data);
            }
        });
    }

    @Override
    public void DoOpenValue(final int state, String COMADDRESS, String CUSTOMER_TELPHONE, String GroupNo, String WaterReturnData) {

        requestUserData.OpenValue(state, COMADDRESS,CUSTOMER_TELPHONE,GroupNo,WaterReturnData,
                new RequestUserData.RequestUsersCallback() {
            @Override
            public void successData(String data) {
                JSONObject json = JSON.parseObject(data);
                String RESCODE = json.get("RESCODE").toString();
                if(RESCODE.equals("1")){
                    String RESPONSEXML = json.get("RESPONSEXML").toString();
                    JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                    JSONObject jsondata = JSON.parseObject(josnArray.get(0).toString()) ;
                    String Status = jsondata.get("Status").toString();
                    String ReturnData = jsondata.get("ReturnData").toString();
                    iShowMainMessage.showOpenValueMessage(state ,Status,ReturnData);
                }else{
                    String RESMSG = json.get("RESMSG").toString();
                    iShowMainMessage.showFailMessage(RESMSG);
                }
            }

            @Override
            public void failData(String data) {
                iShowMainMessage.showFailMessage(data);
            }
        });
    }

    @Override
    public void DoOpenValueReturnData(final int state, String COMADDRESS, String CUSTOMER_TELPHONE, String GroupNo, String WaterReturnData) {
        requestUserData.OpenValueReturnData(state, COMADDRESS,CUSTOMER_TELPHONE,GroupNo,WaterReturnData,
                new RequestUserData.RequestUsersCallback() {
                    @Override
                    public void successData(String data) {
                        JSONObject json = JSON.parseObject(data);
                        String RESCODE = json.get("RESCODE").toString();
                        if(RESCODE.equals("1")){
                            String RESPONSEXML = json.get("RESPONSEXML").toString();
                            JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                            JSONObject jsondata = JSON.parseObject(josnArray.get(0).toString()) ;
                            String Status = jsondata.get("Status").toString();
                            String PRICE = jsondata.get("Price").toString();
                            String SendData = jsondata.get("SendData").toString();
                            iShowMainMessage.showOpenValueReturnData(state ,Status,PRICE,SendData);
                        }else{
                            String RESMSG = json.get("RESMSG").toString();
                            iShowMainMessage.showFailMessage(RESMSG);
                        }
                    }

                    @Override
                    public void failData(String data) {
                        iShowMainMessage.showFailMessage(data);
                    }
                });
    }

    @Override
    public void DoCloseValue(final int state, String COMADDRESS, String CUSTOMER_TELPHONE, String GroupNo, String WaterReturnData) {
        requestUserData.CloseValue(state, COMADDRESS,CUSTOMER_TELPHONE,GroupNo,WaterReturnData,
                            new RequestUserData.RequestUsersCallback() {
                        @Override
                        public void successData(String data) {
                            JSONObject json = JSON.parseObject(data);
                            String RESCODE = json.get("RESCODE").toString();
                            if(RESCODE.equals("1")){
                                String RESPONSEXML = json.get("RESPONSEXML").toString();
                                JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                                JSONObject jsondata = JSON.parseObject(josnArray.get(0).toString()) ;
                                String Status = jsondata.get("Status").toString();
                                String ReturnData = jsondata.get("ReturnData").toString();
                                iShowMainMessage.showCloseValueMessage(state ,Status,ReturnData);
                            }else{
                                String RESMSG = json.get("RESMSG").toString();
                                iShowMainMessage.showFailMessage(RESMSG);
                            }
                        }

                        @Override
                        public void failData(String data) {
                            iShowMainMessage.showFailMessage(data);
                        }
                });
    }

    @Override
    public void DoCloseValueReturnData(final int state, String COMADDRESS, String CUSTOMER_TELPHONE, String GroupNo, String WaterReturnData) {
        requestUserData.CloseValueReturnData(state, COMADDRESS,CUSTOMER_TELPHONE,GroupNo,WaterReturnData,
                new RequestUserData.RequestUsersCallback() {
                    @Override
                    public void successData(String data) {
                        JSONObject json = JSON.parseObject(data);
                        String RESCODE = json.get("RESCODE").toString();
                        if(RESCODE.equals("1")){
                            String RESPONSEXML = json.get("RESPONSEXML").toString();
                            JSONArray josnArray = JSON.parseArray(RESPONSEXML);
                            JSONObject jsondata = JSON.parseObject(josnArray.get(0).toString()) ;
                            String Status = jsondata.get("Status").toString();
                            String oper_UseWater = jsondata.get("oper_UseWater").toString();
                            String oper_UseMoney = jsondata.get("oper_UseMoney").toString();
                            String oper_Times = jsondata.get("oper_Times").toString();
                            String SendData = jsondata.get("SendData").toString();
                            iShowMainMessage.showCloseValueReturnData(state ,Status,oper_UseWater,oper_UseMoney,oper_Times,SendData);
                        }else{
                            String RESMSG = json.get("RESMSG").toString();
                            iShowMainMessage.showFailMessage(RESMSG);
                        }
                    }

                    @Override
                    public void failData(String data) {
                        iShowMainMessage.showFailMessage(data);
                    }
                });
    }

    @Override
    public void DoBlueErrorMessage(String message) {
        iShowMainMessage.showBlueFailMessage(message);
    }

}
