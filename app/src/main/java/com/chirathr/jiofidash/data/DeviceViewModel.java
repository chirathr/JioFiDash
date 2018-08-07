package com.chirathr.jiofidash.data;

public class DeviceViewModel {
    private String macAddress;
    private String deviceName;
    private String ipAddress;
    private String isConnectedString;
    private boolean isBlocked;

    public DeviceViewModel(String deviceInfoString) {
        String[] deviceInfo = deviceInfoString.split(",");
        deviceName = deviceInfo[0].trim();
        macAddress = deviceInfo[1].trim();
        ipAddress = deviceInfo[3].trim();
        isConnectedString = deviceInfo[4].trim();
    }

    public DeviceViewModel(String name, String mac) {
        deviceName = name;
        macAddress = mac;
        ipAddress = "";
        isConnectedString = "Blocked";
        isBlocked = true;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIsConnectedString() {
        return isConnectedString;
    }

    public void setIsConnectedString(String isConnectedString) {
        this.isConnectedString = isConnectedString;
    }

    public boolean getIsConnected() {
        return isConnectedString.equals("Connected");
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
}
