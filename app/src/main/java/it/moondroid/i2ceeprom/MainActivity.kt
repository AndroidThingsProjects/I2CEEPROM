package it.moondroid.i2ceeprom

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.PeripheralManagerService
import java.io.IOException
import java.util.*


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private val TAG = "MainActivity"

    // I2C Device Name
    private val I2C_DEVICE_NAME = "I2C1"
    // I2C Slave Address
//    private val I2C_ADDRESS = 0b10100001
    private val I2C_ADDRESS = 0x50

    private val TEST_REGISTER = 0x0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkI2cBus()
        accessI2cDevice()
//        performScan()
    }

    private fun checkI2cBus(){

        val manager = PeripheralManagerService()
        val deviceList = manager.i2cBusList
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No I2C bus available on this device.")
        } else {
            Log.i(TAG, "List of available devices: " + deviceList)
        }
    }

    private fun accessI2cDevice(){
        // Attempt to access the I2C device
        try {
            val manager = PeripheralManagerService()

            var device = manager.openI2cDevice(I2C_DEVICE_NAME, I2C_ADDRESS)
            Log.i(TAG, "Opened I2C device: " + device)
            var buffer = ByteArray(256)
            device.read(buffer, 256)
            printBuffer(buffer)
        } catch (e: IOException) {
            Log.w(TAG, "Unable to access I2C device", e)
        }

    }

    private fun printBuffer(buffer : ByteArray){
        for (b in buffer.iterator()){
            Log.i(TAG, b.toChar().toString())
        }
    }

    private fun performScan() {
        val peripheralManagerService = PeripheralManagerService()
        for (address in 0..255) {

            //auto-close the devices
            try {
                peripheralManagerService.openI2cDevice(I2C_DEVICE_NAME, address).use({ device ->

                    try {
                        device.readRegByte(TEST_REGISTER)
                        Log.i(TAG, String.format(Locale.US, "Trying: 0x%02X - SUCCESS", address))
                    } catch (e: IOException) {
                        Log.w(TAG, String.format(Locale.US, "Trying: 0x%02X - FAIL", address))
                    }


                })
            } catch (e: IOException) {
                //in case the openI2cDevice(name, address) fails
                Log.w(TAG, String.format(Locale.US, "Trying: 0x%02X - FAIL", address))
            }

        }
    }

}
