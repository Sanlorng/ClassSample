package com.sanlorng.classsample.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView

import com.sanlorng.classsample.R
import kotlinx.android.synthetic.main.fragment_first.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.firstFragment)
        (activity!! as AppCompatActivity).supportActionBar?.title = "初识Android 开发"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.run {
            button_first.setOnClickListener {
                val string = editText_first.text.toString()
                if (string.isEmpty())
                    Toast.makeText(context!!, "请输入内容", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context!!, "你输入了：$string", Toast.LENGTH_LONG).show()
            }
            homeFragment.setOnClickListener {
                findNavController().navigateUp()
            }

        }
    }
}
