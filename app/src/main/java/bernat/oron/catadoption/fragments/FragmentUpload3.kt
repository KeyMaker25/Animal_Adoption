package bernat.oron.catadoption.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.PICK_IMAGE_REQUEST_1
import bernat.oron.catadoption.activities.PICK_IMAGE_REQUEST_2
import bernat.oron.catadoption.activities.PICK_IMAGE_REQUEST_3
import bernat.oron.catadoption.model.UploadNewAnimalInterface

class FragmentUpload3 : Fragment() {

    var image1: ImageView? = null
    var image2: ImageView? = null
    var image3: ImageView? = null
    var listener: UploadNewAnimalInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            if (listener != null){
                listener?.changeBtnText()
                listener?.showNextBtn()
            }else Log.e("listener", "is null")
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
}