package com.example.android_hw;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class bluetoothdata extends AppCompatActivity {
    public static final String DEVICE_NAME = "";
    public static final String RSSI = "";
    public static final String ADDRESS = "";
    public static final String DEVICE_BYTE = "";
    public static final String INTENT_KEY = "";
    private scandata selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_device);

        TextView txt_name = findViewById(R.id.DEVICE_NAME);
        TextView txt_rssi = findViewById(R.id.RSSI);
        TextView txt_address = findViewById(R.id.ADDRESS);
        TextView txt_byte = findViewById(R.id.DEVICE_BYTE);

        selectedDevice = (scandata) getIntent().getSerializableExtra(INTENT_KEY);

        txt_name.setText(selectedDevice.getDeviceName());
        txt_address.setText(selectedDevice.getAddress());
        txt_byte.setText(selectedDevice.getDeviceByteInfo());
        txt_rssi.setText(selectedDevice.getRssi());

        Button btn = (Button)findViewById(R.id.btn_return);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                bluetoothdata.this.finish();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.ECLAIR){
                event.startTracking();
            }else {
                onBackPressed();
            }}
        return false;
    }
}
