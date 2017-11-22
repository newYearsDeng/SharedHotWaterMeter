package com.northmeter.sharedhotwatermeter.northmeter.activity;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import com.northmeter.sharedhotwatermeter.northmeter.BaseActivity.BaseActivity;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowMainMessage;
import com.northmeter.sharedhotwatermeter.northmeter.helper.BlueTooth_UniqueInstance;
import com.northmeter.sharedhotwatermeter.northmeter.helper.ProgressAlertDialog;
import com.northmeter.sharedhotwatermeter.northmeter.helper.Udp_Help;
import com.northmeter.sharedhotwatermeter.northmeter.helper.WebService;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.OpenAndCloseValuePresenter;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.RequestUserData;
import com.northmeter.sharedhotwatermeter.northmeter.presenter.SendBlueMessage;
import com.northmeter.sharedhotwatermeter.northmeter.wxpay.WXPayActivity;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.HashMap;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends BaseActivity implements
        View.OnClickListener ,BluetoothAdapter.LeScanCallback,IShowMainMessage{

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private static final int REQUEST_CAMERARESULT=201;
    private String TAG = getClass().getSimpleName();
    private BluetoothConnectionClient mConnectionClient;
    private BluetoothScanClient mScanClient;

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
    private long exitTime;
    private String receive_msg = "";
    private TextView text_blue_flag,text_money,text_username;
    private String tableNum,tableMac;//水表编号,mac
    private String telNum;//用户账户（手机号码）
    private String GroupNo;
    private String ZoneNumber="00000001";//区域号
    private int safetyFlag = 0;//识别是开阀还是关阀认证
    private OpenAndCloseValuePresenter openAndCloseValuePresenter;
    private SendBlueMessage sendBlueMessage;
    private DrawerLayout drawer;
    private SharedPreferences sp;
    private ImageView button_camear,button_open,button_close;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void inteView(Bundle savedInstanceState) {
        init_view();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init_view(){
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        telNum = sp.getString("TEL", null);
        tableNum = sp.getString(telNum+"MeterNum","000000000000");//上次连接的水表

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
        findViewById(R.id.imageview_message).setOnClickListener(this);//消息通知
        text_username = (TextView) findViewById(R.id.text_username);//用户名
        text_username.setText(telNum);

        mScanClient = BluetoothScanClient.getInstance(this, this);
        sendBlueMessage = new SendBlueMessage(this);
        openAndCloseValuePresenter = new OpenAndCloseValuePresenter(this);
        openAndCloseValuePresenter.DoRequestLoginData(0, telNum, "000000000000");//初始化账户信息

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出",Toast.LENGTH_SHORT).show();
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
                            System.out.println(tableMac+"tableNum::"+tableNum);

                            showDialog("正在开阀");
                            if(mConnectionClient!=null){
                                mConnectionClient.disconnect();//若已存在蓝牙连接，搜索前断开；
                            }
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
                Intent startintent = new Intent(MainActivity.this,UserCenter.class);
                startintent.putExtra("TEL",telNum);
                startActivity(startintent);
                break;
            case R.id.button_camear:
                safetyFlag = 0;
                if(!mScanClient.isBluetoothOpen()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED&&
                                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                            //method to get Images
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, CaptureActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                        }else{
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                Toast.makeText(MainActivity.this,"Your Permission is needed to get access the camera or location",Toast.LENGTH_LONG).show();
                            }
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERARESULT);
                        }
                    }else{
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, CaptureActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                    }
                }
                break;
            case R.id.button_charge_money:
                Intent intent0 = new Intent(MainActivity.this,WXPayActivity.class);
                intent0.putExtra("TEL",telNum);
                startActivity(intent0);
                break;
            case R.id.imageview_message://查询充值记录
                Intent intent1 = new Intent(MainActivity.this,GetChargeRecordActivity.class);
                intent1.putExtra("TEL",telNum);
                startActivity(intent1);
                //openAndCloseValuePresenter.DoRequestLoginData(0,telNum,"000000000000");
                break;
            case R.id.button_open:
//                mProgress.show("");
//                openAndCloseValuePresenter.DoSafetyCertificate(0,tableNum);
                break;
            case R.id.button_close:
