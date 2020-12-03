package com.example.smiledphoto.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.databinding.DataBindingUtil
import com.example.smiledphoto.R
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.databinding.ActivityMainBinding
import com.example.smiledphoto.extension.showDialog
import com.example.smiledphoto.ui.settings_dialog.SettingsDialog
import org.jetbrains.anko.toast
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File


abstract class MainActivity : AppCompatActivity(), KoinComponent {

    protected val viewModel: MainViewModel by inject()
    protected val pictureDirectory: File by lazy {
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: kotlin.run {
                toast(R.string.cannot_access_directory_error_toast)
                throw IllegalStateException()
            }
    }

    abstract fun startCamera()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setDataBinding()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startCameraOrRequestPermissions()
    }

    private fun setDataBinding() {
        DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .let { binding ->
                binding.lifecycleOwner = this
                binding.activity = this
                binding.viewModel = viewModel
            }
    }

    private fun startCameraOrRequestPermissions() {
        if (permissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun permissionsGranted() =
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (permissionsGranted()) {
                startCamera()
            } else {
                toast(R.string.permissions_not_granted_toast)
                finish()
            }
        }
    }

    fun showSettingsDialog() {
        viewModel.isImageAnalysisActive.value = false
        showDialog(SettingsDialog.newInstance())
    }

    fun openPhotoDirectory() {
//        val uri = getUriForFile(this, "com.example.smiledphoto", File(pictureDirectory.absolutePath))
//        val uri2 = Uri.parse("content://sdcard/Android/data/com.example.smiledphoto/files/Pictures")
//        val intent = Intent(Intent.ACTION_VIEW)
//     //   intent.setData(uri)
//        intent.setDataAndType(uri, "image/*");
//     //   intent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivity(Intent.createChooser(intent, "Open folder"));
    }
}
