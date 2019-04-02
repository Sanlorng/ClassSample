package com.sanlorng.classsample.activity

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.sanlorng.classsample.R
import com.sanlorng.classsample.helper.App
import com.sanlorng.classsample.helper.adjustMargin
import com.sanlorng.classsample.model.MusicModel
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.music.MusicTreeLoadImpl
import com.sanlorng.classsample.service.PlayMusicService
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_music_play.*
import java.lang.RuntimeException

class MusicPlayActivity : AppCompatActivity() {
    private var musicBinder: PlayMusicService.PlayMusicBinder? = null
    private var controlTag = "playingActivity"
    private var isDrag = false
    private val strFormat1 = "%02d : %02d"
    private val strFormat2 = "%03d : %02d"
    private val nextPlayTypes = arrayOf(PlayMusicService.NEXT_PLAY_RANDOM,PlayMusicService.NEXT_PLAY_CIRCLE,PlayMusicService.NEXT_PLAY_SINGLE)
    private var nextPlayTypeIndex = 0
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

                addPlayingCallBack(controlTag) { currentPosition, _ ->
                    onPlaying(currentPosition)
                }

                addOnPauseListener(controlTag) {
                        setPlayInfo(it)
                    val temp = it.duration /1000
                    textTotalTime.text = String.format(if (temp<6000) strFormat1 else strFormat2,temp/60,temp%60)
                }

                addOnPlayListener(controlTag) {
                    setPlayInfo(it)
                    val temp = it.duration /1000
                    textTotalTime.text = String.format(if (temp<6000) strFormat1 else strFormat2,temp/60,temp%60)
                }

                addOnResumeListener(controlTag) {
                    setPlayInfo(it)
                }
                supportStartPostponedEnterTransition()
                seekBarPlayingActivity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        isDrag = true
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        musicBinder?.apply {
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
                nextPlayTypeIndex = nextPlayTypes.find {
                    it == nextPlayType
                }?:PlayMusicService.NEXT_PLAY_RANDOM
                switchNextPlayIcon()
                nextPlayTypeMusicActivity.setOnClickListener {
                    musicBinder?.apply {
                        nextPlayTypeIndex = (nextPlayTypeIndex+1)%nextPlayTypes.size
                        nextPlayType = nextPlayTypes[nextPlayTypeIndex]
                        switchNextPlayIcon()
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

    private fun adjustViewMargin() {
        DisplayMetrics().apply {
            getSystemService(WindowManager::class.java).defaultDisplay.getRealMetrics(this)
            val percent:Float = widthPixels*420f/1080f/densityDpi
            toolbar_music_play.adjustMargin(percent)
            cardAlbumPlayActivity.adjustMargin(percent)
            textTitlePlayActivity.adjustMargin(percent)
            textSubtitlePlayActivity.adjustMargin(percent)
            textPlayingFileInfo.adjustMargin(percent)
            layoutPlayControlMusicActivity.adjustMargin(percent)
            seekBarPlayingActivity.adjustMargin(percent)
            layoutPlaySeekBarMusicActivity.adjustMargin(percent)
            buttonPlayMusicActivity.adjustMargin(percent)
        }
    }

    private fun switchNextPlayIcon() {
        nextPlayTypeMusicActivity.setImageResource(
            when(musicBinder?.nextPlayType) {
                PlayMusicService.NEXT_PLAY_RANDOM -> R.drawable.ic_shuffle_black_24dp
                PlayMusicService.NEXT_PLAY_CIRCLE -> R.drawable.ic_repeat_black_24dp
                else -> R.drawable.ic_repeat_one_black_24dp
            })
    }

    private fun onPlaying(current: Int) {
        musicBinder?.playingMusic?.apply {
            if (isDrag.not()) {
                seekBarPlayingActivity.progress = current
            }
            val temp = current /1000
            textPlayTime.text = String.format(if (temp<6000) strFormat1 else strFormat2,temp/60,temp%60)
        }
    }
    private fun setPlayInfo(model: MusicModel) {
        model.apply {
            textSubtitlePlayActivity.text = String.format("%s - %s", artist, album)
            textTitlePlayActivity.text = title
            if (albumCover != null)
                imageAlbumPlayActivity.setImageBitmap(albumCover)
            else
                imageAlbumPlayActivity.setImageResource(R.drawable.ic_album_black_24dp)
            musicBinder?.apply {
                switchPlayIconState(isPlaying)
                seekBarPlayingActivity.max = playDuration
                seekBarPlayingActivity.progress = playPosition
                val total = playDuration /1000
                val current = playPosition / 1000
                textTotalTime.text = String.format(if (total<6000) strFormat1 else strFormat2,total/60,total%60)
                textPlayTime.text = String.format(if (current<6000) strFormat1 else strFormat2,current/60,current%60)
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
        postponeEnterTransition()
        toolbar_music_play.setNavigationOnClickListener { supportFinishAfterTransition() }
        toolbar_music_play.inflateMenu(R.menu.toolbar_music_play)
        window.translucentSystemUI(true)
        adjustViewMargin()
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

    override fun onResume() {
        super.onResume()
        musicBinder?.apply {
            addPlayingCallBack(controlTag) { currentPosition, _ ->
                onPlaying(currentPosition)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        musicBinder?.apply {
            removePlayingCallBack(controlTag)
        }
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

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

}
