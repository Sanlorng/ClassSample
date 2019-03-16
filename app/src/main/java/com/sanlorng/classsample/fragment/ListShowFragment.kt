package com.sanlorng.classsample.fragment


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.sanlorng.classsample.model.Image
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.image.ImageLoadImpl
import kotlinx.android.synthetic.main.fragment_list_show.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


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
class ListShowFragment : Fragment(),BaseListView<Image> {
    // TODO: Rename and change types of parameters
    private var type: String? = null
    private var param2: String? = null
    private val task = ImageLoadImpl(this)
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
                task.doRequest("")
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

    override fun getViewContext(): Context {
        return context!!
    }

    override fun onListLoadFinish(result: ArrayList<Image>) {
        result.apply {
            listView.adapter = object : ArrayAdapter<Image>(context!!,R.layout.list_item,this) {
                @SuppressLint("ViewHolder")
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return layoutInflater.inflate(R.layout.list_item, parent , false).apply {
                        get(position).run {
                            Glide.with(this@ListShowFragment)
                                .load(path)
                                .into(imageViewItem)
                            textViewItem.text = path.split("/").last()
                        }
                    }
                }
            }
            listView.setOnItemClickListener { parent, view, position, id ->
                get(position).apply {
                    AlertDialog.Builder(context!!)
                        .setPositiveButton("确定") { dialog, _ -> dialog.dismiss() }
                        .setTitle(path.split("/").last())
                        .setMessage("路径：$path\n创建时间：$date")
                        .create().apply {
                            show()
                            getButton(AlertDialog.BUTTON_POSITIVE).run {
                                setTextColor(context.getColor(R.color.colorAccent))
                                background = ColorDrawable(Color.parseColor("#00000000"))
                            }
                        }
                }
            }
            listView.setOnItemLongClickListener { _, _, position, _ ->
                get(position).apply {
                    AlertDialog.Builder(context!!)
                        .setPositiveButton("确定") { _, _ -> removeAt(position);(listView.adapter as ArrayAdapter<*>).notifyDataSetChanged() }
                        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                        .setTitle("删除")
                        .setMessage("确定删除${path.split("/").last()}吗?")
                        .create().apply {
                            show()
                            getButton(AlertDialog.BUTTON_POSITIVE).run {
                                setTextColor(context.getColor(R.color.colorAccent))
                                background = ColorDrawable(Color.parseColor("#00000000"))
                            }
                            getButton(AlertDialog.BUTTON_NEGATIVE).run {
                                setTextColor(context.getColor(R.color.colorAccent))
                                background = ColorDrawable(Color.parseColor("#00000000"))
                            }
                        }

                }
                true
            }
        }
    }

    override fun onListLoadNoting() {
        Toast.makeText(context, "未找到照片", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        task.detachView()
        super.onDestroy()
    }


}
