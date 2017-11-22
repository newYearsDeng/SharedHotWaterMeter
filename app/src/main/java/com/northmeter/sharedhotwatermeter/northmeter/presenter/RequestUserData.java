package com.northmeter.sharedhotwatermeter.northmeter.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.sharedhotwatermeter.northmeter.I.IRequestUserData;
import com.northmeter.sharedhotwatermeter.northmeter.helper.WebServiceUtils;

import java.util.List;

/**
 * Created by dyd on 2017/8/24.
 */
public class RequestUserData implements IRequestUserData{

    /**用户登录*/
    @Override
    public void LoginCheck(int state, String CUSTOMER_TELPHONE, String COMADDRESS , final RequestUsersCallback requestUsersCallback) {

        String para = "<customer_telphone>"+CUSTOMER_TELPHONE+"</customer_telphone>\n" +
                "<comAddress>"+COMADDRESS+"</comAddress>\n ";
        WebServiceUtils.getWebServiceInfo("LoginCheck", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        requestUsersCallback.failData("连接超时");
                        break;
                    case "ERROR":
                        requestUsersCallback.failData("数据异常");
                        break;
                    default:
                        requestUsersCallback.successData(result);
                        break;
                }
                return null;
            }
        });

    }

    /**开阀关阀安全认证*/
    @Override
    public void SafetyCertificate(int state, String COMADDRESS, final RequestUsersCallback requestUsersCallback) {
        String para = "<comAddress>"+COMADDRESS+"</comAddress>\n ";
        WebServiceUtils.getWebServiceInfo("SafetyCertificate", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        requestUsersCallback.failData("连接超时");
                        break;
                    case "ERROR":
                        requestUsersCallback.failData("数据异常");
                        break;
                    default:
                        requestUsersCallback.successData(result);
                        break;
                }
                return null;
            }
        });

    }

    /**扫码开阀*/
    @Override
    public void OpenValue(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                          String GroupNo, String WaterReturnData, final RequestUsersCallback requestUsersCallback) {

        String para = "<comAddress>"+COMADDRESS+"</comAddress>\n" +
                "<telphone>"+CUSTOMER_TELPHONE+"</telphone>\n "+
                "<groupid>"+GroupNo+"</groupid>\n "+
                "<returnData>"+WaterReturnData+"</returnData>\n";
        WebServiceUtils.getWebServiceInfo("OpenValue", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                System.out.println("***********"+result);
                switch(result){
                    case "TIME_OUT":
                        requestUsersCallback.failData("连接超时");
                        break;
                    case "ERROR":
                        requestUsersCallback.failData("数据异常");
                        break;
                    default:
                        requestUsersCallback.successData(result);
                        break;
                }
                return null;
            }
        });

    }

    /**扫码开阀热水表返回内容解析*/
    @Override
    public void OpenValueReturnData(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                                    String GroupNo, String WaterReturnData, final RequestUsersCallback requestUsersCallback) {

        String para = "<comAddress>"+COMADDRESS+"</comAddress>\n" +
                "<telphone>"+CUSTOMER_TELPHONE+"</telphone>\n "+
                "<groupid>"+GroupNo+"</groupid>\n "+
                "<returnData>"+WaterReturnData+"</returnData>\n";
        WebServiceUtils.getWebServiceInfo("OpenValueReturnData", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        requestUsersCallback.failData("连接超时");
                        break;
                    case "ERROR":
                        requestUsersCallback.failData("数据异常");
                        break;
                    default:
                        requestUsersCallback.successData(result);
                        break;
                }
                return null;
            }
        });

    }

    /**关阀*/
    @Override
    public void CloseValue(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                           String GroupNo, String WaterReturnData, final RequestUsersCallback requestUsersCallback) {

        String para = "<comAddress>"+COMADDRESS+"</comAddress>\n" +
                "<telphone>"+CUSTOMER_TELPHONE+"</telphone>\n "+
                "<groupid>"+GroupNo+"</groupid>\n "+
                "<returnData>"+WaterReturnData+"</returnData>\n";
        WebServiceUtils.getWebServiceInfo("CloseValue", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        requestUsersCallback.failData("连接超时");
                        break;
                    case "ERROR":
                        requestUsersCallback.failData("数据异常");
                        break;
                    default:
                        requestUsersCallback.successData(result);
                        break;
                }
                return null;
            }
        });

    }

    /**关阀热水表返回内容解析*/
    @Override
    public void CloseValueReturnData(int state, String COMADDRESS, String CUSTOMER_TELPHONE,
                                     String GroupNo, String WaterReturnData, final RequestUsersCallback requestUsersCallback) {

        String para = "<comAddress>"+COMADDRESS+"</comAddress>\n" +
                "<telphone>"+CUSTOMER_TELPHONE+"</telphone>\n "+
                "<groupid>"+GroupNo+"</groupid>\n "+
                "<returnData>"+WaterReturnData+"</returnData>\n";
        WebServiceUtils.getWebServiceInfo("CloseValueReturnData", para, new WebServiceUtils.CallBack() {
            @Override
            public String result(String result) {
                switch(result){
                    case "TIME_OUT":
                        requestUsersCallback.failData("连接超时");
                        break;
                    case "ERROR":
                        requestUsersCallback.failData("数据异常");
                        break;
                    default:
                        requestUsersCallback.successData(result);
                        break;
                }
                return null;
            }
        });

    }
    public interface RequestUsersCallback {

        void successData(String data);

        void failData(String data);
    }
}
