package com.wakeup.data.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wakeup.domain.model.Picture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class InternalFileUtil @Inject constructor(
    private val context: Context,
) {

    fun savePictureInInternalStorage(picture: Picture) {
        Glide.with(context)
            .asBitmap()
            .load(picture.path.toUri())
            .listener(PictureSaveRequestListener(picture, context))
            .override(1000, 1000)
            .fitCenter()
            .submit()
    }

    class PictureSaveRequestListener(
        private val picture: Picture,
        private val context: Context,
    ) : RequestListener<Bitmap> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean,
        ): Boolean {
            Timber.e(e)
            return false
        }

        override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: Target<Bitmap>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    val dirPath = File(context.filesDir, "images").apply { mkdirs() }
                    val filePath = File("${dirPath}/${picture.path.substringAfterLast("/")}")
                    Timber.d("${dirPath}/${picture.path.substringAfterLast("/")}")
                    FileOutputStream(filePath).use { out ->
                        resource?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                }.onFailure { exception ->
                    when (exception) {
                        is FileNotFoundException ->
                            Timber.e("FileNotFoundException : " + exception.message)
                        is IOException ->
                            Timber.e("IOException : " + exception.message)
                        else ->
                            Timber.e("AnotherException : " + exception.message)
                    }
                }
            }
            return false
        }
    }
}

