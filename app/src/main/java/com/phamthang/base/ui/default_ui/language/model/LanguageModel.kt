package com.phamthang.base.ui.default_ui.language.model

data class LanguageModel(
    var languageName: String,
    var isoLanguage: String,
    var isCheck: Boolean,
    var image: Int? = null,
    var listLanguageSubModel: MutableList<LanguageSubModel>,
    var isExpand: Boolean = false
) {
    constructor() : this("", "", false, 0, mutableListOf()) {}
}
