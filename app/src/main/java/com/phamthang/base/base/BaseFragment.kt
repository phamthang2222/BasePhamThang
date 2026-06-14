package com.phamthang.base.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.phamthang.base.utils.SystemUtil
import kotlinx.coroutines.launch

/**
 * BaseFragment nâng cấp hỗ trợ Clean Architecture và Flow.
 */
abstract class BaseFragment<VM : BaseViewModel<*, *>, T : ViewBinding> : Fragment() {
    var TAG = "BaseFragment"

    lateinit var viewModel: VM
    
    private var _binding: T? = null
    protected val binding get() = _binding!!

    lateinit var activity: Activity
    lateinit var baseActivity: BaseActivity<*, *>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(requireContext())
        activity = requireActivity()
        baseActivity = requireActivity() as BaseActivity<*, *>
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TAG = javaClass.simpleName
        viewModel = ViewModelProvider(this)[createViewModel()]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewDataBinging(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
        observeStateInternal()
    }

    private fun observeStateInternal() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeState()
            }
        }
    }

    /**
     * Chịu trách nhiệm quan sát UI State và UI Event từ ViewModel.
     */
    open fun observeState() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun createViewModel(): Class<VM>
    abstract fun getViewDataBinging(inflater: LayoutInflater, container: ViewGroup?): T
    abstract fun initView()
    abstract fun bindViewModel()
}
