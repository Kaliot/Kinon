package com.bolunevdev.kinon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bolunevdev.kinon.databinding.FragmentRecommendedBinding


class RecommendedFragment : Fragment() {

    private lateinit var binding: FragmentRecommendedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecommendedBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
}