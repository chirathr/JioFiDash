package com.chirathr.jiofidash.data;

import com.chirathr.jiofidash.R;

import java.util.ArrayList;
import java.util.List;

public class JioFiDevicesData {

    private static final JioFiDeviceViewModel jioFi6 = new JioFiDeviceViewModel(
            6, R.drawable.jiofi6, "JioFi 6 (JMR815)",
            "JioFi 6(JMR815) 4G device with 3000mah battery, WiFi and micro SD.", true);

    private static final JioFiDeviceViewModel jioFi5 = new JioFiDeviceViewModel(
            6, R.drawable.jiofi6, "JioFi 5 (JMR814)",
            "JioFi 6(JMR815) 4G device with 3000mah battery, WiFi and micro SD.", false);

    public static List<JioFiDeviceViewModel> getDevices() {
        List<JioFiDeviceViewModel> deviceViewModels = new ArrayList<>();
        deviceViewModels.add(jioFi6);

        return deviceViewModels;
    }

    public static JioFiDeviceViewModel getDeviceData(int id) {
        JioFiDeviceViewModel deviceViewModel;
        switch (id) {
            case 6: deviceViewModel = jioFi6;
                break;
            default: deviceViewModel = null;
        }

        return deviceViewModel;
    }
}
