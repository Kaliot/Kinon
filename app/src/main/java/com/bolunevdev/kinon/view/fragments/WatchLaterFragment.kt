package com.bolunevdev.kinon.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.bolunevdev.core_api.entity.Alarm
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.databinding.FragmentWatchLaterBinding
import com.bolunevdev.kinon.utils.AnimationHelper
import com.bolunevdev.kinon.utils.AutoDisposable
import com.bolunevdev.kinon.utils.addTo
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.view.rv_adapters.AlarmListRecyclerAdapter
import com.bolunevdev.kinon.view.rv_adapters.TopSpacingItemDecoration
import com.bolunevdev.kinon.viewmodel.WatchLaterFragmentViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class WatchLaterFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(WatchLaterFragmentViewModel::class.java)
    }
    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    private val alarmAdapter: AlarmListRecyclerAdapter by lazy {
        AlarmListRecyclerAdapter(object : AlarmListRecyclerAdapter.OnMenuButtonClickListener {
            override fun click(alarm: Alarm, menuButton: View) {
                val popupMenu = PopupMenu(requireContext(), menuButton)
                popupMenu.menuInflater.inflate(R.menu.alarm_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.editAlarm -> {
                            context?.let { viewModel.editAlarm(it, alarm) }
                            true
                        }

                        R.id.deleteAlarm -> {
                            context?.let { viewModel.cancelAlarm(it, alarm) }
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
            }
        })
    }
    private var recyclerView: RecyclerView? = null

    private val autoDisposable = AutoDisposable()
    private var alarmDataBase = mutableListOf<Alarm>()
        //Используем backing field
        set(value) {
            //Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            alarmAdapter.submitList(field)
        }

    init {
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        val menuPosition = 2
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root,
            requireActivity(),
            menuPosition
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCircularRevealAnimation()

        initRV()

        bindAutoDisposable()

        loadAlarmsDataBase()
    }

    private fun bindAutoDisposable() {
        autoDisposable.bindTo(lifecycle)
    }

    private fun startCircularRevealAnimation() {
        val menuPosition = 3
        AnimationHelper
            .performFragmentCircularRevealAnimation(
                binding.root,
                requireActivity(),
                menuPosition
            )
        return
    }


    private fun initRV() {
        //находим наш RV
        recyclerView = binding.watchLaterRecyclerView
        recyclerView?.apply {
            //Присваиваем адаптер
            adapter = alarmAdapter

            //Присвоим layoutManager
            layoutManager = LinearLayoutManager(requireContext())

            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(DECORATOR_PADDING_IN_DP)
            addItemDecoration(decorator)
        }
    }

    private fun loadAlarmsDataBase() {
        viewModel.alarmListObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorComplete()
            .subscribe {
                alarmDataBase = it as MutableList<Alarm>
                alarmAdapter.submitList(alarmDataBase)
            }.addTo(autoDisposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DECORATOR_PADDING_IN_DP = 8
    }
}