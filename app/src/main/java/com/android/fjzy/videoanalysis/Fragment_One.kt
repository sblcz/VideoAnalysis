package com.android.fjzy.videoanalysis

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_one.*

class Fragment_One : Fragment() {
    var videoName = ""
    var analysis = 0
    var get_videoTXT = 1;
    var get_videoIMG = 2
    var down_video = 3
    var down_maxsize = 4
    var down_size = 5
    var analysisSign = 0
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                analysis -> {
                    var rtnSign = msg?.obj as Int
                    analysisSign = rtnSign
                    //Toast.makeText(context, "当前：" + analysisSign.toString(), Toast.LENGTH_SHORT).show()
//                    when (analysisSign) {
//                        0 -> editText_HyURL.hint = "http://t.huoshan.com/d6oA72/"
//                        1 -> editText_HyURL.hint = "http://v.douyin.com/dhnXCR/"
//                    }
                    Log.d("analysisSign", analysisSign.toString())
                }
                get_videoTXT -> {
                    var rtnTEXT = msg?.obj.toString()
                    getvideodata(rtnTEXT)
                }
                get_videoIMG -> {
                    var imgdrawable = msg?.obj as Drawable
                    imageView_huoshan.setImageDrawable(imgdrawable)
                }
                down_video -> {
                    var toastTEXT = msg.data.getString("msg").toString()
                    var downpath = msg?.data.getString("path").toString()
                    downOver(toastTEXT, downpath)
                }
                down_maxsize -> {
                    var maxsize = msg?.obj as Int
                    downBar.max = maxsize
                }
                down_size -> {
                    var nowsize = msg?.obj as Int
                    downBar.progress = nowsize
                }
                else -> {

                }
            }
        }
    }

    fun downOver(toast: String, path: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("是否打开此视频？")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(toast)
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    try {
                        val FileUri = Uri.parse(path)
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(FileUri, "video/mp4")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                .setNegativeButton("取消", null)
        builder.create().show()
    }

    fun getvideodata(videotext: String) {
        var video_URL = ""
        var video_editer = ""
        var video_img = ""
        when (analysisSign) {
            0 -> {
                video_URL = otherFun.textchazhao(videotext, "data-src=\"", "\" data-poster-src")
                video_img = otherFun.textchazhao(videotext, "data-poster-src=\"", "\"></div><div class=\"player-poster\"")
                video_editer = otherFun.textchazhao(videotext, "<title>", "</title>")
            }
            1 -> {
                video_URL = otherFun.textchazhao(videotext, "class=\"video-player\" src=\"", "\" preload=\"auto\" type=\"video/mp4\"")
                video_img = otherFun.textchazhao(videotext, "name=\"shareImage\" value=\"", "\">   <div style=\"display:none;\">")
                video_editer = otherFun.textchazhao(videotext, "<p class=\"user-info-name\">@", "</p><p class=\"user-info-id\">")
            }
        }
        editText_HvURL.text = Editable.Factory().newEditable(video_URL)
        otherFun.loadImageFromNetwork(video_img, get_videoIMG, mHandler)
        val getTime = System.currentTimeMillis()
        videoName = video_editer + "-" + getTime.toString() + ".mp4"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_one, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainActivity.otherMainFun.setmhandler(mHandler)
        var yurlSign = ""
        var vurlSign = ""

        button_Hvget.setOnClickListener() {
            when (analysisSign) {
                0 -> yurlSign = "huoshan.com"
                1 -> yurlSign = "douyin.com"
            }
            var edit_yurl = editText_HyURL.text.toString().replace(" ", "")
            if (edit_yurl == "") {
                Toast.makeText(context, "欲解析地址不能为空！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var getpos = edit_yurl.indexOf(yurlSign)
            var getfrist = edit_yurl.substring(0, 4).toLowerCase()
            if (getpos > 0 && getfrist == "http") {
                val gettext = otherFun.httpGetText(edit_yurl, get_videoTXT, mHandler)
            } else {
                Toast.makeText(context, "地址格式不正确！", Toast.LENGTH_SHORT).show()
            }
        }
        button_Hvdown.setOnClickListener {
            when (analysisSign) {
                0 -> vurlSign = "snssdk.com/hotsoon/item"
                1 -> vurlSign = "snssdk.com/aweme"
            }

            var edit_downURL = editText_HvURL.text.toString().replace(" ", "").replace("amp;", "")
            if (edit_downURL == "") {
                Toast.makeText(context, "视频地址不能为空！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var getpos = edit_downURL.indexOf(vurlSign)
            var getfrist = edit_downURL.substring(0, 4).toLowerCase()
            if (getpos > 0 && getfrist == "http") {
                otherFun.downloadFile(edit_downURL, "download_qsy", videoName, down_video, down_maxsize, down_size, mHandler)
            } else {
                Toast.makeText(context, "视频地址格式不正确！", Toast.LENGTH_SHORT).show()
            }
        }
        button_Hvopen.setOnClickListener {
            when (analysisSign) {
                0 -> vurlSign = "snssdk.com/hotsoon/item"
                1 -> vurlSign = "snssdk.com/aweme"
            }
            var edit_downURL = editText_HvURL.text.toString().replace(" ", "")
            if (edit_downURL == "") {
                Toast.makeText(context, "视频地址不能为空！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var getpos = edit_downURL.indexOf(vurlSign)
            var getfrist = edit_downURL.substring(0, 4).toLowerCase()
            if (getpos > 0 && getfrist == "http") {
                val uri = Uri.parse(edit_downURL)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } else {
                Toast.makeText(context, "视频地址格式不正确！", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
