package pl.pilichm.savemaplocation.activities

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pl.pilichm.savemaplocation.R
import pl.pilichm.savemaplocation.databinding.ActivityLocationMapBinding
import pl.pilichm.savemaplocation.models.Location
import pl.pilichm.savemaplocation.util.Constants
import java.util.*


class LocationMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationMapBinding
    private var passedLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.KEY_LOCATION)){
            val locationName = intent.getStringExtra(Constants.KEY_LOCATION)
            val locationLat = intent.getStringExtra(Constants.KEY_LATITUDE)
            val locationLog = intent.getStringExtra(Constants.KEY_LONGITUDE)
            passedLocation = Location(locationName!!, locationLog!!, locationLat!!)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setUpLocationServices()
    }

    /**
     * Set up location service listeners with high accuracy.
     * */
    private fun setUpLocationServices(){
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = UPDATE_INTERVAL

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(applicationContext)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        /**
         * Check for permissions and ask if they aren't granted.
         * */
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        /**
         * Set up function called every update interval.
         * */
        getFusedLocationProviderClient(applicationContext)
            .requestLocationUpdates(mLocationRequest!!, object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    onLocationChanged(locationResult.lastLocation)
                }
            }, Looper.myLooper())
    }

    /**
     * Refreshes users current position.
     * */
    private fun onLocationChanged(location: android.location.Location){
        if (mLocationRequest==null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ), 12.0f
                ))
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this);

        if (passedLocation!=null){
            val location = LatLng(
                passedLocation!!.longitude.toDouble(),
                passedLocation!!.latitude.toDouble())

            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ), 12.0f
                )
            )
        }
    }

    /**
     * After long press ask user if he wants to save selected location.
     * */
    override fun onMapLongClick(position: LatLng) {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1)

        val locationName = if (addresses.size>0) {
            addresses[0].locality
        } else {
            resources.getString(R.string.location_unknown)
        }

        mMap.addMarker(MarkerOptions().position(position)).title = locationName

        /**
         * Ask user if he wants to save selected location.
         * */
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(resources.getString(R.string.alert_dialog_message))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.alert_dialog_yes)
            ) { _, _ ->
                val intent = Intent()
                intent.putExtra(Constants.KEY_LONGITUDE, "%.2f".format(position.longitude))
                intent.putExtra(Constants.KEY_LATITUDE, "%.2f".format(position.latitude))
                intent.putExtra(Constants.KEY_LOCATION, locationName)
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.location_saved),
                    Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK, intent);
                finish()
            }.setNegativeButton(resources.getString(R.string.alert_dialog_no)){ _, _ ->
            }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.alert_dialog_save))
        alert.show()
    }

    /**
     * If requested permissions are granted, then sets up location services.
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== LOCATION_PERMISSION_REQUEST_CODE
            && permissions.isNotEmpty()
        ){
            setUpLocationServices()
        }
    }

    companion object {
        const val UPDATE_INTERVAL = 1000L
        const val LOCATION_PERMISSION_REQUEST_CODE = 0
    }
}