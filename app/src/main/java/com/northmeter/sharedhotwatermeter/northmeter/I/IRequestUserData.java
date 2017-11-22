package com.northmeter.sharedhotwatermeter.northmeter.I;

import com.northmeter.sharedhotwatermeter.northmeter.presenter.RequestUserData;

/**
 * Created by dyd on 2017/8/24.
 */
public interface IRequestUserData {
    /**用户登录*/
    public void LoginCheck(int state, String CUSTOMER_TELPHONE, String COMADDRESS, RequestUserData.RequestUsersCallback requestUsersCallback);

    /**开阀关阀安全认证*/
    public void SafetyCertificate(int state,String COMADDRESS, RequestUserData.RequestUsersCallback requestUsersCallback);

    /**扫码开阀*/
    public void OpenValue(int state,String COMADDRESS,String CUSTOMER_TELPHONE,String GroupNo,String WaterReturnData, RequestUserData.RequestUsersCallback requestUsersCallback);
    /**扫码开阀热水表返回内容解析*/
    public void OpenValueReturnData(int state,String COMADDRESS,String CUSTOMER_TELPHONE,String GroupNo,String WaterReturnData, RequestUserData.RequestUsersCallback requestUsersCallback);

    /**关阀*/
    public void CloseValue(int state,String COMADDRESS,String CUSTOMER_TELPHONE,String GroupNo,String WaterReturnData, RequestUserData.RequestUsersCallback requestUsersCallback);
    /**关阀热水表返回内容解析*/
    public void CloseValueReturnData(int state,String COMADDRESS,String CUSTOMER_TELPHONE,String GroupNo,String WaterReturnData, RequestUserData.RequestUsersCallback requestUsersCallback);
}
