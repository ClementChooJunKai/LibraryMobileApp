package com.example.team10mobileproject.Proximity

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor




class ProximitySensors(context: Context): AndroidSensor(context=context,
    sensorFeature = PackageManager.FEATURE_SENSOR_PROXIMITY,
    sensorType = Sensor.TYPE_PROXIMITY) {

}