package com.mtirado.tracker.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.wahoofitness.connector.HardwareConnector
import com.wahoofitness.connector.HardwareConnectorEnums
import com.wahoofitness.connector.HardwareConnectorTypes
import com.wahoofitness.connector.conn.connections.SensorConnection

class SensorListener: HardwareConnector.Listener {
    override fun onHardwareConnectorStateChanged(
        p0: HardwareConnectorTypes.NetworkType,
        p1: HardwareConnectorEnums.HardwareConnectorState
    ) {
        //TODO("Not yet implemented")
    }

    override fun onFirmwareUpdateRequired(p0: SensorConnection, p1: String, p2: String) {
        //TODO("Not yet implemented")
    }
}

class SensorService: Service() {
    private lateinit var hardwareConnector: HardwareConnector

    private val listener: HardwareConnector.Listener = SensorListener()

    override fun onCreate() {
        hardwareConnector = HardwareConnector(this, listener)
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        hardwareConnector.shutdown()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}