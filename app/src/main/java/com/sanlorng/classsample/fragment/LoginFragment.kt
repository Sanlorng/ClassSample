package com.sanlorng.classsample.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView

import com.sanlorng.classsample.R
import com.sanlorng.classsample.activity.DialogProcessActivity
import com.sanlorng.classsample.activity.LoginActivity
import kotlinx.android.synthetic.main.fragment_login.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.loginFragment)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.sign_in_and_dialog)
        activity?.invalidateOptionsMenu()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_login_login.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }
        button_process_login.setOnClickListener {
            startActivity(Intent(context, DialogProcessActivity::class.java))
        }
    }
}
