package ml.preventiv.bluetooth.gatt

import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import no.nordicsemi.android.support.v18.scanner.*
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanRecord
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.*
import kotlin.collections.ArrayList

class GattClientManager(private val mGattServerClientCallback: GattServerClientCallback?, private val context: Context)
    : BluetoothGattCallback() {


    private val TAG = "GattClientManager"
    private val mHandler: Handler = Handler()
    private var mConnectedGatt: BluetoothGatt? = null


    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        val device = gatt.device

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices()

            if (mGattServerClientCallback != null) {
                mHandler.post { mGattServerClientCallback.onDeviceConnected(device) }
            }

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            disconnect()
            if (mGattServerClientCallback != null) {
                mHandler.post { mGattServerClientCallback.onDeviceDisconnected(device) }
            }
        }
    }



    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)

        for (service in gatt.services) {
            if (service.uuid == GattServerManager.DeviceProfile.SERVICE_UUID) {

                val characteristicUUID = service.characteristics[0]
                //Read the current characteristic's value

                gatt.readCharacteristic(characteristicUUID)
                Log.i(TAG, characteristicUUID.uuid.toString())
            }
        }
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt,
                                      characteristic: BluetoothGattCharacteristic,
                                      status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        val value = characteristic.value

        //Register for further updates as notifications
        gatt.setCharacteristicNotification(characteristic, true)


        if (mGattServerClientCallback != null) {
            mHandler.post { mGattServerClientCallback.onCharacteristicRead(characteristic.uuid, value) }
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                         characteristic: BluetoothGattCharacteristic
    ) {
        super.onCharacteristicChanged(gatt, characteristic)
        val value = characteristic.value


        if (mGattServerClientCallback != null) {
            mHandler.post { mGattServerClientCallback.onCharacteristicRead(characteristic.uuid, value) }
        }
    }

    /*
     * Begin a scan for new servers that advertise our
     * matching service.
     */
    fun startScan() {
        //Scan for devices advertising our custom service
        val filters = ArrayList<ScanFilter>()
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(GattServerManager.DeviceProfile.SERVICE_UUID)).build())

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setReportDelay(500)
            .build()

        Log.i(TAG, "Scan 'bout to start...")
        BluetoothLeScannerCompat.getScanner().startScan(filters, settings, mScanCallback)

    }


    /*
     * Callback handles results from new devices that appear
     * during a scan. Batch results appear when scan delay
     * filters are enabled.
     */
    private val mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            processResult(result)
        }

        override fun onBatchScanResults(results: List<ScanResult>?) {
            for (result in results!!) {
                processResult(result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.i(TAG, "Scan Failed: $errorCode")
        }

        private fun processResult(result: ScanResult?) {
            var device = result!!.device
            val rssi = result!!.rssi
            val scanRecord = result!!.scanRecord

            if (mGattServerClientCallback != null) {
                mHandler.post { mGattServerClientCallback.onNewDeviceFound(device, rssi, scanRecord!!) }
            }
        }
    }

    /*
   * Terminate any active scans
   */
    fun stopScan() {
        BluetoothLeScannerCompat.getScanner().stopScan(mScanCallback)
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (isConnected()) {
            Log.i(TAG, "IN HERE")
            disconnect()
        }
        mConnectedGatt = device.connectGatt(context, false, this@GattClientManager)

        if (mGattServerClientCallback != null) {
            Log.i(TAG, "IN HERE")
            mHandler.post { mGattServerClientCallback.onDeviceDisconnected(device) }
        }
    }

    interface GattServerClientCallback {
        fun onNewDeviceFound(device: BluetoothDevice, rssi:Int, scanRecord: ScanRecord)
        fun onDeviceConnected(device: BluetoothDevice)
        fun onDeviceDisconnected(device: BluetoothDevice)
        fun onCharacteristicRead(uuid: UUID, bytes: ByteArray)
    }

    fun disconnect() {
        //Disconnect from any active connection
        if (mConnectedGatt != null) {
            try {
                mConnectedGatt!!.disconnect()
            } catch (e: Exception) {
                //ignored
            }

            mConnectedGatt = null
        }
    }

    fun isConnected(): Boolean {
        return mConnectedGatt != null
    }

    fun writeToCharacteristics(uuid: UUID, value: ByteArray) {
        if (mConnectedGatt != null) {
            val service = mConnectedGatt!!.getService(GattServerManager.DeviceProfile.SERVICE_UUID)
            if (service != null) {
                val characteristic = service.getCharacteristic(uuid)
                if (characteristic != null) {
                    characteristic.value = value
                    mConnectedGatt!!.writeCharacteristic(characteristic)
                }
            }
        }
    }

    fun readCharacteristic(uuid: UUID) {
        if (mConnectedGatt != null) {
            val service = mConnectedGatt!!.getService(GattServerManager.DeviceProfile.SERVICE_UUID)
            if (service != null) {
                val characteristic = service.getCharacteristic(uuid)
                if (characteristic != null) {
                    mConnectedGatt!!.readCharacteristic(characteristic)
                }
            }
        }
    }
    fun getCharacteristic(key:Int): BluetoothGattCharacteristic? {
        if (mConnectedGatt != null)
        {
            val service = mConnectedGatt!!.getService(GattServerManager.DeviceProfile.SERVICE_UUID)
            if (service != null)
            {
                if (key < service.characteristics.size)
                {
                    return service.characteristics[key]
                }
            }
        }
        return null
    }
}
