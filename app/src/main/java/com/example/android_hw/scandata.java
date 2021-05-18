package com.example.android_hw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;

class scandata implements Serializable {

    private String deviceName;
    private String rssi;
    private String deviceByteInfo;
    private String address;

    public scandata(String deviceName, String rssi, String deviceByteInfo, String address) {
        this.deviceName = deviceName;
        this.rssi = rssi;
        this.deviceByteInfo = deviceByteInfo;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getRssi() {
        return rssi;
    }

    public String getDeviceByteInfo() {
        return deviceByteInfo;
    }

    public String getDeviceName() {
        return deviceName;
    }
}

