package com.northmeter.sharedhotwatermeter.bluetooth.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.bluetooth.view.DialogConfirm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 16/4/21.
 */
@SuppressLint("NewApi")
public class BluetoothScanClient {

    private static BluetoothScanClient INSTANCE;

    public static BluetoothScanClient getInstance(Activity activity
            , BluetoothAdapter.LeScanCallback leScanCallback) {
        if (INSTANCE==null){
            INSTANCE = new BluetoothScanClient(activity,leScanCallback);
        }else{
            INSTANCE.mActivity = activity;
            INSTANCE.setLeScanCallback(leScanCallback);
        }
        return INSTANCE;
    }

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0x667;
    private boolean scanning = false;
    private Activity mActivity;
    private ScanCallback mScanCallback;
    BluetoothLeScanner mScanner;
    ScanSettings mScanSettings;
    List<ScanFilter> mFilterList;
    public String TAG = getClass().getSimpleName();

    private BluetoothAdapter mAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    @SuppressLint("NewApi")
    private BluetoothScanClient(Activity activity, BluetoothAdapter.LeScanCallback leScanCallback) {
        mActivity = activity;
        mLeScanCallback = leScanCallback;
        if (checkSDK()) {
            mFilterList = new ArrayList<>();
            mFilterList.add(new ScanFilter.Builder()
                    .setDeviceName("iUart")
                    .build());

            mScanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    mLeScanCallback.onLeScan(result.getDevice(), result.getRssi(),
                            result.getScanRecord().getBytes());
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    Log.w(TAG, "onBatchScanResults: ");
                    for (ScanResult re : results) {
                        mLeScanCallback.onLeScan(re.getDevice(), re.getRssi(),
                                re.getScanRecord().getBytes());
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.w(TAG, "onScanFailed: "+errorCode);
                }
            };
        }
        initBluetooth();
    }

    public BluetoothDevice getDevice(String address) {
        if (mAdapter != null && mAdapter.isEnabled() && address != null) {
            return mAdapter.getRemoteDevice(address);
        } else {
            Log.e(TAG, "getDevice: null or disabled adapter");
            return null;
        }
    }

    public boolean isBluetoothOpen() {
        if (mAdapter == null) return false;
        return mAdapter.isEnabled();
    }

    private void initBluetooth() {
        mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) return;
        mAdapter = mBluetoothManager.getAdapter();
    }

    public void enableAdapter() {
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                mActivity.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        }
    }

    public void startScan() {
        if (mAdapter == null || mLeScanCallback == null) return;
        if (check()) {
            if (checkSDK()) {
                Log.w(TAG, "startScan: 开始扫描");
                if (mScanner == null) {
                    mScanner = mAdapter.getBluetoothLeScanner();
                }
                mScanner.startScan(mFilterList, mScanSettings, mScanCallback);
            } else {
                Log.w(TAG, "startScan:开始扫描");
                mAdapter.startLeScan(mLeScanCallback);
            }
            scanning = true;
        }
    }

    private boolean checkSDK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return false;
        } else {
            return false;
        }
    }

    public void stopScan() {
        if (mAdapter == null || mLeScanCallback == null) return;
        if (checkSDK()) {
            if (mScanner != null) {
                mScanner.stopScan(mScanCallback);
            }
        } else {
            mAdapter.stopLeScan(mLeScanCallback);
        }
        scanning = false;
    }

    public void destroy(){
        stopScan();
        mLeScanCallback = null;
        mActivity = null;
    }

    public boolean isScanning() {
        return scanning;
    }

    public boolean check() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) return true;
        //閸掋倖鏌囬弰顖氭儊閼奉亝鏁幐浣芥憫閻楁瑥鑻熼崥鎴犳暏閹寸柉袙闁插绱濇稉杞扮矆娑斿牐顩﹂悽瀹狀嚞鐠囥儲娼堥梽锟�
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            DialogConfirm mDialog = new DialogConfirm();
            mDialog.setTitle(R.string.permission_rationale_location);
            mDialog.setOnClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                }
            });
        } else {
            //鐠囬攱鐪伴弶鍐
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
        return false;
    }

    public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback) {
        mLeScanCallback = leScanCallback;
    }
}
