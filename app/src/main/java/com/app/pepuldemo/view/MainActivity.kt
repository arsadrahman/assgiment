package com.app.pepuldemo.view

import android.content.DialogInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.pepuldemo.R
import com.app.pepuldemo.databinding.ActivityMainBinding
import com.app.pepuldemo.model.Data
import com.app.pepuldemo.model.FileModel
import com.app.pepuldemo.utility.Validator.Companion.getOriginalFileName
import com.app.pepuldemo.utility.Validator.Companion.getRealSizeFromUri
import com.app.pepuldemo.utility.Validator.Companion.isValidFileSize
import com.app.pepuldemo.view.adapters.Adapter
import com.app.pepuldemo.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.LinearLayoutManager


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    var datas: ArrayList<Data> = arrayListOf()
    lateinit var adapter:Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
        activityMainBinding.viewModel = homeViewModel
        activityMainBinding.activity = this
        activityMainBinding.lifecycleOwner = this
        activityMainBinding.floatingActionButton.setOnClickListener {
            //upload Video/Image
            pickImageOrVideo()

        }
        adapter = Adapter(this,datas)
        activityMainBinding.feedRv.layoutManager=
            LinearLayoutManager(this)

        adapter.onItemClick = { data ->
            homeViewModel.deleteItem(data)
        }

        homeViewModel.deletedData.observe(this, Observer { data ->
            datas.remove(data)
           adapter.notifyDataSetChanged()
        })
        activityMainBinding.feedRv.adapter = adapter

        homeViewModel.listData.observe(this, Observer { list ->
            datas.addAll(list)
            adapter.notifyDataSetChanged()
        })
        homeViewModel.successMessage.observe(this, Observer { message ->
            Toast.makeText(this, "Success $message", Toast.LENGTH_LONG).show();

        })

        homeViewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, "Error $message", Toast.LENGTH_LONG).show();
        })

        homeViewModel.getList()

    }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uri.getRealSizeFromUri(this)
                    ?.let { fileSize ->
                        if (fileSize.isValidFileSize()) {
                            //Upload the Image
                            homeViewModel.compressMedia(uri,true).observe(this,
                                Observer { compressedFile ->
                                    FileModel(fileUri = compressedFile as Uri, isVideo = false).getMultipartFromUri(this)?.let { part ->
                                        homeViewModel.uploadMedia(
                                            part
                                        )
                                    }
                                })
                        } else {
                            uri.getOriginalFileName(this)
                                ?.let { fileName ->
                                    Toast.makeText(
                                        applicationContext,
                                        "Please upload image less than 200 MB",
                                        Toast.LENGTH_LONG
                                    ).show();
                                }
                        }
                    }
            }
        }

    private val selectVideoFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uri.getRealSizeFromUri(this)
                    ?.let { fileSize ->
                        if (fileSize.isValidFileSize()) {
                            //Upload the Video
                          homeViewModel.compressMedia(uri,false).observe(this,
                                Observer { compressedUri ->
                                    FileModel(compressedUri as Uri?, isVideo = true).getMultipartFromUri(this)?.let { part ->
                                        homeViewModel.uploadMedia(
                                            part
                                        )
                                    }
                                })

                        } else {
                            uri.getOriginalFileName(this)
                                ?.let { fileName ->
                                    Toast.makeText(
                                        applicationContext,
                                        "Please upload video less than 200 MB",
                                        Toast.LENGTH_LONG
                                    ).show();
                                }
                        }
                    }
            }
        }


    private fun pickImageFileFromFolder() {
        selectImageFromGalleryResult.launch("image/jpeg")
    }

    private fun pickVideoFileFromFolder() {
        selectVideoFromGalleryResult.launch("video/mp4")
    }

    private fun pickImageOrVideo() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Pick the media ?")
        val tags_ = arrayOf("Image", "Video")
        builder.setItems(tags_,
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> {
                        pickImageFileFromFolder()
                    }
                    1 -> {
                        pickVideoFileFromFolder()
                    }
                }
            })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}