package com.bolunevdev.kinon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bolunevdev.kinon.databinding.FragmentNewTitlesBinding


class NewTitlesFragment : Fragment() {

    private lateinit var binding: FragmentNewTitlesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewTitlesBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
}