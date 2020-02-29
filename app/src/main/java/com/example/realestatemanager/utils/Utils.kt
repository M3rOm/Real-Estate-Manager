package com.example.realestatemanager.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.example.realestatemanager.R.drawable
import com.example.realestatemanager.utils.Constants.Currencies
import com.example.realestatemanager.utils.Constants.InternetType
import com.example.realestatemanager.utils.Constants.NotificationsChannels
import com.example.realestatemanager.utils.Constants.PrefesKeys
import java.io.IOException
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar

object Utils {

    private const val currencyChangeRatio = 0.9

    fun convertDollarToEuro(dollars: Int): Int {
        return Math.round(dollars * currencyChangeRatio).toInt()
    }

    fun convertEuroToDollar(dollars: Int): Int {
        return Math.round(dollars / currencyChangeRatio).toInt()
    }

    val todayDate: String
        get() {
            val calendar = Calendar.getInstance()
            return formatDate(calendar)
        }
    //Check internet availability
    fun isInternetAvailable(context: Context): Boolean {
        return internetType(context) == InternetType.INTERNET_3G || internetType(
            context
        ) == InternetType.INTERNET_WIFI
    }
    //Determine internet type
    fun internetType(context: Context): Int {
        return try {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                //For Marshmallow and higher versions
                val activeNetwork = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager
                    .getNetworkCapabilities(activeNetwork)
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return InternetType.INTERNET_WIFI
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return InternetType.INTERNET_3G
                }
            } else { //For versions lower than Marshmallow
                val type = connectivityManager.activeNetworkInfo.type
                if (type == ConnectivityManager.TYPE_WIFI) { //is connected to wifi
                    return InternetType.INTERNET_WIFI
                } else if (type == ConnectivityManager.TYPE_MOBILE) { //is connected to 3G
                    return InternetType.INTERNET_3G
                }
            }
            InternetType.INTERNET_NONE
        } catch (e: Exception) {
            InternetType.INTERNET_NONE
        }
    }

    @JvmOverloads
    fun createNotification(
        context: Context?,
        title: String?,
        message: String?,
        channelId: String? = null
    ) {
        var channelId = channelId
        if (channelId == null) {
            channelId = NotificationsChannels.DEFAULT_CHANNEL_ID
        }
        val builder =
            Builder(context!!, channelId)
                .setSmallIcon(drawable.ic_playlist_add_check_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, builder.build())
    }

    @SuppressLint("DefaultLocale")
    fun formatDoubleToString(value: Double?): String {
        return String.format("%,.2f", value)
    }

    fun unixToCalendar(unixTime: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixTime
        return calendar
    }

    fun formatDate(unixTime: Long): String {
        return formatDate(
            unixToCalendar(
                unixTime
            )
        )
    }

    fun formatDate(calendar: Calendar): String {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(calendar.time)
    }

    fun bitmapsFromUrl(
        urlList: List<String?>,
        onReceivingBitmapFromUrl: OnReceivingBitmapFromUrl,
        activity: Activity
    ) {
        val getBitmapsThread = Thread(Runnable {
            var image: Bitmap
            val bitmaps: MutableList<Bitmap> = ArrayList()
            try {
                for (urlString in urlList) {
                    val url = URL(urlString)
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    bitmaps.add(image)
                }
                activity.runOnUiThread { onReceivingBitmapFromUrl.onSuccess(bitmaps) }
            } catch (e: IOException) {
                activity.runOnUiThread { onReceivingBitmapFromUrl.onFailure(e) }
            }
        })
        getBitmapsThread.start()
    }

    fun getAddressClassFromString(
        adressString: String?,
        context: Context?
    ): Address? {
        val coder = Geocoder(context)
        var address: Address? = null
        try {
            val adresses =
                coder.getFromLocationName(adressString, 1) as ArrayList<Address>
            address = adresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PrefesKeys.PREFS_KEY, Context.MODE_PRIVATE)
    }

    fun storeCurrency(context: Context, value: String) {
        storeDataFromPrefs(
            context,
            value,
            PrefesKeys.CURRENCY_KEY
        )
    }

    fun getCurrency(context: Context): String? {
        return getDataFromPrefs(
            context,
            Currencies.EURO,
            PrefesKeys.CURRENCY_KEY
        )
    }

    private fun storeDataFromPrefs(
        context: Context,
        value: String,
        key: String
    ) {
        getSharedPreference(context).edit().putString(key, value)
            .apply()
    }

    private fun getDataFromPrefs(
        context: Context,
        DefaultValue: String,
        key: String
    ): String? {
        return getSharedPreference(context)
            .getString(key, DefaultValue)
    }

    fun setWifiEnabled(context: Context, enabled: Boolean) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = enabled
    }

    interface OnReceivingBitmapFromUrl {
        fun onSuccess(bitmaps: List<Bitmap>)
        fun onFailure(e: Exception)
    }
}