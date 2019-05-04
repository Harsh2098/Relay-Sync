package com.hmproductions.relaysync

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.hmproductions.relaysync.adapter.ContentAdapter
import com.hmproductions.relaysync.data.RelayViewModel
import com.hmproductions.relaysync.fragments.RelayFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var model: RelayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolBar)
        model = ViewModelProviders.of(this).get(RelayViewModel::class.java)

        contentViewPager.adapter = ContentAdapter(supportFragmentManager)

        tabLayout.setupWithViewPager(contentViewPager)
        tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.colorAccent)
        )

        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.calculate_action -> {
                val page = supportFragmentManager.findFragmentByTag(
                    "android:switcher:" + R.id.contentViewPager + ":" +
                            contentViewPager.currentItem
                )
                when (contentViewPager.currentItem) {
                    0 -> (page as RelayFragment).calculateRelayParameters()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
