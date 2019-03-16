package com.sanlorng.classsample.fragment


import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sanlorng.classsample.R
import kotlinx.android.synthetic.main.fragment_list_show.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.DateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListShowFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ListShowFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var type: String? = null
    private var param2: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN)
    private lateinit var task: LoadImageTask
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_show, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        when (type) {
            "fruit" -> {
                val list = arrayOf(
                    "Apple",
                    "Banana",
                    "Orange",
                    "Watermelon",
                    "Pear",
                    "Grape",
                    "Pineapple",
                    "Strawberry",
                    "Cherry",
                    "Mango",
                    "Apple",
                    "Banana",
                    "Orange",
                    "Watermelon",
                    "Pear",
                    "Grape",
                    "Pineapple",
                    "Strawberry",
                    "Cherry",
                    "Mango"
                )
                listView.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, list)
            }
            "image" -> {
                task = LoadImageTask()
                task.execute("")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param type Parameter 1.
         * @return A new instance of fragment ListShowFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(type: String) =
            ListShowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, type)
                }
            }
    }


    @SuppressLint("StaticFieldLeak")
    inner class LoadImageTask : AsyncTask<String, Void, Boolean>() {
        var list: ArrayList<Image>? = null
        override fun doInBackground(vararg params: String?): Boolean {
            val cursor = context!!.contentResolver
                .query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_TAKEN + " desc"
                )
            cursor?.run {
                moveToFirst()
                val tmpList = ArrayList<Image>()
                for (i in 0 until count) {
                    val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val index2 = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
                    val dateLong = getString(index2).toLong()
                    val date = Date(dateLong)
                    tmpList.add(Image(getString(index), DateFormat.getDateInstance().format(date)))
                    moveToNext()
                }
                close()
                if (tmpList.isEmpty().not()) {
                    list = tmpList
                    return true
                }
            }
            return false
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result) {
                listView.adapter = object : ArrayAdapter<Image>(context!!, R.layout.list_item, list!!) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = layoutInflater.inflate(R.layout.list_item, parent, false)
                        view.run {
                            val item = list!![position]
                            Glide.with(this@ListShowFragment)
                                .load(list!![position].path)
                                .into(imageViewItem)
                            textViewItem.text = item.path.split("/").last()
                        }
                        return view
                    }
                }
                listView.setOnItemClickListener { parent, view, position, id ->
                    val item = list!![position]
                    val dialog = AlertDialog.Builder(context!!)
                        .setPositiveButton("确定") { dialog, _ -> dialog.dismiss() }
                        .setTitle(item.path.split("/").last())
                        .setMessage("路径：${item.path}\n创建时间：${item.date}")
                        .create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context!!.getColor(R.color.colorAccent))
                }
                listView.setOnItemLongClickListener { parent, view, position, id ->
                    val item = list!![position]
                    val dialog = AlertDialog.Builder(context!!)
                        .setPositiveButton("确定") { dialog, _ -> list!!.removeAt(position);(listView.adapter as ArrayAdapter<*>).notifyDataSetChanged() }
                        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                        .setTitle("删除")
                        .setMessage("确定删除${item.path.split("/").last()}吗?")
                        .create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context!!.getColor(R.color.colorAccent))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context!!.getColor(R.color.colorAccent))
                    true
                }
            } else {
                Toast.makeText(context, "未找到照片", Toast.LENGTH_SHORT).show()
            }
        }

    }

    data class Image(val path: String, val date: String)
}
