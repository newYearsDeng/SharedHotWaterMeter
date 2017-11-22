package com.northmeter.sharedhotwatermeter.northmeter.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.bluetooth.blueActivity.DeviceListActivity;

/**
 * Created by dyd on 2017/8/29.
 */
public class NavigationItemListener implements NavigationView.OnNavigationItemSelectedListener{
    private final Activity mActivity;
    private final DrawerLayout drawerlayout;

    public NavigationItemListener(Activity activity, DrawerLayout drawerlayout) {
        mActivity = activity;
        this.drawerlayout = drawerlayout;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.nav_camera:
                mActivity.startActivityForResult(new Intent(mActivity, DeviceListActivity.class)
                        , DeviceListActivity.REQUEST_DEVICE);
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;

        }
        drawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }



}
