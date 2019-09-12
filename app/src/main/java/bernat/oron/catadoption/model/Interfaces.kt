package bernat.oron.catadoption.model


interface RegistrationInterface {
    fun didFinish(res: Boolean)
}
interface FilterInterface {
    fun didFilter(checkItems: ArrayList<Int>)
}
interface UploadNewAnimalInterface{
    fun newAnimal(animal: Animal)
}
