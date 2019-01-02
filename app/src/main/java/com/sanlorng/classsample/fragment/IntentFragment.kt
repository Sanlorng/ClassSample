package com.sanlorng.classsample.fragment


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView

import com.sanlorng.classsample.R
import com.sanlorng.classsample.activity.MessageActivity
import kotlinx.android.synthetic.main.fragment_intent.*
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class IntentFragment : Fragment(),View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intent, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_message_intent.setOnClickListener(this)
        button_hidden_intent.setOnClickListener(this)
        homeFragment.setOnClickListener(this)
        toolbar_intent.inflateMenu(R.menu.menu_intent)
        toolbar_intent.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.takePhoto -> startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
                R.id.openInBrowser -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Sanlorng")))
                R.id.shareIntent -> {val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "这是由Sanlorng创建的应用ClassSample分享的内容，用于Android实验内容展示，个人Github地址:https://github.com/Sanlorng")
                    startActivity(Intent.createChooser(shareIntent, "分享"))}
                R.id.callPhone -> {
                    if (ContextCompat.checkSelfPermission(context!!,Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:10086")))
                    else
                        requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),1)
                }
                R.id.sendToMessage -> {
                    val intent = Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:+8610086"))
                    intent.putExtra("sms_body","这是由Sanlorng创建的应用ClassSample发送的内容，用于Android实验内容展示，个人Github地址:https://github.com/Sanlorng")
                    startActivity(intent)
                }
            }
            true
        }
    }
    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.intentFragment)
        (activity as AppCompatActivity).supportActionBar?.title = "Intent的应用"
    }

    override fun onClick(v: View?) {
        val string = editText_intent.editableText.toString()
        when {
            v == homeFragment -> findNavController().navigateUp()
            string.isEmpty() -> Toast.makeText(context,"请输入内容",Toast.LENGTH_SHORT).show()
            else -> when(v){
                button_message_intent -> {
                    val intent = Intent(context,MessageActivity::class.java)
                    intent.putExtra("message",string)
                    startActivity(intent)
                }
                button_hidden_intent -> {
                    try {
                        val intent = Intent("com.sanlorng.test.intent")
                        intent.putExtra("message", string)
                        startActivity(intent)
                    }catch (e: Exception){
                        e.printStackTrace()
                        Toast.makeText(context,"没有这个Activity",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1  -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:10086")))
            else
                Toast.makeText(context,"请授予应用电话权限后再执行此操作",Toast.LENGTH_SHORT).show()
        }
    }

}
