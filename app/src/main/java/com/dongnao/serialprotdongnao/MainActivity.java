package com.dongnao.serialprotdongnao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements ProtDataInterface {

    EditText edit_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_content = findViewById(R.id.edit_content);

    }
//dev/ttsy
    public void open(View view) {
        //SerialPortManager.getInstance().openSerialPort("/dev/ttySAC2", 115200);
        SerialPortManager.getInstance().openSerialPort("/dev/ttySAC3", 115200);
        SerialPortManager.getInstance().regist(this);

    }

    public void send(View view) {

        String command = edit_content.getText().toString().trim();

//        if (TextUtils.isEmpty(command)) {
//            return;
//        }
      //  byte[] sendContentBytes = command.getBytes();
       // byte[] sendContentBytes = {(byte)0xAA};
//        byte[] sendContentBytes = new byte[2];
//        sendContentBytes[0] = (byte) 0x01;
//        sendContentBytes[1] = (byte)0X01;

        SerialPortManager.getInstance().putCommand(F.HexToByteArr("FF5C"));
    }

    @Override
    public void onDataReceived(byte[] bytes) {
        Log.i("david", "   收到信息   " + new String(bytes));
    }

    //启动
    public void start(View view) {
        SerialPortManager.getInstance().putCommand(F.HexToByteArr("550801010100095Caa"));

    }

    //停止
    public void stop(View view) {
        SerialPortManager.getInstance().putCommand(F.HexToByteArr("5508010100008F5Faa"));
    }
}
