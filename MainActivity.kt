package com.marco.ptt

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader

class MainActivity : Activity() {
    private lateinit var web: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 通过 https://appassets.androidplatform.net 提供本地页面，保证是安全上下文，麦克风可用
        val loader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        web = WebView(this)
        setContentView(web)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.mediaPlaybackRequiresUserGesture = false
        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? =
                loader.shouldInterceptRequest(request.url)
        }
        web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread { request.grant(request.resources) }
            }
        }
        web.loadUrl("https://appassets.androidplatform.net/assets/index.html")

        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) perms += Manifest.permission.POST_NOTIFICATIONS
        requestPermissions(perms.toTypedArray(), 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // 前台服务：锁屏/切后台时保持进程和网络，继续收听
            startForegroundService(Intent(this, PttService::class.java))
        }
    }

    // 返回键不退出，只退到后台，保持在线
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onDestroy() {
        stopService(Intent(this, PttService::class.java))
        web.destroy()
        super.onDestroy()
    }
}
