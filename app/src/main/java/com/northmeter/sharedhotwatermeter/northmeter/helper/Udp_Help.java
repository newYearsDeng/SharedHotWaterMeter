package com.northmeter.sharedhotwatermeter.northmeter.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Udp_Help {

	/**16进制字符串转换成byte数组进行广播*/
	public static byte[] strtoByteArray(String hexString) {
		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}

	/**byte数组转换为字符串*/
	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString().toUpperCase();
	}

	/**bytes转换为16进制字符串 *******/
	public static String bytesToHex(byte[] buffer, int length) {
		String ret = "";
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(buffer[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}

		return ret;

	}

	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	public char[] stringToChar(String str) {
		char[] sendStr;
		String[] itemStr = str.split(" ");
		sendStr = new char[itemStr.length];
		for (int i = 0; i < itemStr.length; i++) {
			char ch = (char) Integer.parseInt(itemStr[i], 16);
			sendStr[i] = ch;
		}
		return sendStr;

	}

	/**ASCII转换为字符串*/
	public static String Ascii_To_String(String ascii){//ASCII转换为字符串
		StringBuffer s=new StringBuffer();
		String[] chars = new String[ascii.length()/2];
		for(int j=0;j<ascii.length()/2;j++){
			chars[j]=ascii.substring(j*2,j*2+2);
		}
		for(int i=0;i<chars.length;i++){

			//System.out.println(chars[i]+" "+(char)Integer.parseInt(Integer.valueOf(chars[i],16).toString()));
			s=s.append(String.valueOf((char)Integer.parseInt(Integer.valueOf(chars[i],16).toString())));

		}
		return s.toString();
	}

	/**16进制转10进制*/
	public static String get_inter_add(String add){//16进制转10进制,接收到的报文显示是处理
		StringBuffer sb=new StringBuffer();
		String[] chars=new String[add.length()/2];
		for(int i=0;i<add.length()/2;i++){
			chars[i]=add.substring(i*2,i*2+2);
			sb.append(Integer.valueOf(chars[i],16)+".");
		}
		return sb.toString().substring(0,sb.toString().length()-1);
	}



	/**表号反向*/
	public static String reverseRst(String rst){
		//String newRst=rst.substring(2, rst.length()-2);
		String lastrst = "";
		for(int i=rst.length()/2;i>0;i--){
			lastrst=lastrst+rst.substring(i*2-2, i*2);

		}
		return lastrst;
	}

	/**获取校验码。总加和*/
	public static String get_sum(String num){
		int sum=0;
		for(int i=0;i<num.length()/2;i++){
			sum=sum+Integer.valueOf(num.substring(i*2,i*2+2),16);
		}
		String check_str=Integer.toHexString(sum);
		if(check_str.length()<2){
			check_str="0"+check_str;
		}else{
			System.out.println("总加和："+check_str);
			check_str=check_str.substring(check_str.length()-2,check_str.length());
		}
		return check_str;
	}

	/**数据 +33*/

	public static String get_Stting_HexTo645(String para){
		StringBuffer stringBuffer = new StringBuffer();
		String result = Integer.toHexString(Integer.valueOf(para,16)+51);
		if(result.length()<2){
			result = "0"+result;
		}
		stringBuffer.append(result);
		return stringBuffer.toString();
	}


	/**参数默认为16进制 +33*/
	public static String getpage_HexTo645(String para){
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(para.substring(i*2,i*2+2),16)+51);
			stringBuffer.append(result0);
		}
		return stringBuffer.toString();

	}


	/**拍照窗口数据+33，再转换为16进制*/
	public static String get_came_hexTo645(String para){
		StringBuffer sb = new StringBuffer();
		String flag = "0000";
		String result = Integer.toHexString(Integer.parseInt(para));
		String hex = flag.substring(result.length(),4)+result;
		for(int i = 0;i<hex.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(hex.substring(i*2,i*2+2),16)+51);
			if(result0.length()>2){
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			sb.append(result0);
		}

		return sb.toString();
	}

	/**数据 -33*/
	public static String get_645ToHex(String para){
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(para.substring(i*2,i*2+2),16)-51);
			if(result0.length()<2){
				result0 = "0"+result0;
			}else if(result0.length()==8){//ffffffxx
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			stringBuffer.append(result0);
		}
		return stringBuffer.toString();

	}


	/**
	 * @param 将字节数组转换为ImageView可调用的Bitmap对象
	 * @param bytes
	 * @param opts
	 * @return Bitmap
	 */
	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}
	/**
	 * @param 图片缩放
	 * @param bitmap 对象
	 * @param w 要缩放的宽度
	 * @param h 要缩放的高度
	 * @return newBmp 新 Bitmap对象
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}

	/**
	 * 将时间转换为时间戳
	 */
	public static String dateToStamp(String s) throws ParseException{
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = simpleDateFormat.parse(s);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}

	/**
	 * 将时间戳转换为时间
	 */
	public static String stampToDate(String s){
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lt = new Long(s);
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}

}