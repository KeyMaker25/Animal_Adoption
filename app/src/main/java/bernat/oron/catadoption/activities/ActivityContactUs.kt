package bernat.oron.catadoption.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uid
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ActivityContactUs : AppCompatActivity(){


    var from = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        if (intent.getStringExtra("from") == "animal page"){
            from = 1
        }
        val userTxt = findViewById<EditText>(R.id.contact_edit_txt)
        val btnSend = findViewById<Button>(R.id.btn_contact_us)
        val btnRank = findViewById<Button>(R.id.btn_send_to_rank)

        btnRank.setOnClickListener {
            try {
                startActivity(Intent(
                        Intent.ACTION_VIEW, Uri.parse("market://details?id=${applicationContext.packageName}")
                    ))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(
                        Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${applicationContext.packageName}")
                    ))
            }
        }

        btnSend.setOnClickListener {
            if (userTxt.text.isEmpty() || userTxt.text.isBlank() ){
                userTxt.error = "אנא שלח משהו..."
            } else {
                btnSend.isEnabled = false
                val info = userTxt.text.toString()
                if (info.count() > 5 ){
                    //less then 5 char ... nothing to send //
                    val ref = FirebaseDatabase.getInstance()
                    val timeDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
                    val map = mutableMapOf<String,String>()
                    map[timeDate] = info
                    ref.reference.child("Israel-tst/commands/$uid")
                        .updateChildren(map as Map<String, Any>)
                        .addOnCompleteListener {
                                task ->
                            if (task.isSuccessful){
                                Toast.makeText(this,"תודה ! המידע נשלח", Toast.LENGTH_LONG).show()
                                Log.i("send info", "Successfully")

                            }else{
                                Log.e("send info", "Failed")
                            }
                        }
                }
                android.os.Handler().postDelayed({
                    super.onBackPressed()
                    btnSend.isEnabled = true
                },2000)
            }

        }

    }

    override fun onBackPressed() {
        if (from == 1){
            startActivity(Intent(this,ActivityMain::class.java))
        }else{
            super.onBackPressed()
        }

    }

}