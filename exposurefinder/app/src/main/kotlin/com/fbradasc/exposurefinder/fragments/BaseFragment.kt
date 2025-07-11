package com.fbradasc.exposurefinder.fragments

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.fbradasc.exposurefinder.R
import com.fbradasc.exposurefinder.adapter.Media
import java.io.File

/**Parent class of all the fragments in this project*/
abstract class BaseFragment<B : ViewBinding>(private val fragmentLayout: Int) : Fragment() {
    /**
     *  Generic ViewBinding of the subclasses
     * */
    abstract val binding: B

    // The Folder location where all the files will be stored
    protected val outputDirectory: String by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${Environment.DIRECTORY_DCIM}/CameraXDemo/"
        } else {
            "${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/CameraXDemo/"
        }
    }

    // The permissions we need for the app to work properly
    private val permissions = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
//        Manifest.permission.READ_EXTERNAL_STORAGE,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }
    }

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            onPermissionGranted()
        } else {
            view?.let { v ->
                Snackbar.make(v, R.string.message_no_permissions, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_ok) { ActivityCompat.finishAffinity(requireActivity()) }
                    .show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Adding an option to handle the back press in fragment
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            onPermissionGranted()
        } else {
            permissionRequest.launch(permissions.toTypedArray())
        }
    }

    protected fun getMedia(): List<Media> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getMediaQPlus()
    } else {
        getMediaQMinus()
    }.reversed()

    private fun getMediaQPlus(): List<Media> {
        val items = mutableListOf<Media>()
        val contentResolver = requireContext().applicationContext.contentResolver

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.DATE_TAKEN,
            ),
            null,
            null,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val date = cursor.getLong(dateColumn)

                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                if (path == outputDirectory) {
                    items.add(Media(contentUri, date))
                }
            }
        }
        return items
    }

    private fun getMediaQMinus(): List<Media> {
        val items = mutableListOf<Media>()

        File(outputDirectory).listFiles()?.forEach {
            val authority = requireContext().applicationContext.packageName + ".provider"
            val mediaUri = FileProvider.getUriForFile(requireContext(), authority, it)
            items.add(Media(mediaUri, it.lastModified()))
        }

        return items
    }

    /**
     * Check for the permissions
     */
    protected fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * A function which will be called after the permission check
     * */
    open fun onPermissionGranted() = Unit

    /**
     * An abstract function which will be called on the Back button press
     * */
    abstract fun onBackPressed()

    @RequiresApi(Build.VERSION_CODES.R)
    protected fun hideSystemUI() {
        requireActivity().window.setDecorFitsSystemWindows(false)
        requireActivity().window.insetsController?.apply {
            // hide(WindowInsets.Type.systemBars())
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    protected fun showSystemUI() {
        requireActivity().window.setDecorFitsSystemWindows(true)
        requireActivity().window.insetsController?.apply {
            show(WindowInsets.Type.systemBars())
            // show(WindowInsets.Type.statusBars() and WindowInsets.Type.navigationBars())
        }
    }
}
