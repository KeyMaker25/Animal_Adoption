package bernat.oron.catadoption.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import bernat.oron.catadoption.R
import bernat.oron.catadoption.model.Animal
import bernat.oron.catadoption.model.Cat
import bernat.oron.catadoption.model.Dog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Context
import android.net.ConnectivityManager


class ActivitySplash :AppCompatActivity(){

    private val ref = FirebaseDatabase.getInstance()
    private lateinit var textV : TextView

    companion object {

        fun isUserLogin() : Boolean {
            if (FirebaseAuth.getInstance().currentUser != null){
                uid = FirebaseAuth.getInstance().currentUser!!.uid
                return true
            }
            return false
        }

        var allTypes= arrayListOf("")
        var dogType= arrayListOf("")
        var catType= arrayListOf("")
        var animalCollection = ArrayList<Animal>()
        var dogsCollection = ArrayList<Animal>()
        var catsCollection = ArrayList<Animal>()
        var uid = ""

        var favoriteAnimalCollectionID = arrayListOf<String>()
        var uploadAnimalCollectionID = arrayListOf<String>()
        var uploadAnimalCollection = arrayListOf<Animal>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        textV = findViewById(R.id.textView2)
        if (isOnline())
        {
            initTypes()
            init()
        }
        else {
            connectionFailed("אין חיבור לאינטרנט")
        }

    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
    }

    private fun initTypes() {
        val typeRef = ref.reference.child("Israel-tst/")
        typeRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val children = p0.value as HashMap<String,Any>
                dogType = children["dogTypes"] as ArrayList<String>
                catType = children["catTypes"] as ArrayList<String>
                Log.i(" Types number","Dogs ${dogType.size} types Cats = ${catType.size} types")
                allTypes.addAll(dogType)
                allTypes.addAll(catType)
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.e("Db error", p0.message)
                connectionFailed(p0.message)
            }
        })

    }

    private fun init(){
        val animals = ref.reference.child("Israel-prod/animals")
        animals.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val children = p0.children
                Log.i("animals count " ,p0.children.count().toString())
                children.forEach {
                    val animal = it.value as MutableMap<String, Any>
                    var temp : Animal? = null
                    if (animal["type"] == "Cat") {
                        temp = Cat(it.key as String,
                            animal["name"] as String,
                            animal["age"] as Long,
                            animal["breed"] as String,
                            animal["story"] as String,
                            animal["location"] as String,
                            animal["gender"] as String,
                            animal["weight"] as Long,
                            animal["ownerID"] as String,
                            animal["phone"] as String,
                            animal["image"] as MutableList<String>
                        )
                        catsCollection.add(temp)
                    } else if (animal["type"] == "Dog"){
                        temp = Dog(it.key as String,
                            animal["name"] as String,
                            animal["age"] as Long,
                            animal["breed"] as String,
                            animal["story"] as String,
                            animal["location"] as String,
                            animal["gender"] as String,
                            animal["weight"] as Number,
                            animal["ownerID"] as String,
                            animal["phone"] as String,
                            animal["image"] as MutableList<String>
                        )
                        dogsCollection.add(temp)
                    }
                    animalCollection.add(temp!!)
                }
                startActivity(Intent(applicationContext,ActivityMain::class.java))
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("SingleValueEvent E", p0.message)
                connectionFailed(p0.message)
            }

        })
        if (FirebaseAuth.getInstance().currentUser != null){
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            initFavoriteAndUploads()
        }
    }

    private fun connectionFailed(message: String){
        textV.setBackgroundColor(resources.getColor(R.color.colorTextDel))
        textV.textSize = 18F
        textV.text = message
    }

    private fun initFavoriteAndUploads(){
        val favoriteRef = ref.reference.child("Israel-tst/users/$uid/")
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val map = p0.value as? MutableMap<String, Any>
                map?.let{
                    val uploadsMap = map["uploads"] as? MutableMap<String, Any>
                    val favoriteMap = map["favorite"] as? MutableMap<String, Any>
                    favoriteMap?.forEach { id ->
                        favoriteAnimalCollectionID.add(id.key)
                    }
                    uploadsMap?.forEach { id ->
                        uploadAnimalCollectionID.add(id.key)
                    }
                }
                Log.i("uploaded count ", uploadAnimalCollectionID.size.toString())
                Log.i("favorite count ", favoriteAnimalCollectionID.size.toString())
                getUploadAnimalCollection()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("SingleValueEvent E", p0.message)
                connectionFailed(p0.message)
            }
        })
    }

    private fun getUploadAnimalCollection() {
        val typeRef = ref.reference.child("Israel-tst/animals")
        typeRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val children = p0.value as HashMap<String,Any>
                children.forEach {
                    val animal = it.value as MutableMap<String, Any>
                    val animalTemp = Animal(
                            it.key,
                            animal["name"] as String,
                            animal["type"] as String,
                            animal["age"] as Long,
                            animal["breed"] as String,
                            animal["story"] as String,
                            animal["location"] as String,
                            animal["gender"] as String,
                            animal["weight"] as Long,
                            animal["timeOfUpload"] as String,
                            animal["ownerID"] as String,
                            animal["phone"] as String,
                            animal["image"] as MutableList<String>?
                        )
                    //check if already approved
                    if(animalCollection.firstOrNull {
                                it1 ->
                                it1.ID == animalTemp.ID } == null )
                    {
                        uploadAnimalCollection.add(animalTemp)
                        Log.i("upload animal added", animalTemp.name)
                    }


                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }
}