//                mProgress.show("");
//                openAndCloseValuePresenter.DoSafetyCertificate(1,tableNum);
                if(!mScanClient.isBluetoothOpen()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }else{
                    if(!BlueTooth_UniqueInstance.getInstance().isBooleanConnected()){//蓝牙处于断开状态
                        showDialog("");
                        tableMac = sp.getString(telNum+"MeterMac",null);//上次连接的蓝牙地址
                        tableNum = sp.getString(telNum+"MeterNum","000000000000");//上次连接的水表
                        safetyFlag = 1;
                        mScanClient.startScan();
                    }else{
                        showDialog("");
                        sendBlueMessage.sendBlueMessage(closeBlueMessage());
                    }
                }
                break;
        }
    }


    private String closeBlueMessage(){
        String para_0 = "68"+Udp_Help.reverseRst(tableNum)+"6803183435443AAB896745";
        para_0 = para_0+Udp_Help.getpage_HexTo645(Udp_Help.reverseRst(ZoneNumber));
        para_0 = para_0+Udp_Help.getpage_HexTo645(Udp_Help.reverseRst("0"+telNum));
        para_0 = para_0+Udp_Help.getpage_HexTo645(Udp_Help.reverseRst(tableNum));
        String para = para_0+Udp_Help.get_sum(para_0)+"16";
        System.out.println("关阀指令："+para);
        return para;
    }

    //-*-----------------------------------------------------------
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED://连接成功
                    Toast.makeText(MainActivity.this, "连接成功！", Toast.LENGTH_LONG).show();
                    text_blue_flag.setText("连接成功");
                    BlueTooth_UniqueInstance.getInstance().setConnectionClient(mConnectionClient);
                    BlueTooth_UniqueInstance.getInstance().setBooleanConnected(true);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(telNum+"MeterMac",tableMac);
                    editor.putString(telNum+"MeterNum",tableNum);
                    editor.commit();

                    if(safetyFlag ==0){
                        openAndCloseValuePresenter.DoSafetyCertificate(0,tableNum);
                    }else{
                        sendBlueMessage.sendBlueMessage(closeBlueMessage());
                    }
                    break;
                case DISCONNECTED://断开连接
                    stopDialog();
                    Log.e(TAG,"连接断开...");
                    BlueTooth_UniqueInstance.getInstance().setBooleanConnected(false);
                    Toast.makeText(MainActivity.this, "连接断开！", Toast.LENGTH_LONG).show();
                    text_blue_flag.setText("连接断开");
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
                    mScanClient.stopScan();
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
                        Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (blueMsg.equals("fail")) {
                        Toast.makeText(MainActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        return;
                    }

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
            System.out.println("Uuid: " + service.getUuid());

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
            Log.e(TAG,receive_msg);
            int state = receive_msg.lastIndexOf("FEFEFEFE68");
            int state_lenth = state+36;//从FEFEFE68到标识码的长度
            String ditle = receive_msg.substring(receive_msg.length() - 2, receive_msg.length());//检查最后的字节是否为16

            if(state>=0 && receive_msg.length()>=state_lenth && ditle.equals("16")){
                String stateControl = receive_msg.substring(state+24,state+26);//控制字
                String length = receive_msg.substring(state+26,state+28);//长度
                String stateNum = receive_msg.substring(state+28,state+36);//标示符

                int len = Integer.valueOf(length,16);//字符串内长度字节
                if(receive_msg.substring(state+28,receive_msg.length()-4).length()/2 == len){
                    Log.e(TAG,receive_msg);
                    if(stateControl.equals("C3")||stateControl.equals("D1")||stateControl.equals("D4")){
                        switch(safetyFlag){
                            case 0:
                                Log.e(TAG,"开阀失败");
                                openAndCloseValuePresenter.DoBlueErrorMessage("开阀失败，请稍后重试");
                                break;
                            case 1:
                                Log.e(TAG,"关阀失败");
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
                    receive_msg = "";
                }


            }


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


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.e(TAG,"找到的蓝牙："+device.getAddress());
        if(device.getAddress().equals(tableMac)){
            BluetoothConnectionClient c = new BluetoothConnectionClient(
                    device, this, mGattCallback);
            Message msg = mHandler.obtainMessage(FOUND_DEVICE);
            msg.obj = c;
            mHandler.sendMessage(msg);
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
        Toast.makeText(this, R.string.permission_failed, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 10);
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
                    if(!mScanClient.isBluetoothOpen()){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }else{
                        mScanClient.startScan();
                        text_blue_flag.setText("连接中...");
                    }
                }else if(oper_UserStatus.equals("1")&&oper_MeterStatus.equals("1")||oper_MeterStatus.equals("0")){
                    String meterMac = sp.getString(telNum+"MeterMac",null);//上次连接的蓝牙地址
                    String meterNum = sp.getString(telNum+"MeterNum","000000000000");//上次连接的水表
                    //检查是否存在连接中断开后用户再次连接的情况 tableNum.equals(meterNum)&&tableMac.equals(meterMac)
                    if(true){
                        if(!mScanClient.isBluetoothOpen()){
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 1);
                        }else{
                            mScanClient.startScan();
                            text_blue_flag.setText("连接中...");
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this,
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
        Toast.makeText(MainActivity.this, "当前电价:"+PRICE+"元", Toast.LENGTH_SHORT).show();
        stopDialog();
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
        stopDialog();
        Toast.makeText(MainActivity.this, "本次使用金额："+oper_UseMoney+" 本次用水："+oper_UseWater, Toast.LENGTH_SHORT).show();

        mConnectionClient.disconnect();
        BlueTooth_UniqueInstance.getInstance().setBooleanConnected(false);
    }


    @Override
    public void showFailMessage(String data) {
        stopDialog();
        Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBlueToastMessage(String data) {
        stopDialog();
        Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBlueFailMessage(String data) {
        stopDialog();
        Looper.prepare();
        Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public void showToastMain(String message) {
        super.showToastMain(message);
        Looper.prepare();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
