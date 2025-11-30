package af.mobile.mybmi.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

object ImageUtils {

    // Simpan Foto Profil (Tetap Sama)
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val fileName = "profile_${UUID.randomUUID()}.jpg"
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
            file.absolutePath
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    // --- FUNGSI SCREENSHOT BARU (MANUAL & STABIL) ---

    // 1. Mengubah View Android menjadi Bitmap
    fun captureViewToBitmap(view: View): Bitmap {
        // Buat bitmap kosong seukuran layar
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        // Gambar view ke dalam bitmap tersebut
        val canvas = Canvas(bitmap)

        // Pastikan background tidak transparan (default putih)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }

        view.draw(canvas)
        return bitmap
    }

    // 2. Simpan Bitmap ke Galeri HP
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String): Boolean {
        val filename = "$title.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/myBMI")
                }
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(imagesDir, "myBMI")
                if (!appDir.exists()) appDir.mkdirs()
                val image = File(appDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}