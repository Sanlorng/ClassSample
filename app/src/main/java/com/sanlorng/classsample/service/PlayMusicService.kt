package com.sanlorng.classsample.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
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
        private val prepareListeners = HashMap<String,MediaPlayer.OnPreparedListener>()
        private var job: Job? = null
        var playIndex = 0
        val isPlaying
        get() = mPlayer.isPlaying
        val playPosition
                get() = mPlayer.currentPosition
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
                    setDataSource(path)
                    prepare()
                    start()

            }
        }

        fun pause() {
            mPlayer.pause()
        }

        fun resume() {
            mPlayer.start()
        }

        fun seekTo(msec: Int) {
            mPlayer.seekTo(msec)
        }

        fun nextPlay() {
            playHistory.push(playIndex)
            playIndex = Random.nextInt(playList.size)
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
            if (job == null)
                job = GlobalScope.launch {
                    while (true) {
                        delay(0)
                        withContext(Dispatchers.Main) {
                            callBacks.forEach {
                                it.value.invoke(mPlayer.currentPosition, mPlayer.duration)
                            }
                        }
                    }
                }
        }
        private fun stopCallBack() {
            job?.cancel()
            job = null
        }
        fun addCompletionListener(tag: String,listener: MediaPlayer.OnCompletionListener) {
            listeners[tag] = listener
        }
        fun addOnPreparedListener(tag: String,listener: MediaPlayer.OnPreparedListener) {
            prepareListeners[tag] = listener
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
        fun removeCompletionListener(tag: String) {
            listeners.remove(tag)
        }
        fun removePlayingCallBack(tag: String) {
            callBacks.remove(tag)
        }

        fun removeOnPreparedListener(tag: String) {
            prepareListeners.remove(tag)
        }
        fun addPlayingCallBack(tag: String,callBack:(currentPosition: Int, total: Int) -> Unit) {
            callBacks[tag] = callBack
        }
        fun removeAllListeners(tag: String) {
            removePlayingCallBack(tag)
            removeCompletionListener(tag)
            removeOnPreparedListener(tag)
        }
    }
}
