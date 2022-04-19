package com.msdev.ytidownloads

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*

class YtActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var formate : String = ""
    lateinit var dwd: Button
    lateinit var mAdView: AdView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yt)


        MobileAds.initialize(this) {}
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        mAdView = findViewById(R.id.AdView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView = findViewById(R.id.AdView3)


        dwd = findViewById(R.id.dwd)
        val intent = intent
        val yturl = intent.getStringExtra("Data")
        val txtUrlSourceCode = findViewById<TextView>(R.id.txtUrlSourceCode)
        val spinner: Spinner = findViewById(R.id.spinner)
        txtUrlSourceCode.text = yturl
        spinner.onItemSelectedListener = this
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.planets_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        dwd.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://loader.to/api/button/?url=$yturl"+"&f=$formate"))
            startActivity(browserIntent)
        }


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text:String = parent?.getItemAtPosition(position).toString()
        formate = text
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}