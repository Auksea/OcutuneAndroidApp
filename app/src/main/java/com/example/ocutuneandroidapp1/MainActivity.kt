package com.example.ocutuneandroidapp1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_FILE_PICKER = 1

    private lateinit var btnSelectFile: Button
    private lateinit var btnUpload: Button
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSelectFile = findViewById(R.id.btnSelectFile)
        btnUpload = findViewById(R.id.btnUpload)

        btnSelectFile.setOnClickListener { openFilePicker() }
        btnUpload.setOnClickListener { uploadToAzureBlob() }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/csv"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_CODE_FILE_PICKER)
    }

    private fun uploadToAzureBlob() {
        if (selectedFileUri != null) {
            // TODO: Replace with your Azure Blob storage connection string and container name
            val connectionString = "DefaultEndpointsProtocol=https;AccountName=csvfromocutune;AccountKey=k2DYFS+56cSc7FkpOm/D6EmXP/OhJC1A4d429LKdpI1NHBLGC0gapXuaDI4W3AIpDS9dhbHRj4GD+AStdHoi9A==;EndpointSuffix=core.windows.net"
            val containerName = "csvfiles"

            try {
                val inputStream: InputStream? = selectedFileUri?.let { contentResolver.openInputStream(it) }
                if (inputStream != null) {
                    val blobServiceClient: BlobServiceClient =
                        BlobServiceClientBuilder().connectionString(connectionString).buildClient()
                    val containerClient: BlobContainerClient =
                        blobServiceClient.getBlobContainerClient(containerName)
                    val blobClient: BlobClient = containerClient.getBlobClient("uploaded-file.csv")
                    blobClient.upload(inputStream, inputStream.available().toLong(), true)
                    inputStream.close()
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Selected file is null", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                selectedFileUri = data.data
                btnUpload.isEnabled = true
            }
        }
    }
}
