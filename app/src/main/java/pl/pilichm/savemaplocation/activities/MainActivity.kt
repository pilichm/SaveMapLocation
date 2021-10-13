package pl.pilichm.savemaplocation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import pl.pilichm.savemaplocation.R
import pl.pilichm.savemaplocation.models.Location
import pl.pilichm.savemaplocation.recyclerviews.LocationAdapter
import pl.pilichm.savemaplocation.util.SwipeToDeleteCallback
import pl.pilichm.savemaplocation.util.Utils

class MainActivity : AppCompatActivity() {
    private var mLocations: ArrayList<Location> = ArrayList()
    private var mLocationAdapter: LocationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        mLocations = Utils.getMockLocationData()
        mLocationAdapter = LocationAdapter(applicationContext, mLocations)

        /**
         * Start map activity with coordinates from list.
         * */
        mLocationAdapter!!.setOnClickListener(object : LocationAdapter.OnClickListener {
            override fun onClick(position: Int, item: Location){
                val intent = Intent(applicationContext, LocationMapActivity::class.java)
                startActivity(intent)
            }
        })
        rvLocations.adapter = mLocationAdapter
        rvLocations.layoutManager = LinearLayoutManager(applicationContext)

        /**
         * Swipe helper for deleting items - on swipe left.
         */
        val deleteSwipeHandler = object: SwipeToDeleteCallback(applicationContext){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvLocations.adapter as LocationAdapter
                adapter.notifyDeleteItem(
                    this@MainActivity, viewHolder.adapterPosition)
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rvLocations)

        /**
         * Start map activity without coordinates.
         * */
        fabAddLocation.setOnClickListener {
            val intent = Intent(applicationContext, LocationMapActivity::class.java)
            startActivity(intent)
        }
    }
}