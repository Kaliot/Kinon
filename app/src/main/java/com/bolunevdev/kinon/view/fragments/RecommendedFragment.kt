package com.bolunevdev.kinon.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.bolunevdev.kinon.databinding.FragmentRecommendedBinding
import com.bolunevdev.kinon.utils.AnimationHelper
import com.bolunevdev.kinon.view.activities.MainActivity


class RecommendedFragment : Fragment() {

    private var _binding: FragmentRecommendedBinding? = null
    private val binding get() = _binding!!

    init {
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendedBinding.inflate(inflater, container, false)
        val menuPosition = 4
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root,
            requireActivity(),
            menuPosition
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}