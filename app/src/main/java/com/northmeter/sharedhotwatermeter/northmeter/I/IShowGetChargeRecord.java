package com.northmeter.sharedhotwatermeter.northmeter.I;

import com.northmeter.sharedhotwatermeter.northmeter.entity.GetChargeRecordModel;

import java.util.List;

/**
 * Created by dyd on 2017/8/25.
 */
public interface IShowGetChargeRecord {
    public void showSuccessMessage(int state, List<GetChargeRecordModel> models);
    public void showFailMessage(String message);
    public void stopRefresh();
}
