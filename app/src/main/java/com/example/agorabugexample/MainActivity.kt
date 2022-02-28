package com.example.agorabugexample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

            }
        }

    private var mRtcEngine: RtcEngine? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.connectButton).setOnClickListener {
            connect()
        }

        findViewById<View>(R.id.broadcasterButton).setOnClickListener {
            setClientRoleBroadcaster()
        }

        findViewById<View>(R.id.audioButton).setOnClickListener {
            enableLocalAudio()
        }

        checkRecordAudioPermissions()
    }

    private fun connect() {
        // TODO: Please use your own agora ID here
        val agoraAppId = resources.getString(R.string.agora_id)

        try {
            mRtcEngine = RtcEngine.create(this, agoraAppId, mRtcEventHandler)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error creating agora room: $e")
        }

        mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine?.disableVideo()
        mRtcEngine?.setDefaultAudioRoutetoSpeakerphone(true)
        mRtcEngine?.enableAudioVolumeIndication(200, 3, true)


        val joined = mRtcEngine?.joinChannel(
            "00610f0a94905f7422280018bfc5e5c070bIAAG1a8HdQhYibKHNcajRZ74TCFQSv6fou65FeP9cXsgXQx+f9gAAAAAEAClV51H9m0eYgEAAQD2bR5i",
            "test",
            "",
            0
        )

        Log.i("MainActivity", "Joined agora channel: $joined")

        mRtcEngine?.enableLocalAudio(false)
        mRtcEngine?.muteLocalAudioStream(true)

        if (mRtcEngine != null && joined == 0) {
            Toast.makeText(this, "Successfully connected to channel", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Could not connect to channel", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkRecordAudioPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Audio permissions already granted

        } else {
            // Audio permissions not already granted,
            requestPermissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    fun setClientRoleBroadcaster() {
        if (mRtcEngine == null)
            return
        mRtcEngine?.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        Toast.makeText(this, "Set client role broadcaster", Toast.LENGTH_SHORT).show()
    }

    fun enableLocalAudio() {
        if (mRtcEngine == null)
            return
        mRtcEngine?.enableLocalAudio(true)
        Toast.makeText(this, "Audio input enabled", Toast.LENGTH_SHORT).show()
    }

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onAudioVolumeIndication(
            speakers: Array<out AudioVolumeInfo>?,
            totalVolume: Int
        ) {
            super.onAudioVolumeIndication(speakers, totalVolume)

        }

        override fun onAudioRouteChanged(routing: Int) {
            super.onAudioRouteChanged(routing)
            Log.d(this::class.java.simpleName, "AUDIO ROUTE CHANGED $routing")
        }
    }

}