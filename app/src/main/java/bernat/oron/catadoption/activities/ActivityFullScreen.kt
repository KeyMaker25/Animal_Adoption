package bernat.oron.catadoption.activities


import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import bernat.oron.catadoption.R
import com.bumptech.glide.Glide


class ActivityFullScreen : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_full_screen)
        val imageByteArr = intent.getByteArrayExtra("image")
        val imageView = findViewById<ImageView>(R.id.image_full)


        imageView.post {
            Glide.with(applicationContext)
                .load(imageByteArr)
                .into(imageView)
        }

    }
}