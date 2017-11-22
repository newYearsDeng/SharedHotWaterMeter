package com.northmeter.sharedhotwatermeter.northmeter.wxpay;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.BaseActivity.BaseActivity;
import com.northmeter.sharedhotwatermeter.northmeter.helper.Constants;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.autolayout.AutoLayoutActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 微信支付
 */
public class WXPayActivity extends BaseActivity implements OnClickListener{
	private Button submitButton;
	private Button confirmButton;
	private TextView textView;
	private EditText charge_number;//充值金额
	private StringBuffer sb;
	private Map<String,String> resultunifiedorder;
	private PayReq req;
	private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	private String telNum;
	private boolean charge_flag = false;//是否可进行充值
	private String chargeNum = null;
	private SharedPreferences sp1;
	private String userID = "01",userName = "admin";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wxpay;
    }

    @Override
    protected void inteView(Bundle savedInstanceState) {
        try{
            Intent intent = getIntent();
            telNum = intent.getStringExtra("TEL");

            sb = new StringBuffer();
            req = new PayReq();

            submitButton = (Button) findViewById(R.id.bt_submit_order);
            confirmButton = (Button) findViewById(R.id.bt_corfirm);
            textView = (TextView) findViewById(R.id.tv_prepay_id);
            charge_number = (EditText) findViewById(R.id.charge_number);
            charge_number.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            submitButton.setOnClickListener(this);
            confirmButton.setOnClickListener(this);
            findViewById(R.id.button_back).setOnClickListener(this);
            findViewById(R.id.but_charge_1).setOnClickListener(this);
            findViewById(R.id.but_charge_2).setOnClickListener(this);
            findViewById(R.id.but_charge_3).setOnClickListener(this);
            findViewById(R.id.but_charge_4).setOnClickListener(this);
            findViewById(R.id.but_charge_5).setOnClickListener(this);
            findViewById(R.id.but_charge_6).setOnClickListener(this);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_submit_order://提交订单
            if(TextUtils.isEmpty(charge_number.getText().toString())){
                Toast.makeText(this, "请输入充值金额", Toast.LENGTH_SHORT).show();
                return;
            }
            String urlString="https://api.mch.weixin.qq.com/pay/unifiedorder";
            PrePayIdAsyncTask prePayIdAsyncTask=new PrePayIdAsyncTask();
            prePayIdAsyncTask.execute(urlString);
            break;
		case R.id.bt_corfirm://确认支付
//			genPayReq();//生成签名参数
//			sendPayReq();
			break;
		case R.id.button_back:
			finish();
			break;
			
		case R.id.but_charge_1:
			charge_number.setText("10");
			break;
		case R.id.but_charge_2:
			charge_number.setText("20");
			break;
		case R.id.but_charge_3:
			charge_number.setText("50");
			break;
		case R.id.but_charge_4:
			charge_number.setText("100");
			break;
		case R.id.but_charge_5:
			charge_number.setText("200");
			break;
		case R.id.but_charge_6:
			charge_number.setText("500");
			break;
		default:
			break;
		}
	}
	/*
	 * 调起微信支付
	 */
	private void sendPayReq() {
		if (!msgApi.isWXAppInstalled()) {  
		    //提醒用户没有安装微信  
		    Toast.makeText(this, "请先安装微信", Toast.LENGTH_SHORT).show();  
		    return;  
		}
		if(charge_flag){
			msgApi.registerApp(Constants.WX_APP_ID);
			msgApi.sendReq(req);
			Log.i(">>>>>", req.partnerId);
		}
		
	}
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private void genPayReq() {

		req.appId = Constants.WX_APP_ID;
		req.partnerId = Constants.WX_MCH_ID;
		if (resultunifiedorder!=null) {
			req.prepayId = resultunifiedorder.get("prepay_id");
			req.packageValue = "prepay_id="+resultunifiedorder.get("prepay_id");//这里的package参数值必须是Sign=WXPay,否则IOS端调不起微信支付，
													//* (参数值是"prepay_id="+resultunifiedorder.get("prepay_id")的时候Android可以，IOS不可以)
			if(req.prepayId == null){
				charge_flag = false;
				Toast.makeText(WXPayActivity.this, "充值异常", Toast.LENGTH_SHORT).show();
				return;
			}
		}else{
			charge_flag = false;
			Toast.makeText(WXPayActivity.this, "充值异常", Toast.LENGTH_SHORT).show();
			return;
		}
		req.nonceStr = getNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());


		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));//微信开放平台审核通过的应用APPID
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));//随机字符串，不长于32位。推荐随机数生成算法
		signParams.add(new BasicNameValuePair("package", req.packageValue));//暂填写固定值Sign=WXPay
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));//微信支付分配的商户号
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));//微信返回的支付交易会话ID
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));//时间戳

		req.sign = genAppSign(signParams);

		sb.append("sign\n"+req.sign+"\n\n");

		textView.setText(sb.toString());

		Log.e("Simon", "----"+signParams.toString());

	}
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.WX_API_KEY);

		this.sb.append("sign str\n"+sb.toString()+"\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		Log.e("Simon","----"+appSign);
		return appSign;
	}
	
	/**把支付订单post到后台生成一个预支付订单，返回prepay_id（预支付回话标识）*/
	private class PrePayIdAsyncTask extends AsyncTask<String,Void, Map<String, String>>
	{	//第一个String代表输入到任务的参数类型，也即是doInBackground()的参数类型
		//第二个String代表处理过程中的参数类型，也就是doInBackground()执行过程中的产出参数类型，通过publishProgress()发消息
		//传递给onProgressUpdate()一般用来更新界面
		//第三个String代表任务结束的产出类型，也就是doInBackground()的返回值类型，和onPostExecute()的参数类型
		
		//private ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
            showDialog("");
		}
		@Override
		protected Map<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String,String> xml = null;
			String url = String.format(params[0]);
			String entity = getProductArgs();
			Log.e("Simon",">>>>"+entity);
			byte[] buf=Util.httpPost(url, entity);

			if(buf!=null){
				String content = new String(buf);
				Log.e("orion", "----"+content);
				xml = decodeXml(content);
			}

			return xml;
		}
		
		@Override
		protected void onPostExecute(Map<String, String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
            stopDialog();
		    /**
			 *  {sign=6E3E0D350BC38191186D887B66E2194C, return_code=SUCCESS, trade_type=APP, result_code=SUCCESS,
			 *  appid=wx14af47ab474a0991, mch_id=1415675302, nonce_str=OANSjSJ5aMl0GRd1, 
			 *  prepay_id=wx2017041111220719f38620b20773315727, return_msg=OK}
			 *  */
			//sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
			//textView.setText(sb.toString());
			if(result!=null){
				resultunifiedorder=result;
				genPayReq();//生成签名参数
				sendPayReq();//发起微信支付
			}else{
				Toast.makeText(WXPayActivity.this, "下单失败", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	public Map<String,String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if("xml".equals(nodeName)==false){
						//实例化student对象
						xml.put(nodeName,parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("Simon","----"+e.toString());
		}
		return null;

	}
	
	/**生成预支付订单*/
	private String getProductArgs() {
		// TODO Auto-generated method stub
		StringBuffer xml=new StringBuffer();
		try {
			charge_flag = true;
			float money = Float.parseFloat(charge_number.getText().toString());
			String charge = ""+Math.round(money*100);
			String nonceStr = getNonceStr();
			xml.append("<xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid",Constants.WX_APP_ID));
			packageParams.add(new BasicNameValuePair("attach", telNum+"/android"));//附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
			packageParams.add(new BasicNameValuePair("body", "设备充值"));//商品描述
			packageParams.add(new BasicNameValuePair("device_info", "WEB"));//终端设备号(门店号或收银设备ID)，默认请传"WEB"
			packageParams.add(new BasicNameValuePair("mch_id", Constants.WX_MCH_ID));//商户号
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));//随机字符串，不长于32位。推荐随机数生成算法													
			packageParams.add(new BasicNameValuePair("notify_url", "http://218.17.157.121:7055/Service1.asmx/SaveWXPayInfoByWM"));//的服务器回调地址
			packageParams.add(new BasicNameValuePair("out_trade_no",genOutTradNo()));//商户订单号
			packageParams.add(new BasicNameValuePair("total_fee", charge));//一分钱
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));//支付类型
			
			String sign=getPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
			String xmlString = toXml(packageParams);
			return xmlString;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	//生成订单号,测试用，在客户端生成
	private String genOutTradNo() {
		Random random = new Random();
		//return "dasgfsdg1234"; //订单号写死的话只能支付一次，第二次不能生成订单
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	//生成随机号，防重发
	private String getNonceStr() {
		// TODO Auto-generated method stub
		Random random=new Random();
		
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	/**
	 生成签名
	 */

	private String getPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.WX_API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("Simon",">>>>"+packageSign);
		return packageSign;
	}
	/*
	 * 转换成xml
	 */
	private String toXml(List<NameValuePair> params) {
		String xmlstr = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml>");
			for (int i = 0; i < params.size(); i++) {
				sb.append("<"+params.get(i).getName()+">");
				
				sb.append(params.get(i).getValue());
				
				sb.append("</"+params.get(i).getName()+">");
			}
			sb.append("</xml>");
	
			Log.e("Simon",">>>>"+sb.toString());
			
			//如果参数里面存在中文字符，需要进行重新编码
			xmlstr =  new String(sb.toString().getBytes(), "ISO8859-1");
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return xmlstr;  
	}




}
