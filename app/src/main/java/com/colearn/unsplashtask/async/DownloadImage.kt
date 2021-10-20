package com.colearn.unsplashtask.async

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream

interface DownloadImageInterface {
    fun galleryAddPic(file: String)
}
class DownloadImage(val image: Bitmap, val imageId: String, val callback: DownloadImageInterface): AsyncTask<Unit, Unit, String>() {

    override fun doInBackground(vararg params: Unit): String {
        val filename = "${imageId}.jpg"
        val createFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "unsplash")
        createFolder.mkdirs();
        val path = File(createFolder, filename)
        if (!path.exists()) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(path))
            Log.d("DownloadImage", "Image downloaded");
        } else {
            Log.d("DownloadImage",  "Image already downloaded");
        }
        return path.toString()
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        callback.galleryAddPic(result)
    }
}