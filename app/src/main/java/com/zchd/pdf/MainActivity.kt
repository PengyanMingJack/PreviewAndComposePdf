package com.zchd.pdf

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.registerActivityCallbacks()
//        PdfComposeToWrite().buildAuthorizeBook()
        findViewById<Button>(R.id.btn_compose).setOnClickListener {
//            SignPdfInfo().apply {
//                userName = "张三"
//                userCardType = "身份证"
//                userIdCard = "4215555555555555322"
//                houseRelation = "本人"
//                repName = "李四"
//                repCardType = "身份证"
//                repIdCard = "43255555555555555555"
//                authRelation = "父子"
//                PdfComposeToWrite().buildAuthorizeBook(this)
//            }
            PdfPageHelper().pdfTest()
        }
        findViewById<Button>(R.id.btn_preview).setOnClickListener {
            startActivity(
                Intent(
                    this, WebViewActivity::class.java
                )
            )
        }
    }
}