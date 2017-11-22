package com.northmeter.sharedhotwatermeter.northmeter.helper;


import com.northmeter.sharedhotwatermeter.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.sharedhotwatermeter.bt_bluetooth.BluetoothChatService;

/**全局变量*/
public class BlueTooth_UniqueInstance {

	private static BlueTooth_UniqueInstance uniqueInstance=null;

	public static BlueTooth_UniqueInstance getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new BlueTooth_UniqueInstance();
		}
		return uniqueInstance;
	}

	/**蓝牙对象*/
	private BluetoothConnectionClient connectionClient;

	/**bt蓝牙对象*/
	private static BluetoothChatService bluetoothChatService = null;

	/**蓝牙是否连接成功*/
	private static boolean booleanConnected = false;


	public static BlueTooth_UniqueInstance getUniqueInstance() {
		return uniqueInstance;
	}

	public static void setUniqueInstance(BlueTooth_UniqueInstance uniqueInstance) {
		BlueTooth_UniqueInstance.uniqueInstance = uniqueInstance;
	}

	public BluetoothConnectionClient getConnectionClient() {

		return connectionClient;
	}
	public void setConnectionClient(BluetoothConnectionClient connectionClient) {
		this.connectionClient = connectionClient;
	}
	private BlueTooth_UniqueInstance(){};


	public static boolean isBooleanConnected() {
		return booleanConnected;
	}

	public static void setBooleanConnected(boolean booleanConnected) {
		BlueTooth_UniqueInstance.booleanConnected = booleanConnected;
	}

	public static BluetoothChatService getBluetoothChatService() {
		return bluetoothChatService;
	}

	public static void setBluetoothChatService(BluetoothChatService bluetoothChatService) {
		BlueTooth_UniqueInstance.bluetoothChatService = bluetoothChatService;
	}
}
