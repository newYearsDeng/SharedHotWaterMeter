package com.northmeter.sharedhotwatermeter.northmeter.I;

/**
 * Created by dyd on 2017/8/24.
 */
public interface IShowMainMessage {
    /**登录信息返回*/
    public void showLoginMessage(int state ,String oper_LeftMoney,String oper_Use,String oper_UseMoney,
                                 String oper_Times,String oper_UserStatus,String oper_MeterStatus);
    /**安全认证信息返回*/
    public void showSafetyCertificateMessage(int state ,String Status,String GroupNo,String ReturnData);
    /**开阀信息返回*/
    public void showOpenValueMessage(int state,String Status,String ReturnData);
    /**开阀返回解析数据*/
    public void showOpenValueReturnData(int state,String Status,String PRICE ,String SendData);

    /**关阀信息返回*/
    public void showCloseValueMessage(int state,String Status,String ReturnData);
    /**开阀返回解析数据*/
    public void showCloseValueReturnData(int state,String Status,String oper_UseWater,String oper_UseMoney,String oper_Times,String SendData);

    public void showFailMessage(String data);

    /**发送蓝牙数据时回调*/
    public void showBlueToastMessage(String data);

    public void showBlueFailMessage(String data);
}
