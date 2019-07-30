package com.video.sample

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.video.trimmer.interfaces.OnTrimVideoListener
import com.video.trimmer.interfaces.OnVideoListener
import kotlinx.android.synthetic.main.activity_trimmer.*
import java.io.File

class TrimmerActivity : AppCompatActivity(), OnTrimVideoListener, OnVideoListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trimmer)

        setupPermissions {
            val extraIntent = intent
            var path = ""
            if (extraIntent != null) path = extraIntent.getStringExtra(MainActivity.EXTRA_VIDEO_PATH)
            videoTrimmer.setTextTimeSelectionTypeface(FontsHelper[this, FontsConstants.SEMIBOLD])
            videoTrimmer.setOnTrimVideoListener(this)
            videoTrimmer.setOnVideoListener(this)
            videoTrimmer.setVideoURI(Uri.parse(path))
            videoTrimmer.setVideoInformationVisibility(true)
            videoTrimmer.destinationPath = Environment.getExternalStorageDirectory().toString() + File.separator + "Zoho Social" + File.separator + "Videos" + File.separator
        }

        back.setOnClickListener {
            videoTrimmer.onCancelClicked()
        }

        save.setOnClickListener {
            videoTrimmer.onSaveClicked()
        }
    }

    override fun onTrimStarted() {
        RunOnUiThread(this@TrimmerActivity).safely {
            Toast.makeText(this@TrimmerActivity, "Started Trimming", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getResult(uri: Uri) {
        RunOnUiThread(this@TrimmerActivity).safely {
            RunOnUiThread(this@TrimmerActivity).safely {
                Toast.makeText(this@TrimmerActivity, getString(R.string.video_saved_at, uri.path), Toast.LENGTH_SHORT).show()
            }
//            val values = ContentValues()
//            values.put(MediaStore.Video.Media.DATA, uri.path)
//            val id = ContentUris.parseId(contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values))
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
//            val intent = Intent(Intent.ACTION_VIEW, uri)
//            intent.setDataAndType(uri, "video/*")
//            startActivity(intent)
//            finish()
        }
    }

    override fun cancelAction() {
        RunOnUiThread(this@TrimmerActivity).safely {
            videoTrimmer.destroy()
            finish()
        }
    }

    override fun onError(message: String) {
        RunOnUiThread(this@TrimmerActivity).safely {
            Log.e("ERROR", message)
        }
    }

    override fun onVideoPrepared() {
        RunOnUiThread(this@TrimmerActivity).safely {
            Toast.makeText(this@TrimmerActivity, "onVideoPrepared", Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var dothis: () -> Unit
    private fun setupPermissions(doSomething: () -> Unit) {
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        dothis = doSomething
        if (writePermission != PackageManager.PERMISSION_GRANTED && readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        } else dothis()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    PermissionsDialog(this@TrimmerActivity, "To continue, give Zoho Social access to your Photos.").show()
                } else dothis()
            }
        }
    }
}