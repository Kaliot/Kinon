package com.bolunevdev.kinon

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bolunevdev.kinon.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var clipboardManager: ClipboardManager

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination == navController.findDestination(R.id.home))
                    navController.navigate(R.id.action_homeFragment_to_exitMenuFragment)
                else {
                    navController.popBackStack()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createFilmsDataBase()
        createFavoriteFilmsDataBase()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_placeholder) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, getString(R.string.btn_menu), Toast.LENGTH_SHORT).show()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, R.string.btn_settings, Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun createFavoriteFilmsDataBase() {
        favoriteFilms = FavoriteFilms(this, filmsDataBase)
    }

    private fun createFilmsDataBase() {
        filmsDataBase = FilmsDataBase(this).getFilms()
    }

    fun launchDetailsFragment(film: Film, direction: Int, poster: ImageView) {
        //Создаем "посылку"
        val bundle = Bundle()
        val extras: FragmentNavigator.Extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(
                poster,
                poster.transitionName
            )
            .build()

        //Кладем наш фильм в "посылку"
        bundle.putParcelable(KEY_FILM_DETAILS_FRAGMENT, film)

        //Кладем фрагмент с деталями в перменную
        val fragment = DetailsFragment()

        //Прикрепляем нашу "посылку" к фрагменту
        fragment.arguments = bundle

        //Запускаем фрагмент
        navController.navigate(direction, fragment.arguments, null, extras)
    }

    fun copyFilmTitle(film: Film) {
        val clipData = ClipData.newPlainText(FILM_TITLE, film.title)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, getString(R.string.film_title_copied), Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val KEY_FILM_DETAILS_FRAGMENT = "film"
        const val FAVORITE_FILMS_PREFERENCES = "FAVORITE_FILMS_PREFERENCES"
        const val FILM_TITLE = "FILM_TITLE"
        const val TRANSITION_DURATION = 400L
        const val TRANSITION_DURATION_FAST = 150L
        lateinit var filmsDataBase: MutableList<Film>
        lateinit var favoriteFilms: FavoriteFilms
    }
}