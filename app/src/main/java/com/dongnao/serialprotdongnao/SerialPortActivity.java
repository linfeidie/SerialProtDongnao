package com.dongnao.serialprotdongnao;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/8/27
 * <p>
 * 版本号：SerialProtDongnao
 */
public class SerialPortActivity extends AppCompatActivity implements OnOpenSerialPortListener, RadioGroup.OnCheckedChangeListener {

    public static final String TAG = SerialPortActivity.class.getSimpleName();
    public static final String DEVICE = "device";
    private SerialPortManager mSerialPortManager;
    private EditText et_go_site;
    private EditText et_go_site_fix;
    private TextView tv_running,tv_loading,tv_error,tv_power,tv_zhan_point;
    private RadioGroup radioGroup;
    private boolean isStarted = false;
    public static final int TEST = 0;
    public static final int PRODUCE = 1 ;
    private int crrentEnv = TEST;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port);
        et_go_site = findViewById(R.id.et_go_site);
        tv_running = findViewById(R.id.tv_running);
        tv_loading = findViewById(R.id.tv_loading);
        tv_error = findViewById(R.id.tv_error);
        tv_power = findViewById(R.id.tv_power);
        tv_zhan_point = findViewById(R.id.tv_zhan_point);
        et_go_site_fix  = findViewById(R.id.et_go_site_fix);

        this.radioGroup= (RadioGroup) this.findViewById(R.id.radioGroup);
        this.radioGroup.setOnCheckedChangeListener(this);

       // Device device = (Device) getIntent().getSerializableExtra(DEVICE);
        Device device = new Device("ttySAC3","g_serial",new File("/dev/ttySAC3"));
//        device.setName("ttyGS7");
//        device.setRoot("g_serial");
//        File file =  new File("/dev");
//        File[] files = file.listFiles();
//        for (int i = 0; i <files.length; i++) {
//            Log.i(TAG, "fliename" + files[i].getAbsolutePath());
//            if(files[i].getName().contains("SAC3")) {
//                device.setFile(files[i]);
//                Log.i(TAG, "进去了 " );
//            }
//
//
//        }

        Log.i(TAG, "onCreate: device = " + device);
        if (null == device) {
            finish();
            return;
        }

        mSerialPortManager = new SerialPortManager();

        // 打开串口
        boolean openSerialPort = mSerialPortManager.setOnOpenSerialPortListener(this)
                .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                    @Override
                    public void onDataReceived(final byte[] bytes) {
                        //Log.i(TAG, "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                        Log.i(TAG, "onDataReceived [ String ]: " + new String(bytes));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //showToast(String.format("接收\n%s", F.ByteArrToHex(finalBytes)));
                               // et_go_site.setText(F.ByteArrToHex(finalBytes));
                              // byte[] str =  F.hexStringToByte("0E00");
                                boolean isRunning =  ((bytes[4]) & 0X1)  == 1;
                              Log.i(TAG, "状态:" + (isRunning?"运行":"空闲"));
                                if(isRunning) {
                                    isStarted = isRunning;
                                }
                                boolean isLoading =  ((bytes[4]>>1) & 0X1)  == 1;
                                Log.i(TAG, "是否转载:" + (isLoading?"装载":"空载"));
                                boolean isError =  ((bytes[4]>>2) & 0X1)  == 1;
                                Log.i(TAG, "是否异常:" + (isError?"异常":"正常"));
                                int zhanPoint = (int)bytes[5];
                                Log.i(TAG, "当前站点:" + zhanPoint);
                                int power = (int)bytes[6];
                                Log.i(TAG, "当前电量:" + power);
                                //showToast(+"");

                                tv_running.setText("状态:" + (isRunning?"运行":"空闲"));
                                tv_loading.setText("是否转载:" + (isLoading?"装载":"空载"));
                                tv_error.setText("是否异常:" + (isError?"异常":"正常"));
                                tv_power.setText("当前电量:" + power+"%");
                                tv_zhan_point.setText("当前站点:"+zhanPoint);

                                if(!isRunning) {//空闲状态
                                    if(crrentEnv == TEST) {
                                        final Timer timer=new Timer();
                                        TimerTask task=new TimerTask(){
                                            public void run(){
                                                gotoOther();
                                                //timer.cancel();         终止此计时器，丢弃所有当前已安排的任务。这不会干扰当前正在执行的任务（如果存在）。一旦终止了计时器，那么它的执行线程也会终止，并且无法根据它安排更多的任务。注意，在此计时器调用的计时器任务的 run 方法内调用此方法，就可以绝对确保正在执行的任务是此计时器所执行的最后一个任务。
                                            }
                                        };

                                        timer.schedule(task,10000);   //这个命令就是5秒钟之后执行TimerTask里边的内容，后边的执行时间间隔为2秒钟。

                                    }else if(crrentEnv == PRODUCE && isLoading) {
                                        gotoOther();
                                    }

                                }
                            }
                        });
                    }



                    @Override
                    public void onDataSent(byte[] bytes) {
                        Log.i(TAG, "onDataSent [ byte[] ]: " + Arrays.toString(bytes));
                        Log.i(TAG, "onDataSent [ String ]: " + new String(bytes));
                        final byte[] finalBytes = bytes;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(String.format("发送\n%s", new String(finalBytes)));
                            }
                        });
                    }
                })
                .openSerialPort(device.getFile(), 115200);

        Log.i(TAG, "onCreate: openSerialPort = " + openSerialPort);
    }
    private void gotoOther() {
        if(!isStarted) {
            return;
        }
        String[] data  = et_go_site_fix.getText().toString().split(",");
        byte[] bytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            if(data[i].equals("左") ) {
                bytes[i] = 0X02;
            }else if(data[i].equals("右")) {
                bytes[i] = 0X03;
            }else if(data[i].equals("上")) {
                bytes[i] = 0X01;
            }

        }

        boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.moveLine(bytes));
