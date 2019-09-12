package bernat.oron.catadoption.activities


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bernat.oron.catadoption.adapters.AdapterHAnimal
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.ActivitySplash.Companion.favoriteAnimalCollectionID
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uid
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uploadAnimalCollectionID
import bernat.oron.catadoption.fragments.FragmentUpload
import bernat.oron.catadoption.fragments.FragmentUpload.Companion.PICK_IMAGE_REQUEST_1
import bernat.oron.catadoption.fragments.FragmentUpload.Companion.PICK_IMAGE_REQUEST_2
import bernat.oron.catadoption.fragments.FragmentUpload.Companion.PICK_IMAGE_REQUEST_3
import bernat.oron.catadoption.model.UploadNewAnimalInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_favorite.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class ActivityFavorite: AppCompatActivity() , UploadNewAnimalInterface {

    lateinit var btnFloating: FloatingActionButton
    lateinit var recFavorite: RecyclerView
    lateinit var recMyAnimal: RecyclerView
    lateinit var fragUpload: FragmentUpload
    lateinit var titleMyFavorite: TextView
    lateinit var titleMyUploads: TextView
    lateinit var frameLayout: FrameLayout

    private var filePath: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val dataBase = FirebaseDatabase.getInstance()
    private var arrayOfImages = arrayListOf<Bitmap>()

    private var progressDialog: ProgressDialog? = null

    companion object{
        var uniqueID = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        progressDialog = ProgressDialog(
            baseContext,
            R.style.AppTheme
        )
        progressDialog?.isIndeterminate = true
        progressDialog?.setMessage("מעלה למאגר")

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
        if ((recFavorite.adapter as AdapterHAnimal).items.isEmpty() &&
            (recMyAnimal.adapter as AdapterHAnimal).items.isEmpty()){
            titleMyFavorite.visibility = View.GONE
            titleMyUploads.visibility = View.GONE
        }else if ((recFavorite.adapter as AdapterHAnimal).items.isEmpty()) {
            titleMyFavorite.visibility = View.GONE
            val l1 = v.findViewById<LinearLayout>(R.id.layout_top)
            l1.visibility = View.INVISIBLE
        }
        else if ((recMyAnimal.adapter as AdapterHAnimal).items.isEmpty()){
            titleMyUploads.visibility = View.GONE
            val l1 = v.findViewById<LinearLayout>(R.id.layout_top)
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
        fragUpload = FragmentUpload()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_fav, fragUpload)
            .hide(fragUpload).commit()
        //this is the btn for uploading (after register) a new animal for adoption
        btnFloating.setOnClickListener {

            fragUpload.event = this
            titleMyFavorite.text = "הוספת בעל חיים לאימוץ"

            supportFragmentManager.beginTransaction()
                .show(fragUpload).commit()
            //init id for uploaded(?) animal
            uniqueID = UUID.randomUUID().toString().replace("-","")
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

        val onClick: ((bernat.oron.catadoption.model.Animal) -> Unit)? = {
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

    override fun onBackPressed() {
        if (fragUpload.isHidden){
            super.onBackPressed()
        }else{
            supportFragmentManager.beginTransaction().hide(fragUpload).commit()
            titleMyFavorite.text = "מועדפים"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("favorite on result","$requestCode got image from user ${data?.data}")
        if (requestCode == PICK_IMAGE_REQUEST_1 || requestCode == PICK_IMAGE_REQUEST_2 || requestCode == PICK_IMAGE_REQUEST_3) {
            if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                filePath = data.data
                try {
                    //got image as bitmap (can upload now)
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    changeFragUploadView(bitmap, requestCode)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                println("result ok? ${Activity.RESULT_OK == requestCode}")
                println("$requestCode NOT PICK_IMAGE_REQUEST")
            }
        }
    }

    private fun changeFragUploadView(image: Bitmap, resCode: Int){
        if (!arrayOfImages.contains(image)){
            arrayOfImages.add(image)
        }
        Log.i("add","images to array count = ${arrayOfImages.count()}")
        when(resCode){
            PICK_IMAGE_REQUEST_1 ->{
                fragUpload.image1.setImageBitmap(image)
                fragUpload.image1.background = null
            }
            PICK_IMAGE_REQUEST_2 ->{
                fragUpload.image2.setImageBitmap(image)
                fragUpload.image2.background = null
            }
            PICK_IMAGE_REQUEST_3 ->{
                fragUpload.image3.setImageBitmap(image)
                fragUpload.image3.background = null
            }
        }
    }

    private fun uploadImageToFireBase(animal: bernat.oron.catadoption.model.Animal) {
        var count = 0
        animal.image = mutableListOf()

        val ref = storage.reference
        //number of images to upload
        for (item in arrayOfImages){
            val path = "Israel/${uniqueID}image$count"
            val baos = ByteArrayOutputStream()
            val itemBitMap = arrayOfImages.random()
            itemBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = ref.child(path).putBytes(data)
            //we convert the bitmap to byteArray we are ready to send it
            uploadTask.addOnCompleteListener{
                task ->
                if (task.isSuccessful) {
                    Log.i("FireBase upload photo","Upload Success")
                    //once we upload to image we save the location (path) is storage to main animal object, for later use
                    val res = animal.image!!.add(path)
                    Log.i("images uploaded", "${animal.image!!.count()} out of ${arrayOfImages.count()} current result = $res ")
                    if (animal.image!!.count() == arrayOfImages.count()){
                        //here we can upload animal.
                        Log.i("path for photo", animal.image!!.random())
                        uploadAnimal(animal)
                    }
                } else {
                    Log.e("FireBase upload photo","Upload Failed")
                }
            }
            count++
        }

    }

    private fun uploadAnimal(animal: bernat.oron.catadoption.model.Animal){
        print("animal.image = ${animal.image}")
        //upload the new animal to the tst BD for pre upload verification
        val map1 = mutableMapOf<String, bernat.oron.catadoption.model.Animal>()
        map1[uniqueID] = animal

        //send to tst DB - for verification (Spam)
        //Will be verified manually
        dataBase.reference.child("Israel-tst/animals/")
            .updateChildren(map1 as Map<String, Any>)
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    Log.i("Upload animal", "Successful")
                }else{
                    Log.e("Upload animal", "Failed")
                    print("Upload Failed")
                }
            }

        //upload to user uploads list the ID of new uploaded animal
        val map2 = mutableMapOf<String, String>()
        map2[uniqueID] = animal.type
        dataBase.reference.child("Israel-tst/users/$uid/uploads/")
            .updateChildren(map2 as Map<String, Any>)
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    Log.i("Upload to users", "Successful")
                    print("Upload Successful")
                }else{
                    Log.e("Upload to users", "Failed")
                    print("Upload Failed")

                }
                progressDialog?.dismiss()
            }
        titleMyFavorite.text = "מועדפים"
    }

    override fun newAnimal(animal: bernat.oron.catadoption.model.Animal) {
        supportFragmentManager.beginTransaction().hide(fragUpload).commit()
        progressDialog?.show()

        //here we upload and he animal get the path to animal.images
        uploadImageToFireBase(animal)

    }

}