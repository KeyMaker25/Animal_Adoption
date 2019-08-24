package bernat.oron.catadoption.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bernat.oron.catadoption.R
import bernat.oron.catadoption.model.AnimalsFactory
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage


class AnimalAdapterH(val items: ArrayList<AnimalsFactory>, val context: Context) : RecyclerView.Adapter<AnimalAdapterH.ViewHolder>()  {

    var onItemClick: ((AnimalsFactory) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_card_horizontal, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val images = items[p1].image
        val storageReference = FirebaseStorage.getInstance()
        if (images != null) {
            Log.i("image uri", "uri =  ${images.random()}")
            val image = storageReference.reference.child(images.random())
            Log.i("images ref", "$image")
            val ONE_MEGABYTE: Long = 1024 * 1024
            image.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                Glide.with(context)
                    .load(it)
                    .into(p0.itemImage)
            }.addOnFailureListener {
                Log.e("images from DB","failed to fetch")
            }
        }
        p0.itemName.text = items[p1].name
        p0.itemLocation.text = items[p1].location

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }


    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        // Holds the TextView that will add each animal to
        val itemImage: ImageView = view.findViewById(R.id.recycler_view_h_image_view)
        val itemName: TextView = view.findViewById(R.id.recycler_view_h_txt_name)
        val itemLocation: TextView = view.findViewById(R.id.recycler_view_h_txt_location)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
        }


    }
}

