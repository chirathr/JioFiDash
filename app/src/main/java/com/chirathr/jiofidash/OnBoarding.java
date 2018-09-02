package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chirathr.jiofidash.adapters.JioFiDeviceListAdapter;
import com.chirathr.jiofidash.data.JioFiDeviceViewModel;
import com.chirathr.jiofidash.data.JioFiDevicesData;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.fragments.ErrorSelectingDeviceDialogFragment;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_4_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_5_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_6_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_M2S_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_OTHER_ID;

public class OnBoarding extends AppCompatActivity implements
        JioFiDeviceListAdapter.JioFiDeviceOnClickLisetner,
        ErrorSelectingDeviceDialogFragment.ErrorSelectingButtonListener {

    private static final String TAG = OnBoarding.class.getSimpleName();
    private RecyclerView jioFiDevicesRecyclerView;
    private ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        jioFiDevicesRecyclerView = findViewById(R.id.jioFiDeviceRecyclerView);
        List<JioFiDeviceViewModel> jioFiDeviceViewModels = JioFiDevicesData.getDevices();

        jioFiDevicesRecyclerView.setHasFixedSize(true);
        jioFiDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jioFiDevicesRecyclerView.setAdapter(
                new JioFiDeviceListAdapter(this, this, jioFiDeviceViewModels));

        constraintLayout = findViewById(R.id.onBoardingLayout);
    }

    @Override
    public void selectDevice(int deviceId) {
        switch (deviceId) {
            case DEVICE_OTHER_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_4_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_5_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_M2S_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_6_ID: {
                showChecking();
                verifySelectedDevice(DEVICE_JIOFI_6_ID);
                break;
            }
        }
    }

    private void verifySelectedDevice(final int deviceId) {
        String urlString = NetworkUtils.getHostAddress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document document = Jsoup.parse(response);
                            if (document.title().equals(JioFiDevicesData.getDevicePageTitle(deviceId))) {
                                setDeviceAndStartMainActivity(deviceId);
                                Log.v(TAG, document.title());
                            }
                            else {
                                DialogFragment dialogFragment = new ErrorSelectingDeviceDialogFragment();
                                dialogFragment.show(getSupportFragmentManager(), "errorVerifyingSelection");
                            }
                        }
                        catch (NullPointerException e) {
                            Log.v(TAG, "Parse error :" + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(constraintLayout, "No JioFi found, check your WiFi", Snackbar.LENGTH_LONG).show();
                hideChecking();
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void openWebUI() {
        Uri webPage = Uri.parse(NetworkUtils.DEFAULT_HOST);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            //Page not found
            Log.v(TAG, "page not found, open web page.");
        }
    }

    public void setDeviceAndStartMainActivity(int deviceId) {
        JioFiPreferences.getInstance().setDevice(this, deviceId);
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void showChecking() {
        Snackbar.make(constraintLayout, "Checking device..", Snackbar.LENGTH_INDEFINITE).show();
        jioFiDevicesRecyclerView.setAlpha(Float.parseFloat("0.2"));
    }

    public void hideChecking() {
        jioFiDevicesRecyclerView.setAlpha(Float.parseFloat("1.0"));
    }

    @Override
    public void onSelectAnotherListener() {
        hideChecking();
    }
}
