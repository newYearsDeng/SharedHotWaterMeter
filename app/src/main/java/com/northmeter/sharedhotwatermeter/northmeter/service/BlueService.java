package com.northmeter.sharedhotwatermeter.northmeter.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.northmeter.sharedhotwatermeter.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.sharedhotwatermeter.bluetooth.tools.GattCode;
import com.northmeter.sharedhotwatermeter.northmeter.helper.Udp_Help;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dyd on 2017/8/22.
 */
public class BlueService extends Service{
    private String TAG = "northmeter.service.BlueService";
    private BluetoothConnectionClient mConnectionClient;
    private final int FOUND_DEVICE = 0X01;
    private final int DISCONNECTED = 0X02;
    private final int FOUND_SERVICE = 0X03;
    private final int WRITE_SUCCESS = 0X04;
    private final int WRITE_FAILED = 0X05;
    private final int RECONNECT = 0x06;
    private final int RECEIVE = 0x07;
    private final int SEND = 0x08;
    private final int CONNECTED = 0X09;
    private final int BLUEM_ESSAGE = 0x0a;
    private String receive_msg = "";
    private static long state_time = 0;
    private Map map_count = new HashMap();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED://连接成功
                    //Toast.makeText(CameraMeterMainAty.this, "连接成功！", Toast.LENGTH_LONG).show();
                    break;
                case DISCONNECTED://断开连接
                    //Toast.makeText(CameraMeterMainAty.this, "连接断开！", Toast.LENGTH_LONG).show();
                    break;
                case RECONNECT:
                    if (mConnectionClient != null) {
                        mConnectionClient.connect();
                    }/*else{
                        if(mScanClient!=null){
                            mScanClient.startScan();
                        }
                    }*/
                    break;
                case RECEIVE:
                    String data = (String) msg.obj;
                    break;
                case SEND:

                    break;
                case FOUND_DEVICE:
//                    mScanClient.stopScan();
//                    mConnectionClient = (BluetoothConnectionClient) msg.obj;
                    if (mConnectionClient != null) {
                        mConnectionClient.disconnect();
                    }
                    System.out.println("找到蓝牙并进行连接 ");
                    mConnectionClient = (BluetoothConnectionClient) msg.obj;
                    mConnectionClient.connect();

                    break;
                case BLUEM_ESSAGE:
                    String blueMsg = (String) msg.obj;
                    if (blueMsg.equals("success")) {
                        //Toast.makeText(CameraMeterMainAty.this, "设置成功", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (blueMsg.equals("fail")) {
                       // Toast.makeText(CameraMeterMainAty.this, "操作失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String control = blueMsg.substring(22, 24).toUpperCase();

                    break;

            }
        }
    };

    @SuppressLint("NewApi")
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    gatt.discoverServices();
                }
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED || status == 133) {
                Log.w(TAG, "onConnectionStateChange: disconnected");
                mHandler.sendEmptyMessage(DISCONNECTED);
                mHandler.sendEmptyMessage(RECONNECT);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            BluetoothGattService service = gatt.getService(GattCode.FFF_SERVICE);
            System.out.println("-------===" + service.getUuid());

            mConnectionClient.addCharacteristic(3, service.getCharacteristic(GattCode.FFF_3));
            mConnectionClient.addCharacteristic(4, service.getCharacteristic(GattCode.FFF_4));

            Log.w(TAG, "onServicesDiscovered: ");
            mConnectionClient.setCharacteristicNotification(GattCode.DESCRIPTOR,
                    3, true);

            mHandler.sendEmptyMessage(FOUND_SERVICE);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.w(TAG, "onCharacteristicWrite: status=" + status);
            if (status == 0) {
                Message msg = mHandler.obtainMessage(SEND);
                msg.obj = characteristic.getValue();
                mHandler.sendMessage(msg);
            } else {
                mHandler.sendEmptyMessage(WRITE_FAILED);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            receive_msg = receive_msg + Udp_Help.bytesToHexString(value);


        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite: status=" + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                mHandler.sendEmptyMessage(CONNECTED);
            }
        }
    };
}
