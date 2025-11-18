package com.phamthang.base.utils

import com.phamthang.base.R
import com.phamthang.base.ui.defaut_ui.language.model.LanguageModel
import com.phamthang.base.ui.defaut_ui.language.model.LanguageSubModel

object DataProject {


    fun listLanguage(): MutableList<LanguageModel> {
        val lists: MutableList<LanguageModel> = kotlin.collections.ArrayList()
        lists.add(
            LanguageModel(
                "Hindi", "hi", false, R.drawable.ic_hindi_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_hindi_india, "Hindi (Standard – India)", "hi", false),
                    LanguageSubModel(R.drawable.flag_hindi_el, "Hindi (Hinglish)", "hi", false)
                )
            )
        )
        lists.add(
            LanguageModel(
                "Spanish", "es", false, R.drawable.ic_span_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_spain_spain, "Spanish (Spain)", "es", false),
                    LanguageSubModel(R.drawable.flag_spain_latin, "Spanish (Latin America)", "es", false),
                    LanguageSubModel(R.drawable.flag_spain_mexico, "Spanish (Mexico)", "es", false)
                )
            )
        )
        lists.add(
            LanguageModel(
                "French", "fr", false, R.drawable.ic_french_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_fr_fr, "French (France)", "fr", false),
                    LanguageSubModel(R.drawable.flag_fr_canada, "French (Canada)", "fr", false),
                    LanguageSubModel(R.drawable.flag_fr_afica, "French (Africa)", "fr", false)
                )
            )
        )
        lists.add(
            LanguageModel(
                "English", "en", false, R.drawable.ic_english_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_el_uk, "English (UK)", "en", false),
                    LanguageSubModel(R.drawable.flag_el_us, "English (US)", "en", false),
                    LanguageSubModel(R.drawable.flag_el_india, "English (India)", "en", false),
                    LanguageSubModel(R.drawable.flag_el_international, "English (International)", "en", false)
                )
            )
        )
        lists.add(
            LanguageModel(
                "German", "de", false, R.drawable.ic_german_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_de_de, "German (Germany)", "de", false),
                    LanguageSubModel(R.drawable.flag_de_austria, "German (Austria)", "de", false),
                    LanguageSubModel(R.drawable.flag_de_switzer, "German (Switzerland)", "de", false)
                )
            )
        )
        lists.add(
            LanguageModel(
                "Indonesian", "in", false, R.drawable.ic_indo_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_indo_spain, "Indonesian (Standard)", "in", false),
                    LanguageSubModel(R.drawable.flag_indo_japan, "Indonesian (Japanese-influenced)", "in", false),
                    LanguageSubModel(R.drawable.flag_indo_spain, "Indonesian (Informal, English combined)", "in", false)
                )
            )
        )
        lists.add(
            LanguageModel(
                "Portuguese", "pt", false, R.drawable.ic_portuguese_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_pt_pt, "Portuguese (Portugal)", "pt", false),
                    LanguageSubModel(R.drawable.flag_pt_brazil, "Portuguese (Brazil)", "pt", false),
                    LanguageSubModel(R.drawable.flag_pt_afica, "Portuguese (Africa)", "pt", false)
                )
            )
        )

        lists.add(
            LanguageModel(
                "Chinese", "zh", false, R.drawable.ic_china_flag,

                mutableListOf(
                    LanguageSubModel(R.drawable.flag_cn_cn, "Chinese (China)", "zh", false),
                    LanguageSubModel(R.drawable.flag_cn_kh, "Chinese (Hong Kong)", "zh", false),
                    LanguageSubModel(R.drawable.flag_cn_tw, "Chinese (Taiwan)", "zh", false)
                )
            )
        )

        lists.add(
            LanguageModel(
                "Swahili", "sw", false, R.drawable.ic_swahili_flag,
                mutableListOf()
            )
        )
        lists.add(
            LanguageModel(
                "Korean", "ko", false, R.drawable.ic_korean_flag,
                mutableListOf()
            )
        )
        lists.add(
            LanguageModel(
                "Rusian", "ru", false, R.drawable.ic_russia_flag,
                mutableListOf()
            )
        )
        lists.add(
            LanguageModel(
                "Turkish", "tr", false, R.drawable.ic_turkey_flag,
                mutableListOf()
            )
        )
        lists.add(
            LanguageModel(
                "Arabic", "ar", false, R.drawable.ic_a_rap_flag,
                mutableListOf()
            )
        )
        lists.add(
            LanguageModel(
                "Amharic", "am", false, R.drawable.ic_amharic_flag,
                mutableListOf()
            )
        )
        lists.add(
            LanguageModel(
                "Zulu", "zu", false, R.drawable.ic_zulu_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_zulu_zulu, "Zulu (Standard)", "zu", false),
                    LanguageSubModel(R.drawable.flag_zulu_zulu, "Zulu (Urban)", "zu", false),
                    LanguageSubModel(R.drawable.flag_zulu_zulu, "Zulu (Traditional)", "zu", false)
                )
            )
        )
        lists.add(
            LanguageModel(
                "Yoruba", "yo", false, R.drawable.ic_yoruba_flag,
                mutableListOf()
            )
        )

        lists.add(
            LanguageModel(
                "Afrikaans", "af", false, R.drawable.ic_afrikaans_flag,
                mutableListOf(
                    LanguageSubModel(R.drawable.flag_standard, "Afrikaans (South Africa – Standard)", "af", false),
                    LanguageSubModel(R.drawable.flag_standard, "Afrikaans (Informal)", "af", false),
                    LanguageSubModel(R.drawable.flag_standard_namibia, "Afrikaans (Namibia)", "af", false)
                )
            )
        )
        return lists
    }
}