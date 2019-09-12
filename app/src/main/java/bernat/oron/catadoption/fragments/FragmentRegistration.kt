package bernat.oron.catadoption.fragments

import android.widget.Toast
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.EditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.ActivitySplash
import bernat.oron.catadoption.activities.ActivitySplash.Companion.uid
import bernat.oron.catadoption.model.RegistrationInterface
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class FragmentRegistration : Fragment() {

    var fragmentInterfaceInterface : RegistrationInterface? = null

    lateinit var nameText: EditText
    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var signupButton: Button
    lateinit var loginLink: TextView
    lateinit var auth: FirebaseAuth
    lateinit var nameCon: TextInputLayout
    var isLogin = false


    override fun onDestroy() {
        super.onDestroy()
        fragmentInterfaceInterface = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameText = view.findViewById(R.id.input_name)
        emailText = view.findViewById(R.id.input_email)
        passwordText = view.findViewById(R.id.input_password)
        signupButton = view.findViewById(R.id.btn_signup)
        loginLink = view.findViewById(R.id.link_login)
        nameCon = view.findViewById(R.id.input_name_con)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_signup, container, false)

    override fun onStart() {
        super.onStart()

        signupButton.setOnClickListener {
            if (isLogin){ // login
                login()
            }else{ //sign up
                signup()
            }
        }

        loginLink.setOnClickListener {
            //this btn help change the view.
            if (isLogin){ // login
                nameCon.visibility = View.VISIBLE
                nameText.visibility = View.VISIBLE
                signupButton.text = getString(R.string.create_account)
                loginLink.text = getString(R.string.already_a_member_login)
            }else {
                nameCon.visibility = View.GONE
                nameText.visibility = View.GONE
                signupButton.text = getString(R.string.login)
                loginLink.text = getString(R.string.signup)
            }
            isLogin = !isLogin
        }
    }

    private fun setDisplayName(name: String){
        if (auth.currentUser?.displayName == null){
            //open alert for choosing display name
            val userProfile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            auth.currentUser?.updateProfile(userProfile)
        }

    }

    private fun login(){
        if (!validate(true)){
            onSignupFailed()
            return
        }
        signupButton.isEnabled = false
        val progressDialog = ProgressDialog(
            context,
            R.style.AppTheme
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("מתחבר")
        progressDialog.show()

        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                task ->
            android.os.Handler().postDelayed(
                {
                    if (task.isSuccessful){
                        onSignupSuccess()
                        startActivity(Intent(context,ActivitySplash::class.java))
                    }else{
                        onSignupFailed()
                        Log.e("error" ,task.exception.toString())
                    }
                    progressDialog.dismiss()
                }, 2000
            )
        }

    }

    private fun signup() {
        if (!validate(false)) {
            onSignupFailed()
            return
        }

        signupButton.isEnabled = false

        val progressDialog = ProgressDialog(
            context,
            R.style.AppTheme
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("יוצר חשבון")
        progressDialog.show()

        val name = nameText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            task ->
            android.os.Handler().postDelayed(
                {
                    if (task.isSuccessful){
                        setDisplayName(name)
                        onSignupSuccess()
                    }else{
                        onSignupFailed()
                        println("error ${task.exception}")
                    }
                    progressDialog.dismiss()
                }, 2000
            )

        }

    }

    fun onSignupSuccess() {
        fragmentInterfaceInterface?.didFinish(true)
        Toast.makeText(context, " ברוך הבא ${auth.currentUser?.displayName}", Toast.LENGTH_LONG).show()
        uid = auth.currentUser!!.uid
        signupButton.isEnabled = true
    }

    fun onSignupFailed() {
        fragmentInterfaceInterface?.didFinish(false)
        Toast.makeText(context, "התחברות נכשלה", Toast.LENGTH_LONG).show()
        signupButton.isEnabled = true
    }

    fun validate(isLogin : Boolean): Boolean {
        var valid = true
        val name = nameText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (!isLogin){
            if (name.isEmpty() || name.length < 3) {
                nameText.error = "לפחות 3 תווים"
                valid = false
            } else {
                nameText.error = null
            }
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.error = "הכנס כתובת אימייל תקינה בבקשה"
            valid = false
        } else {
            emailText.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordText.error = "בין 4 ל 10 מספרים נומרים בבקשה"
            valid = false
        } else {
            passwordText.error = null
        }

        return valid
    }
}