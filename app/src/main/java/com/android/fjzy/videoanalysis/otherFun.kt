package com.android.fjzy.videoanalysis


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import javax.net.ssl.SSLSocketFactory
import java.net.HttpURLConnection


object otherFun {
    fun httpGetText(getURL: String, threadsign: Int, mhandler: Handler) {

        Thread(Runnable {
            val httpGet = HttpGet(getURL)
            val httpCient = DefaultHttpClient()
            SSLSocketFactory.getDefault().createSocket()
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36")
            try {
                val httpResponse = httpCient.execute(httpGet)
                if (httpResponse.statusLine.statusCode == 200) {
                    val entity = httpResponse.entity
                    val response = EntityUtils.toString(entity, "utf-8")
                    val message = Message()
                    message.what = threadsign
                    message.obj = response.toString()
                    mhandler.sendMessage(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    fun loadImageFromNetwork(imageUrl: String, threadsign: Int, mhandler: Handler) {
        var drawable: Drawable? = null
        Thread(Runnable {
            try {
                drawable = Drawable.createFromStream(
                        URL(imageUrl).openStream(), "image.jpg")
                val message = Message()
                message.what = threadsign
                message.obj = drawable
                mhandler.sendMessage(message)
            } catch (e: IOException) {
                Log.d("loadImageFromNetwork", e.message)
            }

            if (drawable == null) {
                Log.d("loadImageFromNetwork", "null drawable")
            }
        }).start()
    }

    private fun getRedirectUrl(path: String): String? {
        var url: String? = null
        try {
            val conn = URL(path).openConnection() as HttpURLConnection
            conn.setInstanceFollowRedirects(false)
            conn.setConnectTimeout(5000)
            url = conn.getHeaderField("Location")
            conn.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return url
    }

    fun downloadFile(fileURL: String, filePATH: String, fileNAME: String, oksign: Int, maxsizesign: Int, sizesign: Int, mhandler: Handler) {
        Thread(Runnable {
            try {
                //下载路径，如果路径无效了，可换成你的下载路径
                val url = getRedirectUrl(fileURL)
                val path = "/sdcard/" + filePATH
                Log.i("path", path)
                val startTime = System.currentTimeMillis()
                //下载函数
                val filename = fileNAME
                //获取文件名
                val myURL = URL(url)
                Log.i("URL", "url=$url"+"\n"+"myURL=$myURL")
                val conn = myURL.openConnection()
                conn.connect()
                val `is` = conn.getInputStream()
                val fileSize = conn.contentLength//根据响应获取文件大小
                Log.i("fileSize", fileSize.toString())
                if (fileSize <= 0) throw RuntimeException("无法获知文件大小 ")
                if (`is` == null) throw RuntimeException("stream is null")
                val maxmessage = Message()
                maxmessage.what = maxsizesign
                maxmessage.obj = fileSize
                mhandler.sendMessage(maxmessage)
                val file1 = File(path)
                if (!file1.exists()) {
                    file1.mkdirs()
                }
                //把数据存入路径+文件名
                val fos = FileOutputStream(path + "/" + filename)
                val buf = ByteArray(8192)
                var downLoadFileSize = 0
                do {
                    //循环读取
                    val numread = `is`.read(buf)
                    if (numread == -1) {
                        break
                    }
                    fos.write(buf, 0, numread)
                    downLoadFileSize += numread
                    val message = Message()
                    message.what = sizesign
                    message.obj = downLoadFileSize
                    mhandler.sendMessage(message)
                    //更新进度条
                } while (true)
                var usetime = (System.currentTimeMillis() - startTime).toString()
                var toastTEXT = "下载完成！\n保存路径：$path \n文件名：$fileNAME"
                var pathTEXT = path + "/" + filename
                val bundle = Bundle()
                bundle.putString("msg", toastTEXT)
                bundle.putString("path", pathTEXT)
                val message = Message()
                message.what = oksign
                message.data = bundle
                mhandler.sendMessage(message)
                Log.i("DOWNLOAD", "download success!UseTime=" + usetime)
                `is`.close()
            } catch (ex: Exception) {
                Log.e("DOWNLOAD", "error: " + ex.message, ex)
            }
        }).start()
    }

    fun textchazhao(neirong: String, zuobian: String, youbian: String): String {
//        Log.d("textchazhao-neirong", neirong)
        var sString: String? = null
        var leftof = neirong.indexOf(zuobian)
        var rightof = neirong.indexOf(youbian)
        if (leftof >= 0 && rightof >= 0) {
            sString = neirong.substring(zuobian.length + leftof, rightof)
            return sString
        }
        return "没找到"

    }
}
