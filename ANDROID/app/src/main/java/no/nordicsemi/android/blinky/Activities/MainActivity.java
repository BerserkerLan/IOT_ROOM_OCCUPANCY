/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.blinky.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends BaseActivity {
    public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";

    private BlinkyViewModel mViewModel;

    Boolean triggered1 = false;
    Boolean triggered2 = false;

    String list1 = "";
    String list2 = "";
    Boolean sendListToServer1 = false;
    Boolean sendListToServer2 = false;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting Activity?")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    resetCounter();
                    final Intent controlBlinkIntent = new Intent(this, SplashScreenActivity.class);
                    startActivity(controlBlinkIntent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
       // ButterKnife.bind(this);

        list1 = "";
        list2 = "";
        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      /*  getSupportActionBar().setTitle(deviceName);
        getSupportActionBar().setSubtitle(deviceAddress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); */

        // Configure the view model
        mViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
        mViewModel.connect(device);

        mViewModel.distance1().observe(this,
                pressed -> {
                    if (pressed) {
                        System.out.println(">>>>>>>> " + pressed);
                        if (!triggered2) {
                            sensorTriggerred("DISTANCE2");
                            triggered2 = true;
                        }
                    } else {
                        triggered2 = false;
                    }
                });

        mViewModel.distance2().observe(this,
                pressed -> {
                    if (pressed) {
                        System.out.println(">>>>>>>>PRESSED DISTANCE2 " + pressed);
                        if (!triggered1) {
                            sensorTriggerred("DISTANCE1");
                            triggered1 = true;
                        }
                    } else {
                        triggered1 = false;
                    }
                });

        mViewModel.getDistanceStored1().observe(this,
                pressed -> {
                    System.out.println("Data DISTANCE1 STORED " + pressed);
                    if (list1.equals("")) {
                        list1 = pressed;
                        sendListToServer1 = true;
                    } else if (list1.equals(pressed)) {
                        sendListToServer1 = false;
                        //Do nothing we've already seen this
                    }
                });

        mViewModel.getDistanceStored2().observe(this,
                pressed -> {
                    System.out.println("Data DISTANCE2 STORED " + pressed);
                    if (list2.equals("")) {
                        list2 = pressed;
                        sendListToServer2 = true;
                    } else if (list1.equals(pressed)) {
                        sendListToServer2 = false;
                        //Do nothing we've already seen this
                    }
                });

        int [] a = {0,2,5};
        int [] b = {1,3};
        sendArraysToServer(a, b);

        /*new Thread(() -> {
            while(true){
                try {
                    System.out.println(">>>>IN WHILE");
                    if(sendListToServer1 && sendListToServer2){
                        sendArraysToServer(list1, list2);
                        sendListToServer1 = false;
                        sendListToServer2 = false;
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start(); */
    }
    /*
     Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:01.136 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:04.692 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED
03-18 22:23:04.693 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:05.277 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:05.326 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:07.617 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:07.910 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:08.299 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:08.494 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:08.495 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:09.080 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:09.080 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:10.542 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:10.543 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:11.127 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:11.128 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:18.148 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
03-18 22:23:23.997 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: Data DISTANCE1 STORED [1195919690, 1263291726, 1330663762, 1398035798, 1465407834]
03-18 22:23:23.998 8406-8406/no.nordicsemi.android.nrfblinky I/System.out: >>>>>>>>PRESSED DISTANCE2 true
     */
    @OnClick(R.id.action_clear_cache)
    public void onTryAgainClicked() {
        mViewModel.reconnect();
    }
}
