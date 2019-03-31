package com.sanlorng.classsample.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.sanlorng.classsample.R
import com.sanlorng.classsample.model.MusicModel
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.music.MusicTreeLoadImpl
import com.sanlorng.classsample.service.PlayMusicService
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_music_play.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_music.*
import java.lang.RuntimeException

class MusicPlayActivity : AppCompatActivity() {
    private var musicBinder: PlayMusicService.PlayMusicBinder? = null
    private var controlTag = "playingActivity"
    private var isDrag = false
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as PlayMusicService.PlayMusicBinder
            musicBinder?.apply {
                addOnPreparedListener(controlTag, MediaPlayer.OnPreparedListener {
                    playingMusic.apply {
                        setPlayInfo(this)
                    }
                })

                addCompletionListener(controlTag, MediaPlayer.OnCompletionListener {
                })

                addPlayingCallBack(controlTag) { currentPosition, total ->
                    musicBinder?.playingMusic?.apply {
                        if (isDrag.not())
                            seekBarPlayingActivity.progress = currentPosition
                        seekBarPlayingActivity.max = total
                    }
                }

                addOnPauseListener(controlTag) {
                        setPlayInfo(it)
                }

                addOnPlayListener(controlTag) {
                    setPlayInfo(it)
                }

                addOnResumeListener(controlTag) {
                    setPlayInfo(it)
                }
                seekBarPlayingActivity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        isDrag = true
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        musicBinder?.apply {
                            if (isPlaying)
                                seekTo(seekBar?.progress ?: 0)
                        }
                        isDrag = false
                    }
                })

                buttonNextPlayMusicActivity.setOnClickListener {
                    if (checkPlay())
                        musicBinder?.nextPlay()
                }

                buttonLastMusicActivity.setOnClickListener {
                    if (checkPlay())
                        musicBinder?.lastPlay()
                }

                buttonPlayMusicActivity.setOnClickListener {
                    musicBinder?.apply {
                        if (checkPlay()) {
                            if (isPlaying) {
                                pause()
                            }
                            else {
                                resume()
                            }
                        }
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicBinder?.apply {
                Log.e("onDisc","true")
                removePlayingCallBack(controlTag)
                removeOnPreparedListener(controlTag)
                removeCompletionListener(controlTag)
            }
        }
    }

    private fun setPlayInfo(model: MusicModel) {
        model.apply {
            textSubtitlePlayActivity.text = String.format("%s - %s", artist, album)
            textTitlePlayActivity.text = title
            musicBinder?.apply {
                switchPlayIconState(isPlaying)
                seekBarPlayingActivity.max = playDuration
                seekBarPlayingActivity.progress = playPosition
            }
        }
    }
    fun checkPlay():Boolean{
        musicBinder?.apply {
            return when {
                playList.isEmpty() -> {
                    MusicTreeLoadImpl.getAllMusic(object : BaseListView<MusicModel> {
                        override fun getViewContext(): Context {
                            return this@MusicPlayActivity
                        }

                        override fun onListLoadFinish(result: ArrayList<MusicModel>) {
                            playList = result
                            nextPlay()
                        }
                    })
                    false
                }
                else -> true
            }
        }
        return false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)
        toolbar_music_play.setNavigationOnClickListener { finish() }
        toolbar_music_play.inflateMenu(R.menu.toolbar_music_play)
        window.translucentSystemUI(true)
        bindService(Intent(this,PlayMusicService::class.java),conn, Service.BIND_AUTO_CREATE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_music_play, menu)
        return true
    }

    private fun switchPlayIconState(isPlaying: Boolean) {
        buttonPlayMusicActivity.apply {
            if (isPlaying){
                icon = getDrawable(R.drawable.ic_pause_black_24dp)
                iconTint = getColorStateList(R.color.colorAccent)
                backgroundTintList = getColorStateList(R.color.white)
                rippleColor = getColorStateList(R.color.colorAccent)
            }else {
                icon = getDrawable(R.drawable.ic_play_arrow_black_24dp)
                iconTint = getColorStateList(R.color.white)
                backgroundTintList = getColorStateList(R.color.colorAccent)
                rippleColor = getColorStateList(R.color.white)
            }
        }
    }
    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
        musicBinder?.removeAllListeners(controlTag)
        try {
            unbindService(conn)
        }catch (e: RuntimeException){
            e.printStackTrace()
        }
        musicBinder = null
    }
}
