package com.abner.manager.util

import android.util.Log

object Logger {
    private const val TAG = "SIMPLE_LOGGER"
    private const val DEBUG = true
    @JvmStatic
    fun i(obj: Any?) {
        if (DEBUG) {
            Log.i(TAG, obj?.toString() ?: "null")
        }
    }

    fun d(obj: Any?) {
        if (DEBUG) {
            Log.d(TAG, obj?.toString() ?: "null")
        }
    }

    fun v(obj: Any?) {
        if (DEBUG) {
            Log.v(TAG, obj?.toString() ?: "null")
        }
    }

    fun w(obj: Any?) {
        if (DEBUG) {
            Log.w(TAG, obj?.toString() ?: "null")
        }
    }

    @JvmStatic
    fun e(obj: Any?) {
        if (DEBUG) {
            Log.e(TAG, obj?.toString() ?: "null")
        }
    }
}