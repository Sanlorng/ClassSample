package com.sanlorng.classsample.model

import android.graphics.Bitmap

class MusicControlTreeModel {
//    var currentList: ArrayList<MusicModel> = ArrayList()
//    var currentMusic: MusicModel? = null
//    var unsortList: ArrayList<MusicModel> = ArrayList()
//    var singerList: ArrayList<MusicModel> = ArrayList()
//    var albumList: ArrayList<MusicModel> = ArrayList()
//    var folderList: ArrayList<MusicModel> = ArrayList()
}
abstract class BaseMusicModel(val info: String){
    var list :ArrayList<MusicModel>? = null
}
data class ArtistModel(val name: String): BaseMusicModel(name)
data class FolderModel(val path: String): BaseMusicModel(path) {
    val folderName: String
    init {
        folderName = path.split("/").last()
    }
}
data class AlbumModel(val name: String): BaseMusicModel(name)
data class MusicModel(val id: Long,
                     val path: String,
                     val title: String,
                     val artist: String,
                     val album: String,
                     val albumId: Long,
                     val duration: Long,
                     val fileName: String,
                     val fileSize: Long):BaseMusicModel(fileName) {
    var albumCover:Bitmap? = null
}

data class MusicRequest(val type:String, val key: String)
object RequestType {
    const val ARTIST = "艺术家"
    const val ALBUM = "专辑"
    const val FOLDER = "文件夹"
    const val LIST = "所有音乐"
    const val NULL = ""
}