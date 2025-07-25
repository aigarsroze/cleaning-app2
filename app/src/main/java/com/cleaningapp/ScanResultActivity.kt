
package com.cleaningapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ScanResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)

        val result = intent.getStringExtra("QR_RESULT") ?: "No result"
        val resultView: TextView = findViewById(R.id.qrResultText)
        resultView.text = result
    }
}
