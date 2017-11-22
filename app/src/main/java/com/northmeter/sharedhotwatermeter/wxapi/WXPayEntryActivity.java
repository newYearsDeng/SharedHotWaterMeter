package com.northmeter.sharedhotwatermeter.wxapi;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.northmeter.sharedhotwatermeter.R;
import com.northmeter.sharedhotwatermeter.northmeter.helper.Constants;
import com.northmeter.sharedhotwatermeter.northmeter.wxpay.MD5;
import com.northmeter.sharedhotwatermeter.northmeter.wxpay.Util;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.autolayout.AutoLayoutActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class WXPayEntryActivity extends AutoLayoutActivity implements IWXAPIEventHandler {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);

        api.handleIntent(getIntent(), this);
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}


	@Override
	public void onResp(BaseResp resp) {//resp.errCode 0:成功   -1:错误   -2:取消
//		String url = "https://api.mch.weixin.qq.com/pay/orderquery";
//		String entity = getProductArgs();
//		byte[] buf= Util.httpPost(url, entity);
//		String content = new String(buf);
//		System.out.println("订单 :   "+content);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			switch(resp.errCode){
			case 0:
				System.out.println("-------------"+resp.errCode+"  / "+resp.getType()+" / "+ ConstantsAPI.COMMAND_PAY_BY_WX);
				break;
			case -1:
				System.out.println("-------------"+resp.errCode+"  / "+resp.getType()+" / "+ConstantsAPI.COMMAND_PAY_BY_WX);
				break;
			case -2:
				System.out.println("-------------"+resp.errCode+"  / "+resp.getType()+" / "+ConstantsAPI.COMMAND_PAY_BY_WX);
				break;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("微信支付结果" +String.valueOf(resp.errCode));
			builder.show();
			finish();
		}
	}
	
	/**生成支付订单*/
	private String getProductArgs() {
		// TODO Auto-generated method stub
		StringBuffer xml=new StringBuffer();
		try {
			String nonceStr = getNonceStr();
			xml.append("<xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid",Constants.WX_APP_ID));
			packageParams.add(new BasicNameValuePair("mch_id", Constants.WX_MCH_ID));//商户号
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));//随机字符串，不长于32位。推荐随机数生成算法
			packageParams.add(new BasicNameValuePair("out_trade_no","2639c6bd2a42e714227b06646829d6ea"));//商户订单号
			
			String sign=getPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
			String xmlString=toXml(packageParams);
			return xmlString;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
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
			StringBuilder sb = new StringBuilder();
			sb.append("<xml>");
			for (int i = 0; i < params.size(); i++) {
				sb.append("<"+params.get(i).getName()+">");


				sb.append(params.get(i).getValue());
				sb.append("</"+params.get(i).getName()+">");
			}
			sb.append("</xml>");

			Log.e("Simon",">>>>"+sb.toString());
			return sb.toString();
		}
}