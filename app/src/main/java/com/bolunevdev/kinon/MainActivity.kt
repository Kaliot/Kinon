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
import com.bolunevdev.kinon.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.poster.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster_url)))
            startActivity(browserIntent)
        }

        binding.posterCardGravity.setOnClickListener {
            binding.root.overlay.add(binding.posterCardGravity)
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
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    animator.removeListener(this)
                    binding.root.overlay.clear()

                    val appearance =
                        ObjectAnimator.ofFloat(binding.posterCardGravity2, View.ALPHA, 0f, 1f)
                    rotationAnimation3.duration = 5000

                    appearance.start()
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
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
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.poster2_url)))
            startActivity(browserIntent)
        }

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Меню", Toast.LENGTH_SHORT).show()
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
}