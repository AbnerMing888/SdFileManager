package com.abner.manager

import android.app.Application
import android.content.Context
import android.os.Environment
import com.abner.manager.util.FileUtils
import com.yanzhenjie.andserver.util.IOUtils
import java.io.File

class App : Application() {
    private var mRootDir: File? = null
    override fun onCreate() {
        super.onCreate()
        if (mInstance == null) {
            mInstance = this
            initRootPath(this)
        }
    }

    val rootDir: File
        get() = mRootDir!!

    private fun initRootPath(context: Context) {
        if (mRootDir != null) return
        mRootDir = if (FileUtils.storageAvailable()) {
            Environment.getExternalStorageDirectory()
        } else {
            context.filesDir
        }
        mRootDir = File(mRootDir, "AndServer")
        IOUtils.createFolder(mRootDir)
    }

    companion object {
        private var mInstance: App? = null
        val instance: App
            get() = mInstance!!
    }
}