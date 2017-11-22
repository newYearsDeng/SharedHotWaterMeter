package com.northmeter.sharedhotwatermeter.bluetooth.blueActivity;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.bluetooth.tools.BluetoothScanClient;
import com.northmeter.sharedhotwatermeter.bluetooth.view.DeviceListItemView;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class DeviceListActivity extends AutoLayoutActivity implements View.OnClickListener, BluetoothAdapter.LeScanCallback {
    public static final String DATA_DEVICE = "DEVICE";
    String TAG = getClass().getSimpleName();

    public static final int REQUEST_DEVICE = 0X01;
    private ListView lv;
    private TextView confirm, cancel;

    private DeviceAdapter adapter;
    ArrayList<BluetoothDevice> devices;
    BluetoothDevice checkedDevice;
    int prevCheckedPosition = -1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter.add((BluetoothDevice) msg.obj);
            adapter.notifyDataSetChanged();
        }
    };

    private BluetoothScanClient mScanClient;

    private static final int ENABLE_BT_REQUEST_ID = 1;
    public static int REQUEST_CODE_DEVICE_NAME = 11;
    public static String DEVICE_NAME = "DeviceListActivity.DEVICE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_device_list);


            mScanClient = BluetoothScanClient.getInstance(this, this);
            if(!mScanClient.isBluetoothOpen()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            }else{
                mScanClient.startScan();
            }
            initView();
            initData();
            initListener();
        }catch(Exception e){
            e.printStackTrace();;
        }
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user didn't want to turn on BT
    	System.out.println("requestCode="+requestCode+" RESULT_OK "+RESULT_OK);
        if (requestCode == ENABLE_BT_REQUEST_ID) {
        	if(resultCode == RESULT_OK) {
        		mScanClient.startScan();
		        return;
		    }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScanClient.destroy();
    }

    private void initData() {
//		ArrayList<DeviceInfo> dd = new ArrayList<DeviceInfo>();
//		for(int i=0;i<5;i++){
//			dd.add(new DeviceInfo("iFever"+i,"address"+i));
//		}
//		devices = dd;
        devices = new ArrayList<BluetoothDevice>();
        adapter = new DeviceAdapter(this, devices);
        lv.setAdapter(adapter);
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.device_list);
        confirm = (TextView) findViewById(R.id.confirm);
        cancel = (TextView) findViewById(R.id.cancel);
        lv.setEmptyView(findViewById(R.id.empty_view));
    }

    private void initListener() {
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(TAG, "on ItemClick : position =" + position + ", id=" + id);
                if (position == prevCheckedPosition) return;
                prevCheckedPosition = position;
                checkedDevice = adapter.getItem(position);

                adapter.notifyDataSetChanged();
            }
        });

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                if (checkedDevice == null) return;
                Intent i = new Intent();
                i.putExtra(DATA_DEVICE,checkedDevice);
                setResult(RESULT_OK,i);
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device == null || device.getName() == null) return;
        Log.w(TAG, "onLeScan: device == " + device.getAddress() + " == " + device.getName());
            /*for (BluetoothConnectionClient c : mClients) {
                if (c.getDevice().getAddress().equals(device.getAddress())) return;
            }*/
//        if (device.getName().startsWith("iUart")) {
            if (checkDeviceExist(device)) {
                Message msg = mHandler.obtainMessage(1);
                msg.obj = device;
                mHandler.sendMessage(msg);
//            }
        }
    }

    @SuppressLint("NewApi")
	@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BluetoothScanClient.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The requested permission is granted.
                    if (mScanClient != null) {
                        mScanClient.startScan();
                    }
                } else {
                    // The user disallowed the requested permission.
                    Toast.makeText(this, R.string.permission_failed, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        Context context;

        public DeviceAdapter(Context context, ArrayList<BluetoothDevice> objects) {
            super(context, 0, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BluetoothDevice device = getItem(position);
            DeviceListItemView v;
            if (convertView == null) {
                v = new DeviceListItemView(context, device);

            } else {
                v = (DeviceListItemView) convertView;
                v.setDevice(device);
                v.initData();
            }
            v.setCheckState(position == prevCheckedPosition);

            return v;
        }

    }

    private boolean checkDeviceExist(BluetoothDevice device) {
        if (devices == null)
            return false;

        for (BluetoothDevice d : devices) {
            if (d.getAddress().equals(device.getAddress()))
                return false;
        }
        return true;
    }

}
