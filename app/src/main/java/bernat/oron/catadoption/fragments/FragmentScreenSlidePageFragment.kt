package bernat.oron.catadoption.fragments

import android.content.Intent
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
import bernat.oron.catadoption.activities.*
import bernat.oron.catadoption.model.*


class ScreenSlidePageFragment(position: Int) : Fragment() {

    private var mPosition = position
    var listener: PagerMoveInterface? = null
    private val locations = IsraelDistricts().getall()
    var image1: ImageView? = null
    var image2: ImageView? = null
    var image3: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        when(mPosition){
            0-> return inflater.inflate(R.layout.fragment_upload1, container, false)
            1-> return inflater.inflate(R.layout.fragment_upload2, container, false)
            2-> return inflater.inflate(R.layout.fragment_upload3, container, false)
        }
        return inflater.inflate(R.layout.fragment_upload1, container, false)
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
                val pickerGender = view.findViewById<Spinner>(R.id.fragment_upload1_picker_gender)
                //check buttons
                listener?.hideBackBtn()
                if (validate1(textName,textAge,pickerType,pickerBreed,pickerGender)) listener?.showNextBtn() else
                listener?.hideNextBtn()
                pickerGender.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, arrayOf("זכר","נקבה"))
                pickerType.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item,arrayOf("כלב", "חתול"))
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
                textAge.addTextChangedListener(object : TextWatcher{
                        override fun afterTextChanged(s: Editable?) {
                            if (validate1WithError(textName,textAge,pickerType,pickerBreed,pickerGender)){
                                listener?.showNextBtn()
                                AnimalCreator.stageOne(
                                    textName.text.toString(),
                                    textAge.text.toString(),
                                    pickerType.selectedItem.toString(),
                                    pickerBreed.selectedItem.toString(),
                                    pickerGender.selectedItem.toString()
                                )
                            }else{
                                listener?.hideNextBtn()
                            }
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        }
                    })
                if (validate1(textName,textAge,pickerType,pickerBreed,pickerGender))
                {
                    Log.i("Next Btn","show")
                    listener?.showNextBtn()
                }
                else
                {
                    Log.i("Next Btn","hide")
                    listener?.hideNextBtn()
                }

            }
            //fragment 2
            1-> {
                val location = view.findViewById<AutoCompleteTextView>(R.id.auto_complete_location)
                location.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, locations))
                val weight = view.findViewById<Spinner>(R.id.fragment_animal_picker_weight)
                val ownerNumber = view.findViewById<EditText>(R.id.fragment_upload_edit_phone)
                val story = view.findViewById<EditText>(R.id.fragment_upload_edit_story)
                val weightNumber = arrayListOf<Int>()
                for (i in 1..30){
                    weightNumber.add(i)
                }
                weight.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, weightNumber)
                val watcher = object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (validate2WithError(location,weight,ownerNumber,story)){
                            listener?.showNextBtn()
                            AnimalCreator.stageTwo(
                                location.text.toString(),
                                weight.selectedItem.toString(),
                                ownerNumber.text.toString(),
                                story.text.toString()
                            )
                        }else{
                            listener?.hideNextBtn()
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    }
                }
                listener?.restoreBtnText()
                story.addTextChangedListener(watcher)
                ownerNumber.addTextChangedListener(watcher)

                if (validate2(location,weight,ownerNumber,story)){
                    print("showNextBtn")
                    listener?.showNextBtn()
                }else{
                    print("hideNextBtn")
                    listener?.hideNextBtn()
                }
            }
            //fragment 3
            2-> {
                image1 = view.findViewById(R.id.fragment_upload_edit_image1)
                image2 = view.findViewById(R.id.fragment_upload_edit_image2)
                image3 = view.findViewById(R.id.fragment_upload_edit_image3)
                val imageListener = View.OnClickListener {
                            v->
                        val intent = Intent()
                        val req = getImageNumber(v.id)
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        activity?.startActivityForResult(Intent.createChooser(intent, "Select Picture"),req)
                    }
                image1?.setOnClickListener(imageListener)
                image2?.setOnClickListener(imageListener)
                image3?.setOnClickListener(imageListener)

                listener?.changeBtnText()
                listener?.showNextBtn()
                }
            }
    }


    private fun getImageNumber(id: Int): Int{
        when(id) {
            R.id.fragment_upload_edit_image1 -> {
                Log.i("image upload", "image1")
                return PICK_IMAGE_REQUEST_1
            }
            R.id.fragment_upload_edit_image2 -> {
                Log.i("image upload", "image2")
                return PICK_IMAGE_REQUEST_2
            }
            R.id.fragment_upload_edit_image3 -> {
                Log.i("image upload", "image3")
                return PICK_IMAGE_REQUEST_3
            }
        }
        return 1
    }


    private fun validate1(
        textName: TextView,
        textAge: TextView,
        pickerType: Spinner,
        pickerBreed: Spinner,
        pickerGender: Spinner) : Boolean = !(textName.text.toString().isEmpty() &&
                textName.text.toString().isBlank() &&
                textAge.text.toString().isEmpty() &&
                pickerType.selectedItem == null &&
                pickerBreed.selectedItem == null &&
                pickerGender.selectedItem == null)


    private fun validate1WithError(
        textName: TextView,
        textAge: TextView,
        pickerType: Spinner,
        pickerBreed: Spinner,
        pickerGender: Spinner
    ): Boolean {
        //validate name-age
        if(textName.text.toString().isEmpty() || textName.text.toString().isBlank()){
            textName.error = "שם זה חובה.."
            return false
        }
        if (textAge.text.toString().isEmpty()){
            textAge.error = "אם הוא חי, יש לו גיל.."
            return false
        }
        if (pickerType.selectedItem == null){
            textAge.error = "כלב חתול ?"
            return false
        }
        if (pickerBreed.selectedItem == null){
            textAge.error = "גזע ?"
            return false

        }
        if (pickerGender.selectedItem == null){
            textAge.error = "זכר נקבה ?"
            return false
        }
        return true
    }

    private fun validate2(location: AutoCompleteTextView, weight: Spinner, ownerNumber: EditText, story: EditText)
            : Boolean =
            (locations.contains(location.text.toString()) &&
            ownerNumber.text.toString().count() == 10 &&
            weight.selectedItem != null &&
            (story.text.toString().count() > 10 || story.text.toString().count() < 400))

    private fun validate2WithError(location: AutoCompleteTextView, weight: Spinner, ownerNumber: EditText, story: EditText): Boolean {
        //validate location-number-story
        if(!locations.contains(location.text.toString())){
            location.error = "מיקום מהרשימה"
            return false
        }
        if (ownerNumber.text.toString().count() != 10){
            ownerNumber.error = "מספר ישראלי עם עשר ספרות"
            return false
        }
        if (weight.selectedItem == null){
            story.error = "משקל בקילוגרם"
            return false
        }
        if (story.text.toString().count() < 10 || story.text.toString().count() > 400){
            if(story.text.toString().count() < 10){
                story.error = "ספר קצת יותר..."
                return false
            }
            if (story.text.toString().count() > 400){
                story.error = "ספר קצת  פחות..."
                return false
            }
        }
        return true
    }

}