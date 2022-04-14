package com.abner.manager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.abner.manager.util.NetUtils
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var mServerManager: ServerManager? = null
    private var mBtnStart: Button? = null
    private var mBtnStop: Button? = null
    private var mTvMessage: TextView? = null
    private var mRootUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mBtnStart = findViewById(R.id.btn_start)
        mBtnStop = findViewById(R.id.btn_stop)
        mTvMessage = findViewById(R.id.tv_message)
        mBtnStart!!.setOnClickListener(this)
        mBtnStop!!.setOnClickListener(this)
        mServerManager = ServerManager(this)
        mServerManager!!.register()
        if (NetUtils.getNetworkAvailableType(this) == 0) {
            mBtnStart!!.performClick()
        } else {
            mTvMessage!!.setText(R.string.no_wlan)
        }

        //获取存储权限
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1000
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mServerManager!!.unRegister()
        mServerManager!!.stopServer()
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.btn_start -> {
                if (NetUtils.getNetworkAvailableType(this) != 0) {
                    Toast.makeText(this, "远程管理需要连接WLAN", Toast.LENGTH_SHORT).show()
                    return
                }
                mServerManager!!.startServer()
            }
            R.id.btn_stop -> {
                mServerManager!!.stopServer()
            }
        }
    }

    /**
     * Start notify.
     */
    fun onServerStart(ip: String) {
        mBtnStart!!.visibility = View.GONE
        mBtnStop!!.visibility = View.VISIBLE
        //mBtnBrowser.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(ip)) {
            val addressList: MutableList<String?> = LinkedList()
            mRootUrl = "http://$ip:9999/"
            addressList.add("请保证手机和电脑连接的是同一个网络")
            //addressList.add("当前连接WLAN:" + NetUtils.getConnectWifiSsid(this));
            addressList.add("请在PC端浏览器输入:")
            addressList.add(mRootUrl)
            mTvMessage!!.text = TextUtils.join("\n", addressList)
        } else {
            mRootUrl = null
            mTvMessage!!.setText(R.string.server_ip_error)
        }
    }

    /**
     * Error
     */
    fun onServerError(message: String?) {
        mRootUrl = null
        mBtnStart!!.visibility = View.VISIBLE
        mBtnStop!!.visibility = View.GONE
        mTvMessage!!.text = message
    }

    /**
     * Stop
     */
    fun onServerStop() {
        mRootUrl = null
        mBtnStart!!.visibility = View.VISIBLE
        mBtnStop!!.visibility = View.GONE
        mTvMessage!!.setText(R.string.server_stop_succeed)
    }
}