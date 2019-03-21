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
import android.view.WindowManager;
import android.widget.ImageView;


import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
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
    String timestamp = "";

    public int[] convertString(String a)
    {
        a = a.substring(1, a.length()-1);
        String[] elephantList = a.split(",");
        int[] timeStamps = new int[elephantList.length];
        int i = 0;
        for (String str : elephantList) {
            timeStamps[i] = Integer.parseInt(str.trim());
            i++;
        }
        System.out.println(">>>>>t");
        return timeStamps;
    }
    /**
     * Overriding onBackPressed to prevent accidentally exiting the activity
     */
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

        list1 = "";
        list2 = "";
        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on flag


        // Configure the view model
        mViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
        mViewModel.connect(device);



        /*
          This will deal with getting readings from the inside distance sensor
         */

        mViewModel.distance1().observe(this,
                pressed -> {
                    System.out.println("IN DISTANCE1");
                    if (pressed) {
                        timestamp = getTimeStamp();
                        System.out.println(">>>>>>>> " + pressed);
                        if (!triggered2) {
                            sensorTriggerred("DISTANCE2");
                            triggered2 = true;
                        }
                    } else {
                        triggered2 = false;
                    }
                });

        /*
          This will deal with getting readings from the outside distance sensor
         */

        mViewModel.distance2().observe(this,
                pressed -> {
                    System.out.println("IN DISTANCE2");
                    if (pressed) {
                        timestamp = getTimeStamp();
                        System.out.println(">>>>>>>>PRESSED DISTANCE2 " + pressed);
                        if (!triggered1) {
                            sensorTriggerred("DISTANCE1");
                            triggered1 = true;
                        }
                    } else {
                        triggered1 = false;
                    }
                });

        /*
          This will deal with getting NEW IN data regarding the stored data in the occasion that the gateway disconnects from the board
         */

        mViewModel.getDistanceStored1().observe(this,
                pressed -> {
                    System.out.println("IN DISTANCES STORED1");
                    timestamp = getTimeStamp();
                    System.out.println("Data DISTANCE1 STORED " + pressed);
                    if (list1.equals("")) {
                        list1 = pressed;
                        sendListToServer1 = true;
                    } else if (list1.equals(pressed)) {
                        sendListToServer1 = false;
                        //Do nothing we've already seen this
                    }
                });
        /*
          This will deal with getting NEW OUT data regarding the stored data in the occasion that the gateway disconnects from the board
         */

        //onLinkLossOccurred
        mViewModel.getMIsConnected().observe(this,
                pressed -> {
                   if(!pressed){
                       System.out.println(">>>>>>>>>>>>>BOARD DISCONNECTED");
                       boardDisconnectedSpeak();
                   } else {
                       boardConnectedSpeak();
                       System.out.println("THE BOARD IS CONNECTED");
                   }
                });

        ImageView imageView = findViewById(R.id.image);
        imageView.setOnLongClickListener(v -> {
            mViewModel.getMIsConnected().observe(this,
                    pressed -> {
                        if(!pressed){
                            System.out.println(">>>>>>>>>>>>>BOARD DISCONNECTED");
                            boardDisconnectedSpeak();
                        } else {
                            boardConnectedSpeak();
                            System.out.println("THE BOARD IS CONNECTED");
                        }
                    });
            return true;
        });
        imageView.setOnClickListener(v -> mViewModel.getMIsConnected().observe(this,
                pressed -> {
                    if(!pressed){
                        System.out.println(">>>>>>>>>>>>>BOARD DISCONNECTED");
                        boardDisconnectedSpeak();
                    } else {
                        System.out.println("THE BOARD IS CONNECTED");
                    }
                }));

        mViewModel.getDistanceStored2().observe(this,
                pressed -> {
                    System.out.println("IN DISTANCE STORED2");
                    timestamp = getTimeStamp();
                    System.out.println("Data DISTANCE2 STORED " + pressed);
                    convertString(pressed);
                    if (list2.equals("")) {
                        list2 = pressed;
                        sendListToServer2 = true;
                    } else if (list1.equals(pressed)) {
                        sendListToServer2 = false;
                        //Do nothing we've already seen this
                    }
                });

        /*
        int [] b = {1,2,3,6,7};
        int [] a = {4,5,8};
        sendArraysToServer(a, b);
        System.out.println("D1 D1 D1 D2 D2 D1 D1 D2"); */

        new Thread(() -> {
            while(true){
                try {
                    if(sendListToServer1 && sendListToServer2){
                        try{
                            sendArraysToServer(convertString(list1), convertString(list2));
                        } catch (Exception ignored){

                        }
                        sendListToServer1 = false;
                        sendListToServer2 = false;
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

             }
        }).start();
    }


}
