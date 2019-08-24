package bernat.oron.catadoption.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import bernat.oron.catadoption.R
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import bernat.oron.catadoption.activities.ActivitySplash.Companion.favoriteAnimalCollection
import bernat.oron.catadoption.activities.ActivitySplash.Companion.isUserLogin
import bernat.oron.catadoption.adapters.AdapterSlideImage
import bernat.oron.catadoption.model.AnimalsFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.viewpagerindicator.CirclePageIndicator
import java.util.*
import kotlin.collections.ArrayList


class ActivityAnimalPage : AppCompatActivity(), View.OnClickListener{

    private var mPager: ViewPager? = null
    private var currentPage = 0
    private var NUM_PAGES = 1
    var stringImages: ArrayList<String>? = null
    var animal : AnimalsFactory? = null
    private lateinit var btnAddFavorite : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_page)
        animal = intent.getSerializableExtra("animal") as AnimalsFactory
        if (animal!!.image != null){
            stringImages = ArrayList()
            for (item in animal!!.image!!){
                stringImages!!.add(item)
            }
            initImages()
            Log.i("images count","${stringImages!!.count()}")
        }else{
            Log.i("no images","for animal ${animal!!.ID}")
        }
        if(animal != null) initMiddleBottom(animal!!)

    }


    private fun initMiddleBottom(item: AnimalsFactory){

        val txtName : TextView = findViewById(R.id.animal_txt_page_name)
        btnAddFavorite = findViewById(R.id.animal_page_btn_add_favorite)
        val txtAge : TextView = findViewById(R.id.animal_txt_page_age)
        val txtGender : TextView = findViewById(R.id.animal_txt_page_gender)
        val txtWeight : TextView = findViewById(R.id.animal_txt_page_weight)
        val txtStory : TextView = findViewById(R.id.animal_txt_page_story)
        val txtUploaded : TextView = findViewById(R.id.animal_txt_page_time_uploaded)
        val txtCity : TextView = findViewById(R.id.animal_txt_page_location)
        val btnCall : Button = findViewById(R.id.animal_page_btn_call)
        val btnContact : Button = findViewById(R.id.animal_page_btn_contact)

        txtName.text = item.name
        txtAge.text = item.age.toString()
        txtGender.text = item.gender
        txtWeight.text = item.weight.toString()
        txtStory.text = item.story
        txtUploaded.text = item.timeOfUpload
        txtCity.text = item.location

        btnAddFavorite.setOnClickListener(this)
        initBtnLike()
        btnCall.setOnClickListener(this)
        btnContact.setOnClickListener(this)

    }

    private fun initBtnLike() {
        //check id user saved this animal
        if (favoriteAnimalCollection.contains(animal!!.ID)){
            //this animal was liked before set btn
            Log.i("liked", "name = ${animal!!.name} ID = ${animal!!.ID}")
            btnAddFavorite.background = ContextCompat.getDrawable(this,R.mipmap.ic_start_full)
        }
    }

    private fun initImages() {
        mPager = findViewById<ViewPager>(R.id.pager)

        mPager?.adapter = AdapterSlideImage(this, stringImages!!)
        val indicator = findViewById<CirclePageIndicator>(R.id.indicator)
        indicator.setViewPager(mPager)
        val density = resources.displayMetrics.density
        //Set circle indicator radius
        indicator.radius = 5 * density

        NUM_PAGES = stringImages!!.count()
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            mPager!!.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 2500, 2500)

        // Pager listener over indicator
        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageScrollStateChanged(pos: Int) {
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.animal_page_btn_call->{
                //open phone dialer, with owner number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${animal!!.phone}")
                startActivity(intent)
            }
            R.id.animal_page_btn_add_favorite->{
                if (isUserLogin()){
                    val obj = animal!!
                    Log.i("like animal id", obj.ID)
                    val ref = FirebaseDatabase.getInstance()
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    val map2 = mutableMapOf<String, String>()
                    map2[obj.ID] = obj.type
                    ref.reference.child("Israel-tst/users/$uid/favorite/")
                        .updateChildren(map2 as Map<String, Any>)
                        .addOnCompleteListener {
                                task ->
                            if (task.isSuccessful){
                                btnAddFavorite.background = ContextCompat.getDrawable(this,R.mipmap.ic_start_full)
                                Log.i("Upload to DB favorite", "Successful")
                                showAlert("נשמר","במועדפים בהצלחה")
                            }else{
                                Log.e("Upload to DB favorite", "Failed")

                            }
                        }
                }else{
                    val i = Intent(this,ActivityMain::class.java)
                    i.putExtra("Login","user need to log in")
                    startActivity(i)
                    finish()
                }

            }
            R.id.animal_page_btn_contact->{
                //send to contact us page.

            }
        }
    }

    fun showAlert(title: String, msg: String){
        val alert = AlertDialog.Builder(this).create()
        alert.setTitle(title)
        alert.setMessage(msg)
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok") {
            _,_->
            alert.dismiss()
        }
        alert.show()
    }

}