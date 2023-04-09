package com.bolunevdev.kinon.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.databinding.FragmentSettingsBinding
import com.bolunevdev.kinon.utils.AnimationHelper
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.viewmodel.SettingsFragmentViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(SettingsFragmentViewModel::class.java)
    }

    init {
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Подключаем анимации и передаем номер позиции у кнопки в нижнем меню
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root,
            requireActivity(),
            ANIMATION_HELPER_POSITION
        )
        //Слушаем, какой у нас сейчас выбран вариант в настройках
        viewModel.categoryPropertyLifeData.observe(viewLifecycleOwner) {
            when (it) {
                POPULAR_CATEGORY -> binding.radioGroup.check(R.id.radioPopular)
                TOP_RATED_CATEGORY -> binding.radioGroup.check(R.id.radioTopRated)
                UPCOMING_CATEGORY -> binding.radioGroup.check(R.id.radioUpcoming)
                NOW_PLAYING_CATEGORY -> binding.radioGroup.check(R.id.radioNowPlaying)
            }
        }
        //Слушатель для отправки нового состояния в настройки
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioPopular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radioTopRated -> viewModel.putCategoryProperty(TOP_RATED_CATEGORY)
                R.id.radioUpcoming -> viewModel.putCategoryProperty(UPCOMING_CATEGORY)
                R.id.radioNowPlaying -> viewModel.putCategoryProperty(NOW_PLAYING_CATEGORY)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ANIMATION_HELPER_POSITION = 5
        private const val POPULAR_CATEGORY = "popular"
        private const val TOP_RATED_CATEGORY = "top_rated"
        private const val UPCOMING_CATEGORY = "upcoming"
        private const val NOW_PLAYING_CATEGORY = "now_playing"
    }
}