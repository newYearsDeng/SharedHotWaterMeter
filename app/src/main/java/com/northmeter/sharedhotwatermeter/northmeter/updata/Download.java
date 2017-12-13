package com.northmeter.sharedhotwatermeter.northmeter.updata;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dyd on 2017/12/1.
 */
public class Download extends Thread{
    public ProgressDialog pd;
    public Handler handler;

    public Download(ProgressDialog pd,Handler handler){
        this.pd = pd;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        try {
            downloadFile("","",pd,handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File downloadFile(String path, String appName , ProgressDialog pd,Handler handler) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            String fileName = Environment.getExternalStorageDirectory()
                    + appName+".apk";
            File file = new File(fileName);

            // 目录不存在创建目录
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                handler.sendEmptyMessage(total);
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            throw new IOException("未发现SD卡");
        }
    }

    public static void startInstall(Context context, Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    public static void setProgress(Handler handler){
        for(int i=0;i<100;i++){
            try {
                sleep(500);
                handler.sendEmptyMessage(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
