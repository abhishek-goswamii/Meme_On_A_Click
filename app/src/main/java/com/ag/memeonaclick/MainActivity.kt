package com.ag.memeonaclick

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

var imageurl: String? = null

class MainActivity : AppCompatActivity() {




    lateinit var  toggle : ActionBarDrawerToggle

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //---------------->>>>>>>--------------ad code


        loadbannerad()


        //------------------>>>>>>>--------------ad code









        loadmeme()







        // <-------navigation drawer activity starts from here ------>

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerlayout)
        val navView : NavigationView = findViewById(R.id.navview)


        toggle = ActionBarDrawerToggle(this , drawerLayout ,R.string.open , R.string.close )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.savedmeme -> Toast.makeText(applicationContext , "saved menu clicked" , Toast.LENGTH_SHORT).show()
                R.id.devinfo -> Intent(this , DeveloperInfo ::class.java).also { startActivity(it) }

            }

        true
        }

        //ends













    }

    private fun loadbannerad() {

        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)



    }


    // <-------navigation drawer activity starts from here ------>

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return  true
        }

        return super.onOptionsItemSelected(item)
    }



    //ends


    //api from here
//
//
// <------------------------------------------------------------------------------------------->




    //load meme

    private fun loadmeme(){

        pbar.visibility = View.VISIBLE

// ...

// Instantiate the RequestQueue.


        //later
//        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.herokuapp.com/gimme"

// Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null ,
            Response.Listener { response ->
                // Display the first 500 characters of the response string.
                imageurl  = response.getString("url")


                Glide.with(this).load(imageurl).listener(object: RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        pbar.visibility = View.GONE
                        return false
                    }

                }).into(findViewById(R.id.memeimage))
            },
            Response.ErrorListener {  })

// Add the request to the RequestQueue.

        //later
//        queue.add(jsonObjectRequest)

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    // share btn

    fun sharebtn(view: android.view.View) {

        val image: Bitmap? = getBitmapFromView(memeimage)

        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM , getimageurl(this , image!! ))
        startActivity(Intent.createChooser(share , "share via"))
//
//        val intent  = Intent(Intent.ACTION_SEND)
//        intent.type = "text/plain"
//        intent.putExtra(Intent.EXTRA_TEXT , imageurl)
//        val chooser = Intent.createChooser(intent , "Share this meme link using...")
//        startActivity(chooser)



    }

    private fun getBitmapFromView(view: ImageView): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width , view.height , Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    private fun getimageurl(inContext: Context, inImage: Bitmap): Uri? {
        val byte = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG , 100 , byte)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver , inImage , "Title" , null )
        return Uri.parse(path)

    }


    //nextbtn

    fun nextbtn(view: android.view.View) {
        loadmeme()
    }









}