package com.sanlorng.classsample.mvp.music

import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import com.sanlorng.classsample.R
import com.sanlorng.classsample.model.*
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.base.BasePresenterImpl
import com.sanlorng.classsample.mvp.base.BaseView
import com.sanlorng.classsample.mvp.base.PresenterListInter
import kotlinx.coroutines.*
import java.io.File

class MusicTreeLoadImpl {
    private val musicList = ArrayList<MusicModel>()
    private val albumList = ArrayList<AlbumModel>()
    private val artistList = ArrayList<ArtistModel>()
    private val folderList = ArrayList<FolderModel>()
    private val musicSelection = arrayOf(
        BaseColumns._ID,
        MediaStore.Audio.AudioColumns.IS_MUSIC,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.ALBUM,
        MediaStore.Audio.AudioColumns.ALBUM_ID,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns.SIZE,
        MediaStore.Audio.AudioColumns.DURATION)

    private val musicPresenter = object :PresenterListInter<MusicRequest,MusicModel,BaseListView<MusicModel>> {
        var strictMode = false
        override fun afterRequestSuccess(view: BaseListView<MusicModel>?, data: ArrayList<MusicModel>) {
            view?.run {
                data.apply {
                    if (isEmpty())
                        onListLoadNoting()
                    else
                        onListLoadFinish(this)
                }
            }
        }

        override fun backgroundRequest(request: MusicRequest): ArrayList<MusicModel> {
            return request.run {
                ArrayList<MusicModel>().apply {
                    addAll(filterMusic(musicList,request,strictMode))
                }
            }
        }
    }

    fun doMusicSearch(request: MusicRequest,strictMode: Boolean,view: BaseListView<MusicModel>) {
        musicPresenter.strictMode = strictMode
        musicPresenter.doRequest(view,request)
    }
    fun doArtistSearch(){}
    fun doAlbumSearch(){}
    fun getAllAlbum(view: BaseListView<AlbumModel>) {
        if (albumList.size <= 0)
            view.onListLoadNoting()
        else
            view.onListLoadFinish(albumList)
    }
    fun getAllMusic(view: BaseListView<MusicModel>) {
        if (musicList.size<=0)
            view.onListLoadNoting()
        else
            view.onListLoadFinish(musicList)
    }
    fun getAllArtist(view: BaseListView<ArtistModel>) {
        if (artistList.size<=0)
            view.onListLoadNoting()
        else
            view.onListLoadFinish(artistList)
    }
    fun getAllFolder(view:BaseListView<FolderModel>) {
        if (folderList.size<=0)
            view.onListLoadNoting()
        else
            view.onListLoadFinish(folderList)
    }
    fun filterMusic(list:ArrayList<MusicModel>,request: MusicRequest,strictMode: Boolean):List<MusicModel> {
        return request.run {
            when(type) {
                RequestType.LIST -> list.filter {
                    filterIt(it.title,key,strictMode)
                }
                RequestType.FOLDER -> list.filter {
                    filterIt(it.path,key,strictMode)
                }
                RequestType.ALBUM -> list.filter {
                    filterIt(it.album,key,strictMode)
                }
                RequestType.ARTIST -> list.filter {
                    filterIt(it.artist,key,strictMode)
                }
                else -> list
            }
        }
    }
    private fun filterIt(content: String,key:String,strictMode: Boolean):Boolean {
        return (content.contains(key)&&strictMode.not())||content == key&&strictMode
    }
    fun scanMediaStore(context: Context){
        MusicTreeLoadImpl.isInit = true
        GlobalScope.launch {
            musicList.clear()
            musicList.add(
                MusicModel(
                    id = 0,
                    title = "sample",
                    path = "android.resource://${context.packageName}/${R.raw.sample}",
                    artist = "sanlorng",
                    album = "sample",
                    duration = 0,
                    albumId = 0,
                    fileName = "sample",
                    fileSize = 0
            ))
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                musicSelection,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
            )?.apply {
                while (moveToNext()) {
                    if (getLong(getColumnIndex(MediaStore.Audio.Media.DURATION)) > 30000)
                    musicList.add(MusicModel(
                        id = getLong(getColumnIndex(BaseColumns._ID)),
                        title = getString(getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)),
                        artist = getString(getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)),
                        album= getString(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)),
                        albumId = getLong(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)),
                        duration = getLong(getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        path= getString(getColumnIndex(MediaStore.Audio.AudioColumns.DATA)),
                        fileName= getString(getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)),
                        fileSize = getLong(getColumnIndex(MediaStore.Audio.Media.SIZE))
                    ))
                }
                close()
            }
            folderList.apply {
                clear()
                musicList.groupBy {
                    it.path.split("/").run {
                        var temp = ""
                        for (index in 0 until lastIndex)
                            temp += get(index) + "/"
                        temp
                    }
                }.apply {
                    forEach {
                        add(FolderModel(it.key).apply {
                            list = ArrayList(it.value)
                        })
                    }
                }
            }
            artistList.apply {
                musicList.groupBy {
                    it.artist
                }.apply {
                    forEach {
                        add(ArtistModel(it.key).apply {
                            list = ArrayList(it.value)
                        })
                    }
                }
            }
            albumList.apply {
                musicList.groupBy {
                    it.album
                }.apply {
                    forEach {
                        add(AlbumModel(it.key).apply {
                            list = ArrayList(it.value)
                        })
                    }
                }
            }
        }
    }

    companion object {
        var isInit = false
        private val instance by lazy(LazyThreadSafetyMode.NONE) {
            MusicTreeLoadImpl()
        }
        fun scanMediaStore(context: Context) = instance.scanMediaStore(context)
        fun getAllAlbum(view:BaseListView<AlbumModel>) = instance.getAllAlbum(view)
        fun getAllArtist(view: BaseListView<ArtistModel>) = instance.getAllArtist(view)
        fun getAllFolder(view: BaseListView<FolderModel>) = instance.getAllFolder(view)
        fun getAllMusic(view: BaseListView<MusicModel>) = instance.getAllMusic(view)
    }
}