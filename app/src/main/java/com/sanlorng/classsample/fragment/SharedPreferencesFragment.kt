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
import com.sanlorng.classsample.activity.DataBaseActivity
import com.sanlorng.classsample.activity.LoginActivity
import com.sanlorng.classsample.helper.navigationDefaultAnim
import com.sanlorng.kit.startActivity
import kotlinx.android.synthetic.main.fragment_shared_preferences.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SharedPreferencesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shared_preferences, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_login_share.setOnClickListener {
            context?.startActivity(LoginActivity::class.java)
        }
        button_data_base.setOnClickListener {
            context?.startActivity(DataBaseActivity::class.java)
        }
        homeFragment.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.sharedPreferencesFragment)
        (activity as AppCompatActivity).supportActionBar?.title = "数据持久化的应用"
    }

}
