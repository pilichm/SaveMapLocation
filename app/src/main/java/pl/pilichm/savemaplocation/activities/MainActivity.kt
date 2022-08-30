package pl.pilichm.savemaplocation.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
addLocationRequestimport kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.pilichm.savemaplocation.R
import pl.pilichm.savemaplocation.databinding.ActivityMainBinding
import pl.pilichm.savemaplocation.models.Location
import pl.pilichm.savemaplocation.recyclerviews.LocationAdapter
import pl.pilichm.savemaplocation.util.Constants
import pl.pilichm.savemaplocation.util.Constants.Companion.EMPTY_LOCATION_DATA
import pl.pilichm.savemaplocation.util.SwipeToDeleteCallback
import pl.pilichm.savemaplocation.util.Utils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mLocations: ArrayList<Location> = ArrayList()
    private var mLocationAdapter: LocationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUIAndListeners()
    }

    /**
     * Set up main activity recycler view with listeners for:
     * handling item deletion on swipe left,
     * opening map activity with location showed with pin.
     * --------------------------------------------------------
     * Listener to floating action button for opening map activity.
     * */
    private fun setUpUIAndListeners(){
        /**
         * Check if shared preferences contain saved locations data. If no, then use mock values.
         * */
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(SHARED_PREFERENCES_DATA_KEY)) {
            val locationsCount = sharedPreferences.getInt(SHARED_PREFERENCES_DATA_KEY, 0)
            if (locationsCount==0){
                mLocations = Utils.getMockLocationData()
            } else {
                mLocations = ArrayList()
                for (num in 0..locationsCount){
                    val locationId = "$SHARED_PREFERENCES_DATA_KEY $num"
                    val data = sharedPreferences.getString(locationId, Utils.getEmptyLocationString())
                    val location = Json.decodeFromString<Location>(data!!)
                    if (location.locationName!=EMPTY_LOCATION_DATA) {
                        mLocations.add(location)
                    }
                }
            }
        } else {
            mLocations = Utils.getMockLocationData()
        }

        mLocationAdapter = LocationAdapter(applicationContext, mLocations)

        /**
         * Start map activity with coordinates from list.
         * */
        mLocationAdapter!!.setOnClickListener(object : LocationAdapter.OnClickListener {
            override fun onClick(position: Int, item: Location){
                val intent = Intent(applicationContext, LocationMapActivity::class.java)
                val location = mLocations[position]
                intent.putExtra(Constants.KEY_LATITUDE, location.latitude)
                intent.putExtra(Constants.KEY_LONGITUDE, location.longitude)
                intent.putExtra(Constants.KEY_LOCATION, location.locationName)
                startActivityForResult(intent, Constants.SECOND_ACTIVITY_ID)
            }
        })
        binding.rvLocations.adapter = mLocationAdapter
        binding.rvLocations.layoutManager = LinearLayoutManager(applicationContext)

        /**
         * Swipe helper for deleting items - on swipe left.
         */
        val deleteSwipeHandler = object: SwipeToDeleteCallback(applicationContext){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvLocations.adapter as LocationAdapter
                adapter.notifyDeleteItem(
                    this@MainActivity, viewHolder.adapterPosition)
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvLocations)

        /**
         * Start map activity without coordinates.
         * */
        binding.fabAddLocation.setOnClickListener {
            val intent = Intent(applicationContext, LocationMapActivity::class.java)
            startActivityForResult(intent, Constants.SECOND_ACTIVITY_ID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==Constants.SECOND_ACTIVITY_ID){
            if (resultCode==Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.KEY_LOCATION)) {
                    val locationName = data.getStringExtra(Constants.KEY_LOCATION)
                    val long = data.getStringExtra(Constants.KEY_LONGITUDE)
                    val lat = data.getStringExtra(Constants.KEY_LATITUDE)

                    if (locationName != null && long != null && lat != null) {
                        mLocations.add(Location(locationName, long, lat))
                        mLocationAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    /**
     * Save locations when app stops.
     * */
    override fun onStop() {
        super.onStop()
        val locationsCount = mLocations.size
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()
        editor.putInt(SHARED_PREFERENCES_DATA_KEY, locationsCount)
        for (num in 0 until locationsCount){
            val locationStr = Json.encodeToString(mLocations[num])
            val locationId = "$SHARED_PREFERENCES_DATA_KEY $num"
            if (mLocations[num].locationName!=EMPTY_LOCATION_DATA){
                editor.putString(locationId, locationStr)
            }
        }

        editor.apply()
    }

    /**
     * Constants for saving and reading locations data from shared preferences.
     * */
    companion object {
        const val SHARED_PREFERENCES_KEY = "saved_location_data"
        const val SHARED_PREFERENCES_DATA_KEY = "saved_locations"
    }
}