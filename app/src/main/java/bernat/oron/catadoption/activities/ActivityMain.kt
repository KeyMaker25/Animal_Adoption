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
import bernat.oron.catadoption.adapters.AdapterVAnimal
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
    }

    override fun onStart() {
        super.onStart()
        if (intent.getStringExtra("Login") == "user need to log in"){
            loginUser()
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }   else if (!fragFilter.isHidden ||!fragRegister.isHidden){
            cleanViewFromFrag()
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
                    println("filter show")
                    toolbar.visibility = View.INVISIBLE
                    supportFragmentManager.beginTransaction().show(fragFilter).commit()
                } else {
                    println("filter hidden")
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
                if (isUserLogin()){
                    Toast.makeText(applicationContext," ביי ${auth.currentUser?.displayName}", Toast.LENGTH_LONG).show()
                    auth.signOut()
                    initTitleName()
                }else{
                    Toast.makeText(applicationContext,"...להתנתק? קודם תתחבר", Toast.LENGTH_LONG).show()
                }

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

    override fun didFilter(checkItems: ArrayList<Int>) {
        cleanViewFromFrag()
        Log.i("items to remove", checkItems.count().toString())
        if (!checkItems.isNullOrEmpty()){
            var filteredAnimalCollection = animalCollection
            for (remove in checkItems){
               when(remove){
                   0 ->{ //0 - remove area north
                       Log.i("remove ","area north")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().North.contains(it.location) } as ArrayList<Animal>
                   }
                   1 ->{// 1 - remove area haifa
                       Log.i("remove ","area haifa")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().Haifa.contains(it.location) } as ArrayList<Animal>
                   }
                   2 ->{// 2 - remove area center
                       Log.i("remove ","area center")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().Center.contains(it.location) } as ArrayList<Animal>
                   }
                   3 ->{// 3 - remove area tel-Aviv
                       Log.i("remove ","area tel-Aviv")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().TelAviv.contains(it.location) } as ArrayList<Animal>
                   }
                   4 ->{// 4 - remove area jerusalem
                       Log.i("remove ","area jerusalem")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().Jeruzalem.contains(it.location) } as ArrayList<Animal>
                   }
                   5 ->{// 5 - remove area south
                       Log.i("remove ","area south")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { IsraelDistricts().South.contains(it.location) } as ArrayList<Animal>
                   }
                   6 ->{//6 - remove age 1-6 mouths
                       Log.i("remove ","age 1-6 mouths")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { it.age.toLong()  <= 6.toLong() } as ArrayList<Animal>
                   }
                   7 ->{//7 - remove age 6mouths - 2years
                       Log.i("remove ","age 6mouths - 2years")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { it.age.toLong() >= 6.toLong() && it.age.toLong() <= 24.toLong() } as ArrayList<Animal>
                   }
                   8 ->{//8 - remove age 2years +
                       Log.i("remove ","area age 2years +")
                       filteredAnimalCollection = filteredAnimalCollection.filterNot { it.age.toLong() >= 24.toLong() } as ArrayList<Animal>
                   }
               }

            }
            Log.i("filter","removed ${animalCollection.size - filteredAnimalCollection.size} items")
            (rv.adapter as AdapterVAnimal).setList(filteredAnimalCollection)
            rv.adapter?.notifyDataSetChanged()
        } else {
            Log.i("filter","keep all")
            (rv.adapter as AdapterVAnimal).setList(animalCollection)
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
            txtTitle.text = ""
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

        // true - current show all animal false - currently show one animal
        var didClick = true
        val onClick = View.OnClickListener { v ->
            val rv = findViewById<RecyclerView>(R.id.my_recycler_view)
            when(didClick){
                true -> {
                    if (v.id == R.id.btn_txt_dogs) {
                        dogsCollection.let { (rv.adapter as AdapterVAnimal).setList(it) }
                        btnCatSelection.alpha = 0.4F
                        Snackbar.make(v,"רק כלבים",Snackbar.LENGTH_SHORT).show()
                    }
                    if (v.id == R.id.btn_txt_cats){
                        catsCollection.let { (rv.adapter as AdapterVAnimal).setList(it) }
                        btnDogSelection.alpha = 0.4F
                        Snackbar.make(v,"רק חתולים",Snackbar.LENGTH_SHORT).show()
                    }
                    didClick = false
                }
                false -> {
                    (rv.adapter as AdapterVAnimal).setList(animalCollection)
                    btnCatSelection.alpha = 1.0F
                    btnDogSelection.alpha = 1.0F
                    Snackbar.make(v,"כולם",Snackbar.LENGTH_SHORT).show()
                    didClick = true
                }
            }
            rv.adapter?.notifyDataSetChanged()
        }
        btnCatSelection.setOnClickListener(onClick)
        btnDogSelection.setOnClickListener(onClick)
    }

    private fun initCardView(){
        rv = findViewById(R.id.my_recycler_view)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = AdapterVAnimal(
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
            toolbar.visibility = View.VISIBLE
        }
        if (!fragRegister.isHidden){
            supportFragmentManager.beginTransaction().hide(fragRegister).commit()
            toolbar.visibility = View.VISIBLE
        }
    }

    private fun loginUser() {
        if (auth.currentUser == null){
            Log.i("user","NULL")
            if (fragRegister.isHidden) {
                supportFragmentManager.beginTransaction().show(fragRegister).commit()
                toolbar.visibility = View.INVISIBLE
            } else {
                supportFragmentManager.beginTransaction().hide(fragRegister).commit()
            }
        }
    }

}
