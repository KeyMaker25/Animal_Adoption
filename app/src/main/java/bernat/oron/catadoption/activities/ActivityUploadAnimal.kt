package bernat.oron.catadoption.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import bernat.oron.catadoption.R
import bernat.oron.catadoption.adapters.ZoomOutPageTransformer
import bernat.oron.catadoption.fragments.ScreenSlidePageFragment
import bernat.oron.catadoption.model.PagerMoveInterface

private const val NUM_PAGES = 3

class ActivityUploadAnimal : FragmentActivity() , PagerMoveInterface {


    private lateinit var mPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_animal)

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.upload_pager)
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)

        mPager.adapter = pagerAdapter
        mPager.setOnTouchListener {
                v, event -> true
        }
        mPager.setPageTransformer(true, ZoomOutPageTransformer())
    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    override fun moveNext(name: String, age: String, type: String, breed: String, gender: String) {
        mPager.currentItem += 1
    }

    override fun moveBack(){
        print("move back")
        mPager.currentItem -= 1
    }



    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment{
            val frag = ScreenSlidePageFragment(position)
            frag.listener = this@ActivityUploadAnimal
            return frag
        }


    }

}