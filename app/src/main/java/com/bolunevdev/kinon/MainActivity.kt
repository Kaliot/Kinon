package com.bolunevdev.kinon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bolunevdev.kinon.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.poster.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt12873562/"))
            startActivity(browserIntent)
        }

        binding.poster2.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt12593682/"))
            startActivity(browserIntent)
        }

        binding.poster3.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt13223398/"))
            startActivity(browserIntent)
        }

        binding.poster4.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt1745960/"))
            startActivity(browserIntent)
        }

        binding.poster5.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/tt14614892/"))
            startActivity(browserIntent)
        }

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Меню", Toast.LENGTH_SHORT).show()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.search -> {
                    Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.new_titles -> {
                    Toast.makeText(this, "Новинки", Toast.LENGTH_SHORT).show()
                }
                R.id.favorites -> {
                    Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
                }
                R.id.recommended -> {
                    Toast.makeText(this, "Рекомендованное", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


