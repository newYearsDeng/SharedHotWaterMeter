package com.northmeter.sharedhotwatermeter.northmeter.I;

/**
 * Created by dyd on 2017/8/24.
 */
public interface IOpenAndCloseValuePresenter {

    void DoRequestLoginData(int state,String CUSTOMER_TELPHONE,  String COMADDRESS);

    /**开阀流程，包括安全认证，开阀，水表开阀返回数据解析*/
    void DoSafetyCertificate(int state,String COMADDRESS);
    /**开阀*/
    void DoOpenValue(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                     String GroupNo, String WaterReturnData);
    /**开阀数据解析*/
    void DoOpenValueReturnData(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                     String GroupNo, String WaterReturnData);
    /**关阀*/
    void DoCloseValue(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                     String GroupNo, String WaterReturnData);
    /**关阀数据解析*/
    void DoCloseValueReturnData(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                               String GroupNo, String WaterReturnData);

    /**蓝牙异常应答*/
    void DoBlueErrorMessage(String message);
}
