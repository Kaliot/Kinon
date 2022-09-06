package com.bolunevdev.kinon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_search.setOnClickListener {
            Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show()
        }

        button_new.setOnClickListener {
            Toast.makeText(this, "Новинки", Toast.LENGTH_SHORT).show()
        }

        button_favorites.setOnClickListener {
            Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
        }

        button_recommended.setOnClickListener {
            Toast.makeText(this, "Рекомендованное", Toast.LENGTH_SHORT).show()
        }

        button_settings.setOnClickListener {
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
        }

        poster.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt12873562/"))
            startActivity(browserIntent)
        }

        poster2.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt12593682/"))
            startActivity(browserIntent)
        }

        poster3.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt13223398/"))
            startActivity(browserIntent)
        }

        poster4.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt1745960/"))
            startActivity(browserIntent)
        }

        poster5.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt14614892/"))
            startActivity(browserIntent)
        }
    }

}
