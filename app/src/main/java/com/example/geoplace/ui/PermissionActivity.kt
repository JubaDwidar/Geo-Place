package com.example.geoplace.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.geoplace.databinding.ActivityPermissionBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class PermissionActivity : AppCompatActivity(), PermissionListener {
    private lateinit var binding: ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkPermission()

        binding.permissionBtn.setOnClickListener {
            getPermission()
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //  startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this,"permission granted", Toast.LENGTH_LONG).show()
            Log.i("perTAG", "checkPermission: ")
         //    finish()
           // return
        } else {
            getPermission()
        }

    }

    fun getPermission() {
        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this).check()

    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {

        startActivity(Intent(this, MapsActivity::class.java))
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken) {

        p1.continuePermissionRequest()
    }

    override fun onPermissionDenied(p0: PermissionDeniedResponse) {
        if (p0.isPermanentlyDenied) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permission Denied")
                .setMessage("permission is denied permanently. you must go to settings to modify it..")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("ok", listener).show()

            Toast.makeText(this, "You have denied permission which is mandatory for GeoPlace App", Toast.LENGTH_LONG).show()
        }
        finish()
    }


    private val listener = DialogInterface.OnClickListener { dialog, which ->
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("Package", packageName, null)
    }

}
