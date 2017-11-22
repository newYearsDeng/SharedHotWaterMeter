package com.northmeter.sharedhotwatermeter.northmeter.entity;

/**
 * Created by dyd on 2017/8/25.
 */
public class GetChargeRecordModel {
    /**交易类型*/
    public String ChargeRecordName;
    /**支付方式*/
    public String ChargeWay;
    /**交易金额*/
    public String ChargeAmount;
    /**交易时间*/
    public String ChargeTime;

    public String getChargeRecordName() {
        return ChargeRecordName;
    }

    public void setChargeRecordName(String chargeRecordName) {
        ChargeRecordName = chargeRecordName;
    }

    public String getChargeWay() {
        return ChargeWay;
    }

    public void setChargeWay(String chargeWay) {
        ChargeWay = chargeWay;
    }

    public String getChargeAmount() {
        return ChargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        ChargeAmount = chargeAmount;
    }

    public String getChargeTime() {
        return ChargeTime;
    }

    public void setChargeTime(String chargeTime) {
        ChargeTime = chargeTime;
    }

    public GetChargeRecordModel(){}
}
