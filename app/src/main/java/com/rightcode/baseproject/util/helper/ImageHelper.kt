package com.rightcode.baseproject.util.helper

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import com.rightcode.baseproject.ui.component.imagePicker.model.Image
import java.io.File

object ImageHelper {

    private fun getNameFromFilePath(path: String): String {
        return if (path.contains(File.separator)) {
            path.substring(path.lastIndexOf(File.separator) + 1)
        } else path
    }

    fun grantAppPermission(context: Context, intent: Intent, fileUri: Uri) {
        val resolvedIntentActivities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolvedIntentInfo in resolvedIntentActivities) {
            val packageName = resolvedIntentInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    fun revokeAppPermission(context: Context, fileUri: Uri) {
        context.revokeUriPermission(
            fileUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    fun singleListFromPath(id: Long, path: String): ArrayList<Image> {
        val images = arrayListOf<Image>()
        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        images.add(Image(id, getNameFromFilePath(path), uri))
        return images
    }

    fun singleListFromImage(image: Image): ArrayList<Image> {
        val images = arrayListOf<Image>()
        images.add(image)
        return images
    }

    fun filterImages(images: ArrayList<Image>, bukketId: Long?): ArrayList<Image> {
        if (bukketId == null) return images

        val filteredImages = arrayListOf<Image>()
        for (image in images) {
            if (image.bucketId == bukketId) {
                filteredImages.add(image)
            }
        }
        return filteredImages
    }

    fun findImageIndex(image: Image, images: ArrayList<Image>): Int {
        if (!images.isNullOrEmpty())
            images.forEachIndexed { index, item ->
                return if (item.uri == image.uri) index else -1
            }
        return -1
    }

    fun findImageIndexes(subImages: ArrayList<Image>, images: ArrayList<Image>): ArrayList<Int> {
        val indexes = arrayListOf<Int>()
        for (image in subImages) {
            for (i in images.indices) {
                if (images[i].uri == image.uri) {
                    indexes.add(i)
                    break
                }
            }
        }
        return indexes
    }


    fun isGifFormat(image: Image): Boolean {
        val extension = image.uri.toString().substring(image.uri.toString().lastIndexOf(".") + 1, image.uri.toString().length)
        return extension.equals("gif", ignoreCase = true)
    }
}
