package com.northmeter.sharedhotwatermeter.northmeter.entity;

/**
 * Created by dyd on 2017/10/10.
 * 用水记录model
 */
public class UseWaterModel {

    /**热水表编号*/
    public String comaddress;
    /**本次用水次数*/
    public String BuyTimes;
    /**本次用水金额*/
    public String UseMoney;
    /**用水时间*/
    public String UsingTime;

    public String getComaddress() {
        return comaddress;
    }

    public void setComaddress(String comaddress) {
        this.comaddress = comaddress;
    }

    public String getBuyTimes() {
        return BuyTimes;
    }

    public void setBuyTimes(String buyTimes) {
        BuyTimes = buyTimes;
    }

    public String getUseMoney() {
        return UseMoney;
    }

    public void setUseMoney(String useMoney) {
        UseMoney = useMoney;
    }

    public String getUsingTime() {
        return UsingTime;
    }

    public void setUsingTime(String usingTime) {
        UsingTime = usingTime;
    }

    public UseWaterModel(){};
}
