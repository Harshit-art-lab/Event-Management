package com.eventmanagement

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.eventmanagement.data.FirebaseRepository
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.launch

class QRScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var btnCancel: Button
    private var capture: CaptureManager? = null
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        barcodeView = findViewById(R.id.barcodeScannerView)
        btnCancel = findViewById(R.id.btnCancelScan)

        btnCancel.setOnClickListener {
            finish()
        }

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            initScanner()
        }
    }

    private fun initScanner() {
        capture = CaptureManager(this, barcodeView)
        capture?.initializeFromIntent(intent, null)

        barcodeView.decodeContinuous { result ->
            result?.let {
                handleQRCode(it.text)
            }
        }

        capture?.decode()
    }

    private fun handleQRCode(qrData: String) {
        // Pause scanning while processing
        barcodeView.pause()

        // QR Code format: "EVENT_ID:event_id_here"
        if (qrData.startsWith("EVENT_ID:")) {
            val eventId = qrData.removePrefix("EVENT_ID:")
            val repository = FirebaseRepository()

            lifecycleScope.launch {
                val userId = repository.getCurrentUser()?.uid

                if (userId == null) {
                    Toast.makeText(
                        this@QRScannerActivity,
                        "Please login first",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }

                val result = repository.markAttendance(eventId, userId)

                if (result.isSuccess) {
                    Toast.makeText(
                        this@QRScannerActivity,
                        "Attendance marked! Points added.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@QRScannerActivity,
                        result.exceptionOrNull()?.message ?: "Failed to mark attendance",
                        Toast.LENGTH_SHORT
                    ).show()
                    barcodeView.resume()
                }
            }
        } else {
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show()
            barcodeView.resume()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        capture?.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture?.onDestroy()
    }
}