package bernat.oron.catadoption.dao

import android.content.Context
import androidx.room.*
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.lifecycle.LiveData
import android.os.AsyncTask
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Dao


@Database(entities = [Animal::class,Favorite::class], version = 1, exportSchema = false)
abstract class AnimalDatabase : RoomDatabase() {

    abstract fun daoAccess(): DaoAccess
}

@Dao
interface DaoAccess {

    @Insert
    fun insertTask(Animal: Animal): Long?


    @Query("SELECT * FROM Animal ORDER BY time_of_upload desc")
    fun fetchAllTasks(): LiveData<List<Animal>>


    @Query("SELECT * FROM Animal WHERE id =:taskId")
    fun getTask(taskId: Int): LiveData<Animal>


    @Update
    fun updateTask(Animal: Animal): Boolean


    @Delete
    fun deleteTask(Animal: Animal): Boolean
}

@Entity
data class Animal(
    @PrimaryKey(autoGenerate = true) val Tid: Long?,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "breed") val breed: String,
    @ColumnInfo(name = "story") val story: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "gender") val gender:String,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "time_of_upload") var timeOfUpload: String,
    @ColumnInfo(name = "owner_id") var ownerID: String,
    @ColumnInfo(name = "phone") var phone: String,
    @ColumnInfo(name = "image") var image: String
)

@Entity
data class Favorite(
    @PrimaryKey(autoGenerate = true) val Tid: Long,
    @ColumnInfo(name = "owner_id") val id: String,
    @ColumnInfo(name = "animal_id") val animalId: String,
    @ColumnInfo(name = "type") val type: String

)

class AnimalRepository(context: Context) {

    private val DB_NAME = "db_task"

    private val noteDatabase: AnimalDatabase

    val tasks: LiveData<List<Animal>>
        get() = noteDatabase.daoAccess().fetchAllTasks()

    init {
        noteDatabase = Room.databaseBuilder(context, AnimalDatabase::class.java, DB_NAME).build()
    }

    fun insertTask(Animal: Animal) {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                noteDatabase.daoAccess().insertTask(Animal)
                return null
            }
        }.execute()
    }

    fun updateTask(note: Animal) {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                noteDatabase.daoAccess().updateTask(note)
                return null
            }
        }.execute()
    }

    fun deleteTask(id: Int) {
        val task = getTask(id)
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                noteDatabase.daoAccess().deleteTask(task.getValue()!!)
                return null
            }
        }.execute()
    }

    fun deleteTask(note: Animal) {

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                noteDatabase.daoAccess().deleteTask(note)
                return null
            }
        }.execute()
    }

    fun getTask(id: Int): LiveData<Animal> {
        return noteDatabase.daoAccess().getTask(id)
    }

}

