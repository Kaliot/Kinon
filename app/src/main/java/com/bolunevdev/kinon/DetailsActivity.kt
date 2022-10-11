package com.bolunevdev.kinon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bolunevdev.kinon.databinding.ActivityDetailsBinding
import com.google.android.material.snackbar.Snackbar


class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setDetails()

        val newSnackbar = Snackbar.make(binding.root, R.string.btn_new_titles, Snackbar.LENGTH_SHORT).setAnchorView(binding.bottomNavigation)
        val favoritesSnackbar = Snackbar.make(binding.root, R.string.btn_favorites, Snackbar.LENGTH_SHORT).setAnchorView(binding.bottomNavigation)
        val recommendedSnackbar = Snackbar.make(binding.root, R.string.btn_recommended, Snackbar.LENGTH_SHORT).setAnchorView(binding.bottomNavigation)
        val shareSnackbar = Snackbar.make(binding.root, R.string.btn_share, Snackbar.LENGTH_SHORT).setAnchorView(binding.bottomNavigation)

        binding.bottomNavigation.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.new_titles -> {
                    newSnackbar.show()
                }
                R.id.favorites -> {
                    favoritesSnackbar.show()
                }
                R.id.recommended -> {
                    recommendedSnackbar.show()
                }
            }
        }

        binding.detailsFab.setOnClickListener {
           shareSnackbar.show()
        }
    }

    private fun setDetails(){
        //Получаем наш фильм из переданного бандла
        val film = intent.extras?.get("film") as Film
        //Устанавливаем заголовок
        binding.detailsToolbar.title = film.title
        //Устанавливаем картинку
        binding.detailsPoster.setImageResource(film.poster)
        //Устанавливаем описание
        binding.detailsDescription.text = film.description
    }
}
