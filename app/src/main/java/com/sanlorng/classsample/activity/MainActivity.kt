package com.sanlorng.classsample.activity

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.sanlorng.classsample.R
import com.sanlorng.classsample.mvp.music.MusicTreeLoadImpl
import com.sanlorng.classsample.service.PlayMusicService
import com.sanlorng.kit.navigationBarLight
import com.sanlorng.kit.openStatusBarShadow
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var fragment: Fragment
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fragment = supportFragmentManager.findFragmentById(R.id.fragment)!!
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        navController = fragment.findNavController()
        window.translucentSystemUI(true)
        window.openStatusBarShadow(false)
        window.navigationBarLight(true)
        MusicTreeLoadImpl.scanMediaStore(this)
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            navController.navigateUp() -> {
            }
            else -> super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.homeFragment -> item.onNavDestinationSelected(navController)
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (nav_view.checkedItem!!.itemId != R.id.homeFragment) {
            navController.navigateUp()
            toolbar.menu.findItem(R.id.homeFragment).isVisible = true
        }else
            toolbar.menu.findItem(R.id.homeFragment).isVisible = false
        drawer_layout.closeDrawer(GravityCompat.START)
        item.onNavDestinationSelected(navController)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
//        stopService(Intent(this,PlayMusicService::class.java))
    }
}
