package xh.zero.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.TextView
import java.io.*

class FileUtil {
    companion object {

        /**
         * android assets path: file:///android_asset/file.txt
         * file: 直接填文件名就行了
         */
        fun readAssetsJson(file: String, context: Context): String {
//            var inS = inputStream
//            val inputStream = FileInputStream(file)
            val result = StringBuffer()
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(InputStreamReader(context.assets.open(file)))
                var line = reader.readLine()
                while (line != null) {
                    result.append(line).append("\n")
                    line = reader.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    if (reader != null) {
                        reader.close()
                    }
//                    if (inputStream != null) {
//                        inputStream.close()
//                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return result.toString()
        }

        fun getCacheFolderSize(context: Context) : Long {
            val cacheFile = File(context.cacheDir, ".")
            val outCacheFile = File(context.externalCacheDir, ".")

            val innerCache = getFileSize(cacheFile)
            val outerCache = getFileSize(outCacheFile)

            return innerCache + outerCache
        }

        fun getFileSize(file: File) : Long {
            var size: Long = 0
            if (file.isDirectory) {
                for (f in file.listFiles()) {
                    size += getFileSize(f)
                }
            } else {
                size += file.length()
            }

            return size
        }

        fun clearCacheFolder(context: Context) {
            val cacheFile = File(context.cacheDir, ".")
            val outCacheFile = File(context.externalCacheDir, ".")
            deleteFile(cacheFile)
            deleteFile(outCacheFile)
        }

        fun deleteFile(file: File): Boolean {
            return if (file.exists()) {
                if (file.isDirectory) {
                    for (f in file.listFiles()) {
                        deleteFile(f)
                    }
                }
                file.delete()
            } else {
                false
            }
        }

        fun saveImageToPath(img: Bitmap, path: String) {
            try {
                val out = FileOutputStream(path)
                img.compress(Bitmap.CompressFormat.PNG, 100, out)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun saveInputStreamToFile(input: InputStream, file: File) {
            try {
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }
            } finally {
                input.close()
            }
        }
    }
}