package com.phamthang.base.ui.default_ui.language.model

data class LanguageSubModel(
    var flag : Int,
    var languageName: String,
    var isoLanguage: String,
    var isCheck: Boolean,
) {
    constructor() : this(0, "", "", false) {}
}

