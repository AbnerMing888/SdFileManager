package com.abner.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ServerManager(private val mActivity: MainActivity) : BroadcastReceiver() {

    private var mService: Intent?=null

    /**
     * Register broadcast.
     */
    fun register() {
        val filter = IntentFilter(ACTION)
        mActivity.registerReceiver(this, filter)
    }

    /**
     * UnRegister broadcast.
     */
    fun unRegister() {
        mActivity.unregisterReceiver(this)
    }

    fun startServer() {
        mActivity.startService(mService)
    }

    fun stopServer() {
        mActivity.stopService(mService)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (ACTION == action) {
            val cmd = intent.getIntExtra(CMD_KEY, 0)
            when (cmd) {
                CMD_VALUE_START -> {
                    val ip = intent.getStringExtra(MESSAGE_KEY)
                    mActivity.onServerStart(ip!!)
                }
                CMD_VALUE_ERROR -> {
                    val error = intent.getStringExtra(MESSAGE_KEY)
                    mActivity.onServerError(error)
                }
                CMD_VALUE_STOP -> {
                    mActivity.onServerStop()
                }
            }
        }
    }

    companion object {
        private const val ACTION = "com.yanzhenjie.andserver.receiver"
        private const val CMD_KEY = "CMD_KEY"
        private const val MESSAGE_KEY = "MESSAGE_KEY"
        private const val CMD_VALUE_START = 1
        private const val CMD_VALUE_ERROR = 2
        private const val CMD_VALUE_STOP = 4

        /**
         * Notify serverStart.
         *
         * @param context context.
         */
        fun onServerStart(context: Context, hostAddress: String?) {
            sendBroadcast(context, CMD_VALUE_START, hostAddress)
        }

        /**
         * Notify serverStop.
         *
         * @param context context.
         */
        fun onServerError(context: Context, error: String?) {
            sendBroadcast(context, CMD_VALUE_ERROR, error)
        }

        /**
         * Notify serverStop.
         *
         * @param context context.
         */
        fun onServerStop(context: Context) {
            sendBroadcast(context, CMD_VALUE_STOP)
        }

        private fun sendBroadcast(context: Context, cmd: Int, message: String? = null) {
            val broadcast = Intent(ACTION)
            broadcast.putExtra(CMD_KEY, cmd)
            broadcast.putExtra(MESSAGE_KEY, message)
            context.sendBroadcast(broadcast)
        }
    }

    init {
        mService = Intent(mActivity, HttpService::class.java)
    }
}