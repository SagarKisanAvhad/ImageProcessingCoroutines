package com.devtides.imageprocessingcoroutines

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val IMAGE_URL =
        "https://raw.githubusercontent.com/DevTides/JetpackDogsApp/master/app/src/main/res/drawable/dog.png"
    private var originalBitmap: Bitmap? = null
    private var toggleFilter = true


    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coroutineScope.launch {
            val deferredBitmap = coroutineScope.async(Dispatchers.IO) {
                getOriginalBitmap()
            }
            originalBitmap = deferredBitmap.await()
            loadImage(originalBitmap!!)
        }

        imageView.setOnClickListener {
            coroutineScope.launch {
                val bitmap = when {
                    toggleFilter -> {
                        val deferredBitmap =
                            coroutineScope.async(Dispatchers.Default) { Filter.apply(originalBitmap!!) }
                        deferredBitmap.await()
                    }
                    else -> {
                        requireNotNull(originalBitmap,
                            { "Original Image is not loaded from network" })
                    }
                }
                toggleFilter = !toggleFilter
                loadImage(bitmap)

            }
        }

    }


    private suspend fun getOriginalBitmap(): Bitmap {
        return URL(IMAGE_URL).openStream().use {
            BitmapFactory.decodeStream(it)
        }
    }

    private fun loadImage(bmp: Bitmap) {
        progressBar.visibility = View.GONE
        imageView.setImageBitmap(bmp)
        imageView.visibility = View.VISIBLE
    }
}
