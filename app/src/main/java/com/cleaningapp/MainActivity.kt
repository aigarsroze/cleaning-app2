
package com.cleaningapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.hardware.Camera

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PreviewCallback {

    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null
    private val scanner = BarcodeScanning.getClient()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startCamera()
        else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceView = findViewById(R.id.cameraPreview)
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        camera = Camera.open()
        camera?.setPreviewDisplay(surfaceHolder)
        camera?.setPreviewCallback(this)
        camera?.startPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.stopPreview()
        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        val parameters = camera?.parameters
        val width = parameters?.previewSize?.width ?: return
        val height = parameters.previewSize.height
        val yuvImage = android.graphics.YuvImage(data, ImageFormat.NV21, width, height, null)
        val out = java.io.ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        val inputImage = InputImage.fromByteArray(imageBytes, width, height, 0, InputImage.IMAGE_FORMAT_NV21)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let {
                        camera?.setPreviewCallback(null)
                        this.camera?.stopPreview()
                        val intent = Intent(this, ScanResultActivity::class.java)
                        intent.putExtra("QR_RESULT", it)
                        startActivity(intent)
                        finish()
                    }
                }
            }
    }
}
