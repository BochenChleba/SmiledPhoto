package com.github.bochenchleba.smiledphoto.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.bochenchleba.smiledphoto.R
import com.github.bochenchleba.smiledphoto.data.constants.Constants
import com.github.bochenchleba.smiledphoto.databinding.ActivityMainBinding
import com.github.bochenchleba.smiledphoto.extension.showDialog
import com.github.bochenchleba.smiledphoto.ui.gallery.GalleryDialog
import com.github.bochenchleba.smiledphoto.ui.settings_dialog.SettingsDialog
import org.jetbrains.anko.toast
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

abstract class MainActivity : AppCompatActivity(), KoinComponent {

    // implement photo actions - delete
    // repackage to com.github.bochenchleba.smiledphoto
    // upload on github

    protected val viewModel: MainViewModel by inject()
    protected val galleryDirectory: File by lazy {
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

    fun openGallery() {
        showDialog(GalleryDialog.newInstance(galleryDirectory.path))
    }
}
