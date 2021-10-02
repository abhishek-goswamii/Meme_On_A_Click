package com.ag.memeonaclick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class Policy : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)






//        val title = "Privacy policy"
//        this.setTitle(title)




        supportActionBar!!.title = "Privacy Policy"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)



        val view = WebView(this)
        view.settings.javaScriptEnabled = true
        view.loadUrl("file:///android_asset/PrivacyPolicy.html")
        setContentView(view)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true;
    }
}