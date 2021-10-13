package pl.pilichm.savemaplocation.recyclerviews

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.location_item.view.*
import pl.pilichm.savemaplocation.R
import pl.pilichm.savemaplocation.models.Location

class LocationAdapter (
    private val context: Context,
    private var locations: ArrayList<Location>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.location_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val location = locations[position]

        if (holder is LocationViewHolder){
            holder.itemView.tvLocationItemName.text = location.locationName
            holder.itemView.tvLongitude.text = location.longitude
            holder.itemView.tvLatitude.text = location.latitude
            holder.itemView.setOnClickListener {
                if (onClickListener!=null){
                    onClickListener!!.onClick(position, location)
                }
            }
        }
    }

    fun notifyDeleteItem(activity: Activity, position: Int) {
        locations.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    interface OnClickListener {
        fun onClick(position: Int, item: Location)
    }

    private class LocationViewHolder(view: View): RecyclerView.ViewHolder(view)
}