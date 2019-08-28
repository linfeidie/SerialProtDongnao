package com.dongnao.serialprotdongnao;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;
import java.util.Arrays;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/8/27
 * <p>
 * 版本号：SerialProtDongnao
 */
public class SerialPortActivity extends AppCompatActivity implements OnOpenSerialPortListener{

    public static final String TAG = SerialPortActivity.class.getSimpleName();
    public static final String DEVICE = "device";
    private SerialPortManager mSerialPortManager;
    private EditText et_go_site;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port);
        et_go_site = findViewById(R.id.et_go_site);

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
                    public void onDataReceived(byte[] bytes) {
                        Log.i(TAG, "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                        Log.i(TAG, "onDataReceived [ String ]: " + new String(bytes));
                        final byte[] finalBytes = bytes;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //showToast(String.format("接收\n%s", F.ByteArrToHex(finalBytes)));
                               // et_go_site.setText(F.ByteArrToHex(finalBytes));
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
        byte[] data = new byte[8];
        boolean sendBytes = mSerialPortManager.sendBytes(CommandControl.moveLine());
        Log.i(TAG, "onSend: sendBytes = " + sendBytes);
        showToast(sendBytes ? "发送成功" : "发送失败");
    }

}
