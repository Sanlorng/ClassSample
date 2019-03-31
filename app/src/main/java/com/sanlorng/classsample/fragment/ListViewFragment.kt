package com.sanlorng.classsample.fragment


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.sanlorng.classsample.R
import com.sanlorng.classsample.helper.navigationDefaultAnim
import kotlinx.android.synthetic.main.fragment_list_view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ListViewFragment : Fragment() {
    lateinit var listFragment: Array<Fragment>
    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (ContextCompat.checkSelfPermission(context!!, permissions[1]) == PackageManager.PERMISSION_GRANTED)
            loadLayout()
        else
            requestPermissions(permissions, 1)
    }

    private fun loadLayout() {
        listFragment = arrayOf(
            ListShowFragment.newInstance("fruit"),
            ListShowFragment.newInstance("image")
        )
        viewpagerListView.adapter = FragmentViewPagerAdapter(childFragmentManager)
        tabListView.setupWithViewPager(viewpagerListView)
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.listViewFragment)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.list_view_usage)
        activity?.invalidateOptionsMenu()
    }

    inner class FragmentViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getPageTitle(position: Int): CharSequence? {
            return listFragment[position].arguments?.getString("param1")
        }

        override fun getCount(): Int {
            return listFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return listFragment[position]
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            loadLayout()
        else
            Toast.makeText(context!!, "请授予应用存储权限", Toast.LENGTH_SHORT).show()
    }
}
