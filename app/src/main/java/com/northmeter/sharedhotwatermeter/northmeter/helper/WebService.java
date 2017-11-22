package com.northmeter.sharedhotwatermeter.northmeter.helper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dyd on 2017/8/23.
 */
public class WebService {

    public static String getServiceData(String methodName,String reqData) throws Exception {
        HttpURLConnection con = null;
        String result = "";
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
                    result += inputLine;
                }
            }
            System.out.println(result);
        }finally {
            con.disconnect();
        }
        return result;
    }

}
