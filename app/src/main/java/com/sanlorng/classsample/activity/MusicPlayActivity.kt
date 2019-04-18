package com.sanlorng.classsample.activity

import android.annotation.SuppressLint
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
import android.util.TypedValue
import android.view.*
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.sanlorng.classsample.BuildConfig
import com.sanlorng.classsample.R
import com.sanlorng.classsample.fragment.MusicListDialogFragment
import com.sanlorng.classsample.helper.App
import com.sanlorng.classsample.helper.adjustMargin
import com.sanlorng.classsample.helper.marginTopStatusBarHeight
import com.sanlorng.classsample.helper.paddingTopStatusBarHeight
import com.sanlorng.classsample.model.MusicModel
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.music.MusicTreeLoadImpl
import com.sanlorng.classsample.service.PlayMusicService
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_music_play.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_item.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_item.view.*
import kotlinx.android.synthetic.main.layout_music_play_mini_bar.*
import java.lang.RuntimeException

class MusicPlayActivity : AppCompatActivity() {
    private var musicBinder: PlayMusicService.PlayMusicBinder? = null
    private var controlTag = "playingActivity"
    private var isDrag = false
    private val strFormat1 = "%02d : %02d"
    private val strFormat2 = "%03d : %02d"
    private val nextPlayTypes = arrayOf(PlayMusicService.NEXT_PLAY_RANDOM,PlayMusicService.NEXT_PLAY_CIRCLE,PlayMusicService.NEXT_PLAY_SINGLE)
    private var nextPlayTypeIndex = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
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
                }

                addOnPlayListener(controlTag) {
                    setPlayInfo(it)
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
                listDragBottomSheetMusicList.layoutManager = LinearLayoutManager(this@MusicPlayActivity)
                listDragBottomSheetMusicList.adapter = ItemAdapter(playList)
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
            textDragBottomSheetMusicList.paddingTopStatusBarHeight()
            toolbarMusicMiniBar.paddingTopStatusBarHeight()
        }
    }

    private fun switchNextPlayIcon() {
        nextPlayTypeMusicActivity.setImageResource(
            when(musicBinder?.nextPlayType) {
                PlayMusicService.NEXT_PLAY_RANDOM -> R.drawable.ic_shuffle_black_24dp
                PlayMusicService.NEXT_PLAY_CIRCLE -> R.drawable.ic_repeat_black_24dp
                else -> R.drawable.ic_repeat_one_black_24dp
            })
        toolbarMusicMiniBar.menu.findItem(R.id.nextPlayTypeToolbar).icon = nextPlayTypeMusicActivity.drawable
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
            titleMusicMiniBar.text = title
            subTitleMusicMiniBar.text = textSubtitlePlayActivity.text
            if (albumCover != null)
                imageAlbumPlayActivity.setImageBitmap(albumCover)
            else
                imageAlbumPlayActivity.setImageResource(R.drawable.ic_album_black_24dp)
            albumMusicMiniBar.setImageDrawable(imageAlbumPlayActivity.drawable)
            musicBinder?.apply {
                switchPlayIconState(isPlaying)
                seekBarPlayingActivity.max = playDuration
                seekBarPlayingActivity.progress = playPosition
                val total = playDuration /1000
                val current = playPosition / 1000
                textTotalTime.text = String.format(if (total<6000) strFormat1 else strFormat2,total/60,total%60)
                textPlayTime.text = String.format(if (current<6000) strFormat1 else strFormat2,current/60,current%60)
                if (listDragBottomSheetMusicList.layoutManager == null) {
                    listDragBottomSheetMusicList.layoutManager = LinearLayoutManager(this@MusicPlayActivity)
                    listDragBottomSheetMusicList.adapter = ItemAdapter(playList)
                }
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
        titleMusicMiniBar.transitionName = ""
        subTitleMusicMiniBar.transitionName = ""
        albumMusicMiniBar.transitionName = ""
        indicatorMusicMiniBar.transitionName = ""
        indicatorMusicMiniBar.isVisible = false
        toolbarMusicMiniBar.inflateMenu(R.menu.toolbar_play_control)
        toolbarMusicMiniBar.menu.findItem(R.id.buttonPlayListToolbar).isVisible = false
        toolbarMusicMiniBar.menu.findItem(R.id.nextPlayTypeToolbar).isVisible = true
        layoutMusicMiniBar.alpha = 0f
//        layoutMusicMiniBar.isVisible = true
        toolbarMusicMiniBar.setOnMenuItemClickListener {
            musicBinder?.apply {

                when (it.itemId) {
                    R.id.buttonPlayMusicToolbar -> if (isPlaying) pause() else resume()
                    R.id.nextPlayTypeToolbar -> {
                        nextPlayTypeIndex = (nextPlayTypeIndex+1)%nextPlayTypes.size
                        nextPlayType = nextPlayTypes[nextPlayTypeIndex]
                        switchNextPlayIcon()
                    }
                }
            }
            true
        }
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheetMusicList).apply {
            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    Log.e("offset",slideOffset.toString())
                    layoutMusicMiniBar.alpha = slideOffset
                    textDragBottomSheetMusicList.alpha = 1 - slideOffset
                    layoutMusicMiniBar.isVisible = slideOffset!= 0f
                }


                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when(newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
//                            layoutMusicMiniBar.isVisible = true
//                            textDragBottomSheetMusicList.isVisible = false
//                            (listDragBottomSheetMusicList.layoutParams as RelativeLayout.LayoutParams).apply {
//                                this.addRule(RelativeLayout.BELOW,R.id.layoutMusicMiniBar)
//                                listDragBottomSheetMusicList.layoutParams = this
//                            }
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
//                            layoutMusicMiniBar.isVisible = false
//                            textDragBottomSheetMusicList.isVisible = true
//                            (listDragBottomSheetMusicList.layoutParams as RelativeLayout.LayoutParams).apply {
//                                this.addRule(RelativeLayout.BELOW,R.id.textDragBottomSheetMusicList)
//                                listDragBottomSheetMusicList.layoutParams = this
//                            }
                        }

                        BottomSheetBehavior.STATE_DRAGGING -> {
//                            textDragBottomSheetMusicList.isVisible = true
//                            layoutMusicMiniBar.isVisible = false
                        }
                        else -> {

                        }
                    }
                }
            })
        }
        layoutBottomSheetMusicList.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        postponeEnterTransition()
        toolbar_music_play.setNavigationOnClickListener { supportFinishAfterTransition() }
        toolbar_music_play.inflateMenu(R.menu.toolbar_music_play)
        window.translucentSystemUI(true)
        adjustViewMargin()
        toolbarMusicMiniBar.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
        textDragBottomSheetMusicList.layoutParams = textDragBottomSheetMusicList.layoutParams.apply {
            height = toolbarMusicMiniBar.measuredHeight
        }

        bottomSheetBehavior.peekHeight = toolbarMusicMiniBar.measuredHeight
        layoutMusicMiniBar.elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4f,resources.displayMetrics)
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
        toolbarMusicMiniBar.menu.findItem(R.id.buttonPlayMusicToolbar).icon =
            if (isPlaying) getDrawable(R.drawable.ic_pause_black_24dp) else getDrawable(R.drawable.ic_play_arrow_black_24dp)
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
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            supportFinishAfterTransition()
    }

    private inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
    private inner class ItemAdapter internal constructor(private val list: java.util.ArrayList<MusicModel>) :
        RecyclerView.Adapter<MusicPlayActivity.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicPlayActivity.ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_list_dialog_item, parent, false))
        }

        override fun onBindViewHolder(holder: MusicPlayActivity.ViewHolder, position: Int) {
            list[position].run {
                holder.itemView.apply {
                    textMusicTitle.text = title
                    textMusicAlbum.text = String.format("%s - %s",artist,album)
                    setOnClickListener {
                        musicBinder?.apply {
                            playList = this@ItemAdapter.list
                            playIndex = position
                            play(playList[playIndex].path)
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }
}
