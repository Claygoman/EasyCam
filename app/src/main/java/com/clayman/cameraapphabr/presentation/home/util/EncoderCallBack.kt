package com.clayman.cameraapphabr.presentation.home.util

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import com.clayman.cameraapphabr.presentation.home.MainActivity
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

class EncoderCallBack(
    private val mCodec: MediaCodec,
    private val address: String,
    private val port: Int
) : MediaCodec.Callback() {

    private lateinit var udpSocket: DatagramSocket

    init {
        try {
            udpSocket = DatagramSocket()
        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }

    override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {

    }

    override fun onOutputBufferAvailable(
        codec: MediaCodec,
        index: Int,
        info: MediaCodec.BufferInfo
    ) {
        val outPutByteBuffer = mCodec.getOutputBuffer(index)
        val outDate = ByteArray(info.size)
        outPutByteBuffer?.get(outDate)

        try {
            val inetAddress = InetAddress.getByName(address)
            val packet = DatagramPacket(outDate, outDate.size, inetAddress, port)
            udpSocket.send(packet)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mCodec.releaseOutputBuffer(index, false)
    }

    override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        e.printStackTrace()
    }

    override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
        Log.i(MainActivity.LOG_TAG, "Encoder output format changed: $format")
    }
}