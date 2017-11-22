package com.northmeter.sharedhotwatermeter.bt_bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.bluetooth.blueActivity.DeviceListActivity;
import com.northmeter.sharedhotwatermeter.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.sharedhotwatermeter.bluetooth.tools.BluetoothScanClient;
import com.northmeter.sharedhotwatermeter.bluetooth.tools.GattCode;
import com.northmeter.sharedhotwatermeter.camera.activity.CaptureActivity;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowMainMessage;
import com.northmeter.sharedhotwatermeter.northmeter.activity.GetChargeRecordActivity;
import com.northmeter.sharedhotwatermeter.northmeter.activity.NavigationItemListener;
import com.northmeter.sharedhotwatermeter.northmeter.activity.UserCenter;
import com.northmeter.sharedhotwatermeter.northmeter.helper.BlueTooth_UniqueInstance;
import com.northmeter.sharedhotwatermeter.northmeter.helper.ProgressAlertDialog;
import com.northmeter.sharedhotwatermeter.northmeter.helper.Udp_Help;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.OpenAndCloseValuePresenter;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.SendBlueMessage;
import com.northmeter.sharedhotwatermeter.northmeter.wxpay.WXPayActivity;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.Set;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BTMainActivity extends AutoLayoutActivity implements
        View.OnClickListener ,IShowMainMessage{

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private static final int REQUEST_CAMERARESULT=201;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "BTMainActivity";
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    public static final String TOAST = "toast";
    private String mConnectedDeviceName = null;
    public static final String DEVICE_NAME = "device_name";

    private long exitTime;
    private TextView text_blue_flag,text_money,text_username;
    private String tableNum,tableMac;//水表编号,mac
    private String telNum;//用户账户（手机号码）
    private String ZoneNumber="00000001";//区域号
    private String GroupNo;
    private int safetyFlag = 0;//识别是开阀还是关阀认证
    private ProgressAlertDialog mProgress;
    private OpenAndCloseValuePresenter openAndCloseValuePresenter;
    private SendBlueMessage sendBlueMessage;
    private DrawerLayout drawer;
    private SharedPreferences sp;
    private ImageView button_camear,button_open,button_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        init_view();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, R.string.permission_failed, Toast.LENGTH_SHORT).show();
    }

    private void init_view(){
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        telNum = sp.getString("TEL", null);
        tableNum = sp.getString("MeterNum","000000000000");//上次连接的水表

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationItemListener(this,drawer));

        button_camear = (ImageView) findViewById(R.id.button_camear);
        button_camear.setOnClickListener(this);//扫码二维码
        button_open = (ImageView) findViewById(R.id.button_open);
        button_open.setOnClickListener(this);//开阀放水
        button_close = (ImageView) findViewById(R.id.button_close);
        button_close.setOnClickListener(this);//关阀停水

        findViewById(R.id.button_charge_money).setOnClickListener(this);//充值
        findViewById(R.id.imageview_usercenter).setOnClickListener(this);//用户中心
        findViewById(R.id.imageview_message).setOnClickListener(this);//消息通知
        text_money = (TextView) findViewById(R.id.text_money);//余额显示
        text_blue_flag = (TextView) findViewById(R.id.text_blue_flag);//水表连接提示
        text_username = (TextView) findViewById(R.id.text_username);//用户名
        text_username.setText(telNum);

        sendBlueMessage = new SendBlueMessage(this);
        openAndCloseValuePresenter = new OpenAndCloseValuePresenter(this);
        openAndCloseValuePresenter.DoRequestLoginData(0, telNum, "000000000000");//初始化账户信息
        mProgress = new ProgressAlertDialog(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(BTMainActivity.this, "再按一次退出",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
                    if (data != null) {
                        if (data.hasExtra("result")) {//扫描到水表编号返回数据
                            String result = data.getStringExtra("result").toString();
                            tableNum = result.split("#")[1];
                            tableMac = result.split("#")[2];
                            System.out.println(tableNum+"tableNum::"+tableMac);

                            mProgress.show("开阀中");
                            openAndCloseValuePresenter.DoRequestLoginData(1, telNum, tableNum);

                            return;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imageview_usercenter:
                Intent startintent = new Intent(BTMainActivity.this,UserCenter.class);
                startintent.putExtra("TEL",telNum);
                startActivity(startintent);
                break;
            case R.id.button_camear:
                if(!mBluetoothAdapter.isEnabled()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED&&
                                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                            //method to get Images
                            Intent intent = new Intent();
                            intent.setClass(BTMainActivity.this, CaptureActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                        }else{
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                Toast.makeText(BTMainActivity.this,"Your Permission is needed to get access the camera or location",Toast.LENGTH_LONG).show();
                            }
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERARESULT);
                        }
                    }else{
                        Intent intent = new Intent();
                        intent.setClass(BTMainActivity.this, CaptureActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                    }
                }
                break;
            case R.id.button_charge_money:
                Intent intent0 = new Intent(BTMainActivity.this,WXPayActivity.class);
                intent0.putExtra("TEL",telNum);
                startActivity(intent0);
                break;
            case R.id.imageview_message:
                Intent intent1 = new Intent(BTMainActivity.this,GetChargeRecordActivity.class);
                intent1.putExtra("TEL",telNum);
                startActivity(intent1);
                //openAndCloseValuePresenter.DoRequestLoginData(0,telNum,"000000000000");
                break;
            case R.id.button_open:
                mProgress.show("");
                openAndCloseValuePresenter.DoSafetyCertificate(0,tableNum);
                //sendBlueMessage.sendBlueMessage("680100000000006803183533333A3333333382908A9992A19AAA5F0C8FC8C1359562E816");

                break;
            case R.id.button_close:
                //680100000000006803183435443AAB89674534333333BAB647A9AB34343333333333F216
                mProgress.show("");
                String para_0 = "68"+Udp_Help.reverseRst(tableNum)+"6803183435443AAB896745";
                para_0 = para_0+Udp_Help.getpage_HexTo645(Udp_Help.reverseRst(ZoneNumber));
                para_0 = para_0+Udp_Help.getpage_HexTo645(Udp_Help.reverseRst("0"+telNum));
                para_0 = para_0+Udp_Help.getpage_HexTo645(Udp_Help.reverseRst(tableNum));
                String para = para_0+Udp_Help.get_sum(para_0)+"16";
                System.out.println("关阀指令："+para);
                sendBlueMessage.sendBlueMessage(para);
                //openAndCloseValuePresenter.DoSafetyCertificate(1,tableNum);
                break;
            //    68 01 00 00 00 00 00 68
            //    03
            //    18
            //    34 35 44 3A
            //    AB 89 67 45
            //    34 33 33 33区域号
            //    34 33 33 33 33 33 用户号
            //    34 33 33 33 33 33 表号
            //    E6 16
        }
    }

    /**登录信息返回*/
    @Override
    public void showLoginMessage(int state ,String oper_LeftMoney,String oper_Use,String oper_UseMoney,
                                 String oper_Times,String oper_UserStatus,String oper_MeterStatus) {
        switch(state){
            case 0:
                text_money.setText(oper_LeftMoney);
                break;
            case 1:
                text_money.setText(oper_LeftMoney);
                if(oper_UserStatus.equals("0")&&oper_MeterStatus.equals("0")){
                    if(!mBluetoothAdapter.isEnabled()){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }else{
                        foundBTDevice(tableMac);
                        text_blue_flag.setText("连接中...");
                    }
                }else if(oper_UserStatus.equals("1")&&oper_MeterStatus.equals("1")||oper_MeterStatus.equals("0")){
                    String meterMac = sp.getString("MeterMac",null);//上次连接的蓝牙地址
                    String meterNum = sp.getString("MeterNum","000000000000");//上次连接的水表
                    //检查是否存在连接中断开后用户再次连接的情况
                    if(tableNum.equals(meterNum)&&tableMac.equals(meterMac)){
                        if(!mBluetoothAdapter.isEnabled()){
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 1);
                        }else{
                            foundBTDevice(tableMac);
                            text_blue_flag.setText("连接中...");
                        }
                    }else{
                        Toast.makeText(BTMainActivity.this,
                                "账号或水表已被使用，请稍后再试",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(BTMainActivity.this,
                            "账号或水表已被使用，请稍后再试",Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    /**安全认证信息返回*/
    @Override
    public void showSafetyCertificateMessage(int state, String Status, String GroupNo, String ReturnData) {
        this.GroupNo = GroupNo;
        this.safetyFlag = state;
        switch(state){
            case 0://开阀认证
                sendBlueMessage.sendBlueMessage(ReturnData);
                Log.e(TAG,"开阀认证GroupNo "+GroupNo);
                break;
            case 1://关阀认证
                sendBlueMessage.sendBlueMessage(ReturnData);
                Log.e(TAG,"关阀认证GroupNo "+GroupNo);
                break;
        }
    }

    /**开阀信息返回*/
    @Override
    public void showOpenValueMessage(int state, String Status, String ReturnData) {
        Log.e(TAG,"开阀信息返回 下发到水表 "+ReturnData);
        sendBlueMessage.sendBlueMessage(ReturnData);//下发到水表
    }

    /**开阀返回解析数据*/
    @Override
    public void showOpenValueReturnData(int state, String Status, String PRICE , String SendData) {
        Log.e(TAG,"开阀返回解析数据 "+PRICE+" / "+SendData);
        button_camear.setImageResource(R.drawable.icon_open_water_p);
        button_close.setImageResource(R.drawable.icon_close_water_p);

        sendBlueMessage.sendBlueMessage(SendData);//下发到水表
        Toast.makeText(BTMainActivity.this, "电价:"+PRICE+"元", Toast.LENGTH_SHORT).show();
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
    }

    /**关阀信息返回*/
    @Override
    public void showCloseValueMessage(int state, String Status, String ReturnData) {
        Log.e(TAG,"关阀信息返回 下发到水表 "+ReturnData);
        sendBlueMessage.sendBlueMessage(ReturnData);//下发到水表
    }

    /**本地关阀返回解析数据*/
    @Override
    public void showCloseValueReturnData(int state, String Status, String oper_UseWater, String oper_UseMoney, String oper_Times , String SendData) {
        Log.e(TAG,"关阀返回解析数据 "+oper_UseWater+"/"+oper_UseMoney+"/"+oper_Times);
        sendBlueMessage.sendBlueMessage(SendData);//下发到水表
        button_camear.setImageResource(R.drawable.icon_open_water);
        button_close.setImageResource(R.drawable.icon_close_water);

        //text_money.setText(oper_UseWater);
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
        Toast.makeText(BTMainActivity.this, "本次使用金额："+oper_UseMoney+" 本次用水："+oper_UseWater, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void showFailMessage(String data) {
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
        Toast.makeText(BTMainActivity.this,data,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBlueToastMessage(String data) {
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
        Toast.makeText(BTMainActivity.this, data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBlueFailMessage(String data) {
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
        Toast.makeText(BTMainActivity.this,data,Toast.LENGTH_SHORT).show();
    }




    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (true)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                            if(mProgress.isShowing()){
//                                mProgress.dismiss();
//                            }
                            Log.e(TAG,"已连接："+mConnectedDeviceName);
                            Toast.makeText(BTMainActivity.this, "成功连接:"+mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                            text_blue_flag.setText("已连接"+mConnectedDeviceName);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("MeterMac",tableMac);
                            editor.putString("MeterNum",tableNum);
                            editor.commit();
                            BlueTooth_UniqueInstance.getInstance().setBooleanConnected(true);

                            openAndCloseValuePresenter.DoSafetyCertificate(0,tableNum);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //textviewTitle.setText(R.string.title_connecting);
                            Log.e(TAG,"连接中");
                            text_blue_flag.setText("连接中");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            break;
                        case BluetoothChatService.STATE_NONE:
                            // textviewTitle.setText(R.string.title_not_connected);
                            BlueTooth_UniqueInstance.getInstance().setBooleanConnected(false);
                            text_blue_flag.setText("连接失败");
                            Log.e(TAG,"连接失败");
                            if(mProgress.isShowing()){
                                mProgress.dismiss();
                            }
                            break;
                        case BluetoothChatService.STATE_STOP:
                            BlueTooth_UniqueInstance.getInstance().setBooleanConnected(false);
                            text_blue_flag.setText("关闭连接");
                            Log.e(TAG,"关闭连接");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_DEVICE_NAME:
                    Log.e(TAG,"Connected to " + mConnectedDeviceName);
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    break;
                case MESSAGE_TOAST://连接丢失
                    BlueTooth_UniqueInstance.getInstance().setBooleanConnected(false);
                    if(mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                    text_blue_flag.setText("连接失败");
                    break;
                case MESSAGE_READ:
                    String receive_msg = (String) msg.obj;
                    int state = receive_msg.lastIndexOf("FEFEFEFE68");
                    String stateControl = receive_msg.substring(state+24,state+26);//控制字
                    String length = receive_msg.substring(state+26,state+28);//长度
                    String stateNum = receive_msg.substring(state+28,state+36);//标示符

                    if(stateControl.equals("C3")||stateControl.equals("D1")||stateControl.equals("D4")){
                        switch(safetyFlag){
                            case 0:
                                openAndCloseValuePresenter.DoBlueErrorMessage("开阀失败，请稍后重试");
                                break;
                            case 1:
                                openAndCloseValuePresenter.DoBlueErrorMessage("关阀失败，请稍后重试");
                                break;
                        }

                    }else{
                        switch(stateNum){
                            case "3533333A"://认证 fefefefe6801000000000068 83 0c 3533333a 445566778899aabb3116
                                switch(safetyFlag){
                                    case 0://开阀认证
                                        Log.e(TAG,"开阀认证 "+receive_msg);
                                        openAndCloseValuePresenter.DoOpenValue(0,tableNum,telNum,GroupNo,receive_msg);
                                        break;
                                    case 1://关阀认证
                                        Log.e(TAG,"关阀认证"+receive_msg);
                                        openAndCloseValuePresenter.DoCloseValue(0,tableNum,telNum,GroupNo,receive_msg);
                                        break;
                                }
                                break;
                            case "3435343A"://开阀数据解析 fefefefe6801000000000068 83 1e 3435343A 3433333334333333333379BD0A49C1718DD5515F75DAE664E323B516
                                Log.e(TAG,"开阀数据解析"+receive_msg);
                                openAndCloseValuePresenter.DoOpenValueReturnData(0,tableNum,telNum,GroupNo,receive_msg);
                                break;
//                            case "3435443A"://关阀数据解析 fefefefe6801000000000068 83 2e 3435443a 34333333343333333333a16b131d23430d2979bd0a49c1718dd543ca9a0ce08dd527ab73d9698c31e2e96216
//                                Log.e(TAG,"关阀数据解析"+receive_msg);
//                                openAndCloseValuePresenter.DoCloseValueReturnData(0,tableNum,telNum,GroupNo,receive_msg);
//                                break;
                            case "3435443A"://本地关阀返回
                                //FEFEFEFE6801000000000068834E3435443A34333333BAB647A9AB3489F1652351A6A040E5AFDFA8764A70F50BDD51B888BE7FEC6B80B1B492ADCFCD3E73CC494DFD047A893703713F1A4E00FA89DD45CEDE4629CFFADA29CCA6988E0816
                                Log.e(TAG,"本地关阀返回"+receive_msg);
                                openAndCloseValuePresenter.DoCloseValueReturnData(0,tableNum,telNum,GroupNo,receive_msg);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        if (true)
            Log.e(TAG, "++ ON START ++");
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        } else {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (true)
            Log.e(TAG, "+ ON RESUME +");

        if (mChatService != null) {

            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {

                mChatService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatService.stop();

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);
        BlueTooth_UniqueInstance.getInstance().setBluetoothChatService(mChatService);
    }

    private void foundBTDevice(String mac){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Log.e(TAG,mac+" 蓝牙："+device.getName()+"/"+device.getAddress());
                //mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                if(device.getAddress().equals(mac)){
                    mChatService.connect(device);
                    return;
                }
            }
            mBluetoothAdapter.startDiscovery();
        } else {
            String noDevices = "没有蓝牙设备";
            mBluetoothAdapter.startDiscovery();
        }

    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG,"蓝牙搜索名字："+device.getName());
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if(device.getAddress().equals(tableMac)){
                        mChatService.connect(device);
                    }

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                if (mNewDevicesArrayAdapter.getCount() == 0) {
//                    String noDevices = getResources().getText(R.string.none_found).toString();
//                }
            }
        }
    };


}
