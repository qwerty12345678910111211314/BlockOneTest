package test.blockonetest

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val fm = this.supportFragmentManager
        val ft2 = fm!!.beginTransaction()
        ft2.replace(R.id.content, MenuView())
        ft2.addToBackStack(null)
        ft2.commit()
    }
}
