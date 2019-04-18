package com.sanlorng.classsample.fragment


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.navigation.NavigationView

import com.sanlorng.classsample.R
import com.sanlorng.classsample.activity.DataBaseActivity
import com.sanlorng.classsample.activity.LoginActivity
import com.sanlorng.classsample.activity.textButtonStyle
import com.sanlorng.classsample.helper.navigationDefaultAnim
import com.sanlorng.kit.startActivity
import kotlinx.android.synthetic.main.fragment_shared_preferences.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SharedPreferencesFragment : Fragment() {

    private val installCode = 100
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
        button_content_provider.setOnClickListener {
            try {
                context?.startActivity(Intent("com.sanlorng.classsample.PROVIDER"))
            }catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                MaterialAlertDialogBuilder(context!!)
                    .setTitle("安装ContentProviderSample")
                    .setMessage("完成此功能测试需要安装ContentProvider，您尚未安装，点击继续按钮，将安装ContentProviderSample并打开ContentProviderSample完成此次功能测试")
                    .setCancelable(true)
                    .setPositiveButton("安装") { _,_ ->
                        val context = it.context

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.applicationContext.packageManager.canRequestPackageInstalls()) {
                            val file = File(copyRawFileToDir(R.raw.content_provider_sample,"contentProviderSample.apk","${context.cacheDir.absolutePath}${File.separator}contentProvider"))
                            context.startInstallApp(file)
                        }
                        else
                            MaterialAlertDialogBuilder(context)
                                .setTitle("授予安装权限")
                                .setMessage("为了能够顺利安装，在此之前，需要您授予安装权限")
                                .setCancelable(true)
                                .setPositiveButton("授予") { _,_ ->
                                    this@SharedPreferencesFragment.startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES),installCode)
                                }.create().textButtonStyle().show()
                    }
                    .setNegativeButton("取消") { _,_ ->
                    }.create().textButtonStyle().show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==installCode && resultCode == Activity.RESULT_OK) {
            val file = File(copyRawFileToDir(R.raw.content_provider_sample,"contentProviderSample.apk","${context?.cacheDir?.absolutePath}${File.separator}contentProvider"))
            context?.startInstallApp(file)
        }
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.sharedPreferencesFragment)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.data_and_share_preferences_usage)
        activity?.invalidateOptionsMenu()
    }

    private fun copyRawFileToDir(id: Int,fileName: String,dirPath: String):String {
        val filePath = "$dirPath${File.separator}$fileName"
        File(dirPath).apply {
            if (!exists())
                mkdir()
            readInputStream(filePath,resources.openRawResource(id))
            return filePath
        }
    }

    private fun readInputStream(filePath: String, inputStream: InputStream) {
        try {
            File(filePath).apply {
                deleteOnExit()

                val fos = FileOutputStream(this)
                val buffer = ByteArray(1024)
                var length = inputStream.read(buffer)
                    while (length>=0 ) {
                        fos.write(buffer,0,length)
                        length = inputStream.read(buffer)
                    }

                    fos.close()
                    inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Context.startInstallApp(file: File) {
        Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(this@startInstallApp, "$packageName.file.provider",file)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(apkUri,"application/vnd.android.package-archive")
                Log.e("uri",apkUri.path)
            } else {
                setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive")
            }
            startActivity(this)
        }
    }

}
