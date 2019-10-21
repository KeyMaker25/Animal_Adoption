package bernat.oron.catadoption.activitiesimport android.app.Activityimport android.content.Intentimport android.graphics.Bitmapimport android.net.Uriimport android.os.Bundleimport android.provider.MediaStoreimport android.util.Logimport android.view.Viewimport android.widget.Buttonimport android.widget.ProgressBarimport androidx.fragment.app.Fragmentimport androidx.fragment.app.FragmentActivityimport androidx.fragment.app.FragmentManagerimport androidx.fragment.app.FragmentStatePagerAdapterimport bernat.oron.catadoption.Rimport bernat.oron.catadoption.adapters.ZoomOutPageTransformerimport bernat.oron.catadoption.fragments.FragmentUpload1import bernat.oron.catadoption.fragments.FragmentUpload2import bernat.oron.catadoption.fragments.FragmentUpload3import bernat.oron.catadoption.model.Animalimport bernat.oron.catadoption.model.AnimalCreatorimport bernat.oron.catadoption.model.CustomViewPagerimport bernat.oron.catadoption.model.UploadNewAnimalInterfaceimport com.google.firebase.database.FirebaseDatabaseimport com.google.firebase.storage.FirebaseStorageimport java.io.ByteArrayOutputStreamimport java.io.IOExceptionprivate const val NUM_PAGES = 3const val PICK_IMAGE_REQUEST_1 = 71const val PICK_IMAGE_REQUEST_2 = 72const val PICK_IMAGE_REQUEST_3 = 73class ActivityUploadAnimal : FragmentActivity() , UploadNewAnimalInterface {    private lateinit var mPager: CustomViewPager    private lateinit var indicator: com.viewpagerindicator.CirclePageIndicator    private var filePath: Uri? = null    lateinit var btnNext: Button    lateinit var btnBack: Button    private var arrayOfImages = arrayListOf<Bitmap>()    private val storage = FirebaseStorage.getInstance()    private val dataBase = FirebaseDatabase.getInstance()    lateinit var processBar: ProgressBar    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_upload_animal)        setButtons()        //pager        mPager = findViewById(R.id.upload_pager)        // The pager adapter, which provides the pages to the view pager widget.        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)        mPager.adapter = pagerAdapter        mPager.setPagingEnabled(false)        mPager.setPageTransformer(true, ZoomOutPageTransformer())        //pager indicator        indicator = findViewById(R.id.upload_animal_indicator)        indicator.setViewPager(mPager)        indicator.isCentered = true        //Set circle indicator radius        indicator.radius = 6 * resources.displayMetrics.density        indicator.setOnTouchListener{                _, _ -> true        }        //progressbar        processBar = findViewById(R.id.progress_upload_animal)    }    private fun setButtons() {        btnBack = findViewById(R.id.btn_back)        btnNext = findViewById(R.id.btn_next)        btnNext.setOnClickListener {            if (btnNext.visibility == View.VISIBLE){                if (btnNext.text == "העלה !"){                    startUploadNewAnimal()                }else moveNext()            }        }        btnBack.setOnClickListener {            if (btnBack.visibility == View.VISIBLE){                moveBack()            }        }    }    /** User completed listing the animal     *  First check if got any photos. if so upload and save path in storage.     *  second upload animal to tst DB for inspection    **/    private fun startUploadNewAnimal() {        //setting old but cool progress Bar        showProgressBar()        //check images        val animal: Animal?        if (AnimalCreator.instance.validateAnimal()){            animal = AnimalCreator.instance.animal!!            Log.i("Uploading Animal", " ${animal.name}")            Log.i("Uploading Animal", " image count ${animal.image?.count()}")            if(arrayOfImages.isNotEmpty()){                uploadImageToFireBase(animal)            }else{                uploadAnimal(animal)            }        }    }    private fun showProgressBar() {        mPager.visibility = View.INVISIBLE        indicator.visibility = View.INVISIBLE        hideNextBtn()        hideBackBtn()        processBar.visibility = View.VISIBLE    }    private fun stopProgressBar() {//        mPager.visibility = View.VISIBLE//        indicator.visibility = View.VISIBLE        processBar.visibility = View.INVISIBLE        startActivity(Intent(this,ActivityFavorite::class.java))    }    private fun uploadImageToFireBase(animal: Animal) {        var count = 0        animal.image = mutableListOf()        val ref = storage.reference        //number of images to upload        for (item in arrayOfImages){            val path = "Israel/${ActivityFavorite.uniqueID}image$count"            val baos = ByteArrayOutputStream()            item.compress(Bitmap.CompressFormat.JPEG, 100, baos)            val data = baos.toByteArray()            val uploadTask = ref.child(path).putBytes(data)            //we convert the bitmap to byteArray we are ready to send it            uploadTask.addOnCompleteListener{                    task ->                if (task.isSuccessful) {                    Log.i("FireBase upload photo","Upload Success")                    //once we upload to image we save the location (path) is storage to main animal object, for later use                    val res = animal.image!!.add(path)                    Log.i("images uploaded", "${animal.image!!.count()} out of ${arrayOfImages.count()} current result = $res ")                    if (animal.image!!.count() == arrayOfImages.count()){                        //here we can upload animal.                        Log.i("path for photo", animal.image!!.random())                        uploadAnimal(animal)                    }                } else {                    Log.e("FireBase upload photo","Upload Failed")                }            }            count++        }    }    private fun uploadAnimal(animal: Animal){        print("animal.image = ${animal.image}")        //upload the new animal to the tst BD for pre upload verification        val map1 = mutableMapOf<String, Animal>()        map1[ActivityFavorite.uniqueID] = animal        //send to tst DB - for verification        dataBase.reference.child("Israel-tst/animals/")            .updateChildren(map1 as Map<String, Any>)            .addOnCompleteListener {                    task ->                if (task.isSuccessful){                    Log.i("Upload animal", "Successful")                }else{                    Log.e("Upload animal", "Failed")                }            }        //upload to user uploads list the ID of new uploaded animal        val map2 = mutableMapOf<String, String>()        map2[ActivityFavorite.uniqueID] = animal.type        dataBase.reference.child("Israel-tst/users/${ActivitySplash.uid}/uploads/")            .updateChildren(map2 as Map<String, Any>)            .addOnCompleteListener {                    task ->                if (task.isSuccessful){                    Log.i("Upload to users", "Successful")                    print("Upload Successful")                }else{                    Log.e("Upload to users", "Failed")                    print("Upload Failed")                }                //Stop ProgressBar animation                stopProgressBar()            }    }    override fun onBackPressed() {        if (mPager.currentItem == 0) {            super.onBackPressed()        } else {            mPager.setCurrentItem(mPager.currentItem-1,true)        }    }    override fun hideNextBtn() {        btnNext.visibility = View.INVISIBLE        Log.i("btnNext","INVISIBLE")    }    override fun hideBackBtn() {        btnBack.visibility = View.INVISIBLE        Log.i("btnBack","INVISIBLE")    }    override fun showNextBtn() {        btnNext.visibility = View.VISIBLE        Log.i("btnNext","VISIBLE")    }    override fun showBackBtn() {        btnBack.visibility = View.VISIBLE        Log.i("btnBack","VISIBLE")    }    override fun restoreBtnText() {        btnNext.text = "המשך "        Log.i("btn text change","המשך")    }    override fun changeBtnText() {        btnNext.text = "העלה !"        Log.i("btn text change","העלה ")    }    override fun moveNext() {        mPager.setCurrentItem(mPager.currentItem+1,true)        Log.i("move","next")        showBackBtn()    }    override fun moveBack(){        mPager.setCurrentItem(mPager.currentItem-1,true)        Log.i("move","next")        if (mPager.currentItem == 0){            hideBackBtn()        }    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)        Log.i("favorite on result","$requestCode got image from user ${data?.data}")        if (requestCode == PICK_IMAGE_REQUEST_1 || requestCode == PICK_IMAGE_REQUEST_2 || requestCode == PICK_IMAGE_REQUEST_3) {            if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {                filePath = data.data                try {                    //got image as bitmap                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)                    changeFragUploadView(bitmap, requestCode)                } catch (e: IOException) {                    e.printStackTrace()                }            } else {                println("result ok? ${Activity.RESULT_OK == requestCode}")                println("$requestCode NOT PICK_IMAGE_REQUEST")            }        }    }    private fun changeFragUploadView(image: Bitmap, resCode: Int){        if (!arrayOfImages.contains(image)){            arrayOfImages.add(image)        }        Log.i("add","images to array count = ${arrayOfImages.count()}")        val adapter = (mPager.adapter as ScreenSlidePagerAdapter)        when(resCode){            PICK_IMAGE_REQUEST_1 ->{                adapter.fragment3.image1?.setImageBitmap(image)                adapter.fragment3.image1?.background = null            }            PICK_IMAGE_REQUEST_2 ->{                adapter.fragment3.image2?.setImageBitmap(image)                adapter.fragment3.image2?.background = null            }            PICK_IMAGE_REQUEST_3 ->{                adapter.fragment3.image3?.setImageBitmap(image)                adapter.fragment3.image3?.background = null            }        }    }    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {        override fun getCount(): Int = NUM_PAGES        val fragment1 = FragmentUpload1()        val fragment2 = FragmentUpload2()        val fragment3 = FragmentUpload3()        override fun getItem(position: Int): Fragment{            var frag: Fragment? = null                when(position){                0-> {                    frag = fragment1                    frag.listener = this@ActivityUploadAnimal                }                1-> {                    frag = fragment2                    frag.listener = this@ActivityUploadAnimal                }                2-> {                    frag = fragment3                    frag.listener = this@ActivityUploadAnimal                }            }            return frag!!        }    }}