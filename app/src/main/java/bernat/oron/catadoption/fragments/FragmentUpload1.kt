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
import bernat.oron.catadoption.model.*


class FragmentUpload1 : Fragment() {

    var listener: UploadNewAnimalInterface? = null
    var textName: TextView? = null
    var textAge: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_upload1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textName = view.findViewById(R.id.fragment_upload1_edit_name)
        textAge = view.findViewById(R.id.fragment_upload1_edit_age)
        val pickerType = view.findViewById<Spinner>(R.id.fragment_upload1_picker_type)
        val pickerBreed = view.findViewById<Spinner>(R.id.fragment_upload1_picker_breed)
        val pickerGender = view.findViewById<Spinner>(R.id.fragment_upload1_picker_gender)

        pickerGender.adapter = ArrayAdapter(
            context!!,
            R.layout.simple_spinner_dropdown_textview_white,
            arrayOf("זכר", "נקבה")
        )
        pickerType.adapter = ArrayAdapter(
            context!!,
            R.layout.simple_spinner_dropdown_textview_white,
            arrayOf("כלב", "חתול")
        )
        pickerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                pickerBreed.adapter = ArrayAdapter(
                    context!!, R.layout.simple_spinner_dropdown_textview_white,
                    ActivitySplash.dogType
                )
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when {
                    parent?.getItemAtPosition(position) == "חתול" -> {
                        pickerBreed.adapter =
                            ArrayAdapter(
                                context!!, R.layout.simple_spinner_dropdown_textview_white,
                                ActivitySplash.catType
                            )
                    }
                    parent?.getItemAtPosition(position) == "כלב" -> {
                        pickerBreed.adapter =
                            ArrayAdapter(
                                context!!, R.layout.simple_spinner_dropdown_textview_white,
                                ActivitySplash.dogType
                            )
                    }
                }
            }
        }
        textAge?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (validate1WithError(
                        textName!!,
                        textAge!!,
                        pickerType,
                        pickerBreed,
                        pickerGender))
                {
                    listener?.showNextBtn()
                    AnimalCreator.instance.stageOne(
                        textName!!.text.toString(),
                        textAge!!.text.toString(),
                        pickerType.selectedItem.toString(),
                        pickerBreed.selectedItem.toString(),
                        pickerGender.selectedItem.toString()
                    )
                } else {
                    listener?.hideNextBtn()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {}
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser && textName != null)
            if (validate1(textName!!, textAge!!)) {
            Log.i("Next Btn", "show")
            listener?.showNextBtn()
        }
    }


    private fun validate1(textName: TextView, textAge: TextView)
            : Boolean
            = !(textName.text.toString().isEmpty() &&
                textName.text.toString().isBlank() &&
                textAge.text.toString().isEmpty())


    private fun validate1WithError(textName: TextView, textAge: TextView, pickerType: Spinner,
                                   pickerBreed: Spinner, pickerGender: Spinner): Boolean {
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


}