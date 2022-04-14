package com.abner.manager

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.abner.manager.util.NetUtils.localIPAddress
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import com.yanzhenjie.andserver.Server.ServerListener
import java.util.concurrent.TimeUnit

class HttpService : Service() {
    private var mServer: Server? = null
    override fun onCreate() {
        mServer = AndServer.serverBuilder(this)
            .inetAddress(localIPAddress)
            .port(9999)
            .timeout(10, TimeUnit.SECONDS)
            .listener(object : ServerListener {
                override fun onStarted() {
                    val hostAddress = mServer!!.inetAddress.hostAddress
                    ServerManager.onServerStart(this@HttpService, hostAddress)
                }

                override fun onStopped() {
                    ServerManager.onServerStop(this@HttpService)
                }

                override fun onException(e: Exception) {
                    ServerManager.onServerError(this@HttpService, e.message)
                }
            })
            .build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startServer()
        return START_STICKY
    }

    override fun onDestroy() {
        stopServer()
        super.onDestroy()
    }

    /**
     * Start
     */
    private fun startServer() {
        if (mServer!!.isRunning) {
            val hostAddress = mServer!!.inetAddress.hostAddress
            ServerManager.onServerStart(this@HttpService, hostAddress)
        } else {
            mServer!!.startup()
        }
    }

    /**
     * Stop
     */
    private fun stopServer() {
        mServer!!.shutdown()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}