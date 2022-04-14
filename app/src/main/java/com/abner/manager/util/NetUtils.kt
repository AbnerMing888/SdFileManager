package com.abner.manager.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.abner.manager.util.Logger.i
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import java.util.regex.Pattern

object NetUtils {
    /**
     * Ipv4 address check.
     */
    private val IPV4_PATTERN = Pattern.compile(
        "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$"
    )

    /**
     * Check if valid IPV4 address.
     *
     * @param input the address string to check for validity.
     * @return True if the input parameter is a valid IPv4 address.
     */
    fun isIPv4Address(input: String?): Boolean {
        return IPV4_PATTERN.matcher(input).matches()
    }

    /**
     * Get local Ip address.
     */
    @JvmStatic
    val localIPAddress: InetAddress?
        get() {
            var enumeration: Enumeration<NetworkInterface>? = null
            try {
                enumeration = NetworkInterface.getNetworkInterfaces()
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    val nif = enumeration.nextElement()
                    val inetAddresses = nif.inetAddresses
                    if (inetAddresses != null) {
                        while (inetAddresses.hasMoreElements()) {
                            val inetAddress = inetAddresses.nextElement()
                            if (!inetAddress.isLoopbackAddress && isIPv4Address(inetAddress.hostAddress)) {
                                return inetAddress
                            }
                        }
                    }
                }
            }
            return null
        }

    /**
     * 获取当前连接的wifi名称
     * @param context
     * @return
     */
    fun getConnectWifiSsid(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        i("wifiInfo：$wifiInfo")
        i("SSID：" + wifiInfo.ssid)
        return wifiInfo.ssid
    }

    /**
     * 0代表连接的是WiFi，1代表连接的是GPRS,2 代表无网络
     *
     * @return
     */
    fun getNetworkAvailableType(context: Context): Int {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ?: return 2
        val networkinfo = manager.activeNetworkInfo
        return if (networkinfo == null || !networkinfo.isAvailable) {
            2
        } else {
            val wifi =
                manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.isConnectedOrConnecting
            if (wifi) {
                0
            } else {
                1
            }
        }
    }
}