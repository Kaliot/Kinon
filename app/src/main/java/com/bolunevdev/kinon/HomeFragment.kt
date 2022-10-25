package com.bolunevdev.kinon

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bolunevdev.kinon.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsDataBase: MutableList<Film>
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        filmsDataBase = MainActivity.filmsDataBase

        binding.poster.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster_url)))
            startActivity(browserIntent)
        }

        binding.posterCardGravity.setOnClickListener {
            binding.root.overlay.add(binding.posterCardGravity)
            binding.posterCardGravity.pivotX = binding.posterCardGravity.measuredWidth * .85f
            binding.posterCardGravity.pivotY = binding.posterCardGravity.measuredHeight * .15f
            binding.posterCardGravity2.visibility = View.VISIBLE

            val animDuration = 500L

            val rotationAnimation =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.ROTATION, 0f, -20f)
            rotationAnimation.duration = animDuration
            rotationAnimation.interpolator = AccelerateDecelerateInterpolator()

            val rotationAnimation2 =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.ROTATION, -20f, 30f)
            rotationAnimation2.duration = animDuration * 2
            rotationAnimation2.interpolator = AccelerateDecelerateInterpolator()

            val rotationAnimation3 =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.ROTATION, 30f, -40f)
            rotationAnimation3.duration = animDuration * 3
            rotationAnimation3.interpolator = AccelerateDecelerateInterpolator()

            val fallAnimation =
                ObjectAnimator.ofFloat(binding.posterCardGravity, View.TRANSLATION_Y, 3860f)
            fallAnimation.interpolator = AccelerateInterpolator()
            fallAnimation.duration = animDuration * 2

            val animator = AnimatorSet()
            animator.playSequentially(
                rotationAnimation, rotationAnimation2, rotationAnimation3, fallAnimation
            )

            val animationUpdateListener = object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    animator.removeListener(this)
                    binding.root.overlay.clear()

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV()
    }

    private fun initRV() {
        //находим наш RV
        val recyclerView = binding.mainRecycler
        recyclerView.apply {
            //Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс,
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        (requireActivity() as MainActivity).launchDetailsFragment(
                            film, R.id.action_homeFragment_to_detailsFragment
                        )
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

        //Кладем нашу БД в RV
        val diff = FilmDiff(filmsAdapter.items, filmsDataBase)
        val diffResult = DiffUtil.calculateDiff(diff)
        filmsAdapter.items = filmsDataBase
        diffResult.dispatchUpdatesTo(filmsAdapter)
    }
}