//        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
//        showToast(sendBytes ? "发送成功" : "发送失败");
    }
    @Override
    protected void onDestroy() {
        if (null != mSerialPortManager) {
            mSerialPortManager.closeSerialPort();
            mSerialPortManager = null;
        }
        super.onDestroy();
    }



    /**
     * 串口打开成功
     *
     * @param device 串口
     */
    @Override
    public void onSuccess(File device) {
        Toast.makeText(getApplicationContext(), String.format("串口 [%s] 打开成功", device.getPath()), Toast.LENGTH_SHORT).show();
    }

    /**
     * 串口打开失败
     *
     * @param device 串口
     * @param status status
     */
    @Override
    public void onFail(File device, OnOpenSerialPortListener.Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                showDialog(device.getPath(), "没有读写权限");
                break;
            case OPEN_FAIL:
            default:
                showDialog(device.getPath(), "串口打开失败");
                break;
        }
    }

    /**
     * 显示提示框
     *
     * @param title   title
     * @param message message
     */
    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * 发送数据
     *
     * @param view view
     */
    public void onSend(View view) {
        EditText editTextSendContent = (EditText) findViewById(R.id.et_send_content);
        if (null == editTextSendContent) {
            return;
        }
        String sendContent = editTextSendContent.getText().toString().trim();
        if (TextUtils.isEmpty(sendContent)) {
            Log.i(TAG, "onSend: 发送内容为 null");
            return;
        }

        byte[] sendContentBytes = sendContent.getBytes();

        boolean sendBytes = mSerialPortManager.sendBytes(sendContentBytes);
        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
        showToast(sendBytes ? "发送成功" : "发送失败");
    }

    private Toast mToast;

    /**
     * Toast
     *
     * @param content content
     */
    private void showToast(String content) {
        if (null == mToast) {
            mToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
        }
        mToast.setText(content);
        mToast.show();
    }

    //zj
    public void close(View view) {
        if (null != mSerialPortManager) {
            mSerialPortManager.closeSerialPort();
            mSerialPortManager = null;
        }
    }

    public void start(View view) {
        byte[] data = new byte[8];
        boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.start());
        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
        showToast(sendBytes ? "发送成功" : "发送失败");
    }

    public void stop(View view) {
        boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.stop());
        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
        showToast(sendBytes ? "发送成功" : "发送失败");
    }
    public void bt_go_site(View view) {
        String[] data  = et_go_site.getText().toString().split(",");
        byte[] bytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            if(data[i].equals("左") ) {
                bytes[i] = 0X02;
            }else if(data[i].equals("右")) {
                bytes[i] = 0X03;
            }else if(data[i].equals("上")) {
                bytes[i] = 0X01;
            }

        }
//        byte [] bytes = new byte[4];
//        bytes[0] = 0x02;
//        bytes[1] = 0x03;
//        bytes[2] = 0x03;
//        bytes[3] = 0x01;
        boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.moveLine(bytes));
        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
        showToast(sendBytes ? "发送成功" : "发送失败");
    }

    public void bt_go_back(View view){
        boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.goback());
        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
        showToast(sendBytes ? "发送成功" : "发送失败");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        RadioButton radioButton_checked= (RadioButton) radioGroup.findViewById(checkedId);
        switch (checkedId){
            case R.id.radioButton_test:
                crrentEnv = TEST;
                break;
            case R.id.radioButton_produce:
                crrentEnv = PRODUCE;
                break;
        }
    }
}
