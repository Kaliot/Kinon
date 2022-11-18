package com.bolunevdev.kinon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.bolunevdev.kinon.databinding.FragmentNewTitlesBinding


class NewTitlesFragment : Fragment() {

    private lateinit var binding: FragmentNewTitlesBinding

    init {
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewTitlesBinding.inflate(inflater, container, false)
        val menuPosition = 2
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root,
            requireActivity(),
            menuPosition
        )
        // Inflate the layout for this fragment
        return binding.root
    }
}