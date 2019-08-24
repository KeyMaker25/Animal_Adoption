package bernat.oron.catadoption.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bernat.oron.catadoption.adapters.AnimalAdapterV
import bernat.oron.catadoption.model.FilterInterface
import bernat.oron.catadoption.fragments.FragmentFilter
import bernat.oron.catadoption.fragments.FragmentRegistration
import bernat.oron.catadoption.R
import bernat.oron.catadoption.activities.ActivitySplash.Companion.animalCollection
import bernat.oron.catadoption.activities.ActivitySplash.Companion.catsCollection
import bernat.oron.catadoption.activities.ActivitySplash.Companion.dogsCollection
import bernat.oron.catadoption.activities.ActivitySplash.Companion.isUserLogin
import bernat.oron.catadoption.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class ActivityMain : AppCompatActivity(), View.OnClickListener, RegistrationInterface, FilterInterface {

    lateinit var btnDogSelection: TextView
    lateinit var btnCatSelection: TextView
    lateinit var btnFilter : Button
    lateinit var btnFavorite : Button
    lateinit var txtTitle : TextView
    lateinit var txtSetting : TextView
    lateinit var txtLogout : TextView
    lateinit var txtContact : TextView
    lateinit var auth: FirebaseAuth
    lateinit var fragRegister: Fragment
    lateinit var fragFilter: Fragment
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var rv: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)        // #81C784 app backGroundColor
        initCardView()
        initNavigation()
        initFragmentsAndListener()
        if (intent.getStringExtra("Login") == "user need to log in"){
            loginUser()
        }

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }   else if (!fragFilter.isHidden){
            supportFragmentManager.beginTransaction().hide(fragFilter).commit()
        }   else if(!fragRegister.isHidden){
            supportFragmentManager.beginTransaction().hide(fragRegister).commit()
        }   else {
            finishAffinity()
            exitProcess(0)
        }
    }

    override fun onClick(v: View?) {
        cleanViewFromFrag()
        when (v?.id) {
            R.id.btn_nav_filter-> {
                if (fragFilter.isHidden) {
                    println("hidden")
                    supportFragmentManager.beginTransaction().show(fragFilter).commit()
                } else {
                    println("not hidden")
                    supportFragmentManager.beginTransaction().hide(fragFilter).commit()
                }
            }
            R.id.btn_nav_favorite ->{
                if (isUserLogin()) {
                    startActivity(Intent(applicationContext, ActivityFavorite::class.java))
                }else{
                    Toast.makeText(applicationContext, "צריך להיות מחובר", Toast.LENGTH_LONG).show()
                    loginUser()
                }
            }
            R.id.nav_contact ->{
                if (isUserLogin()) {
                    startActivity(Intent(this, ActivityContactUs::class.java))
                }else{
                    Toast.makeText(applicationContext, "צריך להיות מחובר", Toast.LENGTH_LONG).show()
                    loginUser()
                }
            }
            R.id.nav_setting ->{
                if (isUserLogin()) {
                    startActivity(Intent(this, ActivitySetting::class.java))
                }else{
                    Toast.makeText(applicationContext, "צריך להיות מחובר", Toast.LENGTH_LONG).show()
                    loginUser()
                }
            }
            R.id.nav_logout ->{
                auth.signOut()
                Toast.makeText(applicationContext,"מתנתק", Toast.LENGTH_LONG).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun didFinish(res: Boolean) {
        if (res){
            cleanViewFromFrag()
            initTitleName()
        }
    }

    /**
     9 - remove pure breed of animal
     10 - remove mix breed of animal
    **/
    override fun didFilter(checkItems: ArrayList<Int>) {
        cleanViewFromFrag()
        if (checkItems.isNotEmpty()){
            //TODO: filter out the animals with matching category
            var filteredAnimalCollection = animalCollection
            for (remove in checkItems){
               when(remove){
                   0 ->{ //0 - remove area north
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().North.contains(it.location) } as ArrayList<AnimalsFactory>
                   }
                   1 ->{// 1 - remove area haifa
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().Haifa.contains(it.location) } as ArrayList<AnimalsFactory>
                   }
                   2 ->{// 2 - remove area center
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().Center.contains(it.location) } as ArrayList<AnimalsFactory>
                   }
                   3 ->{// 3 - remove area tel-Aviv
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().TelAviv.contains(it.location) } as ArrayList<AnimalsFactory>
                   }
                   4 ->{// 4 - remove area jerusalem
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().Jeruzalem.contains(it.location) } as ArrayList<AnimalsFactory>
                   }
                   5 ->{// 5 - remove area south
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().South.contains(it.location) } as ArrayList<AnimalsFactory>
                   }
                   6 ->{//6 - remove age 1-6 mouths
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { it.age.toLong()  <= 6.toLong() } as ArrayList<AnimalsFactory>
                   }
                   7 ->{//7 - remove age 6mouths - 2years
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { it.age.toLong() >= 6.toLong() && it.age.toLong() <= 24.toLong() } as ArrayList<AnimalsFactory>
                   }
                   8 ->{//8 - remove age 2years +
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { it.age.toLong() >= 24.toLong() } as ArrayList<AnimalsFactory>
                   }
                   9 ->{

                   }
                   10 ->{

                   }
               }

            }
            (rv.adapter as AnimalAdapterV).setList(filteredAnimalCollection)
            rv.adapter?.notifyDataSetChanged()
        }else{
            (rv.adapter as AnimalAdapterV).setList(animalCollection)
            rv.adapter?.notifyDataSetChanged()
        }

    }

    private fun initTitleName(){
        txtTitle = findViewById(R.id.txtUsername)
        btnCatSelection = findViewById(R.id.btn_txt_cats)
        btnDogSelection = findViewById(R.id.btn_txt_dogs)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.title = null
        if (isUserLogin()){
            txtTitle.text = auth.currentUser?.displayName
        } else {
            txtTitle.text = "היי משתמש חדש"
        }
    }

    private fun initNavigation(){
        initTitleName()
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        btnFilter = findViewById(R.id.btn_nav_filter)
        btnFilter.setOnClickListener(this)
        btnFavorite = findViewById(R.id.btn_nav_favorite)
        btnFavorite.setOnClickListener(this)
        txtContact = findViewById(R.id.nav_contact)
        txtContact.setOnClickListener(this)
        txtSetting = findViewById(R.id.nav_setting)
        txtSetting.setOnClickListener(this)
        txtLogout = findViewById(R.id.nav_logout)
        txtLogout.setOnClickListener(this)

        var clickCat = true
        var clickDog = true
        val onClick = View.OnClickListener{ v ->
            val rv = findViewById<RecyclerView>(R.id.my_recycler_view)
            if (v.id == R.id.btn_txt_cats){
                if (clickCat){
                    Snackbar.make(v,"רק חתולים",Snackbar.LENGTH_SHORT).show()
                    catsCollection.let { (rv.adapter as AnimalAdapterV).setList(it) }
                }else{
                    Snackbar.make(v,"כולם",Snackbar.LENGTH_SHORT).show()
                    (rv.adapter as AnimalAdapterV).setList(animalCollection)
                }
                clickCat = !clickCat
            } else {
                if (clickDog){
                    Snackbar.make(v,"רק כלבים",Snackbar.LENGTH_SHORT).show()
                    dogsCollection.let { (rv.adapter as AnimalAdapterV).setList(it) }
                }else{
                    Snackbar.make(v,"כולם",Snackbar.LENGTH_SHORT).show()
                    (rv.adapter as AnimalAdapterV).setList(animalCollection)
                }
                clickDog= !clickDog
            }
            rv.adapter?.notifyDataSetChanged()
        }

        btnCatSelection.setOnClickListener(onClick)
        btnDogSelection.setOnClickListener(onClick)

    }

    private fun initCardView(){
        rv = findViewById(R.id.my_recycler_view)
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = AnimalAdapterV(
            animalCollection,
            this
        )
        adapter.onItemClick = { item ->
            //send the item to next view page
            val i = Intent(this, ActivityAnimalPage::class.java)
            i.putExtra("animal", item)
            startActivity(i)
        }
        rv.adapter = adapter
    }

    private fun initFragmentsAndListener() {
        fragRegister = FragmentRegistration()
        (fragRegister as FragmentRegistration).fragmentInterfaceInterface = this
        fragFilter = FragmentFilter()
        (fragFilter as FragmentFilter).filter = this

        // adding fragment to view container
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragRegister)
            .hide(fragRegister)
            .add(R.id.fragment_container,fragFilter)
            .hide(fragFilter)
            .commit()
    }

    private fun cleanViewFromFrag(){
        if (!fragFilter.isHidden){
            supportFragmentManager.beginTransaction().hide(fragFilter).commit()
        }
        if (!fragRegister.isHidden){
            supportFragmentManager.beginTransaction().hide(fragRegister).commit()
        }
    }

    private fun loginUser() {
        if (auth.currentUser == null){
            if (fragRegister.isHidden) {
                supportFragmentManager.beginTransaction().show(fragRegister).commit()
            } else {
                supportFragmentManager.beginTransaction().hide(fragRegister).commit()
            }
        }
    }

}
