package com.msdev.ytidownloads.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.msdev.ytidownloads.*
import java.lang.Exception

class PrivateContent : Fragment() {

    lateinit var snakeBar: SnakeBar
    lateinit var public: Public
    lateinit var txtUrlSourceCode: TextView
    lateinit var videoUrl: String
    lateinit var btnSourcecodeUrlCopy: Button
    lateinit var btnOpenChrome: Button
    lateinit var etSourceCode: EditText
    lateinit var btnSourceCodePaste: Button
    lateinit var btnDownload: Button
    lateinit var clipboardManager: ClipboardManager

    var sourceCode = ""
    var isvideo = false


    @SuppressLint("SetTextI18n", "UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_private_content, container, false)

        // Initialising Context
        txtUrlSourceCode = view.findViewById(R.id.txtUrlSourceCode)
        btnSourcecodeUrlCopy = view.findViewById(R.id.btnSourcecodeUrlCopy)
        btnOpenChrome = view.findViewById(R.id.btnOpenChrome)
        etSourceCode = view.findViewById(R.id.etSourceCode)
        btnSourceCodePaste = view.findViewById(R.id.btnSourceCodePaste)
        btnDownload = view.findViewById(R.id.btnDownload)

        // Initializing Class
        snakeBar = context?.let { SnakeBar(view, it) }!!
        public = Public(view, context!!)

        // Setting Text to Video or Image Url from ClipBord
        clipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        videoUrl = arguments?.getString("url").toString()
        txtUrlSourceCode.text = videoUrl.substring(0..30) + "..."

        // Copy SourceCode Url to clip board button
        btnSourcecodeUrlCopy.setOnClickListener {
            if (copyTextToClipboard()) snakeBar.simple("Link Copied ")
        }

        // OpenChrome button
        btnOpenChrome.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            startActivity(browserIntent)
        }

        // Paste Button of Source code Copied From the Chrome
        btnSourceCodePaste.setOnClickListener {
            pasteTextFromClipboard()
        }

        // Download Button
        btnDownload.setOnClickListener {

            if (ConnectionManager().checkConnectivity(context!!)) {
                if(etSourceCode.text.isNotEmpty()){
                    sourceCode = etSourceCode.text.toString()
                    isvideo = public.isVideo(sourceCode)
                    snakeBar.simple("Downloading...")
                    try {
                        public.downloadVideo(isvideo, public.getVideoLink(isvideo, sourceCode))
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        snakeBar.success("Downloaded Successfully")
                    } catch (e: Exception) {
                        snakeBar.fail("Download Unsuccessful")
                    }
                }else {
                    snakeBar.fail("Invalid Source Code")
                }
            } else {
                snakeBar.fail("Please Check Internet Connection")
            }
        }

        return view
    }

    private fun copyTextToClipboard(): Boolean {
        val textToCopy = "view-source:$videoUrl"
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        return true
    }

    private fun pasteTextFromClipboard() {
        val editable: Editable =
            SpannableStringBuilder(clipboardManager.primaryClip?.getItemAt(0)?.text.toString())
        etSourceCode.text = editable
    }

}