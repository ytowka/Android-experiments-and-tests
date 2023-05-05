package com.danilkha.composeapptemplate.data

import android.content.Context
import android.provider.MediaStore
import javax.inject.Inject

class ImageRepository @Inject constructor(
    val context: Context
) {
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.MediaColumns.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns._ID
    )
    val contentResolver = context.contentResolver


    fun getAllImages(): List<String> {
        return contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.MediaColumns._ID} DESC"
        )?.let { cursor ->
            val columnIndexData = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            val columnIndexFolderName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val columnIndexId = cursor.getColumnIndex(MediaStore.MediaColumns._ID)

            buildList(capacity = cursor.count) {
                while (cursor.moveToNext()){
                    val imagePath = cursor.getString(columnIndexData)
                    val imageFolder = cursor.getString(columnIndexFolderName)
                    val imageId = cursor.getLong(columnIndexId)
                    add(imagePath)
                }

            }
        } ?: emptyList()
    }
}