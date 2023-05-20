package com.bolunevdev.kinon.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.data.PreferenceProvider
import com.bolunevdev.kinon.databinding.FragmentHomeBinding
import com.bolunevdev.kinon.utils.AnimationHelper
import com.bolunevdev.kinon.utils.AutoDisposable
import com.bolunevdev.kinon.utils.addTo
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.view.rv_adapters.FilmListRecyclerAdapter
import com.bolunevdev.kinon.view.rv_adapters.TopSpacingItemDecoration
import com.bolunevdev.kinon.viewmodel.HomeFragmentViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {

    private val viewModel: HomeFragmentViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter: FilmListRecyclerAdapter by lazy {
        FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film, poster: ImageView) {
                (requireActivity() as MainActivity).launchDetailsFragment(
                    film,
                    R.id.actionHomeFragmentToDetailsFragment,
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

    private val scrollListener: RecyclerView.OnScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            @Override
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as RecyclerView.LayoutManager
                //смотрим сколько элементов на экране
                val visibleItemCount: Int = layoutManager.childCount
                //сколько всего элементов
                totalItemCount = layoutManager.itemCount

                //какая позиция первого элемента
                val firstVisibleItems =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                //проверяем, грузим мы что-то или нет
                if (!isLoading) {
                    if (visibleItemCount + firstVisibleItems >= totalItemCount - VISIBLE_THRESHOLD) {
                        //ставим флаг, что мы попросили еще элементы
                        isLoading = true
                        //Вызывает загрузку данных в RecyclerView
                        if (isSearchRequestEmpty) {
                            viewModel.increasePageNumber()
                            viewModel.getFilmsFromApi()
                        } else {
                            viewModel.increaseSearchedPageNumber()
                            loadSearchFilms()
                        }
                    }
                }
            }
        }
    }
    private var isShare: Boolean = false
    private var totalItemCount = DEFAULT_TOTAL_ITEM_COUNT
    private val autoDisposable = AutoDisposable()
    private var isSearchRequestEmpty: Boolean = true
    private var searchRequest = ""
    private val searchFilmsList = mutableListOf<Film>()
    private val sharedChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PreferenceProvider.KEY_DEFAULT_CATEGORY -> {
                    changeCategory()
                    //Перемещаемся на первую позицию
                    recyclerView?.scrollToPosition(0)
                }
            }
        }
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = null

        startCircularRevealAnimation()

        initRV()

        initPullToRefresh()

        bindAutoDisposable()

        loadFilmsDataBase()

        initRVTreeObserver()

        initSearchView()

        addRVScrollListener()

        addSharedPreferencesListener()

        setProgressBar()

        setServerErrorToast()
    }

    private fun bindAutoDisposable() {
        autoDisposable.bindTo(lifecycle)
    }

    private fun setProgressBar() {
        viewModel.showProgressBar
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete()
            .subscribe {
                binding.progressBar.isVisible = it
            }.addTo(autoDisposable)
    }

    private fun setServerErrorToast() {
        viewModel.showServerError.observe(viewLifecycleOwner) { shouldShowError ->
            if (shouldShowError) {
                Toast.makeText(context, R.string.toast_server_error_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addSharedPreferencesListener() {
        viewModel.addPreferenceListener(sharedChangeListener)
    }

    private fun addRVScrollListener() {
        recyclerView?.addOnScrollListener(scrollListener)
    }

    private fun initRVTreeObserver() {
        binding.mainRecycler.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
    }

    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        Observable.create { emitter ->
            //Подключаем слушателя изменений введенного текста в поиска
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
                override fun onQueryTextSubmit(query: String?): Boolean = true

                //Этот метод отрабатывает на каждое изменения текста
                override fun onQueryTextChange(newText: String): Boolean {
                    emitter.onNext(newText.trim())
                    return true
                }
            })
        }
            .distinctUntilChanged() //Ввод пробела не запустит повторный поиск
            .subscribeOn(Schedulers.io())
            .debounce(DEBOUNCE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                isSearchRequestEmpty = it.isBlank()
                if (it.isBlank()) {
                    loadFilmsDataBase()
                    isLoading = false
                }
                it.isNotBlank()
            }
            .subscribe({
                searchRequest = it
                searchFilmsList.clear()
                viewModel.resetSearchedPageNumber()
                recyclerView?.smoothScrollToPosition(0)
                loadSearchFilms()
            }, {
                viewModel.interactor.showServerError(true)
            }).addTo(autoDisposable)
    }

    private fun loadSearchFilms() {
        viewModel.getSearchedFilmsFromApi(searchRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                viewModel.interactor.showProgressBar(true)
            }
            .doFinally {
                viewModel.interactor.showProgressBar(false)
                viewModel.increaseSearchedPageNumber()
            }
            .subscribe({ searchedFilmsList ->
                searchFilmsList.addAll(searchedFilmsList)
                //Добавляем в адаптер
                filmsAdapter.submitList(searchFilmsList.toMutableList())
                //Проверяем количество полученных фильмов
                if (searchedFilmsList.size == DEFAULT_TOTAL_ITEM_COUNT) isLoading = false
            }, {
                viewModel.interactor.showServerError(true)
            }).addTo(autoDisposable)
    }

    private fun initRV() {
        //находим наш RV
        recyclerView = binding.mainRecycler

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

    private fun loadFilmsDataBase() {
        viewModel.filmsListLiveData.observe(viewLifecycleOwner) { films ->
            filmsDataBase = films.toMutableList()
            //Загружаем, если нет поискового запроса
            if (isSearchRequestEmpty) filmsAdapter.submitList(filmsDataBase)
            isLoading = false
        }
    }

    private fun changeCategory() {
        // Очищаем список фильмов
        viewModel.clearFilmsList()
        //Делаем новый запрос фильмов на сервер
        viewModel.getFilmsFromApi()
    }

    private fun initPullToRefresh() {
        //Вешаем слушатель, чтобы вызвался pull to refresh
        binding.pullToRefresh.setOnRefreshListener {
            changeCategory()
            //Убираем крутящееся колечко
            binding.pullToRefresh.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unregisterPreferencesListener(sharedChangeListener)
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView?.removeOnScrollListener(scrollListener)
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
        private const val DECORATOR_PADDING_IN_DP = 8
        private var isLoading: Boolean = false
        private const val DEFAULT_TOTAL_ITEM_COUNT = 20
        private const val DEBOUNCE_TIMEOUT_MS = 1000L
    }
}