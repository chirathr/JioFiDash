package com.chirathr.jiofidash.data;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class JioFiDevicesData {

    private static final String NOT_SUPPORTED_YET = "This device will be supported in a future update. Please use the Web UI instead.";

    private static final JioFiDeviceViewModel jioFi6 = new JioFiDeviceViewModel(
            NetworkUtils.DEVICE_JIOFI_6_ID, R.drawable.jiofi_6, "JioFi 6 (JMR815)",
            "JioFi 6(JMR815) 4G device with 3000mah battery, WiFi and micro SD.", true);

    private static final JioFiDeviceViewModel jioFiM2S = new JioFiDeviceViewModel(
            NetworkUtils.DEVICE_JIOFI_M2S_ID, R.drawable.jiofi_m2s, "JioFi M2S",
            NOT_SUPPORTED_YET, false);

    private static final JioFiDeviceViewModel jioFi5 = new JioFiDeviceViewModel(
            NetworkUtils.DEVICE_JIOFI_5_ID, R.drawable.jiofi_5, "JioFi 5 (JMR814)",
            NOT_SUPPORTED_YET, false);

    private static final JioFiDeviceViewModel jioFi4 = new JioFiDeviceViewModel(
            NetworkUtils.DEVICE_JIOFI_4_ID, R.drawable.jiofi_4, "JioFi 4",
            NOT_SUPPORTED_YET, false);

    private static final JioFiDeviceViewModel jioFiOther = new JioFiDeviceViewModel(
            NetworkUtils.DEVICE_OTHER_ID, R.drawable.jiofi_other, "Other Devices",
            "Sorry! Older devices won't be supported. Please use the Web UI instead.", false);

    public static List<JioFiDeviceViewModel> getDevices() {
        List<JioFiDeviceViewModel> deviceViewModels = new ArrayList<>();
        deviceViewModels.add(jioFi6);
        deviceViewModels.add(jioFiM2S);
        // deviceViewModels.add(jioFi5);
        deviceViewModels.add(jioFi4);
        deviceViewModels.add(jioFiOther);

        return deviceViewModels;
    }

    public static JioFiDeviceViewModel getDeviceData(int id) {
        JioFiDeviceViewModel deviceViewModel;
        switch (id) {
            case NetworkUtils.DEVICE_JIOFI_6_ID: deviceViewModel = jioFi6;
                break;
            default: deviceViewModel = null;
        }

        return deviceViewModel;
    }
}
