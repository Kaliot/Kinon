package com.bolunevdev.kinon

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bolunevdev.kinon.databinding.FragmentDetailsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var film: Film
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var favoriteFilms: FavoriteFilms


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteFilms = MainActivity.favoriteFilms
        initBottomNavigationView()
        setDetails()

        binding.detailsFabFavorites.setOnClickListener {
            if (!favoriteFilms.contains(film)) {
                binding.detailsFabFavorites.setImageResource(R.drawable.ic_baseline_favorite)
                film.isInFavorites = true
                favoriteFilms.addFilm(film)
            } else {
                binding.detailsFabFavorites.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                film.isInFavorites = false
                favoriteFilms.deleteFilm(film)
            }
        }

        binding.detailsFabShare.setOnClickListener {
            //Создаем интент
            val intent = Intent()
            //Указываем action с которым он запускается
            intent.action = Intent.ACTION_SEND
            //Кладем данные о нашем фильме
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "${getString(R.string.CheckOutThisFilm)} ${film.title} \n\n ${film.description}"
            )
            //Указываем MIME тип, чтобы система знала, какое приложения предложить
            intent.type = MIME_TYPE
            //Запускаем наше активити
            startActivity(Intent.createChooser(intent, getString(R.string.ShareTo)))
        }
    }

    private fun initBottomNavigationView() {
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(findNavController())
    }

    private fun setDetails() {
        film = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Получаем наш фильм из переданного бандла
            arguments?.getParcelable(
                MainActivity.KEY_FILM_DETAILS_FRAGMENT,
                Film::class.java
            ) as Film
        } else {
            //Получаем наш фильм из переданного бандла
            arguments?.getParcelable<Film>(MainActivity.KEY_FILM_DETAILS_FRAGMENT) as Film
        }
        //Устанавливаем заголовок
        binding.detailsToolbar.title = film.title
        //Устанавливаем картинку
        binding.detailsPoster.setImageResource(film.poster)
        //Устанавливаем описание
        binding.detailsDescription.text = film.description

        binding.detailsFabFavorites.setImageResource(
            if (favoriteFilms.contains(film)) R.drawable.ic_baseline_favorite
            else R.drawable.ic_baseline_favorite_border_24
        )
    }


    companion object {
        private const val MIME_TYPE = "text/plain"
    }
}
