package bernat.oron.catadoption.model


interface RegistrationInterface {
    fun didFinish(res: Boolean)
}
interface FilterInterface {
    fun didFilter(checkItems: ArrayList<Int>)
}

interface UploadNewAnimalInterface{
    fun hideBackBtn()
    fun hideNextBtn()
    fun showNextBtn()
    fun showBackBtn()
    fun moveNext()
    fun moveBack()
    fun changeBtnText()
    fun restoreBtnText()

}
