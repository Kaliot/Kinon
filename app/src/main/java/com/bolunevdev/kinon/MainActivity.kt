package com.bolunevdev.kinon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bolunevdev.kinon.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (supportFragmentManager.backStackEntryCount == 1)
                ExitMenuFragment().show(supportFragmentManager, "dialog1")
            else supportFragmentManager.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_placeholder, HomeFragment())
            .addToBackStack(null)
            .commit()

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, getString(R.string.btn_menu), Toast.LENGTH_SHORT).show()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, R.string.btn_settings, Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.search -> {
                    Toast.makeText(this, R.string.btn_search, Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.new_titles -> {
                    Toast.makeText(this, R.string.btn_new_titles, Toast.LENGTH_SHORT).show()
                }
                R.id.favorites -> {
                    Toast.makeText(this, R.string.btn_favorites, Toast.LENGTH_SHORT).show()
                }
                R.id.recommended -> {
                    Toast.makeText(this, R.string.btn_recommended, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun launchDetailsFragment(film: Film) {
        //Создаем "посылку"
        val bundle = Bundle()
        //Кладем наш фильм в "посылку"
        bundle.putParcelable("film", film)
        //Кладем фрагмент с деталями в перменную
        val fragment = DetailsFragment()
        //Прикрепляем нашу "посылку" к фрагменту
        fragment.arguments = bundle
        //Запускаем фрагмент
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }
}