package com.sanlorng.classsample.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView

import com.sanlorng.classsample.R
import com.sanlorng.classsample.helper.navigationDefaultAnim
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment(),View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        firstFragment.setOnClickListener(this)
        intentFragment.setOnClickListener(this)
        loginFragment.setOnClickListener(this)
        listViewFragment.setOnClickListener(this)
        sharedPreferencesFragment.setOnClickListener(this)
        musicFragment.setOnClickListener(this)
        toastFragment.setOnClickListener(this)
        networkFragment.setOnClickListener(this)

    }
    override fun onClick(v: View?) {
        findNavController().navigationDefaultAnim(v!!.id)
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.homeFragment)
        (activity!! as AppCompatActivity).supportActionBar?.title = "首页"
    }
}
