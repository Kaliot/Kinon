package com.bolunevdev.kinon.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.databinding.FragmentHomeBinding
import com.bolunevdev.kinon.domain.Film
import com.bolunevdev.kinon.utils.AnimationHelper
import com.bolunevdev.kinon.utils.FavoriteFilms
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.view.rv_adapters.FilmListRecyclerAdapter
import com.bolunevdev.kinon.view.rv_adapters.TopSpacingItemDecoration
import com.bolunevdev.kinon.viewmodel.HomeFragmentViewModel
import java.util.*


class HomeFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private var isShare: Boolean = false
    private var filmsDataBase = mutableListOf<Film>()
        //Используем backing field
        set(value) {
            //Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            filmsAdapter.updateData(field)
            createFavoriteFilmsDataBase()
        }

    init {
        reenterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        postponeEnterTransition()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = null

        startCircularRevealAnimation()


        initRV()

        viewModel.filmsListLiveData.observe(viewLifecycleOwner) {
            filmsDataBase = it.toMutableList()
            filmsAdapter.updateData(filmsDataBase)
        }

        initRVTreeObserver()

        initSearchView()

        addRVScrollListener()
    }

    private fun addRVScrollListener() {
        val scrollListener = object: RecyclerView.OnScrollListener() {
            @Override
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as RecyclerView.LayoutManager
                //смотрим сколько элементов на экране
                val visibleItemCount: Int = layoutManager.childCount
                //сколько всего элементов
                val totalItemCount: Int = layoutManager.itemCount

                //какая позиция первого элемента
                val firstVisibleItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                //проверяем, грузим мы что-то или нет
                if (!isLoading) {
                    if (visibleItemCount + firstVisibleItems >= totalItemCount - VISIBLE_THRESHOLD) {
                        //ставим флаг, что мы попросили еще элементы
                        isLoading = true
                        //Вызывает загрузку данных в RecyclerView
                        viewModel.increasePageNumber()
                        viewModel.getFilmsFromApi()
                    }
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun initRVTreeObserver() {
        recyclerView.viewTreeObserver
            .addOnDrawListener {
                startPostponedEnterTransition()
            }
    }

    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        //Подключаем слушателя изменений введенного текста в поиска
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String?) = false

            //Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String): Boolean {
                //Если ввод пуст то вставляем в адаптер всю БД
                if (newText.isEmpty()) {
                    isLoading = false
                    filmsAdapter.updateData(filmsDataBase)
                    return true
                }
                //Фильтруем список на поискк подходящих сочетаний
                val result = filmsDataBase.filter {
                    //Чтобы все работало правильно, нужно и запрос, и имя фильма приводить к нижнему регистру
                    it.title.lowercase(Locale.getDefault())
                        .contains(newText.lowercase(Locale.getDefault()))
                }
                //Добавляем в адаптер
                isLoading = true
                filmsAdapter.updateData(result)
                return true
            }
        })
    }

    private fun initRV() {
        //находим наш RV
        recyclerView = binding.mainRecycler

        recyclerView.apply {
            //Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс,
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film, poster: ImageView) {

                        (requireActivity() as MainActivity).launchDetailsFragment(
                            film,
                            R.id.action_homeFragment_to_detailsFragment,
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
            val decorator = TopSpacingItemDecoration(DECORATOR_PADDING_IN_DP)
            addItemDecoration(decorator)
        }
    }

    private fun startCircularRevealAnimation() {
        val menuPosition = 1
        if (!isShare) {
            AnimationHelper.performFragmentCircularRevealAnimation(
                binding.root,
                requireActivity(),
                menuPosition
            )
            return
        }
        binding.root.visibility = View.VISIBLE
        isShare = false
    }

    private fun createFavoriteFilmsDataBase() {
        favoriteFilms = FavoriteFilms(requireContext(), filmsDataBase)
    }

    companion object {
        lateinit var favoriteFilms: FavoriteFilms
        private const val VISIBLE_THRESHOLD  = 5
        private const val DECORATOR_PADDING_IN_DP = 8
        var isLoading: Boolean = false
    }
}