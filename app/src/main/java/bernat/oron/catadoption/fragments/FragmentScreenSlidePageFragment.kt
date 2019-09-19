package bernat.oron.catadoption.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.ActivitySplash
import bernat.oron.catadoption.activities.ActivitySplash.Companion.allTypes
import bernat.oron.catadoption.model.*



class ScreenSlidePageFragment(position: Int) : Fragment() {

    var mPosition = position
    var listener: PagerMoveInterface? = null
    private val locations = IsraelDistricts().getall()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        when(mPosition){
            0-> return inflater.inflate(R.layout.fragment_upload1, container, false)
            1-> return inflater.inflate(R.layout.fragment_upload2, container, false)
            2-> return inflater.inflate(R.layout.fragment_upload3, container, false)
        }
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(mPosition){
            //fragment 1
            0-> {
                val textName = view.findViewById<TextView>(R.id.fragment_upload1_edit_name)
                val textAge = view.findViewById<TextView>(R.id.fragment_upload1_edit_age)
                val pickerType= view.findViewById<Spinner>(R.id.fragment_upload1_picker_type)
                val pickerBreed = view.findViewById<Spinner>(R.id.fragment_upload1_picker_breed)
                val btnNext = view.findViewById<Button>(R.id.btn_next)
                val pickerGender = view.findViewById<Spinner>(R.id.fragment_upload1_picker_gender)

                pickerGender.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, arrayOf("זכר","נקבה"))
                pickerType.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item,arrayListOf("כלב", "חתול"))
                pickerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        pickerBreed.adapter = ArrayAdapter(context!!, R.layout.select_dialog_item,
                            ActivitySplash.dogType
                        )
                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        when {
                            parent?.getItemAtPosition(position) == "חתול" -> {
                                pickerBreed.adapter =
                                    ArrayAdapter(context!!, R.layout.select_dialog_item,
                                        ActivitySplash.catType
                                    )
                                Log.i("selected","CAT")
                            }
                            parent?.getItemAtPosition(position) == "כלב" -> {
                                pickerBreed.adapter =
                                    ArrayAdapter(context!!, R.layout.select_dialog_item,
                                        ActivitySplash.dogType
                                    )
                                Log.i("selected", "DOG")
                            }
                        }
                    }

                }

                textAge.addTextChangedListener(
                    object : TextWatcher{
                        override fun afterTextChanged(s: Editable?) {
                            if (validate1(textName,textAge)) btnNext.visibility = View.VISIBLE
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            if (validate1(textName,textAge)) btnNext.visibility = View.VISIBLE
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                    }
                )
                //validate info -
                btnNext.setOnClickListener {
                    Log.i("click", "next")
                    var shouldPass = true
                    if (validate1(textName, textAge)){
                        //check spinners
                        if (pickerType.selectedItem == null){
                            Log.e("err", "pickerType")
                            textName.error = "בחר סוג"
                            shouldPass = false
                        }
                        if (pickerGender.selectedItem == null){
                            Log.e("err", "pickerType")
                            textName.error = "זכר ? נקבה ?"
                            shouldPass = false
                        }
                        if (!allTypes.contains(pickerBreed.selectedItem)){
                            Log.e("err", "pickerType")
                            textName.error = "בחר גזע"
                            shouldPass = false
                        }
                        Log.e("pass ? ", (listener == null).toString())
                        if (shouldPass) {
                            listener?.moveNext(textName.text.toString()
                                , textAge.text.toString()
                                , pickerType.selectedItem.toString()
                                , pickerBreed.selectedItem.toString()
                                , pickerGender.selectedItem.toString())
                            }
                        }
                }
            }
            //fragment 2
            1-> {
//                autoCompleteLocation = view.findViewById(R.id.auto_complete_location)
//                autoCompleteLocation.setAdapter(ArrayAdapter(context!!, R.layout.select_dialog_item, locations))
//                animalTypePicker = view.findViewById(R.id.fragment_animal_picker_type)
//                animalTypePicker.adapter = ArrayAdapter(context!!,R.layout.support_simple_spinner_dropdown_item,arrayListOf("כלב", "חתול"))
//                animalWeightPicker = view.findViewById(R.id.fragment_animal_picker_weight)
//                val weightNumber = arrayListOf<Int>()
//                for (i in 1..30){
//                    weightNumber.add(i)
//                }
//                animalWeightPicker.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, weightNumber)
            }
            //fragment 3
            2-> {

            }
        }

    }

    private fun validate1(textName: TextView, textAge: TextView): Boolean {
        //validate name-age
        if(textName.text.toString().isEmpty() || textName.text.toString().isBlank()){
            textName.error = "שם זה חובה.."
            return false
        }
        if (textAge.text.toString().isEmpty()){
            textAge.error = "אם הוא חי, יש לו גיל.."
            return false
        }

        return true
    }

    private fun validate2(textName: TextView, textAge: TextView): Boolean {
        //validate name-age
        if(textName.text.toString().isEmpty() || textName.text.toString().isBlank()){
            textName.error = "שם זה חובה.."
            return false
        }
        if (textAge.text.toString().isEmpty()){
            textAge.error = "אם הוא חי, יש לו גיל.."
            return false
        }

        return true
    }


