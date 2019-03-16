package com.sanlorng.classsample.fragment


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sanlorng.classsample.R
import com.sanlorng.classsample.model.*
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.music.MusicTreeLoadImpl
import kotlinx.android.synthetic.main.fragment_item_list_dialog.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_item.view.*
import kotlinx.android.synthetic.main.fragment_music_sort.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MusicSortFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MusicSortFragment : Fragment(),MusicListDialogFragment.Listener {
    var artistList:ArrayList<ArtistModel>? = null
    var albumList: ArrayList<AlbumModel>? = null
    var folderList: ArrayList<FolderModel>? = null
    var musicList: ArrayList<MusicModel>? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_sort, container, false)
    }






    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val list = when (param1) {
            RequestType.ARTIST -> {
                MusicTreeLoadImpl.getAllArtist(object :BaseListView<ArtistModel> {
                    override fun getViewContext(): Context {
                        return context!!
                    }

                    override fun onListLoadFinish(result: ArrayList<ArtistModel>) {
                        artistList = result
                        listMusicSort.layoutManager = LinearLayoutManager(context)
                        listMusicSort.adapter = MusicSortListAdapter()
                    }
                })
            }
            RequestType.ALBUM -> {
                MusicTreeLoadImpl.getAllAlbum(object :BaseListView<AlbumModel> {
                    override fun getViewContext(): Context {
                        return context!!
                    }

                    override fun onListLoadFinish(result: ArrayList<AlbumModel>) {
                        albumList = result
                        listMusicSort.layoutManager = LinearLayoutManager(context)
                        listMusicSort.adapter = MusicSortListAdapter()
                    }
                })
            }
            RequestType.FOLDER -> {
                MusicTreeLoadImpl.getAllFolder(object :BaseListView<FolderModel> {
                    override fun getViewContext(): Context {
                        return context!!
                    }

                    override fun onListLoadFinish(result: ArrayList<FolderModel>) {
                        folderList = result
                        listMusicSort.layoutManager = LinearLayoutManager(context)
                        listMusicSort.adapter = MusicSortListAdapter()
                    }
                })
            }
            else -> MusicTreeLoadImpl.getAllMusic(object :BaseListView<MusicModel> {
                override fun getViewContext(): Context {
                    return context!!
                }

                override fun onListLoadFinish(result: ArrayList<MusicModel>) {
                    musicList = result
                    listMusicSort.layoutManager = LinearLayoutManager(context)
                    listMusicSort.adapter = MusicSortListAdapter()
                }
            })
        }


//        MusicControlPresenterImpl.instance.getList(this)
    }

    override fun onItemClicked(list: ArrayList<MusicModel>, position: Int) {
        if (parentFragment is MusicListDialogFragment.Listener)
            (parentFragment as MusicListDialogFragment.Listener).onItemClicked(list,position)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment MusicSortFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            MusicSortFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
        fun String.removeFileSuffix(): String{
            val file = File(this)
            return if(file.isFile){
                val tmp = split(".")
                var mString = ""
                for (i in 0 until tmp.lastIndex) {
                        mString += tmp[i]
                    if (i != tmp.lastIndex - 1)
                        mString += "."
                }
                return mString
            }else{
                this
            }
        }
    }

    inner class MusicSortListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_list_dialog_item,parent,false)){}
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.run {
                when {
                    artistList != null -> artistList!![position].apply {
                        textMusicTitle.text = name
                        textMusicAlbum.text = String.format("共有 %d 首歌",if(list == null) 0 else list!!.size)
                        setOnClickListener {
                            MusicListDialogFragment(list!!).apply {
                                show(this@MusicSortFragment.childFragmentManager,tag)
                            }
                        }
                    }
                    albumList != null -> albumList!![position].apply {
                        textMusicTitle.text = name
                        textMusicAlbum.text = String.format("共有 %d 首歌",if(list == null) 0 else list!!.size)
                        setOnClickListener {
                            MusicListDialogFragment(list!!).apply {
                                show(this@MusicSortFragment.childFragmentManager,tag)
                            }
                        }
                    }

                    folderList != null -> folderList!![position].apply {
                        textMusicTitle.text = path
                        textMusicAlbum.text = String.format("共有 %d 首歌",if(list == null) 0 else list!!.size)
                        setOnClickListener {
                            MusicListDialogFragment(list!!).apply {
                                show(this@MusicSortFragment.childFragmentManager,tag)
                            }
                        }
                    }

                    musicList != null -> musicList!![position].apply {
                        textMusicTitle.text = title
                        textMusicAlbum.text = String.format("%s - %s",artist,album)
                        setOnClickListener {
                            onItemClicked(musicList!!,position)
                        }
                    }

                    else -> null
                }
            }
        }

        override fun getItemCount(): Int {
            return when {
                artistList != null -> artistList!!.size
                albumList != null -> albumList!!.size
                folderList != null -> folderList!!.size
                musicList != null -> musicList!!.size
                else -> 0
            }
        }
    }
}
