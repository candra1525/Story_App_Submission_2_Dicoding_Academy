package com.candra.dicodingstoryapplication.activity.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.main.MainActivity
import com.candra.dicodingstoryapplication.data.result.Result
import com.candra.dicodingstoryapplication.databinding.ActivityAddStoryBinding
import com.candra.dicodingstoryapplication.utils.getImageUri
import com.candra.dicodingstoryapplication.utils.uriToFile
import com.candra.dicodingstoryapplication.viewModelFactory.StoryViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var imageUri: Uri? = null
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        StoryViewModelFactory.getInstance(this@AddStoryActivity, true)
    }
    private var check: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            btnGallery.setOnClickListener { startGallery() }
            backToMainFromAddStory.setOnClickListener {
                onBackPressed()
            }

            btnCamera.setOnClickListener {
                startCamera()
            }
            buttonAdd.setOnClickListener {
                runBlocking {
                    uploadImageToServer()
                }
            }
            checkboxLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                check = isChecked

            }
        }

        // 1
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
    }

    // 2
    private fun startCamera() {
        imageUri = getImageUri(this)
        launcherIntentCamera.launch(imageUri)
    }

    //3 DONE
    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    // DONE
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled || isNetworkEnabled
    }

    // 4 DONE
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.permission_ok), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun uploadImageToServer() {
        imageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            Log.d("Image File", "showImage: ${imageFile.path}")
            val desc = binding.edAddDescription.text.toString()
            var lat: Double? = null
            var lon: Double? = null
            showLoading(true)
            val requestBodyDesc = desc.toRequestBody("text/plain".toMediaType())
            if (check) {
                if (ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (isLocationEnabled()) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                            loc?.let {
                                lat = loc.latitude
                                lon = loc.longitude
                                Log.i("LOCATION", "Lat: $lat, Lon: $lon")
                                runBlocking {
                                    continueUpload(imageFile, requestBodyDesc, lat, lon)
                                }
                            } ?: runBlocking {
                                continueUpload(imageFile, requestBodyDesc, null, null)
                            }
                        }
                    }
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                continueUpload(imageFile, requestBodyDesc, null, null)
            }
        } ?: showToast(getString(R.string.upload_fail))
    }

    private suspend fun continueUpload(
        imageFile: File,
        requestBodyDesc: RequestBody,
        lat: Double?,
        lon: Double?
    ) {
        val requestImageToFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", imageFile.name, requestImageToFile
        )

        addStoryViewModel.uploadStory(multipartBody, requestBodyDesc, lat, lon)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val sweetAlertDialog =
                                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText(
                                    getString(R.string.upload_success)
                                ).setContentText(getString(R.string.story_success_upload))
                                    .setConfirmButton("OK") {
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    }
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.show()
                        }

                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this, result.error.toString(), Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.pbLoadingAddStory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imageAddStory.setImageURI(it)
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}