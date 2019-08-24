package bernat.oron.catadoption.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import bernat.oron.catadoption.R
import bernat.oron.catadoption.dao.AnimalRepository
import bernat.oron.catadoption.model.AnimalsFactory
import bernat.oron.catadoption.model.Cat
import bernat.oron.catadoption.model.Dog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActivitySplash :AppCompatActivity(){

    val ref = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initFromRoom()
        init()
    }

    companion object{

        fun isUserLogin() : Boolean {
            if (FirebaseAuth.getInstance().currentUser != null){
                uid = FirebaseAuth.getInstance().currentUser!!.uid
                return true
            }
            return false
        }
        val dogType= arrayListOf(
            "בולדוג אנגלי" ,"בוקסר" ,"בייגלה" ,"גולדן רטריבר" ,
            "דוברמן" ,"הסקי סיבירי" ,"פאג" , "פודל" ,"ציוואווה" ,
            "רוטוויילר" ,"לברדור" ,"דני ענק" ,"רואה גרמני", "אחר"
        )
        val catType= arrayListOf(
            "אמריקאי","פרסי" ,"סיאמי" ,"אביסיני" ,"סיבירי" ,
            "הימליה" ,"בורמזי" ,"אקזוטי" , "רוסי","אחר"
        )

        var animalCollection = ArrayList<AnimalsFactory>()
        var dogsCollection = ArrayList<AnimalsFactory>()
        var catsCollection = ArrayList<AnimalsFactory>()
        var uid = ""

        var favoriteAnimalCollection = arrayListOf<String>()
        var uploadAnimalCollection = arrayListOf<String>()
    }

    private fun initFromRoom(): Boolean{
        val db = AnimalRepository(this).tasks
        Log.i("db res",db.value.toString())
        AnimalRepository(this).tasks.observe(this, Observer {
            list ->
            for (item in list){
                Log.i("name: ",item.name)
                Log.i("id: ",item.id)
                Log.i("type: ",item.type)
            }
        })
        return false
    }

    private fun init(){
        val animals = ref.reference.child("Israel-tst/animals")
        animals.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val children = p0.children
                // This returns the correct child count...

                Log.i("animals count: " ,p0.children.count().toString())
                children.forEach {
                    val animal = it.value as MutableMap<String, Any>
                    if (animal["type"] == "Cat") {
                        val temp = Cat(it.key as String,
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
                        animalCollection.add(temp)
                    } else if (animal["type"] == "Dog"){
                        val temp = Dog(it.key as String,
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
                        animalCollection.add(temp)
                    }
                }
                startActivity(Intent(applicationContext,ActivityMain::class.java))
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.e("SingleValueEvent E", p0.message)
            }

        })

        if (FirebaseAuth.getInstance().currentUser != null){
            initFavoriteAndUploads()
        }
    }

    private fun initFavoriteAndUploads(){
        val favoriteRef = ref.reference.child("Israel-tst/users/$uid/")
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {it->
                    val map1 = it.value as MutableMap<String, Any>
                    val uploadsMap = map1["uploads"] as MutableMap<String, Any>
                    (map1["favorite"] as? MutableMap<String, Any>)?.forEach { id ->
                        favoriteAnimalCollection.add(id.key)
                    }
                    uploadsMap.forEach { id ->
                        uploadAnimalCollection.add(id.key)
                    }
                }
                Log.i("upload count- ", uploadAnimalCollection.size.toString())
                Log.i("favorite count- ", favoriteAnimalCollection.size.toString())
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("SingleValueEvent E", p0.message)
            }
        })
    }
}