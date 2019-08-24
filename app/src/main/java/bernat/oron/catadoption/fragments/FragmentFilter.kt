package bernat.oron.catadoption.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import bernat.oron.catadoption.model.FilterInterface
import bernat.oron.catadoption.R

class FragmentFilter: Fragment(){

    lateinit var btnFiler: Button
    lateinit var checkBoxL_N: CheckBox
    lateinit var checkBoxL_C: CheckBox
    lateinit var checkBoxL_S: CheckBox
    lateinit var checkBoxL_T: CheckBox
    lateinit var checkBoxL_J: CheckBox
    lateinit var checkBoxL_H: CheckBox
    lateinit var checkBoxA_1: CheckBox
    lateinit var checkBoxA_2: CheckBox
    lateinit var checkBoxA_3: CheckBox
    lateinit var checkBoxB_1: CheckBox
    lateinit var checkBoxB_2: CheckBox
    var filter: FilterInterface? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init objects
        btnFiler = view.findViewById(R.id.frag_btn_filter)

        checkBoxL_N = view.findViewById(R.id.checkboxL_north)
        checkBoxL_H = view.findViewById(R.id.checkboxL_haifa)
        checkBoxL_C = view.findViewById(R.id.checkboxL_center)
        checkBoxL_T = view.findViewById(R.id.checkboxL_tlv)
        checkBoxL_J = view.findViewById(R.id.checkboxL_jero)
        checkBoxL_S = view.findViewById(R.id.checkboxL_south)
        checkBoxA_1 = view.findViewById(R.id.checkboxA_1_6M)
        checkBoxA_2 = view.findViewById(R.id.checkboxA_6M_2Y)
        checkBoxA_3 = view.findViewById(R.id.checkboxA_2Y_)
        checkBoxB_1 = view.findViewById(R.id.checkboxB_P)
        checkBoxB_2 = view.findViewById(R.id.checkboxB_M)



    }


    /**
     * Location Location
     * I choose to use location on the unchecked box for the filter fragment
     * for filter results
     * **/
    override fun onStart() {
        super.onStart()
        val answersArr = ArrayList<Boolean>()
        btnFiler.setOnClickListener{
            answersArr.add(checkBoxL_N.isChecked) // 0 - remove area north
            answersArr.add(checkBoxL_H.isChecked) // 1 - remove area haifa
            answersArr.add(checkBoxL_C.isChecked) // 2 - remove area center
            answersArr.add(checkBoxL_T.isChecked) // 3 - remove area tel-Aviv
            answersArr.add(checkBoxL_J.isChecked) // 4 - remove area jerusalem
            answersArr.add(checkBoxL_S.isChecked) // 5 - remove area south
            answersArr.add(checkBoxA_1.isChecked) // 6 - remove age 1-6 mouths
            answersArr.add(checkBoxA_2.isChecked) // 7 - remove age 6mouths - 2years
            answersArr.add(checkBoxA_3.isChecked) // 8 - remove age 2years +
            answersArr.add(checkBoxB_1.isChecked) // 9 - remove pure breed of animal
            answersArr.add(checkBoxB_2.isChecked)// 10 - remove mix breed of animal
            val indexList = ArrayList<Int>()
            for (i in 0 until answersArr.count()){
                if (!answersArr[i]){
                    indexList.add(i)
                }
            }
            //pass the filter parameters
            filter?.didFilter(indexList)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragmet_filter, container, false)
    }

}