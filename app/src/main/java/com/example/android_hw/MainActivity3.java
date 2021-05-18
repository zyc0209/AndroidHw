package com.example.android_hw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class MainActivity3 extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner mBluetoothLeScanner = null;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning = false;
    ArrayList<scandata> findDevice = new ArrayList<>();
    RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        checkPermission(R.layout.activity_main3);
        bluetoothScan();
        mAdapter.OnItemClick(itemClick);
    }

    private final static String[] permissionsWeNeed = new String[]{
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int PERMISSION_REQUEST_CODE = 500;
    private void checkPermission(int activity_main3) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            int hasGone = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasGone != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
                requestPermissions(permissionsWeNeed,PERMISSION_REQUEST_CODE);
            }

            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this,"Not support Bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            }

            if(!mBluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            }
        }else finish();
    }

    private void bluetoothScan() {

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if(bluetoothManager != null){
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter != null){
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                Toast.makeText(this,"Bluetooth function started",Toast.LENGTH_SHORT).show();
            }
        }

        mBluetoothLeScanner.startScan(mLeScanCallback);
        isScanning = true;


        RecyclerView recyclerView = findViewById(R.id.recyclerView_ScannedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);
        //停止、開始掃描
        final Button btScan = findViewById(R.id.button_Scan);
        btScan.setOnClickListener((v)-> {
            if (isScanning) {
                //開始
                isScanning = false;
                btScan.setText("Start");
                mBluetoothLeScanner.stopScan(mLeScanCallback);
            }else{
                //停止
                isScanning = true;
                btScan.setText("Stop");
                findDevice.clear();
                mBluetoothLeScanner.startScan(mLeScanCallback);
                mAdapter.clearDevice();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Button btScan = findViewById(R.id.button_Scan);
        isScanning = false;
        btScan.setText("Start");
        mBluetoothLeScanner.stopScan(mLeScanCallback);
    }

    //離開頁面後停止掃描
    @Override
    protected void onStop() {
        super.onStop();
        final Button btScan = findViewById(R.id.button_Scan);
        //關閉掃描
        isScanning = true;
        btScan.setText("Stop");
        findDevice.clear();
        mBluetoothLeScanner.startScan(mLeScanCallback);
        mAdapter.clearDevice();
    }

    //顯示掃描到的物件
    private ScanCallback mLeScanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            ScanRecord scanRecord = result.getScanRecord();
            String address = device.getAddress();
            byte[] content = scanRecord.getBytes();
            new Thread(()->{
                if (address!= null){
                    findDevice.add(new scandata(device.getName()
                            , String.valueOf(result.getRssi())
                            , byteArrayToHexStr(content)
                            , device.getAddress()));
                    ArrayList newList = getSingle(findDevice);
                    runOnUiThread(()->{
                        //將陣列送到RecyclerView列表
                        mAdapter.addDevice(newList);
                    });
                }
            }).start();
        }
    };

    //Address過濾重複
    private ArrayList getSingle(ArrayList list) {
        ArrayList tempList = new ArrayList<>();
        try {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                } else {
                    tempList.set(getIndex(tempList, obj), obj);
                }
            }
            return tempList;
        } catch (ConcurrentModificationException e) {
            return tempList;
        }
    }

    private int getIndex(ArrayList temp, Object obj) {
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).toString().contains(obj.toString())) {
                return i;
            }
        }
        return -1;
    }

    //轉16進位
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(byteArray.length * 2);
        for (byte aData : byteArray) {
            hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }

    //跳轉頁面
    private RecyclerViewAdapter.OnItemClick itemClick = new RecyclerViewAdapter.OnItemClick(){
        @Override
        public void onItemClick(scandata selectedDevice) {
            Intent intent = new Intent(MainActivity3.this, bluetoothdata.class);
            intent.putExtra(bluetoothdata.DEVICE_NAME,selectedDevice.getDeviceName());
            intent.putExtra(bluetoothdata.RSSI,selectedDevice.getRssi());
            intent.putExtra(bluetoothdata.ADDRESS,selectedDevice.getAddress());
            intent.putExtra(bluetoothdata.DEVICE_BYTE,selectedDevice.getDeviceByteInfo());
            intent.putExtra(bluetoothdata.INTENT_KEY,selectedDevice);
            startActivity(intent);
        }
    };

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