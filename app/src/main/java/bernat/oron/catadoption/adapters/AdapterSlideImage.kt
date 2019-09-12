package bernat.oron.catadoption.adapters

import android.os.Parcelable
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import bernat.oron.catadoption.R
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage


class AdapterSlideImage(val context: Context, val imageModelArrayList: ArrayList<String>): PagerAdapter(){

    var inflater: LayoutInflater? = LayoutInflater.from(context)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return imageModelArrayList.count()
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater?.inflate(R.layout.activity_animal_page_sliding_images, view, false)!!
        val imageView = imageLayout.findViewById(R.id.image) as ImageView

        val stringImage: String? = imageModelArrayList[position]
        val storageReference = FirebaseStorage.getInstance()
        if (stringImage != null) {
            val ref = storageReference.reference.child(stringImage)
            val ONE_MEGABYTE: Long = 1024 * 1024
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                Glide.with(context.applicationContext)
                    .load(it)
                    .into(imageView)
                view.addView(imageLayout, 0)
            }.addOnFailureListener {
                Log.e("images from DB","failed to fetch")
            }
        }
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}