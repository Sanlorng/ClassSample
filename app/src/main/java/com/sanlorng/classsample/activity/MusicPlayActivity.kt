package com.sanlorng.classsample.activity

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

class MusicPlayActivity : AppCompatActivity() {
    private var musicBinder: PlayMusicService.PlayMusicBinder? = null
    private var controlTag = "playingActivity"
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as PlayMusicService.PlayMusicBinder
            musicBinder?.apply {
                if (isPlaying)
                    playingMusic.apply {
                        textSubtitlePlayActivity.text = String.format("%s - %s",artist,album)
                        textTitlePlayActivity.text = title
                        switchPlayIconState(isPlaying)
                    }
                addOnPreparedListener(controlTag, MediaPlayer.OnPreparedListener {
                    playingMusic.apply {
                        textSubtitlePlayActivity.text = String.format("%s - %s",artist,album)
                        textTitlePlayActivity.text = title
                    }
                })

                addCompletionListener(controlTag, MediaPlayer.OnCompletionListener {
                })

                addPlayingCallBack(controlTag) { currentPosition, total ->
                    musicBinder?.playingMusic?.apply {
                        seekBarPlayingActivity.progress = currentPosition
                        seekBarPlayingActivity.max = total
                        switchPlayIconState(isPlaying)
                    }
                }

                seekBarPlayingActivity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        musicBinder?.apply {
                            if (isPlaying)
                                seekTo(seekBar?.progress ?: 0)
                        }
                    }
                })

                buttonNextPlayMusicActivity.setOnClickListener {
                    musicBinder?.nextPlay()
                }

                buttonLastMusicActivity.setOnClickListener {
                    musicBinder?.lastPlay()
                }

                buttonPlayMusicActivity.setOnClickListener {
                    musicBinder?.apply {
                        if (playList.isEmpty().not()) {
                            if (isPlaying)
                                pause()
                            else
                                resume()
                        }
                        else {
                            MusicTreeLoadImpl.getAllMusic(object : BaseListView<MusicModel> {
                                override fun getViewContext(): Context {
                                    return this@MusicPlayActivity
                                }

                                override fun onListLoadFinish(result: ArrayList<MusicModel>) {
                                    playList = result
                                    nextPlay()
                                }
                            })
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
//        if (isPlaying) {
//            buttonPlayMusicActivity.isChecked = true
//        }
    }
    override fun onStop() {
        super.onStop()
        unbindService(conn)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicBinder = null
    }
}
