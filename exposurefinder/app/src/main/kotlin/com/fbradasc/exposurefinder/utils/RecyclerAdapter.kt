package com.fbradasc.exposurefinder.utils

import androidx.recyclerview.widget.RecyclerView
import android.widget.*
import android.view.*
import com.fbradasc.exposurefinder.R

class RecyclerAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val textView: TextView = view.findViewById(R.id.textView)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(viewGroup.context)
                                 .inflate(R.layout.recycler_item, viewGroup, false))

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val pos = position % dataSet.size
        viewHolder.textView.text = dataSet[pos]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = Int.MAX_VALUE // dataSet.size

}