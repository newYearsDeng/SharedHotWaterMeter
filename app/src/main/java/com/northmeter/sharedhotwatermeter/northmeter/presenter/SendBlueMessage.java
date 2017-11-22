package com.northmeter.sharedhotwatermeter.northmeter.presenter;

import com.northmeter.sharedhotwatermeter.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.sharedhotwatermeter.bt_bluetooth.BluetoothChatService;
import com.northmeter.sharedhotwatermeter.northmeter.I.ISendBlueMessage;
import com.northmeter.sharedhotwatermeter.northmeter.I.IShowMainMessage;
import com.northmeter.sharedhotwatermeter.northmeter.helper.BlueTooth_UniqueInstance;
import com.northmeter.sharedhotwatermeter.northmeter.helper.Udp_Help;

/**
 * Created by dyd on 2017/8/22.
 * 通过蓝牙发送数据
 */
public class SendBlueMessage implements ISendBlueMessage{

    public IShowMainMessage iShowMainMessage;
    public SendBlueMessage(IShowMainMessage iShowMainMessage){
        this.iShowMainMessage = iShowMainMessage;
    }

//    /**bt版本蓝牙*/
//    private BluetoothChatService bluetoothChatService;
//    @Override
//    public void sendBlueMessage(String para) {
//        bluetoothChatService = BlueTooth_UniqueInstance.getInstance().getBluetoothChatService();
//        boolean flag = BlueTooth_UniqueInstance.getInstance().isBooleanConnected();
//
//        if(!flag){
//            iShowMainMessage.showBlueToastMessage("未连接水表");
//        }else{
//            final String input = para;
//            new Thread(){
//                public void run(){
//                    byte[] sendData = Udp_Help.strtoByteArray(input);
//                    bluetoothChatService.write(sendData);
//                }
//            }.start();
//            // iShowMainMessage.showBlueToastMessage("发送成功");
//        }
//
//
//    }

    /**低功耗蓝牙*/
    private BluetoothConnectionClient mConnectionClient;

    @Override
    public void sendBlueMessage(String para) {
        mConnectionClient = BlueTooth_UniqueInstance.getInstance().getConnectionClient();
        boolean flag = BlueTooth_UniqueInstance.getInstance().isBooleanConnected();
        if(mConnectionClient==null||flag == false){
           iShowMainMessage.showBlueToastMessage("蓝牙未连接");
        }else{
            final String input = para;
            new Thread(){
                public void run(){
                    if(input.length()>40){
                        for(int i=0;i<input.length()/40;i++){
                            String send_1 = input.substring(i*40,i*40+40);
                            mConnectionClient.write(4, Udp_Help.strtoByteArray(send_1));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        int state = input.length()/40;
                        String send_2 = input.substring(state*40,state*40+input.length()%40);
                        mConnectionClient.write(4, Udp_Help.strtoByteArray(send_2));
                    }else{
                        mConnectionClient.write(4, Udp_Help.strtoByteArray( input));
                    }
                }
            }.start();
           // iShowMainMessage.showBlueToastMessage("发送成功");
        }


    }
}
