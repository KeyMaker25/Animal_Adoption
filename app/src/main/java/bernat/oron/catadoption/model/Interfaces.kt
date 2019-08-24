package bernat.oron.catadoption.model

import android.graphics.Bitmap

interface RegistrationInterface {
    fun didFinish(res: Boolean)
}
interface FilterInterface {
    fun didFilter(checkItems: ArrayList<Int>)
}
interface UploadNewAnimalInterface{
    fun newAnimal(animal: AnimalsFactory)
}
