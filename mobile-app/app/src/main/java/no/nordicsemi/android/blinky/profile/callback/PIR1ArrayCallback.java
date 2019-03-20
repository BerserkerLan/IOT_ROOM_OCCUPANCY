package no.nordicsemi.android.blinky.profile.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface PIR1ArrayCallback {


    void onPIR1ArrayStateChanged(@NonNull final BluetoothDevice device, final String pressed);

}