package bernat.oron.catadoption.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.Fragment
import bernat.oron.catadoption.R
import android.content.Intent
import android.util.Log
import android.widget.*
import bernat.oron.catadoption.model.*
import androidx.appcompat.app.AlertDialog
import bernat.oron.catadoption.activities.ActivityFavorite
import android.net.Uri
import bernat.oron.catadoption.activities.ActivitySplash.Companion.catType
import bernat.oron.catadoption.activities.ActivitySplash.Companion.dogType
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uid


class FragmentUpload(position: Int?) : Fragment() {

    lateinit var event: UploadNewAnimalInterface
    lateinit var animalTypePicker : Spinner
    lateinit var animalBreedPicker: Spinner
    lateinit var animalGenderPicker: Spinner
    lateinit var animalWeightPicker: Spinner

    lateinit var name: EditText
    lateinit var ageEditText: EditText
    lateinit var phoneNumberEditText: EditText
    lateinit var autoCompleteLocation: AppCompatAutoCompleteTextView
    lateinit var storyEditText: EditText
    lateinit var image1: ImageView
    lateinit var image2: ImageView
    lateinit var image3: ImageView
    lateinit var btnDone: Button



    companion object{
        const val PICK_IMAGE_REQUEST_1 = 71
        const val PICK_IMAGE_REQUEST_2 = 72
        const val PICK_IMAGE_REQUEST_3 = 73
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :
        View? = inflater.inflate(R.layout.fragment_upload, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnDone = view.findViewById(R.id.btn_done)


        btnDone.setOnClickListener{
            if (validate() != null){
                event.newAnimal(validate()!!)
            }else{
                var msg = "צריך לשים לב לכל הפרטים, בשביל למקסם את הסיכוי של אימוץ - תודה"
                if (name.text.isNotEmpty() || !name.text.isBlank()){
                    msg = " בשביל ש${name.text} "
                    msg+= "יזכה לסיכוי המקסימאלי לאימוץ, אנא מלא בצומת לב רבה - תודה"
                }
                val alert = AlertDialog.Builder(context!!).create()
                alert.setTitle("שים לב")
                alert.setMessage(msg)
                alert.setButton(AlertDialog.BUTTON_NEGATIVE,"סגור"
                ) { _, _ ->
                    alert.dismiss()
                }
                alert.setButton(AlertDialog.BUTTON_POSITIVE,"תודה"
                ) { _, _ ->
                    alert.dismiss()
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW, Uri.parse("market://details?id=${context!!.packageName}"))
                        )
                    } catch (e: android.content.ActivityNotFoundException) {
                        startActivity(
                            Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=${context!!.packageName}"))
                        )
                    }

                }
                alert.show()
            }
        }

        initSpinners(view)

        name = view.findViewById(R.id.fragment_upload_edit_name)
        ageEditText = view.findViewById(R.id.fragment_upload_edit_age)
        phoneNumberEditText = view.findViewById(R.id.fragment_upload_edit_phone)
        storyEditText = view.findViewById(R.id.fragment_upload_edit_story)

        initImages()

    }

    private fun initSpinners(view: View) {


        autoCompleteLocation = view.findViewById(R.id.auto_complete_location)


        animalTypePicker = view.findViewById(R.id.fragment_animal_picker_type)
        animalTypePicker.adapter = ArrayAdapter(context!!,R.layout.support_simple_spinner_dropdown_item,arrayListOf("כלב", "חתול"))
        animalBreedPicker = view.findViewById(R.id.fragment_animal_picker_breed)
        animalTypePicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                animalBreedPicker.adapter = ArrayAdapter(context!!, R.layout.select_dialog_item, dogType)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when {
                    parent?.getItemAtPosition(position) == "חתול" -> {
                        animalBreedPicker.adapter =
                            ArrayAdapter(context!!, R.layout.select_dialog_item, catType)
                        Log.i("selected","CAT")
                    }
                    parent?.getItemAtPosition(position) == "כלב" -> {
                        animalBreedPicker.adapter =
                            ArrayAdapter(context!!, R.layout.select_dialog_item, dogType)
                        Log.i("selected", "DOG")
                    }
                }
            }

        }

        animalGenderPicker = view.findViewById(R.id.fragment_animal_picker_gender)
        animalGenderPicker.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, arrayOf("זכר","נקבה"))

        animalWeightPicker = view.findViewById(R.id.fragment_animal_picker_weight)
        val weightNumber = arrayListOf<Int>()
        for (i in 1..30){
            weightNumber.add(i)
        }
        animalWeightPicker.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, weightNumber)

    }

    private fun getIntImage(id: Int): Int{
        when(id){
            R.id.fragment_upload_edit_image1 ->{
                Log.i("image upload", "image1")
                return PICK_IMAGE_REQUEST_1
            }
            R.id.fragment_upload_edit_image2 ->{
                Log.i("image upload", "image2")
                return PICK_IMAGE_REQUEST_2
            }
            R.id.fragment_upload_edit_image3 ->{
                Log.i("image upload", "image3")
                return PICK_IMAGE_REQUEST_3
            }
        }
        return 1
    }

    private fun initImages(){
        image1 = view!!.findViewById(R.id.fragment_upload_edit_image1)
        image2 = view!!.findViewById(R.id.fragment_upload_edit_image2)
        image3 = view!!.findViewById(R.id.fragment_upload_edit_image3)
        val imageListener = View.OnClickListener {
                v->
            val intent = Intent()
            val req = getIntImage(v.id)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activity?.startActivityForResult(Intent.createChooser(intent, "Select Picture"),req)
        }

        image1.setOnClickListener(imageListener)
        image2.setOnClickListener(imageListener)
        image3.setOnClickListener(imageListener)
    }
    // validate inputs
    private fun validate(): Animal?{
        //check name
        if(name.text.toString().isEmpty() || name.text.toString().isBlank()){
            name.error = "שם זה חובה.."
            return null
        }
        val animalName = name.text.toString()
        //check age
        if (ageEditText.text.toString().isEmpty()){
            ageEditText.error = "אם הוא חי, יש לו גיל.."
            return null
        }
        if (ageEditText.text.toString().toInt() > 108){
            ageEditText.error = "הגזמת בגיל - עד גיל 9"
            return null
        }
        val animalAge = ageEditText.text.toString()
        //check number
        if (phoneNumberEditText.text.toString().count() != 10){
            this.phoneNumberEditText.error = "מספר ישראלי עם עשר ספרות"
            return null
        }
        val phoneNumber = phoneNumberEditText.text.toString()

        val animalLocation = autoCompleteLocation.text.toString()
        //check story
        if (storyEditText.text.toString().count() < 10 || storyEditText.text.toString().count() > 400){
            storyEditText.error = "ספר קצת יותר..."
            return null
        }
        val animalStory = storyEditText.text.toString()
        //check breed
        if (animalBreedPicker.selectedItem == null){
            storyEditText.error = "בחר סוג מהרשימה"
            return null
        }
        val animalBreed = animalBreedPicker.selectedItem.toString()
        //check type
        if (animalTypePicker.selectedItem == null){
            storyEditText.error = "בחר סוג מהרשימה"
            return null
        }
        val animalType = animalTypePicker.selectedItem.toString()
        //check gender
        if (animalGenderPicker.selectedItem == null){
            storyEditText.error = "בחר מין"
            return null
        }
        val animalGender = animalGenderPicker.selectedItem.toString()
        //check weight
        if (animalWeightPicker.selectedItem == null){
            storyEditText.error = "משקל בקילוגרם"
            return null
        }
        val animalWeight = animalWeightPicker.selectedItem.toString().toInt()


        //images we don't need to check, it's not a must... yet
        if (animalType == "חתול"){
            return Cat(
                ActivityFavorite.uniqueID,
                animalName ,
                animalAge.toInt() ,
                animalBreed ,
                animalStory,
                animalLocation ,
                animalGender ,
                animalWeight ,
                uid,
                phoneNumber,
                null
            )
        }
        else if (animalType == "כלב"){
            return Dog(
                ActivityFavorite.uniqueID,
                animalName,
                animalAge.toInt(),
                animalBreed,
                animalStory,
                animalLocation,
                animalGender,
                animalWeight,
                uid,
                phoneNumber,
                null
            )
        }
        return null

    }

}