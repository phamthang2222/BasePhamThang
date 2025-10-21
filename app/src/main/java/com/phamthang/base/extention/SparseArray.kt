package com.phamthang.base.extention

import android.util.SparseArray

fun <T> SparseArray<T>.toSortedListByKey(): List<T> {
    val list = mutableListOf<T>() // Khởi tạo một danh sách rỗng
    for (i in 0 until this.size()) { // Lặp qua các phần tử của SparseArray theo chỉ số nội bộ
        list.add(this.valueAt(i)) // Thêm giá trị của phần tử vào danh sách
    }
    return list // Trả về danh sách các giá trị
}