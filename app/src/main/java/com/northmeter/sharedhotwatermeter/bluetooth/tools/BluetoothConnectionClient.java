package com.northmeter.sharedhotwatermeter.bluetooth.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by benjamin on 16/4/21.
 */
public class BluetoothConnectionClient {
    private String TAG = getClass().getSimpleName();

    private int clientId = 0;
    private BluetoothDevice mDevice;
    private BluetoothGatt mGatt;
    private Context mContext;
    private HashMap<Object, BluetoothGattCharacteristic> mCharacteristics;

    private BluetoothGattCallback mGattCallback;

    public BluetoothConnectionClient(BluetoothDevice device, Context context,
                                     BluetoothGattCallback gattCallback) {
        mDevice = device;
        mContext = context;
        mGattCallback = gattCallback;
        mCharacteristics = new HashMap<>();
    }

    public BluetoothConnectionClient(int clientId,
                                     BluetoothDevice device, Context context,
                                     BluetoothGattCallback gattCallback) {
        this.clientId = clientId;
        mDevice = device;
        mContext = context;
        mGattCallback = gattCallback;
        mCharacteristics = new HashMap<>();
    }

    @SuppressLint("NewApi")
	public void connect() {
        if(mGatt!=null){
            disconnect();
        }
        mGatt = mDevice.connectGatt(mContext, false, mGattCallback);
    }

    @SuppressLint("NewApi")
	public void write(BluetoothGattCharacteristic characteristic, byte[] value) {
        if (mGatt == null || value == null||characteristic==null){
            Log.e(TAG, "write: null error");
            return;
        }
        //Log.d(TAG, "write: value="+printBytes(value));
        characteristic.setValue(value);
        mGatt.writeCharacteristic(characteristic);
    }

    public void write(Object characteristicName, byte[] value) {
        write(mCharacteristics.get(characteristicName), value);
    }


    public void addCharacteristic(Object name, BluetoothGattCharacteristic characteristic) {
        mCharacteristics.put(name, characteristic);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void disconnect() {
        if (mGatt == null) return;
        mGatt.close();
        mGatt = null;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public BluetoothGatt getGatt() {
        return mGatt;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	public void setCharacteristicNotification(UUID descriptorUuid,
                                              Object characteristicName, boolean enabled) {

        BluetoothGattCharacteristic characteristic = mCharacteristics.get(characteristicName);

        mGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(descriptorUuid);
        if (enabled) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        // 通知服务端，descriptor改变，设置通知完成
        mGatt.writeDescriptor(descriptor);
    }

    public int getClientId() {
        return clientId;
    }


    public String printBytes(byte[] bytes){
        String s = "";
        for(byte b: bytes){
            s += Integer.toHexString(b & 0x00ff)+", ";
        }
        return s;
    }
}
