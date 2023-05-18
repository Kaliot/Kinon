package com.bolunevdev.kinon.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.databinding.FragmentFavoritesBinding
import com.bolunevdev.kinon.utils.AnimationHelper
import com.bolunevdev.kinon.utils.AutoDisposable
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.view.rv_adapters.FilmListRecyclerAdapter
import com.bolunevdev.kinon.view.rv_adapters.TopSpacingItemDecoration
import com.bolunevdev.kinon.viewmodel.FavoritesFragmentViewModel


class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesFragmentViewModel by viewModels()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter: FilmListRecyclerAdapter by lazy {
        FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film, poster: ImageView) {
                (requireActivity() as MainActivity).launchDetailsFragment(
                    film,
                    R.id.actionFavoritesToDetailsFragment,
                    poster
                )
                isShare = true
            }
        }, object : FilmListRecyclerAdapter.OnItemLongClickListener {
            override fun longClick(film: Film) {
                (requireActivity() as MainActivity).copyFilmTitle(film)
            }
        })
    }

    private var recyclerView: RecyclerView? = null

    private var isShare: Boolean = false
    private val autoDisposable = AutoDisposable()
    private var filmsDataBase = mutableListOf<Film>()
        //Используем backing field
        set(value) {
            //Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            filmsAdapter.submitList(field)
        }

    init {
        reenterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = null

        startCircularRevealAnimation()

        initRV()

        bindAutoDisposable()

        loadFilmsDataBase()

        initRVTreeObserver()
    }

    private fun bindAutoDisposable() {
        autoDisposable.bindTo(lifecycle)
    }

    private fun startCircularRevealAnimation() {
        if (!isShare) {
            val menuPosition = 3
            AnimationHelper
                .performFragmentCircularRevealAnimation(
                    binding.root,
                    requireActivity(),
                    menuPosition
                )
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

    private fun initRV() {
        //находим наш RV
        recyclerView = binding.favoritesRecycler
        recyclerView?.apply {
            //Присваиваем адаптер
            adapter = filmsAdapter

            //Присвоим layoutManager
            layoutManager = LinearLayoutManager(requireContext())

            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(DECORATOR_PADDING_IN_DP)
            addItemDecoration(decorator)
        }
    }

    private fun loadFilmsDataBase() {
        viewModel.filmsListLiveData.observe(viewLifecycleOwner) { films ->
            filmsDataBase = films.toMutableList()
            filmsAdapter.submitList(filmsDataBase)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val DECORATOR_PADDING_IN_DP = 8
    }
}