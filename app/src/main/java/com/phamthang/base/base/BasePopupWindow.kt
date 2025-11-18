package com.phamthang.base.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.*
import android.widget.PopupWindow
import androidx.viewbinding.ViewBinding
import com.phamthang.base.extention.logDebug

abstract class BasePopupWindow<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB,
    private val popupWidth: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    private val popupHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    focusable: Boolean = true,
    outsideTouchable: Boolean = true,
) : PopupWindow() {

    protected lateinit var binding: VB

    protected lateinit var context: Context

    init {
        isFocusable = focusable
        isOutsideTouchable = outsideTouchable
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    protected open fun onViewCreated() {}

    /**
     * Hiển thị popup bên dưới anchor view.
     * @param anchor View làm mốc để hiển thị popup.
     * @param xOff Khoảng cách lệch theo chiều ngang (trái/phải) so với anchor.
     * @param yOff Khoảng cách lệch theo chiều dọc (trên/dưới) so với anchor.
     * @param gravity Vị trí của popup so với anchor (ví dụ: Gravity.END để căn phải).
     */
    fun show(anchor: View, xOff: Int = 0, yOff: Int = 0, gravity: Int = Gravity.NO_GRAVITY, isDropDown: Boolean = true) {
        val context = anchor.context
        val inflater = LayoutInflater.from(context)
        binding = bindingInflater(inflater)
        contentView = binding.root

        width = popupWidth
        height = popupHeight
        logDebug(message = "checkShowPopup: xOff: $xOff, yOff: $yOff")
        onViewCreated()


        if(isDropDown){
            this.showAsDropDown(anchor, xOff, yOff, gravity)
        }else{
            val location = IntArray(2)
            anchor.getLocationOnScreen(location)

            val xOff = location[0]
            val yOff: Int = location[1]

            showAtLocation(anchor.rootView, gravity, xOff, yOff)
        }

    }

    fun showAbove(anchor: View, xOff: Int = 0, yOff: Int = 0) {
        val inflater = LayoutInflater.from(anchor.context)
        binding = bindingInflater(inflater)
        contentView = binding.root
        width = popupWidth
        height = popupHeight
        onViewCreated()

        // Đo kích thước popup trước
        binding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupHeight = binding.root.measuredHeight

        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val x = location[0] + xOff
        val y = location[1] - popupHeight + yOff // nằm phía trên

        showAtLocation(anchor, Gravity.NO_GRAVITY, x, y)
    }

    open fun dismissPopup(){
        if(isShowing){
            dismiss()
        }
    }
}

