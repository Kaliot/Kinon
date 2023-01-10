package com.bolunevdev.kinon.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.databinding.FragmentFavoritesBinding
import com.bolunevdev.kinon.domain.Film
import com.bolunevdev.kinon.utils.AnimationHelper
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.view.rv_adapters.FilmListRecyclerAdapter
import com.bolunevdev.kinon.view.rv_adapters.TopSpacingItemDecoration


class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private var isShare: Boolean = false

    init {
        reenterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = null

        //Получаем список при транзакции фрагмента
        val favoritesList = HomeFragment.favoriteFilms.getFilms()

        startCircularRevealAnimation()

        initRV(favoritesList)

        initRVTreeObserver()
    }

    private fun startCircularRevealAnimation() {
        if (!isShare) {
            val menuPosition = 3
            AnimationHelper
                .performFragmentCircularRevealAnimation(binding.root,
                    requireActivity(),
                    menuPosition)
            return
        }
        binding.root.visibility = View.VISIBLE
        isShare = false
    }

    private fun initRVTreeObserver() {
        binding.favoritesRecycler.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
    }

    private fun initRV(filmsDataBase: MutableList<Film>) {
        //находим наш RV
        recyclerView = binding.favoritesRecycler
        recyclerView.apply {
            //Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс,
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film, poster: ImageView) {
                        (requireActivity() as MainActivity).launchDetailsFragment(
                            film,
                            R.id.action_favorites_to_detailsFragment,
                            poster
                        )
                        isShare = true
                    }
                }, object : FilmListRecyclerAdapter.OnItemLongClickListener {
                    override fun longClick(film: Film) {
                        (requireActivity() as MainActivity).copyFilmTitle(film)
                    }
                })

            //Присваиваем адаптер
            recyclerView.adapter = filmsAdapter

            //Присвоим layoutManager
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager

            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
        filmsAdapter.updateData(filmsDataBase)
    }
}