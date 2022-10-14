package com.bolunevdev.kinon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bolunevdev.kinon.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.Snackbar


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val shareSnackbar = Snackbar.make(binding.detailsDescription, R.string.btn_share, Snackbar.LENGTH_SHORT)
        binding.detailsFab.setOnClickListener {
            shareSnackbar.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDetails()
    }

    private fun setDetails(){
        //Получаем наш фильм из переданного бандла
        val film = arguments?.get(MainActivity.KEY_FILM_DETAILS_FRAGMENT) as Film
//        Устанавливаем заголовок
        binding.detailsToolbar.title = film.title
        //Устанавливаем картинку
        binding.detailsPoster.setImageResource(film.poster)
        //Устанавливаем описание
        binding.detailsDescription.text = film.description
    }
}
