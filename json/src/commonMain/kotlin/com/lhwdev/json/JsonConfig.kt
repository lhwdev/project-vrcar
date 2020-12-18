package com.lhwdev.json


enum class ConfigValue(val isAllowed: Boolean, val isEnabled: Boolean) {
	never(false, false), allowed(true, false), enabled(true, true)
}


enum class StringUnicodeEncodeMode { asIs, allInAscii, always }

data class JsonConfig(
	val isCommentAllowed: ConfigValue = ConfigValue.never,
	val isLenient: ConfigValue = ConfigValue.never,
	val writeValueWithoutQuote: Boolean = false,
	val prettyPrint: PrettyPrint? = null,
	val stringUnicodeEncodeMode: StringUnicodeEncodeMode = StringUnicodeEncodeMode.allInAscii,
	val keyStringUnicodeEncodeMode: StringUnicodeEncodeMode = stringUnicodeEncodeMode
)

data class PrettyPrint(
	val indent: String = "\t",
	val objectBegin: String = "{",
	val objectPair: String = ": ",
	val objectComma: String = ",",
	val objectEnd: String = "}",
	val arrayBegin: String = "[",
	val arrayComma: String = ",",
	val arrayEnd: String = "]"
)
