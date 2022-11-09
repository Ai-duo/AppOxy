package com.kd.appoxy.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.kd.appoxy.Oxy;
import com.kd.appoxy.WS;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xixun.joey.uart.BytesData;
import com.xixun.joey.uart.IUartListener;
import com.xixun.joey.uart.IUartService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ElementsService extends Service {
    public IUartService uart;
    StringBuffer builder = new StringBuffer();
    StringBuffer builder1 = new StringBuffer();
    boolean start = false;
    boolean dmgd = false, dmgd1 = false, start1 = false;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            uart = IUartService.Stub.asInterface(iBinder);
            Log.i("TAG_uart", "================ onServiceConnected ====================");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("TAG_uart", "================== onServiceDisconnected ====================");
            uart = null;
        }
    };
    public int lenth;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG_Service", "服务开启");
        getCardSystemUartAidl();
        startTimer();
       /*  new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     Thread.sleep(5000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 getElements("0204");
                 getWS("01030401F900E9EA70");
             }
         }).start();*/
    }

    String s2 = "";
    ArrayList<Byte> byteArrayList = new ArrayList<>();
    static int ii = 0;
    boolean PmFlag = false;
    String port = "/dev/ttysWK0";
    String port1 = "/dev/ttysWK2";
    public void getCardSystemUartAidl() {
        Intent intent = new Intent("xixun.intent.action.UART_SERVICE");
        intent.setPackage("com.xixun.joey.cardsystem");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i("TAG_uart", "正在获取uart======================");
                } while (null == uart);

                try {

                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i("TAG_uart", "========获取到串口数据===========");

                            for (byte a : data.getData()) {
                                String s1= Integer.toHexString(a & 0xFF);
                                if(s1.length()==1)s1="0"+s1;
                                Log.i("TAG_uart", "s1:" + s1);
                                if (s1  .equals(  "09")) {
                                    dmgd = true;
                                    start = true;
                                    builder.append(s1);
                                } else if (s1  .equals(  "03")) {
                                    builder.append(s1);
                                } else if (s1  .equals(  "14")) {

                                    builder.append(s1);
                                } else if (start) {
                                    builder.append(s1);
                                }
                                Log.i("TAG_uart", "builder:"+builder.toString());
                                if (builder.length() == 2) {
                                    if (!builder.toString().equals("09")) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        start = false;
                                    }
                                } else if (builder.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder.toString().equals("0903")) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        start = false;
                                    }
                                } else if (builder.length() == 6) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder.toString().equals("090314")) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        start = false;
                                    }
                                }
                                if (builder.length() ==50) {
                                    dmgd = false;
                                    start = false;
                                    Log.i("TAG_uart1234", builder.toString());
                                    getElements(builder.substring(6,10));
                                    builder.delete(0, builder.length());
                                    Log.i("TAG_uart1234", "END");
                                }

                            }

                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i("TAG_uart", "正在获取uart======================");
                } while (null == uart);

                try {

                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port1, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i("TAG_uart", "========获取到串口数据===========");

                            for (byte a : data.getData()) {
                                String s1= Integer.toHexString(a & 0xFF);
                                if(s1.length()==1)s1="0"+s1;
                                Log.i("TAG_uart", "s:" + s1);
                                if (s1 .equals( "01")) {
                                    dmgd1 = true;
                                    start1 = true;
                                    builder1.append(s1);
                                } else if (s1 .equals( "03")) {
                                    builder1.append(s1);
                                } else if (s1  .equals( "04")) {

                                    builder1.append(s1);
                                } else if (start1) {
                                    builder1.append(s1);
                                }
                                Log.i("TAG_uart", "builder1:"+builder1.toString());
                                if (builder1.length() == 2) {
                                    if (!builder1.toString().equals("01")) {
                                        builder1.delete(0, builder1.length());
                                        dmgd1 = false;
                                        start1= false;
                                    }
                                } else if (builder1.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder1.toString().equals("0103")) {
                                        builder1.delete(0, builder1.length());
                                        dmgd1 = false;
                                        start1 = false;
                                    }
                                } else if (builder1.length() == 6) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder1.toString().equals("010304")) {
                                        builder1.delete(0, builder1.length());
                                        dmgd1 = false;
                                        start1 = false;
                                    }
                                }
                                if (builder1.length() >=16) {
                                    dmgd1= false;
                                    start1 = false;
                                    Log.i("TAG_uart1234", builder1.toString());
                                    getWS(builder1.toString());
                                    builder1.delete(0, builder1.length());
                                    Log.i("TAG_uart1234", "END");
                                }

                            }

                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    ArrayList<Integer> index = new ArrayList<Integer>();

    Timer timer;

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
            Log.i("TAG_uart", "create timer ======================");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (uart != null) {
                            uart.write(port1, new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x02,(byte)0xc4, 0x0b});//, 0x5c, 0x72, 0x5c, 0x6e
                            Log.i("TAG_uart", "发送  WS ======================");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 60 * 1000);
        }
    }

    public void getElements(String info) {
        //builder.delete(0, builder.length());
        //开始接受PM2.5数据
        Log.i("TAG", "getElements:");
        if (TextUtils.isEmpty(info)) {
            return;
        }

       int oxy = Integer.valueOf(info,16)+500;
        EventBus.getDefault().post(new Oxy(oxy+"个/cm³"));
    }
    public void getWS(String info) {
        //builder.delete(0, builder.length());
        //开始接受PM2.5数据
        Log.i("TAG", "getElements:");
        if (TextUtils.isEmpty(info)) {
            return;
        }
        String wds = info.substring(6,10);
        String sds = info.substring(10,14);
        float wd = (float)Integer.valueOf(wds,16)/10;
        float sd = (float)Integer.valueOf(sds,16)/10;
        EventBus.getDefault().post(new WS(sd,wd));
    }
    public boolean isNum(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) str);
        boolean result = matcher.matches();
        return result;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    public String stringToGbk(String string) throws Exception {
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            byte high = Byte.parseByte(string.substring(i * 2, i * 2 + 1), 16);
            byte low = Byte.parseByte(string.substring(i * 2 + 1, i * 2 + 2), 16);
            bytes[i] = (byte) (high << 4 | low);
        }
        String result = new String(bytes, "gbk");
        return result;
    }

}