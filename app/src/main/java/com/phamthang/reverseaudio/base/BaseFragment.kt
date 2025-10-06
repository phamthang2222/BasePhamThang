package com.phamthang.reverseaudio.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.phamthang.reverseaudio.utils.SystemUtil


abstract class BaseFragment<VM : ViewModel, T : ViewBinding> : Fragment() {
    var TAG = "BaseFragment"

    lateinit var viewModel: VM
    lateinit var binding: T
    lateinit var activity: Activity
    lateinit var baseActivity :  BaseActivity<*,*>
    val isBindingInitializer get() = ::binding.isInitialized
    private var isPostedUpdateRecentEvent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(requireContext())
        activity = requireActivity()
        baseActivity = requireActivity() as BaseActivity<*,*>
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
        binding = getViewDataBinging()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()

    }

    abstract fun createViewModel(): Class<VM>
    abstract fun getViewDataBinging(): T
    abstract fun initView()
    abstract fun bindViewModel()

    override fun onResume() {
        super.onResume()

    }

    fun showActivity(act: Class<*>, bundle: Bundle?) {
        val intent = Intent(activity, act)
        intent.putExtras(bundle ?: Bundle())
        startActivity(intent)
    }

    inline fun <reified VM : ViewModel> Fragment.activityViewModel(): VM {
        return ViewModelProvider(requireActivity())[VM::class.java]
    }

}
