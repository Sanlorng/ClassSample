package com.sanlorng.classsample.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.net.toUri
import androidx.media.MediaBrowserServiceCompat
import com.sanlorng.classsample.BuildConfig
import com.sanlorng.classsample.R
import com.sanlorng.classsample.model.MusicModel
import kotlinx.coroutines.*
import java.util.*
import kotlin.random.Random

class PlayMusicService : Service() {

    private val mPlayer = MediaPlayer()
    private val mBinder = PlayMusicBinder()
    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mPlayer.setOnCompletionListener {
            mBinder.onPlayCompletion(it)
        }
        mPlayer.setOnPreparedListener {
            mBinder.onPlayPrepared(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.stop()
        mPlayer.release()
    }
    inner class PlayMusicBinder: Binder() {
        val playingMusic
        get() = playList[playIndex]
        private val playHistory = Stack<Int>()
        private val listeners = HashMap<String,MediaPlayer.OnCompletionListener>()
        private val callBacks = HashMap<String,((Int, Int) -> Unit)>()
        private val onPlayListeners = HashMap<String,((MusicModel) -> Unit)>()
        private val onPauseListeners = HashMap<String,((MusicModel) -> Unit)>()
        private val onResumeListeners = HashMap<String,((MusicModel) -> Unit)>()
        private val prepareListeners = HashMap<String,MediaPlayer.OnPreparedListener>()
        private var job: Job? = null
        var playIndex = 0
        val isPlaying
        get() = mPlayer.isPlaying
        val playPosition
            get() = mPlayer.currentPosition
        val playDuration
            get() = mPlayer.duration
        var playingPath = ""
        var playList = ArrayList<MusicModel>()
            set(value) {
                playHistory.clear()
                playIndex = 0
                field = value
            }
        fun play(path: String){
            playingPath = path
            mPlayer.apply {
                if (isPlaying)
                    stop()
                    reset()
                if (path.startsWith("android.resource://"))
                    setDataSource(this@PlayMusicService, path.toUri())
                else
                    setDataSource(path)
                    prepare()
                    start()
                onPlayListeners.forEach {
                    it.value.invoke(playingMusic)
                }
            }
        }

        fun pause() {
            mPlayer.pause()
            stopCallBack()
            onPauseListeners.forEach {
                it.value.invoke(playingMusic)
            }
        }

        fun resume() {
            mPlayer.start()
            startCallBack()
            onResumeListeners.forEach {
                it.value.invoke(playingMusic)
            }
        }

        fun seekTo(msec: Int) {
            mPlayer.seekTo(msec)
        }

        fun nextPlay() {
            playHistory.push(playIndex)
            while (true) {
                val temp = Random.nextInt(playList.size)
                if (playList.size==1||temp != playIndex) {
                    playIndex = temp
                    break
                }
            }
            play(playList[playIndex].path)
        }

        fun lastPlay() {
            if (playHistory.isEmpty())
                nextPlay()
            else {
                playIndex = playHistory.pop()
                play(playList[playIndex].path)
            }
        }
        private fun startCallBack(){
            if (BuildConfig.DEBUG)
                Log.e("startCallBack","true")
            if (job == null&&callBacks.isNotEmpty())
                job = GlobalScope.launch {
                    while (true) {
                        withContext(Dispatchers.Main) {
                            if (callBacks.isEmpty()) {
                                stopCallBack()
                            }
                            else
                                callBacks.forEach {
                                    it.value.invoke(mPlayer.currentPosition, mPlayer.duration)
                            }
                        }
                        delay(200)
                    }
                }
        }
        private fun stopCallBack() {
            if (BuildConfig.DEBUG)
            Log.e("stopCallBack","true")
            job?.cancel()
            job = null
        }
        fun onPlayCompletion(mediaPlayer: MediaPlayer){
            mBinder.stopCallBack()
            listeners.forEach {
                it.value.onCompletion(mediaPlayer)
            }
            nextPlay()
        }

        fun onPlayPrepared(mediaPlayer: MediaPlayer) {
            mBinder.startCallBack()
            prepareListeners.forEach {
                it.value.onPrepared(mediaPlayer)
            }
        }

        fun addCompletionListener(tag: String,listener: MediaPlayer.OnCompletionListener) {
            listeners[tag] = listener
        }
        fun addOnPreparedListener(tag: String,listener: MediaPlayer.OnPreparedListener) {
            prepareListeners[tag] = listener
        }

        fun addOnPlayListener(tag: String,callBack:(it:MusicModel) -> Unit) {
            onPlayListeners[tag] = callBack
            if (isPlaying)
                callBack.invoke(playingMusic)
        }

        fun addOnPauseListener(tag: String,callBack:(it:MusicModel) -> Unit) {
            onPauseListeners[tag] = callBack
            if (playList.isNotEmpty()&&isPlaying.not())
                callBack.invoke(playingMusic)
        }

        fun addOnResumeListener(tag: String,callBack:(it:MusicModel) -> Unit) {
            onResumeListeners[tag] = callBack
        }

        fun addPlayingCallBack(tag: String,callBack:(currentPosition: Int, total: Int) -> Unit) {
            callBacks[tag] = callBack
            if (isPlaying)
                startCallBack()
        }

        fun removeOnResumeListener(tag: String) {
            onResumeListeners.remove(tag)
        }

        fun removeOnPauseListener(tag: String) {
            onPauseListeners.remove(tag)
        }
        fun removeOnPlayListener(tag: String) {
            onPlayListeners.remove(tag)
        }
        fun removeCompletionListener(tag: String) {
            listeners.remove(tag)
        }
        fun removePlayingCallBack(tag: String) {
            callBacks.remove(tag)
        }
        fun removeOnPreparedListener(tag: String) {
            prepareListeners.remove(tag)
        }

        fun removeAllListeners(tag: String) {
            removePlayingCallBack(tag)
            removeCompletionListener(tag)
            removeOnPreparedListener(tag)
            removeOnPauseListener(tag)
            removeOnResumeListener(tag)
            removeOnPlayListener(tag)
        }
    }
}
