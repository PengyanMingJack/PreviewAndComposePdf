package com.zchd.pdf

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

/**
 * Copyright (C), 2021-2023, 中传互动（湖北）信息技术有限公司
 * Author: 彭艳明
 * Date:2023年03月08日
 * Description:
 */
class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val webView = findViewById<WebView>(R.id.webView)
        val webSettings = webView.settings
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        if (PdfComposeToWrite.isSignPdf) {
            webView.loadUrl("file:///android_asset/index.html?${PdfComposeToWrite.signPdfFilePath}")
        } else {
            webView.loadUrl("file:///android_asset/index.html?${PdfComposeToWrite.defPdfFilePath}")
        }
    }
}