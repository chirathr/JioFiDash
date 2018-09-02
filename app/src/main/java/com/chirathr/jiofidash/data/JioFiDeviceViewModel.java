package com.chirathr.jiofidash.data;

public class JioFiDeviceViewModel {
    private int deviceImage;
    private String deviceName;
    private int deviceId;
    private String description;
    private boolean isSupported;

    public JioFiDeviceViewModel(int id, int image, String name, String description, boolean isSupported) {
        deviceId = id;
        deviceImage = image;
        deviceName = name;
        this.description = description;
        this.isSupported = isSupported;
    }

    public boolean isSupported() {
        return isSupported;
    }

    public void setSupported(boolean supported) {
        isSupported = supported;
    }

    public int getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(int deviceImage) {
        this.deviceImage = deviceImage;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
