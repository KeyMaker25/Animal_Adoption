package bernat.oron.catadoption.activities

import android.content.DialogInterface
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import bernat.oron.catadoption.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.*

class ActivitySetting : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val editName: EditText = findViewById(R.id.setting_name_edit_txt)
        val btnSave: Button = findViewById(R.id.setting_btn_save)
        val btnDel: Button = findViewById(R.id.setting_btn_del)
        if (user != null){
            editName.setText(user.displayName)
        }else{
            super.onBackPressed()
        }
        btnSave.setOnClickListener {
            //check if the name have changed
            if (user!!.displayName!!.toUpperCase(Locale.ENGLISH).removePrefix(" ") != editName.text.toString().toUpperCase(Locale.ENGLISH).removePrefix(" ")){
                val newDisplayName = editName.text.toString()
                user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(newDisplayName).build())
            }
        }
        btnDel.setOnClickListener {
            val alert = AlertDialog.Builder(this).create()
            alert.setTitle("${user?.displayName} מחיקה ")
            alert.setMessage("האם אתה בטוח שברצונך למחוק את המשתמש ? לא יהיה ניתן לשחזרו")
            alert.setButton(AlertDialog.BUTTON_NEGATIVE,"מחיקה") {
                _,_->
                if (user!!.delete().isSuccessful){
                    Log.i("user deletion","USER DELETED")
                }else {
                    Log.i("user deletion", user.delete().exception.toString())
                }
            }
            alert.setButton(AlertDialog.BUTTON_POSITIVE,"בטל"){
                _,_->
                alert.dismiss()
            }
            alert.show()

        }




    }






}
