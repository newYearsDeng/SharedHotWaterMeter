package com.northmeter.sharedhotwatermeter.northmeter.helper;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dyd on 2017/8/24.
 */
public class WebServiceUtils {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    public static final int TIME_OUT = -1;
    public static final int ERROR = 0;
    public static final int SUCCESS = 1;

    public interface CallBack {
        String result(String result);
    }

    public static void getWebServiceInfo(final String methodName, final String reqData, final CallBack callBack) {
        // 用于子线程与主线程通信的Handler
        final Handler mHandler = new Handler(Looper.getMainLooper()) {//TODO
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case TIME_OUT:
                        //连接超时
                        callBack.result("TIME_OUT");
                        break;
                    case ERROR:
                        //数据异常
                        callBack.result("ERROR");
                        break;
                    case SUCCESS:
                        callBack.result((String) msg.obj);
                        break;
                }
            }
        };
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb_result = new StringBuilder();
                HttpURLConnection con = null;
                try {
                    String httpstr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                            "<soap:Body>\n" +
                            "<" + methodName + " xmlns=\"http://WaterMeterPort.org/\">\n" +
                            reqData +
                            "</" + methodName + ">\n" +
                            "</soap:Body>\n" +
                            "</soap:Envelope>";

                    System.out.println(httpstr);

                    String soapAction = Constants.SERVICE_NAMESPACE + methodName;
                    URL url = new URL(Constants.SERVICE_URL);

                    System.out.println("url" + url);
                    con = (HttpURLConnection) url.openConnection();
                    byte[] bytes = httpstr.getBytes("UTF-8");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setUseCaches(false);
                    con.setConnectTimeout(10 * 1000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
                    con.setRequestProperty("SOAPAction", soapAction);
                    con.setRequestProperty("Content-Length", "" + bytes.length);
                    OutputStream outStream = con.getOutputStream();
                    outStream.write(bytes);
                    outStream.flush();
                    outStream.close();

                    String inputLine;
                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        while ((inputLine = reader.readLine()) != null) {
                            sb_result.append(inputLine) ;
                        }
                    }else{
                        mHandler.sendMessage(mHandler.obtainMessage(ERROR, con.getResponseCode()));
                    }
                    System.out.println(sb_result.toString());
                    mHandler.sendMessage(mHandler.obtainMessage(SUCCESS, sb_result.toString()));
                }catch(Exception e){
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(TIME_OUT, e.getMessage()));
                } finally {
                    con.disconnect();
                }
            }
        });
    }


}
