package no.nordicsemi.android.blinky.profile.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public interface PIR2ArrayCallback {


    void onPIR2ArrayStateChanged(@NonNull final BluetoothDevice device, final String pressed);


}