package pl.pilichm.savemaplocation.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pl.pilichm.savemaplocation.R
import pl.pilichm.savemaplocation.databinding.ActivityLocationMapBinding

class LocationMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        // Add a marker in Sydney and move the camera
        val location = LatLng(52.22, 21.01)
        mMap.addMarker(MarkerOptions().position(location).title("Marker in Sydney"))
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

    /**
     * After long press ask user if he wants to save selected location.
     * */
    override fun onMapLongClick(p0: LatLng) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(resources.getString(R.string.alert_dialog_message))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.alert_dialog_yes)
            ) { _, _ ->
                Toast.makeText (applicationContext, "Saving", Toast.LENGTH_SHORT).show()
            }.setNegativeButton(resources.getString(R.string.alert_dialog_no)){ _, _ ->
            }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.alert_dialog_save))
        alert.show()
    }
}