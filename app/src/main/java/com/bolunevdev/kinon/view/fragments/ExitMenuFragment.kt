package com.bolunevdev.kinon.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bolunevdev.kinon.databinding.FragmentExitMenuBinding
import com.bolunevdev.kinon.view.activities.MainActivity


class ExitMenuFragment : DialogFragment() {

    private var _binding: FragmentExitMenuBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExitMenuBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Привязываем кнопки
        binding.btnY.setOnClickListener {
            (requireActivity() as MainActivity).finish()
        }
        binding.btnN.setOnClickListener {
            dialog?.let { it1 -> super.onDismiss(it1) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
