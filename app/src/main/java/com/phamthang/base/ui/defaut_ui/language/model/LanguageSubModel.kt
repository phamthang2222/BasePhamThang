package com.phamthang.base.ui.defaut_ui.language.model

data class LanguageSubModel(
    var flag : Int,
    var languageName: String,
    var isoLanguage: String,
    var isCheck: Boolean,
) {
    constructor() : this(0, "", "", false) {}
}

