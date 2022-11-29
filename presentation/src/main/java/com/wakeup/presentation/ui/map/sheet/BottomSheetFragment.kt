package com.wakeup.presentation.ui.map.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.wakeup.domain.model.SortType
import com.wakeup.presentation.R
import com.wakeup.presentation.adapter.MomentPagingAdapter
import com.wakeup.presentation.databinding.BottomSheetBinding
import com.wakeup.presentation.ui.map.MapFragment
import com.wakeup.presentation.ui.map.MapViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetBinding
    private val viewModel: MapViewModel by viewModels({ requireParentFragment() })
    private val momentAdapter = MomentPagingAdapter()
    private lateinit var locationListener: LocationListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenus()
        setBottomSheetState()
        setAdapter()
        setAdapterListener()
        setCallback()

        collectData()
    }

    private fun setMenus() {
        val items = listOf(
            SortType.MOST_RECENT.str,
            SortType.OLDEST.str,
            SortType.NEAREST.str
        )
        val menuAdapter = ArrayAdapter(requireContext(), R.layout.item_menu, items)
        binding.textField.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (viewModel.bottomSheetState.value == BottomSheetBehavior.STATE_EXPANDED) {
                    changeVisibleMenu(true)
                }

                binding.sortMenu.setText(viewModel.sortType.value.str)
                (binding.textField.editText as? MaterialAutoCompleteTextView)?.setAdapter(menuAdapter)
                binding.textField.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        binding.sortMenu.setOnItemClickListener { _, _, position, _ ->
            viewModel.location.value = null
            viewModel.sortType.value = when (position) {
                0 -> SortType.MOST_RECENT
                1 -> SortType.OLDEST
                else -> {
                    locationListener = parentFragment as MapFragment
                    locationListener.onSetLocation()
                    SortType.NEAREST
                }
            }
            viewModel.fetchMoments()
        }
    }

    private fun setBottomSheetState() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = viewModel.bottomSheetState.value
    }

    private fun setAdapter() {
        binding.rvMoments.adapter = momentAdapter
    }

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            if (viewModel.scrollToTop.value) {
                binding.rvMoments.scrollToPosition(0)
            }
            viewModel.setScrollToTop(false)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            binding.rvMoments.scrollToPosition(0)
        }
    }

    private fun setAdapterListener() {
        momentAdapter.registerAdapterDataObserver(adapterDataObserver)

        momentAdapter.addLoadStateListener {
            binding.hasMoments = momentAdapter.itemCount > 0
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moments.collectLatest {
                    momentAdapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sortType.collectLatest {
                    binding.sortMenu.setText(it.str)
                }
            }
        }
    }

    private fun setCallback() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            binding.textField.visibility = View.INVISIBLE
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (viewModel.bottomSheetState.value != newState) {
                            changeVisibleMenu(true)
                            viewModel.bottomSheetState.value = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (viewModel.bottomSheetState.value != newState) {
                            changeVisibleMenu(false)
                            viewModel.bottomSheetState.value = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })
    }

    private fun changeVisibleMenu(isVisible: Boolean) = with(binding.textField) {
        if (isVisible) {
            visibility = View.VISIBLE
            animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        } else {
            visibility = View.INVISIBLE
            animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        }
    }

    override fun onDestroyView() {
        momentAdapter.unregisterAdapterDataObserver(adapterDataObserver)
        binding.unbind()
        super.onDestroyView()
    }
}

interface LocationListener {
    fun onSetLocation()
}