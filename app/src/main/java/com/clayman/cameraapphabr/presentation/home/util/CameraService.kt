package com.clayman.cameraapphabr.presentation.home.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Handler
import android.util.Log
import android.view.Surface
import androidx.core.content.ContextCompat
import com.clayman.cameraapphabr.presentation.home.MainActivity

class CameraService(
    private val context: Context,
    private val mBackgroundHandler: Handler?,
    private val cameraManager: CameraManager?,
    private val cameraId: String,
    private val address: String,
    private val port: Int
) {

    private var outputSurfaceTexture: SurfaceTexture? = null
    private var mEncoderSurface: Surface? = null
    private var mCodec: MediaCodec? = null
    private var mCameraDevice: CameraDevice? = null
    private var mSession: CameraCaptureSession? = null
    private var mPreviewBuilder: CaptureRequest.Builder? = null

    private val mCameraCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                mCameraDevice = camera
                Log.i(MainActivity.LOG_TAG, "Open camera  with id:" + mCameraDevice?.id)
                startCameraPreviewSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                mCameraDevice?.close()
                Log.i(MainActivity.LOG_TAG, "disconnect camera  with id:" + mCameraDevice?.id)
                mCameraDevice = null
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.i(MainActivity.LOG_TAG, "error! camera id:" + camera.id + " error:" + error)
            }
        }

    private fun startCameraPreviewSession() {


        outputSurfaceTexture?.setDefaultBufferSize(1920, 1080)
        val surface = Surface(outputSurfaceTexture)

        try {
            mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewBuilder?.addTarget(surface)
            mPreviewBuilder?.addTarget(mEncoderSurface!!)

            mCameraDevice?.createCaptureSession(
                listOf(surface, mEncoderSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        mSession = session
                        try {
                            mSession?.setRepeatingRequest(
                                mPreviewBuilder!!.build(),
                                null,
                                mBackgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun setUpMediaCodec() {
        mCodec = MediaCodec.createEncoderByType("video/avc")

        val width = 1920
        val height = 1080
        val colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        val videoBitrate = 50000
        val videoFramePerSecond = 60
        val iframeInterval = 0
        val format = MediaFormat.createVideoFormat("video/avc", width, height)

        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
        format.setInteger(MediaFormat.KEY_BIT_RATE, videoBitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, videoFramePerSecond)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iframeInterval)

        mCodec?.configure(
            format,
            null,
            null,
            MediaCodec.CONFIGURE_FLAG_ENCODE
        )
        mEncoderSurface = mCodec?.createInputSurface()

        val encoderCallBack = EncoderCallBack(mCodec!!, address, port)
        mCodec?.setCallback(encoderCallBack)
        mCodec?.start()
        Log.i(MainActivity.LOG_TAG, "Запустили кодек")
    }

    val isOpen: Boolean
        get() = mCameraDevice != null

    fun openCamera(surfaceTexture: SurfaceTexture) {
        outputSurfaceTexture = surfaceTexture

        try {
            val hasCameraPermission = ContextCompat
                .checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

            if (hasCameraPermission) {
                cameraManager?.openCamera(cameraId, mCameraCallback, mBackgroundHandler)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice?.close()
            mCameraDevice = null
        }
    }

    fun stopStreamingVideo() {
        if (mCameraDevice != null && mCodec != null) {
            try {
                mSession?.stopRepeating()
                mSession?.abortCaptures()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
            mCodec?.stop()
            mCodec?.release()
            mEncoderSurface?.release()
            closeCamera()
        }
    }
}