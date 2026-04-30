package com.example.plantasya_mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarouselAdapter(private val images: List<ByteArray?>, private val names: List<String>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.carousel_image)
        val textView: TextView = view.findViewById(R.id.carousel_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val imageBytes = images[position]
        if (imageBytes != null) {
            val bitmap = BitmapConverter.byteArrayToBitmap(imageBytes)
            holder.imageView.setImageBitmap(bitmap)
        } else {
            holder.imageView.setImageResource(R.drawable.background_tree) // Default placeholder
        }
        holder.textView.text = names[position]
    }

    override fun getItemCount(): Int = images.size
}
