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

package no.nordicsemi.android.blinky.profile;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.blinky.profile.callback.PIR1ArrayDataCallback;
import no.nordicsemi.android.blinky.profile.callback.PIR2ArrayDataCallback;
import no.nordicsemi.android.blinky.profile.callback.PIR2DataCallback;
import no.nordicsemi.android.blinky.profile.callback.PIRDataCallback;
import no.nordicsemi.android.blinky.profile.callback.distanceDataCallback;
import no.nordicsemi.android.blinky.profile.callback.readSwitchDataCallback;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class Manager extends BleManager<ManagerCallbacks> {

    /**
     * Nordic Blinky Service UUID.
     */
    public final static UUID LBS_UUID_SERVICE_PIR1 = UUID.fromString("0000A000-0000-1000-8000-00805F9B34FB"); //PIR_UUID
    private final static UUID LBS2_UUID_SERVICE_PIR2 = UUID.fromString("0000A003-0000-1000-8000-00805F9B34FB"); //PIR2
    private final static UUID LBS3_UUID_SERVICE_READSWITCH = UUID. fromString("0000A004-0000-1000-8000-00805F9B34FB"); //READSWITCH
    private final static UUID LBS_UUID_SERVICE_PIR1ARRAY = UUID.fromString("0000B000-0000-1000-8000-00805F9B34FB"); //D1 Array UUID
    private final static UUID LBS_UUID_SERVICE_PIR2ARRAY = UUID.fromString("0000B004-0000-1000-8000-00805F9B34FB"); //D2 Array UUID

    /**
     * SENSOR characteristic UUID.
     */
    private final static UUID PIR_UUID = UUID.fromString("0000A001-0000-1000-8000-00805F9B34FB");
    private final static UUID PIR2_UUID = UUID.fromString("0000A002-0000-1000-8000-00805F9B34FB");
    private final static UUID READSWITCH_UUID = UUID.fromString("0000B005-0000-1000-8000-00805F9B34FB");
    private final static UUID PIR1_ARRAY_CHAR_UUID = UUID.fromString("0000B002-0000-1000-8000-00805F9B34FB");
    private final static UUID PIR2_ARRAY_CHAR_UUID = UUID.fromString("0000B060-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic pirCharacteristic, pir2Characteristic, readSwitchCharacteristics, distanceCharacteristic, pir1ArrayCharacteristic, pir2ArrayCharacteristic;
    private LogSession mLogSession;

    public Manager(@NonNull final Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    /**
     * Sets the log session to be used for low level logging.
     *
     * @param session the session, or null, if nRF Logger is not installed.
     */

    public void setLogger(@Nullable final LogSession session) {
        this.mLogSession = session;
    }

    @Override
    public void log(final int priority, @NonNull final String message) {
        // The priority is databaseInstance Log.X constant, while the Logger accepts it's log levels.
        Logger.log(mLogSession, LogContract.Log.Level.fromPriority(priority), message);
    }


    private final PIRDataCallback pir1callBack = new PIRDataCallback() {

        @Override
        public void onPIRStateChanged(@NonNull final BluetoothDevice device, final boolean pressed) {
            mCallbacks.onPIRStateChanged(device, pressed);
        }

    };


    private final PIR2DataCallback pir2callBack = new PIR2DataCallback() {

        @Override
        public void onPIR2StateChanged(@NonNull BluetoothDevice device, boolean pressed) {
            mCallbacks.onPIR2StateChanged(device, pressed);
        }

    };


    private final readSwitchDataCallback readSwitchCallBack = new readSwitchDataCallback() {

        @Override
        public void readswitchstatechanged(@NonNull final BluetoothDevice device, final boolean pressed) {
            mCallbacks.readswitchstatechanged(device, pressed);
        }

    };


    private final distanceDataCallback distanceCallBack = new distanceDataCallback() {

        @Override
        public void distancestatechanged(@NonNull final BluetoothDevice device, final boolean pressed) {
            mCallbacks.onPIRStateChanged(device, pressed);
        }

    };

    private final PIR1ArrayDataCallback pir1ArrayCallback = new PIR1ArrayDataCallback() {
        @Override
        public void onPIR1ArrayStateChanged(@NonNull BluetoothDevice device, String pressed) {
            mCallbacks.onPIR1ArrayStateChanged(device, pressed);
        }
    };

    private final PIR2ArrayDataCallback pir2ArrayCallback = new PIR2ArrayDataCallback() {
        @Override
        public void onPIR2ArrayStateChanged(@NonNull BluetoothDevice device, String pressed) {
            System.out.println("IN pir2ArrayDataCallBack");
            mCallbacks.onPIR2ArrayStateChanged(device, pressed);
        }
    };

    /**
     * BluetoothGatt callbacks object.
     */
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
        @Override
        protected void initialize() {
            setNotificationCallback(pirCharacteristic).with(pir1callBack);
            setNotificationCallback(pir2Characteristic).with(pir2callBack);
            setNotificationCallback(readSwitchCharacteristics).with(readSwitchCallBack);
            setNotificationCallback(distanceCharacteristic).with(distanceCallBack);
            setNotificationCallback(pir1ArrayCharacteristic).with(pir1ArrayCallback);
            setNotificationCallback(pir2ArrayCharacteristic).with(pir2ArrayCallback);


            readCharacteristic(pirCharacteristic).with(pir1callBack).enqueue();
            readCharacteristic(pir2Characteristic).with(pir2callBack).enqueue();
            readCharacteristic(readSwitchCharacteristics).with(readSwitchCallBack).enqueue();
            readCharacteristic(distanceCharacteristic).with(distanceCallBack).enqueue();
            readCharacteristic(pir1ArrayCharacteristic).with(pir1ArrayCallback).enqueue();
            readCharacteristic(pir2ArrayCharacteristic).with(pir2ArrayCallback).enqueue();

            enableNotifications(pirCharacteristic).enqueue();
            enableNotifications(pir2Characteristic).enqueue();
            enableNotifications(readSwitchCharacteristics).enqueue();
            enableNotifications(distanceCharacteristic).enqueue();
            enableNotifications(pir1ArrayCharacteristic).enqueue();
            enableNotifications(pir2ArrayCharacteristic).enqueue();
        }

        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            final BluetoothGattService pirService1 = gatt.getService(LBS_UUID_SERVICE_PIR1);
            final BluetoothGattService pirService2 = gatt.getService(LBS2_UUID_SERVICE_PIR2);
            final BluetoothGattService readSwitchService = gatt.getService(LBS3_UUID_SERVICE_READSWITCH);
            final BluetoothGattService pir1ArrayService = gatt.getService(LBS_UUID_SERVICE_PIR1ARRAY);
            final BluetoothGattService pir2ArrayService = gatt.getService(LBS_UUID_SERVICE_PIR2ARRAY);

            if (pirService1 != null) {
                pirCharacteristic = pirService1.getCharacteristic(PIR_UUID);
            }

            if (pirService2 != null) {
                pir2Characteristic = pirService2.getCharacteristic(PIR2_UUID);
            }

            if (readSwitchService != null) {
                readSwitchCharacteristics = readSwitchService.getCharacteristic(READSWITCH_UUID);
            }

            if (pir1ArrayService != null) {
                pir1ArrayCharacteristic = pir1ArrayService.getCharacteristic(PIR1_ARRAY_CHAR_UUID);
            }

            if (pir2ArrayService != null) {
                pir2ArrayCharacteristic = pir2ArrayService.getCharacteristic(PIR2_ARRAY_CHAR_UUID);
            }


            return true;

        }

        @Override
        protected void onDeviceDisconnected() {
            pirCharacteristic = null;
            pir2Characteristic = null;
            readSwitchCharacteristics = null;
            distanceCharacteristic = null;
            pir1ArrayCharacteristic = null;
            pir2ArrayCharacteristic = null;
        }
    };


    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return true;
    }
}
