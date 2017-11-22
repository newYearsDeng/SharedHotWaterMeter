package com.northmeter.sharedhotwatermeter.northmeter.I;

import com.northmeter.sharedhotwatermeter.northmeter.entity.GetChargeRecordModel;
import com.northmeter.sharedhotwatermeter.northmeter.entity.UseWaterModel;

import java.util.List;

/**
 * Created by dyd on 2017/8/25.
 */
public interface IShowUseWater {
    public void showSuccessMessage(int state, List<UseWaterModel> models);
    public void showFailMessage(String message);
    public void stopRefresh();
}
