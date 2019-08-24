package bernat.oron.catadoption.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*
import java.io.Serializable


open class AnimalsFactory constructor(val ID: String,
                                      val name: String,
                                      val type: String,
                                      val age: Number,
                                      val breed: String,
                                      val story: String,
                                      val location: String,
                                      val gender:String,
                                      val weight: Number ,
                                      var timeOfUpload: String,
                                      var ownerID: String,
                                      var phone: String,
                                      var image: MutableList<String>?): Serializable
{
    override fun toString(): String {
        return "Hey my name is $name and I'm a $breed $type I would love for you to adopt me :)"
    }
}

class Cat(ID: String,
          name: String,
          age: Number,
          breed: String,
          story: String,
          location: String,
          gender: String,
          weight: Number,
          ownerID: String,
          phone: String,
          image: MutableList<String>?) :
    AnimalsFactory(ID,name,"Cat",age,breed,story,location,gender,weight, SimpleDateFormat("dd-MM-yyyy").format(Date()), ownerID,phone,image)

class Dog(ID: String,
          name: String,
          age: Number,
          breed: String,
          story: String,
          location: String,
          gender: String,
          weight: Number,
          ownerID: String,
          phone: String,
          image: MutableList<String>?) :
    AnimalsFactory(ID,name,"Dog",age,breed,story,location,gender,weight,SimpleDateFormat("dd-MM-yyyy").format(Date()), ownerID,phone,image)





