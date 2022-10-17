package com.bolunevdev.kinon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bolunevdev.kinon.databinding.FragmentExitMenuBinding


class ExitMenuFragment : DialogFragment() {
    private lateinit var binding: FragmentExitMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExitMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Привязываем кнопки
        binding.btnY.setOnClickListener {
            (requireActivity() as MainActivity).finish()
        }
        binding.btnN.setOnClickListener {
            super.onDismiss(dialog!!)
        }
    }
}
