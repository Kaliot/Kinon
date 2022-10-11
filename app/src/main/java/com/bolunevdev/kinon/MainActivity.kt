package com.bolunevdev.kinon

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bolunevdev.kinon.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var filmsDataBase: MutableList<Film>
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        filmsDataBase = arrayListOf(
            Film(
                getString(R.string.memento_title),
                R.drawable.memento,
                getString(R.string.memento_description)
            ),
            Film(
                getString(R.string.inception_title),
                R.drawable.inception,
                getString(R.string.inception_description)
            ),
            Film(
                getString(R.string.the_dark_knight_title),
                R.drawable.the_dark_knight,
                getString(R.string.the_dark_knight_description)
            ),
            Film(
                getString(R.string.forrest_gump_title),
                R.drawable.forrest_gump,
                getString(R.string.forrest_gump_description)
            ),
            Film(
                getString(R.string.pulp_fiction_title),
                R.drawable.pulp_fiction,
                getString(R.string.pulp_fiction_description)
            ),
            Film(
                getString(R.string.the_matrix_title),
                R.drawable.the_matrix,
                getString(R.string.the_matrix_description)
            ),
            Film(
                getString(R.string.interstellar_title),
                R.drawable.interstellar,
                getString(R.string.interstellar_description)
            ),
            Film(
                getString(R.string.the_wolf_of_wall_street_title),
                R.drawable.the_wolf_of_wall_street,
                getString(R.string.the_wolf_of_wall_street_description)
            ),
            Film(
                getString(R.string.american_beauty_title),
                R.drawable.american_beauty,
                getString(R.string.american_beauty_description)
            ),
            Film(
                getString(R.string.fight_club_title),
                R.drawable.fight_club,
                getString(R.string.fight_club_description)
            )
        )
        initRV()


        binding.poster.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster_url)))
            startActivity(browserIntent)
        }

        binding.posterCardGravity.setOnClickListener {
            view.overlay.add(binding.posterCardGravity)
            binding.posterCardGravity.pivotX = binding.posterCardGravity.measuredWidth * .85f
            binding.posterCardGravity.pivotY = binding.posterCardGravity.measuredHeight * .15f
            binding.posterCardGravity2.visibility = VISIBLE

            val rotationAnimation =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.ROTATION, 0f, -20f)
            rotationAnimation.duration = 500
            rotationAnimation.interpolator = AccelerateDecelerateInterpolator()

            val rotationAnimation2 =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.ROTATION, -20f, 30f)
            rotationAnimation2.duration = 1000
            rotationAnimation2.interpolator = AccelerateDecelerateInterpolator()

            val rotationAnimation3 =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.ROTATION, 30f, -40f)
            rotationAnimation3.duration = 1500
            rotationAnimation3.interpolator = AccelerateDecelerateInterpolator()

            val fallAnimation =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.TRANSLATION_Y, 3860f)
            fallAnimation.interpolator = AccelerateInterpolator()
            fallAnimation.duration = 1000

            val animator = AnimatorSet()
            animator.playSequentially(
                rotationAnimation,
                rotationAnimation2,
                rotationAnimation3,
                fallAnimation
            )

            val animationUpdateListener = object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    animator.removeListener(this)
                    view.overlay.clear()

                    val appearance =
                        ObjectAnimator.ofFloat(binding.posterCardGravity2, View.ALPHA, 0f, 1f)
                    rotationAnimation3.duration = 5000

                    appearance.start()
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationStart(p0: Animator) {
                }
            }

            animator.addListener(animationUpdateListener)
            animator.start()
        }

        binding.posterCardGravity2.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster_gravity_url)))
            startActivity(browserIntent)
        }


        binding.poster3.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster3_url)))
            startActivity(browserIntent)
        }

        binding.poster4.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster4_url)))
            startActivity(browserIntent)
        }

        binding.poster5.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster5_url)))
            startActivity(browserIntent)
        }

        binding.poster6.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster6_url)))
            startActivity(browserIntent)
        }

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

    private fun initRV() {
//        находим наш RV
        val recyclerView = binding.mainRecycler
        recyclerView.apply {
            //Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс,
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        //Создаем бандл и кладем туда объект с данными фильма
                        val bundle = Bundle()
                        //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
                        //передаваемый объект
                        bundle.putParcelable("film", film)
                        //Запускаем наше активити
                        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                        //Прикрепляем бандл к интенту
                        intent.putExtras(bundle)
                        //Запускаем активити через интент
                        startActivity(intent)
                    }
                })
//            Присваиваем адаптер
            recyclerView.adapter = filmsAdapter

            //Присвоим layoutManager
            val layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.layoutManager = layoutManager

            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

//        Кладем нашу БД в RV
        val diff = FilmDiff(filmsAdapter.items, filmsDataBase)
        val diffResult = DiffUtil.calculateDiff(diff)
        filmsAdapter.items = filmsDataBase
        diffResult.dispatchUpdatesTo(filmsAdapter)
    }
}