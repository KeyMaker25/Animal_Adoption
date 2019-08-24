package bernat.oron.catadoption.activities


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bernat.oron.catadoption.adapters.AnimalAdapterH
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.ActivitySplash.Companion.animalCollection
import bernat.oron.catadoption.activities.ActivitySplash.Companion.favoriteAnimalCollection
import bernat.oron.catadoption.activities.ActivitySplash.Companion.isUserLogin
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uploadAnimalCollection
import bernat.oron.catadoption.dao.Animal
import bernat.oron.catadoption.dao.AnimalRepository
import bernat.oron.catadoption.fragments.FragmentUpload
import bernat.oron.catadoption.fragments.FragmentUpload.Companion.PICK_IMAGE_REQUEST_1
import bernat.oron.catadoption.fragments.FragmentUpload.Companion.PICK_IMAGE_REQUEST_2
import bernat.oron.catadoption.fragments.FragmentUpload.Companion.PICK_IMAGE_REQUEST_3
import bernat.oron.catadoption.model.AnimalsFactory
import bernat.oron.catadoption.model.UploadNewAnimalInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ActivityFavorite: AppCompatActivity() , UploadNewAnimalInterface {

    lateinit var btnFloating: FloatingActionButton
    lateinit var recFavorite: RecyclerView
    lateinit var recMyAnimal: RecyclerView
    lateinit var fragUpload: FragmentUpload
    lateinit var titleTop: TextView
    private var filePath: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val dataBase = FirebaseDatabase.getInstance()
    private var arrayOfImages = arrayListOf<Bitmap>()
    private var progressDialog: ProgressBar? = null

    companion object{
        var uniqueID = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        progressDialog =  ProgressBar(this)
        btnFloating = findViewById(R.id.btn_add_floating)
        initFloatingBtn()
        recFavorite = findViewById(R.id.recycler_view_favorite)
        recMyAnimal = findViewById(R.id.recycler_view_favorite_my_animal)
        initRecyclerView()
        titleTop = findViewById(R.id.txt_top_title)

    }

    private fun initFloatingBtn() {
        fragUpload = FragmentUpload()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_fav, fragUpload)
            .hide(fragUpload).commit()
        //this is the btn for uploading (after register) a new animal for adoption
        btnFloating.setOnClickListener {
            if (isUserLogin()){
                fragUpload.event = this
                titleTop.text = "הוספת בעל חיים לאימוץ"
                supportFragmentManager.beginTransaction().show(fragUpload).commit()
                //init id for uploaded(?) animal
                uniqueID = UUID.randomUUID().toString().replace("-","")
            }else{
                //show login fragment,

            }
        }
    }

    private fun initRecyclerView(){
        recFavorite.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recMyAnimal.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        val uploadList = ArrayList<AnimalsFactory>()
        val favoriteList = ArrayList<AnimalsFactory>()
        for (id in favoriteAnimalCollection){
            favoriteList.add(animalCollection.single { it.ID == id })
        }
        for (id in uploadAnimalCollection){
            uploadList.add(animalCollection.single { it.ID == id })
        }
        val adapterFavorite = AnimalAdapterH(
            favoriteList,
            this
        )
        val adapterUploads = AnimalAdapterH(
            uploadList,
            this
        )

        val onClick: ((AnimalsFactory) -> Unit)? = {
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
            titleTop.text = "מועדפים"
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

    private fun uploadImageToFireBase(animal: AnimalsFactory) {
        var count = 0
        animal.image = mutableListOf()

        val ref = storage.reference

        //this is the number of images that we have
        for (item in arrayOfImages){
            val path = "Israel/${uniqueID}image$count"
            val baos = ByteArrayOutputStream()
            val itemBitMap = arrayOfImages.random()
            itemBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = ref.child(path).putBytes(data)
            uploadTask.addOnCompleteListener{
                task ->
                if (task.isSuccessful) {
                    Log.i("FireBase upload photo","Upload Success")
                    val res = animal.image!!.add(path)
                    Log.i("images uploaded", "${animal.image!!.count()} out of ${arrayOfImages.count()} current res = $res ")
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

    /**
     * this function is used when the user place all details on animal and it's been validate
     * new we need to upload the images to storage (better for images) with uniqueID+image+number
     * after that we need to upload the animal details to DB
     * **/

    private fun uploadAnimal(animal: AnimalsFactory){
        print("animal.image = ${animal.image}")
        //we first upload to user list (update user uploads)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val map1 = mutableMapOf<String, AnimalsFactory>()
        map1[uniqueID] = animal

        dataBase.reference.child("Israel-tst/animals/")
            .updateChildren(map1 as Map<String, Any>)
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    Log.i("Upload animal", "Successful")
                    val daoAnimal = animal.image?.let {
                        Animal(null,animal.ID,
                            animal.name,animal.type,
                            animal.age.toInt(),animal.breed,
                            animal.story,animal.location,
                            animal.gender,animal.weight.toInt(),
                            animal.timeOfUpload,animal.ownerID,
                            animal.phone,
                            it.joinToString { " " }
                        )
                    }
                    AnimalRepository(this).insertTask(daoAnimal!!)

                }else{
                    Log.e("Upload animal", "Failed")
                    print("Upload Failed")
                }
            }

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
                progressDialog!!.visibility = View.INVISIBLE
            }
        titleTop.text = "מועדפים"
    }

    override fun newAnimal(animal: AnimalsFactory) {
        supportFragmentManager.beginTransaction().hide(fragUpload).commit()
        progressDialog!!.isIndeterminate = true
        progressDialog!!.visibility = View.VISIBLE

        //here we upload and he animal get the path to animal.images
        uploadImageToFireBase(animal)

    }

}