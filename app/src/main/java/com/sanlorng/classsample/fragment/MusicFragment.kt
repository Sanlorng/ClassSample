package com.sanlorng.classsample.fragment


import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.sanlorng.classsample.BuildConfig

import com.sanlorng.classsample.R
import com.sanlorng.classsample.activity.MusicPlayActivity
import com.sanlorng.classsample.helper.App
import com.sanlorng.classsample.model.*
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.music.MusicTreeLoadImpl
import com.sanlorng.classsample.service.PlayMusicService
import com.sanlorng.kit.startActivity
import kotlinx.android.synthetic.main.activity_music_play.*
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.android.synthetic.main.fragment_music.view.*
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MusicFragment : Fragment(),MusicListDialogFragment.Listener {
    private val listenerTag = "mainFragment"
    private lateinit var listFragment: List<Fragment>
    private val permissions =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private  var musicBinder: PlayMusicService.PlayMusicBinder? = null
    private var conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            (service as PlayMusicService.PlayMusicBinder).apply {
                musicBinder = this
                addPlayingCallBack(listenerTag) { currentPosition, total ->
                    musicBinder?.playingMusic?.apply {
                        indicatorPlayingMusicFragment?.apply {
                            progress = currentPosition
                            max = total
                        }
                    }
                }
                addCompletionListener(listenerTag, MediaPlayer.OnCompletionListener {

                })
                addOnPreparedListener(listenerTag, MediaPlayer.OnPreparedListener {
                    musicBinder?.playingMusic?.apply {
                        if (albumCover != null)
                            imagePlayingAlbumMusicFragment.setImageBitmap(albumCover)
                        else
                            imagePlayingAlbumMusicFragment.setImageResource(R.drawable.ic_album_black_24dp)
                    }
                })
                toolbarMusicFragment.inflateMenu(R.menu.toolbar_play_control)
                toolbarMusicFragment.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.buttonPlayMusicToolbar -> {
                            musicBinder?.apply {
                                if (checkPlay()) {
                                    if (isPlaying)
                                        pause()
                                    else
                                        resume()
                                }
                                else {

                                }
                            }
                        }

                        R.id.buttonPlayListToolbar -> {
                            musicBinder?.apply {
                                if (playList.isNotEmpty())
                                    MusicListDialogFragment(playList).show(childFragmentManager,listenerTag)
                                else
                                    Toast.makeText(context!!,"音乐列表为空",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    true
                }
                addOnPauseListener(listenerTag) {
                    setMusicInfo(it)
                }

                addOnResumeListener(listenerTag) {
                    setMusicInfo(it)
                }

                addOnPlayListener(listenerTag) {
                    setMusicInfo(it)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    private fun setMusicInfo(model: MusicModel) {
        model.apply {
            textPlayingTitleMusicFragment.text = title
            textPlayingArtistMusicFragment.text = String.format("%s - %s",artist,album)
            if (albumCover != null)
                imagePlayingAlbumMusicFragment.setImageBitmap(albumCover)
            else
                imagePlayingAlbumMusicFragment.setImageResource(R.drawable.ic_album_black_24dp)
            musicBinder?.apply {
                switchPlayIconState(isPlaying)
            }
        }
    }

    private fun switchPlayIconState(isPlaying: Boolean) {
        toolbarMusicFragment.menu.findItem(R.id.buttonPlayMusicToolbar).apply {
            icon = if (isPlaying.not())  context?.getDrawable(R.drawable.ic_play_arrow_black_24dp)
                    else context?.getDrawable(R.drawable.ic_pause_black_24dp)
        }
    }
    private fun checkPlay():Boolean{
        musicBinder?.apply {
            return when {
                playList.isEmpty() -> {
                    MusicTreeLoadImpl.getAllMusic(object : BaseListView<MusicModel> {
                        override fun getViewContext(): Context {
                            return context!!
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
    private fun loadLayout() {

        toolbarMusicFragment.setOnClickListener {
            context?.apply {
                val intent = Intent(this,MusicPlayActivity::class.java)
                val option = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity!!, Pair(imagePlayingAlbumMusicFragment,"imageAlbum"),
                        Pair(textPlayingTitleMusicFragment,"musicTitle"),
                        Pair(textPlayingArtistMusicFragment, "musicSubTitle"),
                        Pair(indicatorPlayingMusicFragment,"musicSeekBar")
                    )
                startActivity(intent,option.toBundle())
            }
        }
        if (MusicTreeLoadImpl.isInit.not())
            MusicTreeLoadImpl.scanMediaStore(context!!)
        listFragment = listOf(
                MusicSortFragment.newInstance(RequestType.LIST), MusicSortFragment.newInstance(RequestType.ARTIST),
                MusicSortFragment.newInstance(RequestType.ALBUM), MusicSortFragment.newInstance(RequestType.FOLDER)
            )
        viewPager_music.adapter = FragmentViewPagerAdapter(childFragmentManager)
        tabLayout_music.setupWithViewPager(viewPager_music)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            activity?.apply {
                val intent = Intent(this, PlayMusicService::class.java)
                ContextCompat.startForegroundService(this, intent)
                startService(intent)
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
//        homeFragment.setOnClickListener {
//            findNavController().navigateUp()
//        }
        if (ContextCompat.checkSelfPermission(context!!, permissions[1]) == PackageManager.PERMISSION_GRANTED)
            loadLayout()
        else
            requestPermissions(permissions, 1)
        context?.bindService(Intent(context,PlayMusicService::class.java),conn,Service.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.musicFragment)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.music_player)
        activity?.invalidateOptionsMenu()
        musicBinder?.apply {
            addPlayingCallBack(listenerTag) { currentPosition, total ->
                playingMusic.apply {
                    indicatorPlayingMusicFragment?.apply {
                        progress = currentPosition
                        max = total
                    }
                }
            }
        }
//        context?.bindService(Intent(context!!,PlayMusicService::class.java),conn,Service.BIND_AUTO_CREATE)
    }

    inner class FragmentViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getPageTitle(position: Int): CharSequence? {
            return listFragment[position].arguments?.getString("param1")
        }

        override fun getCount(): Int {
            return listFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return listFragment[position]
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            loadLayout()
        else
            Toast.makeText(context!!, "请授予应用存储权限", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClicked(list: ArrayList<MusicModel>, position: Int) {
        musicBinder?.apply {
            if (BuildConfig.DEBUG) {
                Log.e("isPlaying",isPlaying.toString())
                Log.e("playingPath",playingPath)
                Log.e("list[position].path",list[position].path)
            }
            playList = ArrayList(list)
            playIndex = position
            list[position].run {
                when {
                    playingPath !=path -> play(path)
                    playingPath == path && isPlaying.not() -> resume()
                }
            }
        }
    }
    override fun onStop() {
        super.onStop()
        if (BuildConfig.DEBUG)
            Log.e("onStop","true")
        musicBinder?.apply {
            removePlayingCallBack(listenerTag)
        }
    }

    override fun onPause() {
        if (BuildConfig.DEBUG)
            Log.e("onPause","true")
        super.onPause()
    }
    override fun onDestroy() {
        context?.unbindService(conn)
        musicBinder?.removeAllListeners(listenerTag)
        if (BuildConfig.DEBUG)
            Log.e("onDestroy","true")
        super.onDestroy()
    }
}
