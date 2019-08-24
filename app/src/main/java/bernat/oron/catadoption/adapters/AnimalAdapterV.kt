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
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import android.util.DisplayMetrics
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

class AnimalAdapterV(var items: ArrayList<AnimalsFactory>, val context: Context) : RecyclerView.Adapter<AnimalAdapterV.ViewHolder>()  {

    var onItemClick: ((AnimalsFactory) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_card_vertical, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val images =items[p1].image
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
        val m = items[p1].age.toDouble()
        var age: String
        if (m > 10){
            age = "גיל - "
            age += m.div(12).roundToInt().toString()
            age += " שנים "
        }else{
            age = "גיל - "
            age += m.roundToInt().toString()
            age += " חודשים"
        }

        p0.itemAge.text = age
        p0.itemGender.text = items[p1].gender
        p0.itemLocation.text = items[p1].location
        p0.itemName.text = items[p1].name
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(list :ArrayList<AnimalsFactory>){
        this.items = list
        this.notifyDataSetChanged()
    }





    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        // Holds the TextView that will add each animal to
        val itemImage: ImageView = view.findViewById(R.id.recycler_view_v_image_view)
        val itemName: TextView = view.findViewById(R.id.recycler_view_v_txt_name)
        val itemAge: TextView = view.findViewById(R.id.recycler_view_v_txt_age)
        val itemGender: TextView = view.findViewById(R.id.recycler_view_v_txt_gender)
        val itemLocation: TextView = view.findViewById(R.id.recycler_view_v_txt_location)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
        }



    }
}

