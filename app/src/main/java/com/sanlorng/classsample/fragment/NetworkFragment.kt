package com.sanlorng.classsample.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView

import com.sanlorng.classsample.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class NetworkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.networkFragment)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.ok_http_usage)
        activity?.invalidateOptionsMenu()
    }

}
