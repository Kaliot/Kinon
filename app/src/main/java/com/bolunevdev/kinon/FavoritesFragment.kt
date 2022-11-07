package com.bolunevdev.kinon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.bolunevdev.kinon.databinding.FragmentFavoritesBinding


class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    init {
        exitTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
        reenterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Получаем список при транзакции фрагмента
        val favoritesList = MainActivity.favoriteFilms.getFilms()
        initRV(favoritesList)
        initRVTreeObserver()
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
                    }
                }, object : FilmListRecyclerAdapter.OnItemLongClickListener {
                    override fun longClick(film: Film) {
                        (requireActivity() as MainActivity).copyFilmTitle(film)
                    }
                })

            //Присвоим layoutManager
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager

            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
        updateData(filmsDataBase)
    }

    private fun updateData(filmsDataBase: MutableList<Film>) {
        //Присваиваем адаптер
        recyclerView.adapter = filmsAdapter

        //Кладем нашу БД в RV
        val diff = FilmDiff(filmsAdapter.items, filmsDataBase)
        val diffResult = DiffUtil.calculateDiff(diff)
        filmsAdapter.items = filmsDataBase
        diffResult.dispatchUpdatesTo(filmsAdapter)
    }
}