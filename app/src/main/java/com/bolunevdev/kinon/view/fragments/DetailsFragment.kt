package com.bolunevdev.kinon.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.data.ApiConstants
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.databinding.FragmentDetailsBinding
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.viewmodel.DetailsFragmentViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class DetailsFragment : Fragment() {
    private val viewModel: DetailsFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var film: Film
    private var favoriteFilms = mutableListOf<Film>()

    init {
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity)
            .findViewById<FrameLayout>(R.id.background_share_transition)
            .visibility = View.VISIBLE

        loadFilmsDataBase()

        initSharedElementEnterTransition()

        setDetails()

        initDetailsFabShare()
    }

    private fun loadFilmsDataBase() {
        viewModel.filmsListLiveData.observe(viewLifecycleOwner) {
            favoriteFilms = it.toMutableList()
            initDetailsFabFavorites()
            setFabFavoritesIcon()
        }
    }

    private fun initDetailsFabFavorites() {
        binding.detailsFabFavorites.setOnClickListener {
            if (!favoriteFilms.contains(film)) {
                viewModel.addToFavoritesFilms(film)
            } else {
                viewModel.deleteFromFavoritesFilms(film.id)
            }
        }
    }

    private fun initSharedElementEnterTransition() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.image_shared_element_transition)
    }

    private fun initDetailsFabShare() {
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
        binding.detailsPoster.transitionName = film.id.toString()
        binding.detailsToolbar.title = film.title

        //Устанавливаем картинку
        Glide.with(this)
            .load(ApiConstants.IMAGES_URL + IMAGE_SIZE + film.poster)
            .error(R.drawable.no_poster_holder)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(binding.detailsPoster)

        //Устанавливаем описание
        binding.detailsDescription.text = film.description
    }

    private fun setFabFavoritesIcon() {
        binding.detailsFabFavorites.setImageResource(
            if (favoriteFilms.contains(film)) R.drawable.ic_baseline_favorite
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    companion object {
        private const val MIME_TYPE = "text/plain"
        private const val IMAGE_SIZE = "w780"
    }
}
