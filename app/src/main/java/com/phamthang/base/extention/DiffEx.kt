package com.phamthang.base.extention

import androidx.recyclerview.widget.DiffUtil

/**
 * Hàm mở rộng cho List, giúp tính toán DiffResult một cách ngắn gọn.
 * Hàm này ẩn đi việc phải tạo DiffUtil.Callback một cách thủ công.
 *
 * @receiver Danh sách cũ (oldList).
 * @param newList Danh sách mới.
 * @param areItemsTheSame Lambda để kiểm tra xem hai item có phải là một (thường so sánh bằng ID).
 * @param areContentsTheSame Lambda để kiểm tra xem nội dung của hai item có giống nhau không.
 * @return DiffUtil.DiffResult có thể được dùng với adapter.dispatchUpdatesTo().
 */
fun <T : Any> List<T>.diff(
    newList: List<T>,
    areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    areContentsTheSame: (oldItem: T, newItem: T) -> Boolean
): DiffUtil.DiffResult {
    val diffCallback = object : DiffUtil.Callback() {
        override fun getOldListSize(): Int = this@diff.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSame(this@diff[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSame(this@diff[oldItemPosition], newList[newItemPosition])
        }
    }
    return DiffUtil.calculateDiff(diffCallback)
}

/**
 * Một phiên bản đơn giản hơn của hàm diff, chuyên dùng cho các data class
 * có một trường định danh (key) duy nhất như 'id'.
 *
 * - `areItemsTheSame` sẽ được so sánh dựa trên key.
 * - `areContentsTheSame` sẽ được so sánh bằng toán tử `==` (so sánh toàn bộ đối tượng).
 *
 * @param keySelector Lambda để trích xuất key duy nhất từ item.
 */
fun <T : Any, K> List<T>.diffBy(
    newList: List<T>,
    keySelector: (T) -> K
): DiffUtil.DiffResult {
    return this.diff(
        newList = newList,
        areItemsTheSame = { old, new -> keySelector(old) == keySelector(new) },
        areContentsTheSame = { old, new -> old == new }
    )
}