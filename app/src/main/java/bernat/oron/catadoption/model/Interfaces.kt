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

interface PagerMoveInterface{
    fun moveNext(name: String, age: String, type: String, breed: String, gender: String)
    fun moveBack()
}
