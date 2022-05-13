package com.example.agorabugexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.agora.rtc.Constants.*
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.RtcEngineConfig
import io.agora.rtc.RtcEngineConfig.LogConfig


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

        val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/test.log"
        val logConfig = LogConfig()
        logConfig.level = LogLevel.getValue(LogLevel.LOG_LEVEL_INFO)

        Log.i(this::class.java.simpleName, "Setting agora log path $path")
        logConfig.filePath = path
        logConfig.fileSize = 2048

        val config = RtcEngineConfig()
        config.mAppId = agoraAppId
        config.mEventHandler = mRtcEventHandler
        config.mContext = this
        config.mLogConfig = logConfig

        try {
            mRtcEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, "Error creating agora room: $e")
        }

        mRtcEngine?.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine?.disableVideo()
        mRtcEngine?.setDefaultAudioRoutetoSpeakerphone(true)
        mRtcEngine?.enableAudioVolumeIndication(200, 3, true)
        mRtcEngine?.setParameters("{\"che.audio.force.bluetooth.a2dp\":0}")

        val joined = mRtcEngine?.joinChannel(
            "00610f0a94905f7422280018bfc5e5c070bIACi2sqLOXS82MAFSFDmZFyGp1b0NR89M2ycZRhg/ISe3gx+f9gAAAAAEAA4smrcIet/YgEAAQAh639i",
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
        mRtcEngine?.setClientRole(CLIENT_ROLE_BROADCASTER)
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