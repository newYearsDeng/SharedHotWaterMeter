package com.northmeter.sharedhotwatermeter.northmeter.updata;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import java.io.File;

/**
 * 网络检查
 *
 */
public class NetworkUtil {
	/**
	 * 没有网络
	 */
	public static final int NONETWORK = 0;
	/**
	 * 当前是wifi连接
	 */
	public static final int WIFI = 1;
	/**
	 * 不是wifi连接
	 */
	public static final int NOWIFI = 2;
	
	
	/**
	 * 检测当前网络的类型 是否是wifi
	 * @param context
	 * @return
	 */
	public static int checkedNetWorkType(Context context){
		if(!checkedNetWork(context)){
			return NONETWORK;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting() ){
			return WIFI;
		}else{
			return NOWIFI;
		}
	}
	
	
	/**
	 * 检查是否连接网络
	 * @param context
	 * @return
	 */
	public static boolean  checkedNetWork(Context context){
		// 1.获得连接设备管理器
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm == null) return false;
		/**
		 * 获取网络连接对象
		 */
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		
		if(networkInfo == null || !networkInfo.isAvailable()){
			return false;
		}
		return true;
	}

	public static void downLoadApk(final Context mContext) {
		final ProgressDialog pd =  new ProgressDialog(mContext); // 进度条对话框

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int i = msg.what;
				pd.setProgress(i);
			}
		};

		pd.setCancelable(false);// 必须一直下载完，不可取消
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载安装包，请稍后");
		pd.setTitle("版本升级");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					//sleep(500);
					Download.sendPD(handler);
					// 结束掉进度条对话框
					pd.dismiss();
				} catch (Exception e) {
					pd.dismiss();

				}
			}
		}.start();
	}

}
