package no.nordicsemi.android.blinky.profile.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface readSwitchCallback {

    /**
     * Called when databaseInstance button was pressed or released on device.
     *
     * @param device the target device.
     * @param pressed true if the button was pressed, false if released.
     */
    void readswitchstatechanged(@NonNull final BluetoothDevice device, final boolean pressed);

}