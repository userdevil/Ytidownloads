package com.msdev.ytidownloads

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.msdev.ytidownloads.fragment.PrivateContent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var snakeBar: SnakeBar
    lateinit var public: Public
    lateinit var etUrlVideo: EditText
    lateinit var btnUrlPaste: Button
    lateinit var btnSearchAndDownload: Button
    lateinit var btnDownloadsFolder: View
    private var sourceCode: String = ""
    private var videoUrl: String = ""
    lateinit var mAdView: AdView
    private var isprivate: Boolean = false
    lateinit var btn1Download: Button
    private var isvideo: Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialising
        etUrlVideo = findViewById(R.id.etUrlVideo)
        btnUrlPaste = findViewById(R.id.btnUrlPaste)
        btnUrlPaste.text = "Paste"
        btn1Download = findViewById(R.id.btn1Download)
        btnSearchAndDownload = findViewById(R.id.btnSearchAndDownload)
        btnDownloadsFolder = findViewById(R.id.btnDownloadsFolder)
        val view: View = this.findViewById(R.id.mainLayout)
        // Initialising Classes
        snakeBar = SnakeBar(view, this)
        public = Public(view, this)


        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.AdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        btn1Download.setOnClickListener {
            val i = Intent(this, YtActivity::class.java).apply {
                putExtra("Data",etUrlVideo.text.toString())
            }
            startActivity(i)
        }

        // Changing the Text of btnUrlPaste And Enabling btnSearchAndDownload
        etUrlVideo.addTextChangedListener {

            changeBtnBackground(etUrlVideo.text.toString())
            if (isInstaLink()) {
                btnSearchAndDownload.visibility = View.VISIBLE
                btnSearchAndDownload.isClickable = true
                btnSearchAndDownload.isEnabled = true
            } else {
                btnSearchAndDownload.visibility = View.INVISIBLE
                btnSearchAndDownload.isClickable = false
                btnSearchAndDownload.isEnabled = false
            }
        }


        etUrlVideo.addTextChangedListener {

            changeBtnBackground(etUrlVideo.text.toString())
            if (isLink()) {
                btn1Download.visibility = View.VISIBLE
                btn1Download.isClickable = true
                btn1Download.isEnabled = true
            } else {
                btn1Download.visibility = View.INVISIBLE
                btn1Download.isClickable = false
                btn1Download.isEnabled = false
            }
            if(isInstaLink()){
                btn1Download.visibility = View.INVISIBLE
                btn1Download.isClickable = false
                btn1Download.isEnabled = false
            }
        }

        // OnClick Listener for btnSearchAndDownload
        btnSearchAndDownload.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                if (btnSearchAndDownload.text == "Search") {
                    videoUrl = etUrlVideo.text.toString()
                    sourceCode = public.getSourceCode(videoUrl)
                    if (sourceCode != false.toString()) {
                        isvideo = public.isVideo(sourceCode)
                        isprivate = isPrivate()
                        if (isprivate) {
                            mainLayout.removeAllViews()

                            // Passing the URL of Video to Fragment using bundle
                            val bundle = Bundle()
                            bundle.putString("url", videoUrl)
                            val i = PrivateContent()
                            i.arguments = bundle

                            // Moving to Fragment
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.mainLayout, i)
                                .commit()
                        } else {
                            btnSearchAndDownload.text = "Download"
                            etUrlVideo.isEnabled = false
                        }
                    } else {
                        snakeBar.fail("Invalid URL")
                    }
                } else {
                    snakeBar.simple("Downloading...")
                    try {
                        public.downloadVideo(isvideo, public.getVideoLink(isvideo, sourceCode))
                        etUrlVideo.text.clear()
                        btnSearchAndDownload.text = "Search"
                    } catch (e: Exception) {
                        snakeBar.fail("Download Unsuccessful")
                        btnSearchAndDownload.text = "Search"
                        etUrlVideo.text.clear()
                    }
                    etUrlVideo.isEnabled = true
                }

            } else {
                snakeBar.fail("Please Check Internet Connection")
            }
        }

        // Paste Link From URL
        btnUrlPaste.setOnClickListener {
            if (btnUrlPaste.text == "Paste") {
                pasteTextFromClipboard()
            } else {
                btnSearchAndDownload.text = "Search"
                etUrlVideo.text.clear()
            }
        }

        // Downloads Folder
        btnDownloadsFolder.setOnClickListener {
            openFolder()
        }

    }

    // Function

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    fun changeBtnBackground(etUrlVideo: String) {
        if (etUrlVideo.isNotEmpty() && etUrlVideo.isNotBlank()) {
            btnUrlPaste.text = "Clear"
            btnUrlPaste.background = getDrawable(R.drawable.custom_btn_clear)
            findViewById<EditText>(R.id.etUrlVideo).background =
                getDrawable(R.drawable.custom_edit_text_grey_stock)
        } else {
            btnUrlPaste.text = "Paste"
            btnUrlPaste.background = getDrawable(R.drawable.custom_btn_paste)
            findViewById<EditText>(R.id.etUrlVideo).background =
                getDrawable(R.drawable.custom_edit_text_blue_stock)
        }
    }

    private fun isInstaLink(): Boolean {
        val m = "instagram".toRegex().find(etUrlVideo.text.toString())
        return m != null
    }

    private fun isLink(): Boolean {
        val m = "youtu.be";"youtube".toRegex().find(etUrlVideo.text.toString())
        return m != ""
    }

    fun reload() {
        finish()
        startActivity(intent)
    }

    private fun isPrivate(): Boolean {
        val m = "is_private".toRegex().find(sourceCode)
//        etUrlVideo.hint = sourceCode[m?.range?.last?.plus(3)!!].toString()
        return sourceCode[m?.range?.last?.plus(3)!!].toString() == "t"
    }

    private fun pasteTextFromClipboard() {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val editable: Editable =
            SpannableStringBuilder(clipboardManager.primaryClip?.getItemAt(0)?.text.toString())
        etUrlVideo.text = editable
    }


    private fun openFolder() {
        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }

}