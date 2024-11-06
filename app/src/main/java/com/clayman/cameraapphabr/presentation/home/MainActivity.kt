package com.clayman.cameraapphabr.presentation.home

import android.Manifest
import android.R
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clayman.cameraapphabr.presentation.home.util.CameraService
import com.clayman.cameraapphabr.presentation.home.util.PermissionController
import com.clayman.cameraapphabr.databinding.ActivityMainBinding
import com.clayman.cameraapphabr.presentation.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var cameraServices: Array<CameraService?> = arrayOf()
    private var mCameraManager: CameraManager? = null
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var address = "192.168.31.186"
    private var port = 40002
    private val resourcesSpinner1 = arrayOf(
        "1920x1080",
        "1280x1024",
        "640x480"
    )
    private val fpsSpinner2 = arrayOf("60 fps", "30 fps")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionController = PermissionController(this, requiredPermissions)
        permissionController.requestPermissions()

        val adapterSpinner1 = ArrayAdapter(this, R.layout.simple_spinner_item, resourcesSpinner1)
        adapterSpinner1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        val adapterSpinner2 = ArrayAdapter(this, R.layout.simple_spinner_item, fpsSpinner2)
        adapterSpinner2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.dpmResourses.adapter = adapterSpinner1
        binding.dpmFps.adapter = adapterSpinner2

        binding.btStartStream.setOnClickListener {
            binding.textureView.visibility = View.VISIBLE

            cameraServices[CAMERA1]?.setUpMediaCodec()
            if (cameraServices[CAMERA1] != null) {
                val surfaceTexture = binding.textureView.surfaceTexture

                if (!cameraServices[CAMERA1]?.isOpen!! && surfaceTexture != null) {
                    cameraServices[CAMERA1]?.openCamera(surfaceTexture)
                }
            }
        }

        binding.btStopStream.setOnClickListener {
            cameraServices[CAMERA1]?.stopStreamingVideo()
            binding.textureView.visibility = View.INVISIBLE
            Toast.makeText(this@MainActivity, "Стрим остановлен", Toast.LENGTH_SHORT).show()
        }

        binding.btSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        mCameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraServices = arrayOfNulls(
                mCameraManager!!.cameraIdList.size
            )

            for (cameraID in mCameraManager!!.cameraIdList) {
                Log.i(LOG_TAG, "cameraID: $cameraID")
                val id = cameraID.toInt()

                cameraServices[id] = CameraService(
                    context = this,
                    mBackgroundHandler = mBackgroundHandler,
                    cameraManager = mCameraManager!!,
                    cameraId = cameraID,
                    address = address,
                    port = port
                )
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    public override fun onPause() {
        if (cameraServices[CAMERA1]?.isOpen == true) {
            cameraServices[CAMERA1]?.closeCamera()
        }
        stopBackgroundThread()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        startBackgroundThread()
    }

    companion object {
        const val LOG_TAG = "myLogs"
        private const val CAMERA1 = 0
    }
}