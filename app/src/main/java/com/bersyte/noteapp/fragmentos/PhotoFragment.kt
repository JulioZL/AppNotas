package com.bersyte.noteapp.fragmentos

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FragmentPhotoBinding
import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.model.Multimedia
import com.bersyte.noteapp.model.Tarea
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoFragment : Fragment() {
    private lateinit var binding: FragmentPhotoBinding
    private lateinit var photoURI: Uri
    private lateinit var miContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        miContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(layoutInflater)

        binding.takePhoto.setOnClickListener {
            validarPermisos()
        }

        binding.savePhoto.setOnClickListener {
            val file = Multimedia (
                arguments?.getString("id")!!.toInt(),
                "photo",
                photoURI.toString(),
                binding.description.text.toString()
            )
            //Insert
            NoteDatabase.getInstance(requireActivity().applicationContext).MultimediaDao().insert(file)

            binding.savePhoto.visibility = View.INVISIBLE
            binding.takePhoto.visibility = View.INVISIBLE
            binding.description.isEnabled = false
        }

        return binding.root
    }

    private lateinit var currentPhotoPath: String
    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            setPic()
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = binding.imageView.width
        val targetH: Int = binding.imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    private val REQUEST_TAKE_PHOTO = 1
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            activity?.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoURI= FileProvider.getUriForFile(
                            miContext,
                            "com.example.noteeapp.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    }
                }
            }
        }
    }

    private fun validarPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                miContext,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        miContext,
                        "android.permission.CAMERA"
                    ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                dispatchTakePictureIntent()
            }
            shouldShowRequestPermissionRationale("android.permission.CAMERA") -> {
                val dialog = AlertDialog.Builder(miContext).apply {
                    setTitle("Acepta permisos, por favor :c")
                    setMessage("Acepta los permisos para poder guardar archivos multimedia en tus tareas y notas")
                        .setNegativeButton("Ok", DialogInterface.OnClickListener {
                                dialogInterface, i ->
                        })
                        .setPositiveButton("Solicitar permiso de nuevo",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                requestPermissions(
                                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"),
                                    REQUEST_TAKE_PHOTO)
                            })
                    create()
                }
                dialog.show()
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"),
                    REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    dispatchTakePictureIntent()
                } else {

                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}