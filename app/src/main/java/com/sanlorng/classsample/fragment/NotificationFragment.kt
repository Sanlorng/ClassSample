package com.sanlorng.classsample.fragment


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.navigation.NavigationView
import com.sanlorng.classsample.BuildConfig

import com.sanlorng.classsample.R
import com.sanlorng.classsample.activity.MainActivity
import com.sanlorng.classsample.helper.roundBitmap
import kotlinx.android.synthetic.main.fragment_notification.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 *
 */
class NotificationFragment : Fragment() {
    var appSender = "Classsample"
    private val sanlorng = "Sanlorng"
    private lateinit var appName: String
    private val batteryReceiver = BatteryReceiver()
    private val appBroadcastReceiver = AppBroadcastReceiver()
    private val messageArray = arrayOf("Hi，这是一条示例信息","Hi，这是一条示例信息，由${appSender}创建")
    private lateinit var notificationManager: NotificationManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activity?.apply {
            appName = "Classsample"
            registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            registerReceiver(appBroadcastReceiver, IntentFilter(ACTION_APP_NOTIFICATION).apply {
                addAction(ACTION_APP_MESSAGE)
            })
            notificationManager = getSystemService(NotificationManager::class.java)
            createNotificationChannel()
        }
        removeBatteryStatusNotification.setOnClickListener {
            notificationManager.cancel(BATTERY_CHANNEL_INT)
        }
        updateBatteryStatusNotification.setOnClickListener {
                context?.sendBroadcast(Intent(ACTION_APP_NOTIFICATION))
        }
        sendMessageNotification.setOnClickListener {
            messageNotification()
        }
        super.onActivityCreated(savedInstanceState)
    }
    override fun onResume() {
        super.onResume()
        activity!!.findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.notificationFragment)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.notification_broad_usage)
        activity?.invalidateOptionsMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(batteryReceiver)
        context?.unregisterReceiver(appBroadcastReceiver)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.run {
                createNotificationChannel(
                    NotificationChannel(BATTERY_CHANNEL,"电池状态",NotificationManager.IMPORTANCE_DEFAULT).apply {
                        description = "显示电池状态相关信息"
                    })
                createNotificationChannel(
                    NotificationChannel(MESSAGE_CHANNEL,"消息显示",NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "显示应用发出的消息"
                    })
                createNotificationChannel(
                    NotificationChannel(APP_CHANNEL,"应用通知",NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "显示接收应用发出的广播"
                    })
            }
        }
    }

    private fun messageNotification(senderString:String = sanlorng,messages:Array<String> = messageArray) {
        activity?.apply {
            val pendingIntent = PendingIntent.getActivity(
                this, MESSAGE_CHANNEL_INT,
                Intent(this,MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val sender = Person.Builder()
                .setName(senderString)
                .also {
                    if (senderString == sanlorng)
                        it.setIcon(IconCompat.createWithBitmap(getDrawable(R.drawable.sanlorng_avatar)!!.toBitmap().roundBitmap))
                }
                .build()
            with(NotificationManagerCompat.from(this)) {
                notify(MESSAGE_CHANNEL_INT,
                    NotificationCompat.Builder(this@apply, MESSAGE_CHANNEL)
                        .setSmallIcon(R.drawable.ic_message_black_24dp)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setStyle(NotificationCompat.MessagingStyle(sender)
                            .setConversationTitle("来自$senderString")
                            .also {
                                messages.forEach { its ->
                                    it.addMessage(its,System.currentTimeMillis(),sender)
                                }
                            })
                        .setColor(getColor(R.color.colorAccent))
                        .build())
            }
        }
    }
    companion object {
        const val ACTION_APP_MESSAGE = "com.sanlorng.classsample.APP_MESSAGE"
        const val ACTION_APP_NOTIFICATION = "com.sanlorng.classsample.APP_NOTIFICATION"
        const val BATTERY_CHANNEL = "battery"
        const val BATTERY_CHANNEL_INT = 0
        const val MESSAGE_CHANNEL = "message"
        const val MESSAGE_CHANNEL_INT = 1
        const val APP_CHANNEL = "app"
        const val APP_CHANNEL_INT = 2
    }
    inner class BatteryReceiver: BroadcastReceiver() {
        private var status = BatteryManager.BATTERY_STATUS_UNKNOWN
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                status = getIntExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_UNKNOWN)
                if (action == Intent.ACTION_BATTERY_CHANGED)
                    batteryNotificationWithLevel(getIntExtra(BatteryManager.EXTRA_LEVEL,0))
            }
        }
        private fun batteryNotificationWithLevel(level: Int) {
            val statusString = when(status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "正在充电"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "正在放电"
                BatteryManager.BATTERY_STATUS_FULL -> "已充满"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "未充电"
                else -> "未知状态"
            }
            context?.apply {
                val pendingIntent = PendingIntent.getActivity(
                    this, BATTERY_CHANNEL_INT,
                    Intent(this,MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                with(NotificationManagerCompat.from(this)) {
                    notify(BATTERY_CHANNEL_INT,
                        NotificationCompat.Builder(this@apply,BATTERY_CHANNEL)
                            .setSmallIcon(R.drawable.ic_battery_std_black_24dp)
                            .setContentTitle("电池状态：$statusString")
                            .setContentText("当前电量：$level%")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(true)
                            .setContentIntent(pendingIntent)
                            .setColor(getColor(R.color.colorAccent))
                            .build())
                }
            }
        }
    }
    inner class AppBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            appNotification()
        }
        private fun appNotification() {
            context?.apply {
                val pendingIntent = PendingIntent.getActivity(
                    this, APP_CHANNEL_INT,
                    Intent(this,MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                with(NotificationManagerCompat.from(this)) {
                    notify(APP_CHANNEL_INT,
                        NotificationCompat.Builder(this@apply, APP_CHANNEL)
                            .setSmallIcon(R.drawable.ic_cast_black_24dp)
                            .setContentTitle("广播信息")
                            .setContentText("您的应用发出了广播")
                            .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("该通知来自$appName"))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setColor(getColor(R.color.colorAccent))
                            .build())
                }
            }
        }
    }
}