//    private fun validate(): Animal?{
//        //check name
//        //check age
//
//        if (ageEditText.text.toString().toInt() > 108){
//            ageEditText.error = "הגזמת בגיל - עד גיל 9"
//            return null
//        }
//        val animalAge = ageEditText.text.toString()
//        //check number
//        if (phoneNumberEditText.text.toString().count() != 10){
//            this.phoneNumberEditText.error = "מספר ישראלי עם עשר ספרות"
//            return null
//        }
//        val phoneNumber = phoneNumberEditText.text.toString()
//        //check location
//        if (!locations.contains(autoCompleteLocation.text.toString())){
//            //not valid location
//            autoCompleteLocation.error = "בחר מהרשימה"
//            return null
//        }
//        val animalLocation = autoCompleteLocation.text.toString()
//        //check story
//        if (storyEditText.text.toString().count() < 10 || storyEditText.text.toString().count() > 400){
//            storyEditText.error = "ספר קצת יותר..."
//            return null
//        }
//        val animalStory = storyEditText.text.toString()
//        //check breed
//        if (animalBreedPicker.selectedItem == null){
//            storyEditText.error = "בחר סוג מהרשימה"
//            return null
//        }
//        val animalBreed = animalBreedPicker.selectedItem.toString()
//        //check type
//        if (animalTypePicker.selectedItem == null){
//            storyEditText.error = "בחר סוג מהרשימה"
//            return null
//        }
//        val animalType = animalTypePicker.selectedItem.toString()
//        //check gender
//        if (animalGenderPicker.selectedItem == null){
//            storyEditText.error = "בחר מין"
//            return null
//        }
//        val animalGender = animalGenderPicker.selectedItem.toString()
//        //check weight
//        if (animalWeightPicker.selectedItem == null){
//            storyEditText.error = "משקל בקילוגרם"
//            return null
//        }
//        val animalWeight = animalWeightPicker.selectedItem.toString().toInt()
//
//
//        //images we don't need to check, it's not a must... yet
//        if (animalType == "חתול"){
//            return Cat(
//                ActivityFavorite.uniqueID,
//                animalName ,
//                animalAge.toInt() ,
//                animalBreed ,
//                animalStory,
//                animalLocation ,
//                animalGender ,
//                animalWeight ,
//                ActivitySplash.uid,
//                phoneNumber,
//                null
//            )
//        }
//        else if (animalType == "כלב"){
//            return Dog(
//                ActivityFavorite.uniqueID,
//                animalName,
//                animalAge.toInt(),
//                animalBreed,
//                animalStory,
//                animalLocation,
//                animalGender,
//                animalWeight,
//                ActivitySplash.uid,
//                phoneNumber,
//                null
//            )
//        }
//        return null
//
//    }
}