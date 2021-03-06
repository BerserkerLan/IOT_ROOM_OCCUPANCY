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

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.adapter.DevicesAdapter;
import no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.blinky.utils.Utils;
import no.nordicsemi.android.blinky.viewmodels.ScannerStateLiveData;
import no.nordicsemi.android.blinky.viewmodels.ScannerViewModel;

public class ScannerActivity extends BaseActivity implements DevicesAdapter.OnItemClickListener {
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1022; // random number
    private ScannerViewModel mScannerViewModel;
    private static final String MAC_ADDRESS_1 = "FD:88:79:84:08:84";
    private static final String MAC_ADDRESS_2 = "DA:A3:E2:4F:50:FD";
    //private static final String MAC_ADDRESS_2 = "";

    private boolean showingDialog = false;
    @BindView(R.id.state_scanning)
    View mScanningView;
    @BindView(R.id.no_devices)
    View mEmptyView;
    @BindView(R.id.no_location_permission)
    View mNoLocationPermissionView;
    @BindView(R.id.action_grant_location_permission)
    Button mGrantPermissionButton;
    @BindView(R.id.action_permission_settings)
    Button mPermissionSettingsButton;
    @BindView(R.id.no_location)
    View mNoLocationView;
    @BindView(R.id.bluetooth_off)
    View mNoBluetoothView;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        ButterKnife.bind(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Finding Nordic");

        // Create view model containing utility methods for scanning
        mScannerViewModel = ViewModelProviders.of(this).get(ScannerViewModel.class);
        mScannerViewModel.getScannerState().observe(this, this::startScan);
        mScannerViewModel.getDevices().observe(this, devices -> {
            //Here we check if we can autoconnect
            boolean MAC1 = false;
            no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice MAC1_DEVICE = null;
            boolean MAC2 = false;
            no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice MAC2_DEVICE = null;
            try {
                for (int i = 0; i < devices.size(); i++) {
                    if (MAC1 && MAC2) {
                        //If both boards are on, we break and we deal with this event later
                        break;
                    } else if (devices.get(i).getAddress().equals(MAC_ADDRESS_1)) {
                        //If only board 1 is on
                        MAC1 = true;
                        MAC1_DEVICE = devices.get(i);
                    } else if (devices.get(i).getAddress().equals(MAC_ADDRESS_2)) {
                        //If only board 2 is on
                        MAC2 = true;
                        MAC2_DEVICE = devices.get(i);
                    }
                }
            } catch (Exception ignored) {

            }
            if (MAC1 && MAC2) {
                //If board 1 AND board 2 is on, we ask the user which one they want to connect to
                if (!showingDialog) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    DiscoveredBluetoothDevice finalMAC2_DEVICE = MAC2_DEVICE;
                    DiscoveredBluetoothDevice finalMAC1_DEVICE = MAC1_DEVICE;
                    builder.setMessage("Which Device?")
                            .setCancelable(false)
                            .setPositiveButton("Device 1", (dialog, id) -> {
                                final Intent controlBlinkIntent = new Intent(getApplicationContext(), MainActivity.class);
                                controlBlinkIntent.putExtra(MainActivity.EXTRA_DEVICE, finalMAC2_DEVICE);
                                startActivity(controlBlinkIntent);
                            })
                            .setNegativeButton("Device 2", (dialog, id) -> {
                                final Intent controlBlinkIntent = new Intent(getApplicationContext(), MainActivity.class);
                                controlBlinkIntent.putExtra(MainActivity.EXTRA_DEVICE, finalMAC1_DEVICE);
                                startActivity(controlBlinkIntent);
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    showingDialog = false;
                }
            } //If we reach this stage, its either MAC1 or MAC2, cannot be both
            else if (MAC2) {
                //Start with MAC2, as its the only one which is one
                final Intent controlBlinkIntent = new Intent(this, MainActivity.class);
                controlBlinkIntent.putExtra(MainActivity.EXTRA_DEVICE, MAC2_DEVICE);
                startActivity(controlBlinkIntent);
            } else if (MAC1) {
                //Start with MAC1, as its the only one which is one
                final Intent controlBlinkIntent = new Intent(this, MainActivity.class);
                controlBlinkIntent.putExtra(MainActivity.EXTRA_DEVICE, MAC1_DEVICE);
                startActivity(controlBlinkIntent);
            }
        });

        // Configure the recycler view
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_ble_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        final DevicesAdapter adapter = new DevicesAdapter(this, mScannerViewModel.getDevices());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        menu.findItem(R.id.filter_uuid).setChecked(mScannerViewModel.isUuidFilterEnabled());
        menu.findItem(R.id.filter_nearby).setChecked(mScannerViewModel.isNearbyFilterEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_uuid:
                item.setChecked(!item.isChecked());
                mScannerViewModel.filterByUuid(item.isChecked());
                return true;
            case R.id.filter_nearby:
                item.setChecked(!item.isChecked());
                mScannerViewModel.filterByDistance(item.isChecked());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(@NonNull final DiscoveredBluetoothDevice device) {
        final Intent controlBlinkIntent = new Intent(this, MainActivity.class);
        controlBlinkIntent.putExtra(MainActivity.EXTRA_DEVICE, device);
        startActivity(controlBlinkIntent);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION:
                mScannerViewModel.refresh();
                break;
        }
    }

    @OnClick(R.id.action_enable_location)
    public void onEnableLocationClicked() {
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @OnClick(R.id.action_enable_bluetooth)
    public void onEnableBluetoothClicked() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableIntent);
    }

    @OnClick(R.id.action_grant_location_permission)
    public void onGrantLocationPermissionClicked() {
        Utils.markLocationPermissionRequested(this);
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_ACCESS_COARSE_LOCATION);
    }

    @OnClick(R.id.action_permission_settings)
    public void onPermissionSettingsClicked() {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    /**
     * Start scanning for Bluetooth devices or displays databaseInstance message based on the scanner state.
     */
    private void startScan(final ScannerStateLiveData state) {
        // First, check the Location permission. This is required on Marshmallow onwards in order
        // to scan for Bluetooth LE devices.
        if (Utils.isLocationPermissionsGranted(this)) {
            mNoLocationPermissionView.setVisibility(View.GONE);

            // Bluetooth must be enabled
            if (state.isBluetoothEnabled()) {
                mNoBluetoothView.setVisibility(View.GONE);

                // We are now OK to start scanning
                mScannerViewModel.startScan();
                mScanningView.setVisibility(View.VISIBLE);

                if (!state.hasRecords()) {
                    mEmptyView.setVisibility(View.VISIBLE);

                    if (!Utils.isLocationRequired(this) || Utils.isLocationEnabled(this)) {
                        mNoLocationView.setVisibility(View.INVISIBLE);
                    } else {
                        mNoLocationView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            } else {
                mNoBluetoothView.setVisibility(View.VISIBLE);
                mScanningView.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.GONE);
                clear();
            }
        } else {
            mNoLocationPermissionView.setVisibility(View.VISIBLE);
            mNoBluetoothView.setVisibility(View.GONE);
            mScanningView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.GONE);

            final boolean deniedForever = Utils.isLocationPermissionDeniedForever(this);
            mGrantPermissionButton.setVisibility(deniedForever ? View.GONE : View.VISIBLE);
            mPermissionSettingsButton.setVisibility(deniedForever ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * stop scanning for bluetooth devices.
     */
    private void stopScan() {
        mScannerViewModel.stopScan();
    }

    /**
     * Clears the list of devices, which will notify the observer.
     */
    private void clear() {
        mScannerViewModel.getDevices().clear();
        mScannerViewModel.getScannerState().clearRecords();
    }
}