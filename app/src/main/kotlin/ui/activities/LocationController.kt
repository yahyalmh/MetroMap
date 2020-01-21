package ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

/**
* Created by yaya-mh on 23/07/2018 01:32 PM.
*/
class LocationController {
    private var locationManager : LocationManager = LaunchActivity.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private  var started : Boolean = false
     var lastKnownLocation: Location? = null
    private val gpsLocationListener = GpsLocationListener()
    private val networkLocationListener = GpsLocationListener()
    private val passiveLocationListener = GpsLocationListener()


     companion object {
         private var locationController: LocationController? = null

         fun getInstance(): LocationController? {
             var localInstance = locationController
             if (localInstance == null) {
                 synchronized(LocationController::class.java) {
                     localInstance = locationController
                     if (localInstance == null) {
                         localInstance = LocationController()
                         locationController = localInstance
                     }
                 }
             }
             return localInstance
         }
     }


    init {
        start()
    }

    private inner class GpsLocationListener : LocationListener {

        override fun onLocationChanged(location: Location?) {
            if (location == null) {
                return
            }
            if (lastKnownLocation != null && (this === networkLocationListener || this === passiveLocationListener)) {
                if (!started && location.distanceTo(lastKnownLocation ) > 20) {
                    lastKnownLocation = location
                }
            } else {
                lastKnownLocation = location
            }
//            support.NotificationCenter.getInstance()!!.postNotificationName(support.NotificationCenter.liveLocationsChanged)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        if (started) {
            return
        }
        var lastLocationStartTime = System.currentTimeMillis()
        started = true
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0f, gpsLocationListener)
        } catch (e: Exception) {

        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0f, networkLocationListener)
        } catch (e: Exception) {

        }

        try {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1, 0f, passiveLocationListener)
        } catch (e: Exception) {

        }

        if (lastKnownLocation == null) {
            try {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            } catch (e: Exception) {

            }

        }
    }


    fun stop(empty: Boolean) {
        started = false
        locationManager.removeUpdates(gpsLocationListener)
        if (empty) {
            locationManager.removeUpdates(networkLocationListener)
            locationManager.removeUpdates(passiveLocationListener)
        }
    }

    fun locationMap(value:Double, firstRangeStart : Double, firstRangeEnd: Double, secondRangeStart: Float, secondRangeEnd: Float): Double{
        val result = (value - firstRangeStart) *(secondRangeEnd - secondRangeStart) /(firstRangeEnd - firstRangeStart) + secondRangeStart
        return result
    }
}