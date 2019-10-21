package bernat.oron.catadoption.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import bernat.oron.catadoption.R
import bernat.oron.catadoption.model.AnimalCreator
import bernat.oron.catadoption.model.IsraelDistricts
import bernat.oron.catadoption.model.UploadNewAnimalInterface

class FragmentUpload2 : Fragment() {


    var listener: UploadNewAnimalInterface? = null
    private val locations = IsraelDistricts().getall()
    lateinit var location: AutoCompleteTextView
    lateinit var ownerNumber: EditText
    lateinit var story: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        location = view.findViewById(R.id.auto_complete_location)
        location.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, locations))
        val weight = view.findViewById<Spinner>(R.id.fragment_animal_picker_weight)
        ownerNumber = view.findViewById(R.id.fragment_upload_edit_phone)
        story = view.findViewById(R.id.fragment_upload_edit_story)
        val weightNumber = arrayListOf<Int>()
        for (i in 1..30){
            weightNumber.add(i)
        }
        weight.adapter = ArrayAdapter(context!!, R.layout.simple_spinner_dropdown_textview_white, weightNumber)
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (validate2WithError(location,weight,ownerNumber,story)){
                    listener?.showNextBtn()
                    AnimalCreator.instance.stageTwo(
                        location.text.toString(),
                        weight.selectedItem.toString(),
                        ownerNumber.text.toString(),
                        story.text.toString()
                    )
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }
        story.addTextChangedListener(watcher)
        ownerNumber.addTextChangedListener(watcher)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            if (listener != null) {
                listener?.restoreBtnText()
                if (validate2(location,ownerNumber,story)){
                    listener?.showNextBtn()
                }else{
                    listener?.hideNextBtn()
                }
            } else Log.e("listener", "is null")
        }
    }


    private fun validate2(location: AutoCompleteTextView, ownerNumber: EditText, story: EditText)
            : Boolean =
        (locations.contains(location.text.toString()) &&
                ownerNumber.text.toString().count() == 10 &&
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