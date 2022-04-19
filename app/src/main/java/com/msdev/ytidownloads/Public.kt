package com.msdev.ytidownloads

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import org.jsoup.Jsoup
import java.util.*

class Public(private val view: View, private val context: Context) {

    fun getSourceCode(etUrlVideo: String): String {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var html = ""
        try {
            val conn = Jsoup.connect(etUrlVideo).get()
            html = conn.html()
            if (html == null) {
                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT)
                    .show()
                MainActivity().reload()
            }
        } catch (e: Exception) {
            html = false.toString()
        }
        return html
    }

    fun isVideo(sourceCode: String): Boolean {
        var i = 0
        val match = "is_video".toRegex().find(sourceCode)
        if (match != null) {
            i = match.range.last + 3
        }
        return sourceCode[i] == 't'
    }

    fun downloadVideo(isvideo: Boolean, url: String) {
        if (isvideo) {
            val request = DownloadManager.Request(Uri.parse(url))
                .setMimeType("video/MP4")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/Ytidownloads/Videos/insta_${Random(123456789)}.mp4"
                )

            val dm: DownloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        } else {
            val request = DownloadManager.Request(Uri.parse(url))
                .setMimeType("image/JPG")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/Ytidownloads/Images/insta_${Random(123456789)}.jpg"
                )
            val dm: DownloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
        SnakeBar(view, context).success("Downloaded Successfully")

    }

    fun getVideoLink(isvideo: Boolean, sourceCode: String): String {
        var url = ""
        if (isvideo) {
            // Video
            var videoUrl = ""
            val fromWordRegex = "video_url".toRegex()
            val toWordRegex = "video_view_count".toRegex()
            val indexOfFirst = fromWordRegex.find(sourceCode)
            val indexOfLast = toWordRegex.find(sourceCode)
            val fromIndex = indexOfFirst?.range?.last!!
            val toIndex = indexOfLast?.range?.first!!
            videoUrl = sourceCode.substring(fromIndex +4 .. toIndex-4)

            if (videoUrl != "") {
                videoUrl += " "
                var i = 0
                while (videoUrl[i].toString() != " ") {
                    if (videoUrl[i].toString() == "\\") {
                        i += 6
                        url += "&"
                    } else {
                        url += videoUrl[i].toString()
                        i += 1
                    }
                }
            }
        } else {
            // image
            val indexFirst = "display_url".toRegex().find(sourceCode)
            val indexLast = "display_resources".toRegex().find(sourceCode)
            if (indexFirst != null && indexLast != null) {
                var imageUrl =
                    sourceCode.substring(indexFirst.range.last + 4..indexLast.range.first - 4)
                if (imageUrl != "") {
                    imageUrl += " "
                    var i = 0
                    while (imageUrl[i].toString() != " ") {
                        if (imageUrl[i].toString() == "\\") {
                            i += 6
                            url += "&"
                        } else {
                            url += imageUrl[i].toString()
                            i += 1
                        }
                    }
                }
            }
        }

        return url
    }

    fun getUrlArray(sourceCode: String, isvideo: Boolean): Array<ArrayList<Int?>> {
        val start: String
        val end: String
        if (isvideo) {
            start = "video_url"
            end = "video_view_count"
        } else {
            start = "display_url"
            end = "display_resources"
        }
        var tempLast: Int? = 0
        var tempFirst: Int? = 0
        var flag = true
        val listOfVideoUrl = ArrayList<Int?>()
        val listOfVideoViewCount = ArrayList<Int?>()
        var matchViedoUrlLast: Int?
        var matchViedoViewCountLast: Int?
        while (flag) {
            if (tempLast != null && tempFirst != null) {
                matchViedoUrlLast = start.toRegex()
                    .find(sourceCode.substring(tempLast until sourceCode.length))?.range?.last
                matchViedoViewCountLast = end.toRegex()
                    .find(sourceCode.substring(tempFirst + 1 until sourceCode.length))?.range?.first
                if (matchViedoUrlLast != null && matchViedoViewCountLast != null) {
                    tempLast += matchViedoUrlLast
                    tempFirst += matchViedoViewCountLast - 1
                    listOfVideoUrl.add(tempLast)
                    listOfVideoViewCount.add(tempFirst)
                } else {
                    tempLast = matchViedoUrlLast
                }
            } else {
                flag = false
            }
        }
        return arrayOf(listOfVideoUrl, listOfVideoViewCount)
    }

}