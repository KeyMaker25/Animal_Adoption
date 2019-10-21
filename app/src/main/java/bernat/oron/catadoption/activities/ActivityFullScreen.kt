package bernat.oron.catadoption.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import bernat.oron.catadoption.R
import bernat.oron.catadoption.adapters.AdapterSlideImage
import com.viewpagerindicator.CirclePageIndicator


class ActivityFullScreen : AppCompatActivity(){

    lateinit var pager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        val arr = intent.getStringArrayListExtra("images")
        Log.e("array - ",arr.random())

        pager = findViewById(R.id.slide_pager)
        val adapter = AdapterSlideImage(this, arr)
        adapter.onItemClick = null
        pager.adapter = adapter

        val indicator = findViewById<CirclePageIndicator>(R.id.slide_indicator)
        indicator.setViewPager(pager)
        indicator.radius = 5 * resources.displayMetrics.density

    }
}