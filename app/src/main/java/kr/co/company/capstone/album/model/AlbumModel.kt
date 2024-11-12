package kr.co.company.capstone.album.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kr.co.company.capstone.album.contract.AlbumContract

class AlbumModel(private val context: Context) : AlbumContract.Model {
    override fun fetchImages(): List<Uri> {
        val imageList = mutableListOf<Uri>()

        val contentResolver: ContentResolver = context.contentResolver

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android Q 이상 버전
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI // Android Q 미만
        }

        val projection = arrayOf(MediaStore.Images.Media._ID)

        // 동영상 제외
        val selection = "${MediaStore.Images.Media.MIME_TYPE} LIKE ?"
        val selectionArgs = arrayOf("image/%")

        // 최신순 정렬
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(collection, id.toString())
                imageList.add(contentUri)
            }
        }

        return imageList
    }
}
