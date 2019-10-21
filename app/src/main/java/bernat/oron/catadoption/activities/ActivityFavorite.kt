package bernat.oron.catadoption.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bernat.oron.catadoption.adapters.AdapterHAnimal
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.ActivitySplash.Companion.favoriteAnimalCollectionID
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uploadAnimalCollectionID
import bernat.oron.catadoption.model.Animal
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class ActivityFavorite: AppCompatActivity() {

    lateinit var btnFloating: FloatingActionButton
    lateinit var recFavorite: RecyclerView
    lateinit var recMyAnimal: RecyclerView
    lateinit var titleMyFavorite: TextView
    lateinit var titleMyUploads: TextView
    lateinit var frameLayout: FrameLayout

    companion object{
        var uniqueID = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        btnFloating = findViewById(R.id.btn_add_floating)
        initFloatingBtn()

        recFavorite = findViewById(R.id.recycler_view_favorite)
        recMyAnimal = findViewById(R.id.recycler_view_favorite_my_animal)
        initRecyclerView()

        titleMyFavorite = findViewById(R.id.title_my_favorite)
        titleMyUploads = findViewById(R.id.title_favorite_upload)
        frameLayout = findViewById(R.id.frame_container)
        checkRecyclerContent()
    }

    private fun checkRecyclerContent(){
        val v = layoutInflater.inflate(R.layout.activity_favorite_blank,null)
        val l1 = v.findViewById<LinearLayout>(R.id.layout_top)
        if ((recFavorite.adapter as AdapterHAnimal).items.isEmpty() &&
            (recMyAnimal.adapter as AdapterHAnimal).items.isEmpty())
        {
            val contactUs = v.findViewById<TextView>(R.id.click_here_contact)
            contactUs.setOnClickListener {
                val intent = Intent(this,ActivityContactUs::class.java)
                startActivity(intent)
            }
            titleMyFavorite.visibility = View.GONE
            titleMyUploads.visibility = View.GONE
        }else if ((recFavorite.adapter as AdapterHAnimal).items.isEmpty()) {
            titleMyFavorite.visibility = View.GONE
            l1.visibility = View.INVISIBLE
        }
        else if ((recMyAnimal.adapter as AdapterHAnimal).items.isEmpty()){
            titleMyUploads.visibility = View.GONE
            l1.visibility = View.INVISIBLE
        }else {
            frameLayout.removeAllViews()
            titleMyFavorite.visibility = View.VISIBLE
            titleMyUploads.visibility = View.VISIBLE
            return
        }
        frameLayout.addView(v)

    }

    override fun onResume() {
        recFavorite.adapter?.notifyDataSetChanged()
        checkRecyclerContent()
        super.onResume()
    }

    private fun initFloatingBtn() {
        btnFloating.setOnClickListener {
            uniqueID = UUID.randomUUID().toString().replace("-","")
            startActivity(Intent(applicationContext, ActivityUploadAnimal::class.java))
        }
    }

    private fun initRecyclerView(){
        recFavorite.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recMyAnimal.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        val adapterFavorite = AdapterHAnimal(
            favoriteAnimalCollectionID,
            this
        )
        val adapterUploads = AdapterHAnimal(
            uploadAnimalCollectionID,
            this
        )

        val onClick: ((Animal) -> Unit)? = {
                item ->
            val i = Intent(this, ActivityAnimalPage::class.java)
            i.putExtra("animal", item)
            startActivity(i)
        }

        adapterFavorite.onItemClick = onClick
        adapterUploads.onItemClick = onClick
        recFavorite.adapter = adapterFavorite
        recMyAnimal.adapter = adapterUploads

    }

}