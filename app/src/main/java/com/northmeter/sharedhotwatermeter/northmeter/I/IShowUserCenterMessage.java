package com.northmeter.sharedhotwatermeter.northmeter.I;

/**
 * Created by dyd on 2017/8/24.
 */
public interface IShowUserCenterMessage {
    public void showFailMessage(String message);
    public void showSuccessMessage(String oper_LeftMoney ,String OPER_Use,String OPER_UseMoney,String OPER_Times);
}
