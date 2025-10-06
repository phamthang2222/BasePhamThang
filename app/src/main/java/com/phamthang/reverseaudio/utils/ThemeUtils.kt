/*
 * This file is part of Open Sudoku - an open-source Sudoku game.
 * Copyright (C) 2009-2025 by Open Sudoku authors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("HardCodedStringLiteral")

package com.phamthang.reverseaudio.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.helper.game.GameSettings
import com.phamthang.reverseaudio.helper.game.SameValueHighlightMode
import com.phamthang.reverseaudio.helper.game.SudokuBoard
import com.phamthang.reverseaudio.helper.game.THEME_AMOLED
import com.phamthang.reverseaudio.helper.game.THEME_OPENSUDOKU
import com.phamthang.reverseaudio.helper.game.UI_MODE_DARK
import com.phamthang.reverseaudio.helper.game.UI_MODE_LIGHT
import com.phamthang.reverseaudio.helper.gui.SudokuBoardView

import kotlin.math.abs
import kotlin.math.ceil

enum class Colors(val suffix: String, val attribute: Int) {
	ACCENT("Accent", androidx.appcompat.R.attr.colorPrimaryDark),
	BACKGROUND("Background", R.attr.colorBackground),
	BACKGROUND_ERROR("BackgroundError", R.attr.colorInvalidBackground),
	EVEN_BACKGROUND("EvenBackground", R.attr.colorEvenBackground),
	EVEN_MARKS_TEXT("EvenMarksText", R.attr.colorEvenMarksText),
	EVEN_TEXT("EvenText", R.attr.colorEvenText),
	HIGHLIGHTED_BACKGROUND("HighlightedBackground", R.attr.colorHighlightedBackground),
	HIGHLIGHTED_MARKS_TEXT("HighlightedMarksText", R.attr.colorHighlightedMarksText),
	HIGHLIGHTED_TEXT("HighlightedText", R.attr.colorHighlightedText),
	LINE("Line", R.attr.colorLine),
	MARKS_TEXT("MarksText", R.attr.colorMarksText),
	PRIMARY("Primary", androidx.appcompat.R.attr.colorPrimary),
	READ_ONLY_BACKGROUND("ReadOnlyBackground", R.attr.colorReadOnlyBackground),
	READ_ONLY_TEXT("ReadOnlyText", R.attr.colorReadOnlyText),
	SECTOR_LINE("SectorLine", R.attr.colorSectorLine),
	SELECTED_BACKGROUND("SelectedBackground", R.attr.colorSelectedCellFrame),
	VALUE_TEXT("ValueText", R.attr.colorValueText),
	TEXT_ERROR("TextError", R.attr.colorInvalidText),
	TOUCHED_BACKGROUND("TouchedBackground", R.attr.colorTouchedBackground),
	TOUCHED_MARKS_TEXT("TouchedMarksText", R.attr.colorTouchedMarksText),
	TOUCHED_TEXT("TouchedText", R.attr.colorTouchedText);

	fun key(isLightTheme: Boolean): String {
		return if (isLightTheme) "custom_light_theme_color$suffix" else "custom_dark_theme_color$suffix"
	}
}
const val UI_MODE_KEY = "ui_mode"
const val THEME_CUSTOM_LIGHT = "custom_light"
const val THEME_CUSTOM_DARK = "custom_dark"
const val COLOR_DIM_FACTOR = 0.33
val LIGHT_THEMES = arrayOf(THEME_OPENSUDOKU, "latte", "sunrise", "crystal", "emerald", "amethyst", "paper_light", "graph_paper_light", "high_contrast_light",
	THEME_CUSTOM_LIGHT)
val DARK_THEMES = arrayOf(THEME_AMOLED, "espresso", "honey_bee", "midnight_blue", "forest", "ruby", "paper_dark", "graph_paper_dark", "high_contrast_dark",
THEME_CUSTOM_DARK)

object ThemeUtils {
	private val MATERIAL_COLORS = intArrayOf(
		-0x21f24,
		-0x64245,
		-0x96678,
		-0xc93a0,
		-0x17b1c0,
		-0x1ae3dd,
		-0x22e6e3,
		-0x2fe8ea,
		-0x3bebef,
		-0x4fedf6,
		-0x8669,
		-0xae89,
		-0xd291,
		-0x1fffce,
		-0x31b14,
		-0x74430,
		-0xb704f,
		-0xf9d6e,
		-0x13bf86,
		-0x16e19d,
		-0x27e4a0,
		-0x3de7a5,
		-0x52eba9,
		-0x77f1b1,
		-0x7f55,
		-0xbf7f,
		-0xaffa9,
		-0x3aee9e,
		-0xc1a0b,
		-0x1e4119,
		-0x316c28,
		-0x459738,
		-0x54b844,
		-0x63d850,
		-0x71db56,
		-0x84e05e,
		-0x95e466,
		-0xb5eb74,
		-0x157f04,
		-0x1fbf05,
		-0x2aff07,
		-0x55ff01,
		-0x12180a,
		-0x2e3b17,
		-0x4c6225,
		-0x6a8a33,
		-0x81a83e,
		-0x98c549,
		-0xa1ca4f,
		-0xaed258,
		-0xbad860,
		-0xcee46e,
		-0x4c7701,
		-0x83b201,
		-0x9ae001,
		-0x9dff16,
		-0x17150a,
		-0x3a3517,
		-0x605726,
		-0x867935,
		-0xa39440,
		-0xc0ae4b,
		-0xc6b655,
		-0xcfc061,
		-0xd7ca6d,
		-0xe5dc82,
		-0x736101,
		-0xac9202,
		-0xc2a502,
		-0xcfb002,
		-0x181603,
		-0x2f2601,
		-0x504001,
		-0x6e5801,
		-0x8c7002,
		-0xa98804,
		-0xb19311,
		-0xbaa122,
		-0xc4af32,
		-0xd5c94f,
		-0x594501,
		-0x977601,
		-0xb28c01,
		-0xb29601,
		-0x1e0a02,
		-0x4c1a04,
		-0x7e2b06,
		-0xb03c09,
		-0xd6490a,
		-0xfc560c,
		-0xfc641b,
		-0xfd772f,
		-0xfd8843,
		-0xfea865,
		-0x7f2701,
		-0xbf3b01,
		-0xff4f01,
		-0xff6e16,
		-0x1f0806,
		-0x4d140e,
		-0x7f2116,
		-0xb22f1f,
		-0xd93926,
		-0xff432c,
		-0xff533f,
		-0xff6859,
		-0xff7c71,
		-0xff9f9c,
		-0x7b0001,
		-0xe70001,
		-0xff1a01,
		-0xff472c,
		-0x1f0d0f,
		-0x4d2025,
		-0x7f343c,
		-0xb24954,
		-0xd95966,
		-0xff6978,
		-0xff7685,
		-0xff8695,
		-0xff96a4,
		-0xffb2c0,
		-0x580015,
		-0x9b0026,
		-0xe2164a,
		-0xff405b,
		-0x2f0732,
		-0x5c165c,
		-0x8d2a8e,
		-0xbd42bf,
		-0xd450d5,
		-0xda64dc,
		-0xf570f8,
		-0xf581f9,
		-0xfa9100,
		-0xf2acfe,
		-0x5d0873,
		-0xa50ea8,
		-0xeb18eb,
		-0xed3900,
		-0xe0717,
		-0x231238,
		-0x3a1e5b,
		-0x512a7f,
		-0x63339b,
		-0x743cb6,
		-0x834cbe,
		-0x9760c8,
		-0xaa74d1,
		-0xcc96e2,
		-0x330070,
		-0x4d00a7,
		-0x8900fd,
		-0x9b22e9,
		-0x60419,
		-0xf0b3d,
		-0x191164,
		-0x23188b,
		-0x2b1ea9,
		-0x3223c7,
		-0x3f35cd,
		-0x504bd5,
		-0x6162dc,
		-0x7d88e9,
		-0xb007f,
		-0x1100bf,
		-0x390100,
		-0x511600,
		-0x219,
		-0x63c,
		-0xa63,
		-0xe8a,
		-0x11a8,
		-0x14c5,
		-0x227cb,
		-0x43fd3,
		-0x657db,
		-0xa80e9,
		-0x73,
		-0x100,
		-0x1600,
		-0x2a00,
		-0x71f,
		-0x134d,
		-0x1f7e,
		-0x2ab1,
		-0x35d8,
		-0x3ef9,
		-0x4d00,
		-0x6000,
		-0x7100,
		-0x9100,
		-0x1a81,
		-0x28c0,
		-0x3c00,
		-0x5500,
		-0xc20,
		-0x1f4e,
		-0x3380,
		-0x48b3,
		-0x58da,
		-0x6800,
		-0x47400,
		-0xa8400,
		-0x109400,
		-0x19af00,
		-0x2e80,
		-0x54c0,
		-0x6f00,
		-0x9300,
		-0x41619,
		-0x3344,
		-0x546f,
		-0x759b,
		-0x8fbd,
		-0xa8de,
		-0xbaee2,
		-0x19b5e7,
		-0x27bceb,
		-0x40c9f4,
		-0x6180,
		-0x91c0,
		-0xc300,
		-0x22d400,
		-0x101417,
		-0x283338,
		-0x43555c,
		-0x5e7781,
		-0x72919d,
		-0x86aab8,
		-0x92b3bf,
		-0xa2bfc9,
		-0xb1cbd2,
		-0xc1d8dd,
		-0x50506,
		-0xa0a0b,
		-0x111112,
		-0x1f1f20,
		-0x424243,
		-0x616162,
		-0x8a8a8b,
		-0x9e9e9f,
		-0xbdbdbe,
		-0xdededf,
		-0x1000000,
		-0x1,
		-0x13100f,
		-0x302724,
		-0x4f413b,
		-0x6f5b52,
		-0x876f64,
		-0x9f8275,
		-0xab9186,
		-0xbaa59c,
		-0xc8b8b1,
		-0xd9cdc8
	)
	var sTimestampOfLastThemeUpdate: Long = 0
	private val alphaShifted = ceil(COLOR_DIM_FACTOR * 0xFF).toInt() shl 24

	fun getThemeResourceIdFromString(theme: String?): Int {
		return when (theme) {
			THEME_OPENSUDOKU -> R.style.Base_Theme_MyApplication
			else -> R.style.Base_Theme_MyApplication
		}
	}

	fun isDarkTheme(themeCode: String?): Boolean {
		if (LIGHT_THEMES.contains(themeCode)) return false
		if (DARK_THEMES.contains(themeCode)) return true
		Log.e(ThemeUtils::class.java.simpleName, "Unknown theme code: $themeCode")
		return false
	}

	fun setThemeFromPreferences(activity: Activity) {
		val settings = GameSettings(activity)

		val currentNightMode = AppCompatDelegate.getDefaultNightMode()
		// A dark theme overrides the UI mode, and is always in night mode. Otherwise, follow the user's preference.
		// If the UI mode has changed then signal the caller to recreate.
		val newNightMode: Int = when (settings.uiMode) {
			UI_MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
			UI_MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
			else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
		}
		if (newNightMode != currentNightMode) {
			AppCompatDelegate.setDefaultNightMode(newNightMode)
		}

		val theme = settings.theme
		activity.setTheme(getThemeResourceIdFromString(theme))

		if (theme == THEME_CUSTOM_LIGHT || theme == THEME_CUSTOM_DARK) {
			val isLightTheme = theme == THEME_CUSTOM_LIGHT
			val themeResource = activity.theme
			val colorPrimaryResourceId = getColorPrimaryResourceId(activity, findClosestMaterialColor(settings.customColors(isLightTheme).colorPrimary))
			themeResource.applyStyle(colorPrimaryResourceId, true)
			val colorAccentResourceId = getColorAccentResourceId(activity, findClosestMaterialColor(settings.customColors(isLightTheme).colorAccent))
			themeResource.applyStyle(colorAccentResourceId, true)
		}
	}

	fun getCurrentThemeColor(context: Context, colorAttribute: Int): Int {
		val attributes = intArrayOf(colorAttribute)
		val themeColors = context.theme.obtainStyledAttributes(attributes)
		return themeColors.getColor(0, Color.BLACK)
	}

	/**
	 * Updates the colors of `board` to use colors defined by the `custom_theme_...` preferences.
	 */
	fun applyCustomThemeToSudokuBoardViewFromSharedPreferences(context: Context, board: SudokuBoardView, isLightTheme: Boolean) {
		val settings = GameSettings(context)
		board.apply {
			line.color = settings.customColors(isLightTheme).colorLine
			sectorLine.color = settings.customColors(isLightTheme).colorSectorLine
			value.color = settings.customColors(isLightTheme).colorValueText
			textMark.color = settings.customColors(isLightTheme).colorMarkText
			background.color = settings.customColors(isLightTheme).colorBackground
			textReadOnly.color = settings.customColors(isLightTheme).colorReadOnlyText
			backgroundReadOnly.color = settings.customColors(isLightTheme).colorReadOnlyBackground
			textTouched.color = settings.customColors(isLightTheme).colorTouchedText
			textTouchedMarks.color = settings.customColors(isLightTheme).colorTouchedMarksText
			backgroundTouched.color = settings.customColors(isLightTheme).colorTouchedBackground
			selectedCellFrame.color = settings.customColors(isLightTheme).colorSelectedCellFrame
			textHighlighted.color = settings.customColors(isLightTheme).colorHighlightedText
			textMarkHighlighted.color = settings.customColors(isLightTheme).colorMarkHighlightedText
			backgroundHighlighted.color = settings.customColors(isLightTheme).colorHighlightedBackground
			textEvenCells.color = settings.customColors(isLightTheme).colorEvenCellsText
			textEvenCellsMarks.color = settings.customColors(isLightTheme).colorEvenCellsMarksText
			backgroundEvenCells.color = settings.customColors(isLightTheme).colorEvenCellsBackground
			textInvalid.color = settings.customColors(isLightTheme).colorInvalidText
			backgroundInvalid.color = settings.customColors(isLightTheme).colorInvalidBackground
		}
	}

	fun applyConfiguredThemeToSudokuBoardView(boardView: SudokuBoardView, context: Context) {
		val theme = GameSettings(context).theme
		applyThemeToSudokuBoardViewFromContext(theme, boardView, context)
	}

	fun applyThemeToSudokuBoardViewFromContext(theme: String, board: SudokuBoardView, context: Context) {
		if (theme == THEME_CUSTOM_LIGHT || theme == THEME_CUSTOM_DARK) {
			applyCustomThemeToSudokuBoardViewFromSharedPreferences(context, board, theme == THEME_CUSTOM_LIGHT)
		} else {
			// If the theme implies dark mode then show the dark mode colours. Do this by constructing a new context with a `Configuration` that forces UI_MODE_NIGHT_YES.
			// This will cause the later colour lookups to use the version of the theme from values-night/.
			// Doing this with AppCompatDelegate.setDefaultNightMode() is destructive -- the activity/fragment/dialog would be destroyed and recreated whenever
			// the user previews a new theme.
			val config = if (isDarkTheme(theme)) {
				context.resources.configuration.apply { uiMode = Configuration.UI_MODE_NIGHT_YES or (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) }
			} else {
				context.resources.configuration.apply { uiMode = Configuration.UI_MODE_NIGHT_NO or (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) }
			}
			val configContext = context.createConfigurationContext(config)
			val themeWrapper = ContextThemeWrapper(configContext, getThemeResourceIdFromString(theme))
			board.setAllColorsFromThemedContext(themeWrapper)
		}
		board.invalidate()
	}

	fun prepareBoardPreviewView(boardView: SudokuBoardView) {
		boardView.apply {
			isFocusable = false
			// This provides a sample of an in-progress game that will demonstrate all of the possible scenarios that
			// have different theme colors applied to them.
			board = SudokuBoard.deserialize(
				"version: 4\n" +
						"9|32|0|0|0|42|10|1|3|0|0|1|0|2|2|1|7|0|0|0|5|32|0|1|0|40|8|1|8|0|0|0|1|0|0|1|" +
						"5|32|0|0|0|42|10|1|8|0|0|1|0|6|2|1|1|0|0|0|0|36|32|1|0|40|8|1|7|0|0|0|9|0|0|1|" +
						"0|32|0|1|7|32|0|1|1|0|0|1|9|0|0|0|4|0|0|1|8|32|32|1|3|32|0|1|2|0|0|1|5|0|0|0|" +
						"9|128|0|1|1|0|0|0|5|0|0|1|6|128|0|1|2|128|0|0|9|0|0|1|0|192|128|1|3|0|0|1|0|192|128|1|" +
						"0|160|160|1|0|32|32|1|7|0|0|1|0|133|128|1|5|128|0|1|0|5|0|1|2|0|0|1|9|0|0|0|4|128|0|0|" +
						"2|128|128|1|3|0|0|1|9|0|0|1|7|128|0|1|0|128|128|1|4|0|0|0|1|0|0|1|5|0|0|0|6|128|0|0|" +
						"0|64|0|1|8|0|0|0|4|0|0|1|5|0|0|1|6|0|0|0|2|64|0|0|9|64|0|0|1|0|0|1|3|64|0|1|" +
						"0|65|1|1|9|0|0|1|2|0|0|0|4|129|0|0|3|128|128|1|0|65|64|1|5|192|0|1|6|0|0|1|0|192|128|1|" +
						"3|65|1|1|5|0|0|0|6|0|0|0|0|129|128|1|9|128|0|1|0|65|64|1|0|192|128|1|4|0|0|1|2|192|0|1|",
				false
			).first
			board.validateAllCells()
			moveCellSelectionTo(2, 1)
			touchedCell = boardView.board.getCell(1, 3)
			sameValueHighlightMode = SameValueHighlightMode.NUMBERS_AND_MARK_VALUES
			highlightDirectlyWrongValues = true
		}
	}

	fun findClosestMaterialColor(color: Int): Int {
		var minDifference = Int.MAX_VALUE
		var selectedIndex = 0
		var difference: Int
		var rdiff: Int
		var gdiff: Int
		var bdiff: Int
		for (i in MATERIAL_COLORS.indices) {
			if (color == MATERIAL_COLORS[i]) {
				return color
			}
			rdiff = abs(Color.red(color) - Color.red(MATERIAL_COLORS[i]))
			gdiff = abs(Color.green(color) - Color.green(MATERIAL_COLORS[i]))
			bdiff = abs(Color.blue(color) - Color.blue(MATERIAL_COLORS[i]))
			difference = rdiff + gdiff + bdiff
			if (difference < minDifference) {
				minDifference = difference
				selectedIndex = i
			}
		}
		return MATERIAL_COLORS[selectedIndex]
	}

	@SuppressLint("DiscouragedApi")
	private fun getColorResourceIdHelper(context: Context, style: String, color: Int): Int {
		val colorAsString = String.format("%1$06x", findClosestMaterialColor(color) and 0x00FFFFFF)
		return context.resources.getIdentifier(style + colorAsString, "style", context.packageName)
	}

	private fun getColorPrimaryResourceId(context: Context, color: Int): Int = getColorResourceIdHelper(context, "colorPrimary_", color)

	private fun getColorAccentResourceId(context: Context, color: Int): Int = getColorResourceIdHelper(context, "colorAccent_", color)

	/**
	 * Adjusts the alpha channel of the given color.
	 *
	 * @param color color to adjust
	 * @return The adjusted color
	 */
	@ColorInt
	fun dimmedColor(@ColorInt color: Int): Int {
		val colorOnly = color and 0x00ffffff
		return colorOnly or alphaShifted
	}
}
