package com.phamthang.reverseaudio.extention

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDateString(format: DateFormatPattern = DateFormatPattern.DD_MM_YYYY): String {
    val dateFormat = SimpleDateFormat(format.pattern, Locale.ENGLISH)
    return dateFormat.format(Date(this))
}


enum class DateFormatPattern(val pattern: String) {
    /** Ex: 22-08-2025 */
    DD_MM_YYYY("dd-MM-yyyy"),

    /** Ex: 2025-08-22 */
    YYYY_MM_DD("yyyy-MM-dd"),

    /** Ex: 22-08-2025 14:35:50 */
    FULL_DATE_TIME("dd-MM-yyyy HH:mm:ss"),

    /** Ex: 14:35:50 */
    TIME_ONLY("HH:mm:ss"),

    /** Ex: Aug 22 */
    TIME_MONTH_DAY("MMM d"),

    /** Ex: Friday, 22 Aug 2025 */
    DAY_NAME("EEEE, dd MMM yyyy"),

    /** Ex: Friday, 22 Aug */
    DAY_NAME_2("EEEE, dd MMM"),

    /** Ex: Aug 22,2025 */
    MONTH_NAME_DAY_YEAR("MMM dd,yyyy"),

    /** Ex: Friday, Aug 22 */
    DAY_NAME_MONTH_DAY("EEEE, MMM d"),

    /** Ex: 14:35 */
    TIME_HH_MM("HH:mm"),

    /** Ex: 20 Aug 2025 */
    DD_MMM_YYYY("dd MMM yyyy"),

    /** Ex: 22 Aug, 02:33 PM */
    DD_MMM_HH_MM_A("dd MMM, hh:mm a"),

    /** Ex:  02:33 PM - Apr 22 2025*/
    HH_MM_A_MMM_DD_YYYY("hh:mm a - MMM dd yyyy")
}


fun Long.formatTimeDuration(isAdvanced: Boolean = false) : String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return if(h == 0L && isAdvanced) "%02d:%02d".format(m, s) else "%02d:%02d:%02d".format(h, m, s)
}


