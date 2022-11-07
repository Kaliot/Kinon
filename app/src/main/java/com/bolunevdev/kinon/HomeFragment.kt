package com.bolunevdev.kinon

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import androidx.transition.Fade.IN
import androidx.transition.Fade.OUT
import com.bolunevdev.kinon.databinding.FragmentHomeBinding
import com.bolunevdev.kinon.databinding.MergeHomeScreenContentBinding
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var bindingMerge: MergeHomeScreenContentBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private var isFirstRun = true
    private val filmsDataBase = MainActivity.filmsDataBase

    init {
        exitTransition = Fade(OUT).apply { duration = MainActivity.TRANSITION_DURATION }
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

        selectTransitionManager(savedInstanceState)

        bindingMerge = MergeHomeScreenContentBinding.bind(binding.root)

        initRV(filmsDataBase)

        initRVTreeObserver()

        initSearchView()
    }

    private fun initRVTreeObserver() {
        recyclerView.viewTreeObserver
            .addOnDrawListener {
                startPostponedEnterTransition()
            }
    }

    private fun initSearchView() {
        bindingMerge.searchView.setOnClickListener {
            bindingMerge.searchView.isIconified = false
        }

        //Подключаем слушателя изменений введенного текста в поиска
        bindingMerge.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String?) = false

            //Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String): Boolean {
                //Если ввод пуст то вставляем в адаптер всю БД
                if (newText.isEmpty()) {
                    updateData(filmsDataBase)
                    return true
                }
                //Фильтруем список на поискк подходящих сочетаний
                val result = filmsDataBase.filter {
                    //Чтобы все работало правильно, нужно и запрос, и имя фильма приводить к нижнему регистру
                    it.title.lowercase(Locale.getDefault())
                        .contains(newText.lowercase(Locale.getDefault()))
                }
                //Добавляем в адаптер
                updateData(result as MutableList<Film>)
                return true
            }
        })
    }

    private fun selectTransitionManager(savedInstanceState: Bundle?) {
        val scene = Scene.getSceneForLayout(
            binding.root,
            R.layout.merge_home_screen_content,
            requireContext()
        )
        //Создаем анимацию выезда поля поиска сверху
        val searchSlide = Slide(Gravity.TOP).addTarget(R.id.search_view)
        val searchFade = Fade(IN).addTarget(R.id.search_view)
        //Создаем анимацию выезда RV снизу
        val recyclerSlide = Slide(Gravity.BOTTOM).addTarget(R.id.main_recycler)
        val recyclerFade = Fade(IN).addTarget(R.id.main_recycler)
        //Создаем экземпляр TransitionSet, который объединит все наши анимации
        val firstTransition = TransitionSet().apply {
            //Устанавливаем время, за которое будет проходить анимация
            duration = MainActivity.TRANSITION_DURATION
            //Добавляем сами анимации
            addTransition(recyclerFade)
            addTransition(searchFade)
            addTransition(recyclerSlide)
            addTransition(searchSlide)
        }

        val transition = Fade(IN).apply { duration = MainActivity.TRANSITION_DURATION }

        if (savedInstanceState == null && isFirstRun) {
            isFirstRun = false
            TransitionManager.go(scene, firstTransition)
        } else TransitionManager.go(scene, transition)

    }

    private fun initRV(filmsDataBase: MutableList<Film>) {
        //находим наш RV
        recyclerView = bindingMerge.mainRecycler

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