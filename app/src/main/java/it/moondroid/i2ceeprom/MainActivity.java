package it.moondroid.i2ceeprom;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Marco on 03/01/2018.
 */

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // I2C Device Name
    private static final String I2C_DEVICE_NAME = "I2C1";
    // I2C Slave Address
//    private static final int I2C_ADDRESS = 0b10100001
    private static final int I2C_ADDRESS = 0x50;

    private static final int TEST_REGISTER = 0x00;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkI2cBus();
        accessI2cDevice();
//        performScan();
    }

    private void checkI2cBus(){

        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> deviceList = manager.getI2cBusList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No I2C bus available on this device.");
        } else {
            Log.i(TAG, "List of available devices: " + deviceList);
        }
    }

    private void accessI2cDevice(){
        // Attempt to access the I2C device
        try {
            PeripheralManagerService manager = new PeripheralManagerService();

            I2cDevice device = manager.openI2cDevice(I2C_DEVICE_NAME, I2C_ADDRESS);
            Log.i(TAG, "Opened I2C device: " + device);

//            byte[] buffer = new byte[256];
//            device.read(buffer, 256);
//            printBuffer(buffer);

            for (int addr=0; addr<256; addr++){
                byte b = device.readRegByte(addr);
                Log.i(TAG, addr + " : " + String.valueOf((char)b));
            }

        } catch (IOException e) {
            Log.w(TAG, "Unable to access I2C device", e);
        }

    }

    private void printBuffer(byte[] buffer){
        for (int i=0; i<buffer.length; i++){
            Log.i(TAG, String.valueOf((char)buffer[i]));
        }
    }

    private void performScan() {
        PeripheralManagerService manager = new PeripheralManagerService();
        for (int address = 0; address<256; address++) {

            //auto-close the devices
            try {
                I2cDevice device = manager.openI2cDevice(I2C_DEVICE_NAME, address);

                try {
                    device.readRegByte(TEST_REGISTER);
                    Log.i(TAG, String.format(Locale.US, "Trying: 0x%02X - SUCCESS", address));
                } catch (IOException e) {
                    Log.w(TAG, String.format(Locale.US, "Trying: 0x%02X - FAIL", address));
                }

            } catch (IOException e) {
                //in case the openI2cDevice(name, address) fails
                Log.w(TAG, String.format(Locale.US, "Trying: 0x%02X - FAIL", address));
            }

        }
    }
}
