package com.ag.memeonaclick

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.io.*

var imageurl: String? = null

class MainActivity : AppCompatActivity() {

    lateinit var  toggle : ActionBarDrawerToggle

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val title = "Home"

        this.setTitle(title)

        requestpermission() //runtime permission

        getStorageDir() //creating dir

        loadbannerad() //ad

        loadmeme() //api call



        // <-------navigation drawer activity from here ------>

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerlayout)
        val navView : NavigationView = findViewById(R.id.navview)


        toggle = ActionBarDrawerToggle(this , drawerLayout ,R.string.open , R.string.close )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.savedmeme -> savedintent()
                R.id.devinfo -> Intent(this , DeveloperInfo ::class.java).also { startActivity(it) }
                R.id.rate -> playstorelink()

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


    private fun loadmeme(){

        pbar.visibility = View.VISIBLE

// Instantiate the RequestQueue
//        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.herokuapp.com/gimme"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null ,
            Response.Listener { response ->
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

//        queue.add(jsonObjectRequest)

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun sharebtn(view: android.view.View) {

        val image: Bitmap? = getBitmapFromView(memeimage)

        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM , getimageurl(this , image!! ))
        startActivity(Intent.createChooser(share , "share via"))

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


    fun nextbtn(view: android.view.View) {

        loadmeme()

        Toast.makeText(this, "loading" , Toast.LENGTH_SHORT).show()

    }


    // runtime storage permission

    private fun haspermisson() : Boolean {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestpermission(){
        var permission = mutableListOf<String>()

        if(!haspermisson()){
            permission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(permission.isNotEmpty()){
            ActivityCompat.requestPermissions(this , permission.toTypedArray() , 0)
        }
    }


    fun savebtn(view: android.view.View) {

        Toast.makeText(this, "Saved Inside DCIM/Meme On A Click", Toast.LENGTH_SHORT).show()

        saveimage() //bitmap image



    }

    private fun saveimage(){

        //>>>>>>>>>>>>>>>>>>>>>>>>

        val draw = memeimage.getDrawable() as BitmapDrawable
        val bitmap = draw.bitmap

        var outStream: FileOutputStream? = null
        val sdCard = Environment.getExternalStorageDirectory()
        val dir = File(sdCard.absolutePath.toString() + "/DCIM/Meme On A Click")

        val fileName = String.format("%d.jpg", System.currentTimeMillis())


        val outFile = File(dir, fileName)
        outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()

    }


    fun getStorageDir() {

        val folderName = "Meme On A Click"
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            folderName
        )


        if (!folder.exists()) {

            folder.mkdirs()

            if(!folder.mkdirs()){
                Toast.makeText(this , "Enable storage permission manually" , Toast.LENGTH_SHORT).show()

            }


        }

    }


    fun savedintent(){

        val selectedUri: Uri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/DCIM/Meme On A Click/")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(selectedUri, "resource/folder")
        startActivity(intent)

    }

    fun memeimgclick(view: android.view.View) {
        loadmeme()

    }

    fun playstorelink(){
        val url = "https://play.google.com/store/apps"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }


}