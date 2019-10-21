package bernat.oron.catadoption.activities

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import bernat.oron.catadoption.R
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import bernat.oron.catadoption.activities.ActivitySplash.Companion.animalCollection
import bernat.oron.catadoption.activities.ActivitySplash.Companion.favoriteAnimalCollectionID
import bernat.oron.catadoption.activities.ActivitySplash.Companion.isUserLogin
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uid
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uploadAnimalCollection
import bernat.oron.catadoption.adapters.AdapterSlideImage
import bernat.oron.catadoption.model.Animal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.viewpagerindicator.CirclePageIndicator
import java.util.*
import kotlin.collections.ArrayList

class ActivityAnimalPage : AppCompatActivity(), View.OnClickListener{

    private var mPager: ViewPager? = null
    private var currentPage = 0
    private var NUM_PAGES = 1
    private var isLiked: Boolean = false
    companion object{
        var stringImages: ArrayList<String>? = null
    }
    var animal : Animal? = null
    private lateinit var btnAddFavorite : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_page)
        animal = intent.getSerializableExtra("animal") as Animal
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

    private fun initMiddleBottom(item: Animal){

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
        val btnShare : Button = findViewById(R.id.animal_page_btn_share)


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
        btnShare.setOnClickListener(this)

        if (!animalCollection.contains(item)){
            showAlert("לא במאגר", "עדיין לא אושרה", null)
        }

        if (uid == item.ownerID){
            val btnRemove = findViewById<Button>(R.id.animal_page_btn_remove)
            btnRemove.visibility = View.VISIBLE
            btnRemove.setOnClickListener {
                showAlert("אתה בטוח ? ", "${item.name} ימחק ",DialogInterface.OnClickListener
                { dialog, which ->
                    if (which == -1){
                        dialog.dismiss()
                    }else if (which == -2){
                        deleteAnimalFromDB(item)
                    }
                })
            }
        }

    }

    private fun deleteAnimalFromDB(item: Animal) {
        val ref = FirebaseDatabase.getInstance()
        /**
         * Delete the animal from all locations in DB
         * 1 - tst-upload animal
         * 2 - tst-upload list
         * 3 - images from storage
         * 4 - ? prod - upload animal,
         * 5 - ? like ? - prod
         * **/

        //stage 1
        ref.reference.child("Israel-tst/animals/${item.ID}").removeValue()
            .addOnSuccessListener {
                Log.i("remove","successfully ${item.name}")
                //stage 2
                ref.reference.child("Israel-tst/users/${uid}/uploads/${item.ID}").removeValue()
                    .addOnSuccessListener {
                        Log.i("remove","successfully ${item.name}")
                        //stage 3
                        for (image in item.image!!){
                            val storageRef = FirebaseStorage.getInstance().reference.child(image)
                            storageRef.delete()
                                .addOnSuccessListener {
                                    Log.i("remove","successfully ${item.name}")
                                }
                                .addOnFailureListener {
                                    Log.e("remove","error with ${item.name}")
                                    Log.e("remove",it.toString())
                                    showAlert("ERROR","בעיות חיבור לאינטרנט",null)
                                }
                        }
                        //stage 4
                        if (animalCollection.contains(item)){
                            ref.reference.child("Israel-prod/animals/${item.ID}").removeValue()
                                .addOnSuccessListener {
                                    //stage 5
                                    removeAnimalFromLikedList(item)
                                    showAlert("מחיקה הושלמה","${item.name} לא יופיע יותר במאגר ",null)
                                    startActivity(Intent(this,ActivitySplash::class.java))
                                }
                                .addOnFailureListener {
                                    Log.e("remove","error with ${item.name}")
                                    Log.e("remove",it.toString())
                                    showAlert("ERROR","בעיות חיבור לאינטרנט",null)
                                }
                        }
                    }.addOnFailureListener {
                        Log.e("remove","error with ${item.name}")
                        Log.e("remove",it.toString())
                        showAlert("ERROR","בעיות חיבור לאינטרנט",null)
                    }
            }
            .addOnFailureListener {
                Log.e("remove","error with ${item.name}")
                Log.e("remove",it.toString())
                showAlert("ERROR","בעיות חיבור לאינטרנט",null)
            }

    }

    private fun removeAnimalFromLikedList(item: Animal) {
        val ref = FirebaseDatabase.getInstance()
        ref.reference.child("Israel-tst/users/").
            addListenerForSingleValueEvent( object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val map = p0.value as? MutableMap<String, Any>
                    map?.let {
                        for (i in it){
                            val temp = i.value as MutableMap<String, Any>
                            var count = 0
                            val favoriteMap = temp["favorite"] as MutableMap<String,String>
                            for (f in favoriteMap){
                                if (f.key == item.ID){
                                    ref.reference.child("Israel-tst/users/${i.key}/favorite/${item.ID}")
                                        .removeValue()
                                        .addOnSuccessListener {
                                            Log.i("removed","Successfully ${item.type} ${item.name} from user ID = ${i.key}")
                                        }
                                        .addOnFailureListener { exp ->
                                            Log.e("remove","like from users Error with $exp")
                                            showAlert("ERROR","בעיות חיבור לאינטרנט",null)
                                        }
                                    count++
                                }
                            }
                            Log.i("removed","removed $count liked")
                        }
                    }

                }

                override fun onCancelled(p0: DatabaseError) {
                    showAlert("ERROR","בעיות חיבור לאינטרנט",null)
                }
            })

    }

    private fun initBtnLike() {
        //check id user saved this animal
        if (favoriteAnimalCollectionID.contains(animal!!.ID)){
            //this animal was liked before set btn
            Log.i("liked", "name = ${animal!!.name} ID = ${animal!!.ID}")
            isLiked = true
            btnAddFavorite.background = ContextCompat.getDrawable(this,R.drawable.btn_like_star_full)
        }
    }

    private fun initImages() {
        mPager = findViewById(R.id.pager)
        val adapter = AdapterSlideImage(this, stringImages!!)
        adapter.onItemClick = {
                _->
            val intent = Intent(applicationContext,ActivityFullScreen::class.java)
            intent.putExtra("images", stringImages)
            startActivity(intent)
        }
        mPager?.adapter = adapter
        val indicator = findViewById<CirclePageIndicator>(R.id.indicator)
        indicator.setViewPager(mPager)
        val density = resources.displayMetrics.density
        //Set circle indicator radius
        indicator.radius = 5 * density
        // change the picture every 2.5 second
        //switchWithDelay(2000, 2500)
    }

    private fun switchWithDelay(delay: Long, peroid: Long) {
        NUM_PAGES = stringImages!!.count()
        val handler = Handler()
        val run = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            mPager!!.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(run)
            }
        }, 2000, 2500)

        // Pager listener over indicator
        findViewById<CirclePageIndicator>(R.id.indicator).setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
                if (isUserLogin()) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${animal!!.phone}")
                    startActivity(intent)
                } else {
                    showAlert("סליחה","צריך להרשם לפני יצירת קשר",DialogInterface.OnClickListener {
                        dialog, which ->
                        if(which == -2) sendUserToLogin()
                        dialog.dismiss()
                    })
                }
            }
            R.id.animal_page_btn_add_favorite->{
                if (isUserLogin()){
                    if (uploadAnimalCollection.firstOrNull { it.ID == animal?.ID } == null){
                        if (isLiked) unLike(true)
                        else like()
                    } else {
                        showAlert("לא ניתן לבצע לייק ","עדיין ממתין לאישור", null)
                    }
                } else {
                    sendUserToLogin()
                }

            }
            R.id.animal_page_btn_contact->{
                if (isUserLogin()){
                    val i = Intent(this, ActivityContactUs::class.java)
                    i.putExtra("from","animal page")
                    startActivity(i)
                } else {
                    sendUserToLogin()
                }
            }
            R.id.animal_page_btn_share->{
                if (uploadAnimalCollection.firstOrNull { it.ID == animal?.ID } == null){
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = """
                    hey im ${animal?.name} and im ${animal?.age} old
                    of type ${animal?.breed} and im usually here -  ${animal?.location}
                    Do you wish to adopt me ? 
                """.trimIndent()
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
                } else {
                    showAlert("לא ניתן לשתף ","עדיין ממתין לאישור", null)
                }
            }
        }
    }

    private fun like() {
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
                    favoriteAnimalCollectionID.add(obj.ID)
                    isLiked = !isLiked
                    btnAddFavorite.background = ContextCompat.getDrawable(this,R.drawable.btn_like_star_full)
                    Log.i("Upload to DB favorite", "Successful")
                    showAlert("נשמר","במועדפים בהצלחה",null)
                }else{
                    Log.e("Upload to DB favorite", "Failed")

                }
            }
    }

    private fun unLike(fromUser: Boolean) {
        val obj = animal!!
        Log.i("UnLike animal id", obj.ID)
        val ref = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        ref.reference.child("Israel-tst/users/$uid/favorite/${obj.ID}")
            .removeValue()
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    isLiked = !isLiked
                    btnAddFavorite.background = ContextCompat.getDrawable(this,R.drawable.btn_like_star_empty)
                    Log.i("Removed from DB", "Successful")
                    favoriteAnimalCollectionID.remove(obj.ID)
                    if (fromUser) showAlert("הוסר", "לא יופיע יותר במועדפים",null)
                }else{
                    Log.e("Removed from DB", "Failed")

                }
            }

    }

    private fun sendUserToLogin(){
        val i = Intent(this,ActivityMain::class.java)
        i.putExtra("Login","user need to log in")
        startActivity(i)
        finish()
    }

    private fun showAlert(title: String, msg: String, listener: DialogInterface.OnClickListener?){
        val alert = AlertDialog.Builder(this).create()
        alert.setTitle(title)
        alert.setMessage(msg)
        if (listener == null)
        {
            alert.setButton(AlertDialog.BUTTON_NEUTRAL, "הבנתי") {
                    _,_->
                alert.dismiss()
            }
        }else{
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "אוקי", listener)
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "בטל" , listener)
        }
        alert.show()
    }

}