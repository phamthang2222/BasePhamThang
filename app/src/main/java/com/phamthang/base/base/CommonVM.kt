package com.phamthang.base.base

/**
 * CommonVM được cập nhật để kế thừa BaseViewModel mới.
 * Nếu không có state cụ thể, sử dụng Unit.
 */
class CommonVM : BaseViewModel<Unit, Unit>() {
    override fun initialState() = Unit
}
