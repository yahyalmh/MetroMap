/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support

import Constants
import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Xml
import com.yaya.map.R
import org.xmlpull.v1.XmlPullParser
import support.time.FastDateFormat
import ui.activities.LaunchActivity
import java.io.File
import java.io.FileInputStream
import java.text.NumberFormat
import java.util.*


class LocaleController {
    var formatterDay: FastDateFormat? = null
    var formatterWeek: FastDateFormat? = null
    var formatterMonth: FastDateFormat? = null
    var formatterYear: FastDateFormat? = null
    var formatterMonthYear: FastDateFormat? = null
    var formatterYearMax: FastDateFormat? = null
    var formatterStats: FastDateFormat? = null
    var formatterBannedUntil: FastDateFormat? = null
    var formatterBannedUntilThisYear: FastDateFormat? = null
    var chatDate: FastDateFormat? = null
    var chatFullDate: FastDateFormat? = null
    private val allRules = HashMap<String, PluralRules>()
    private var currentLocale: Locale? = null
    var systemDefaultLocale: Locale
    private var currentPluralRules: PluralRules? = null
    var currentLocaleInfeo: LocaleInfo? = null
    private var localeValues = HashMap<String, String>()
    private var languageOverride: String? = null
    private var changingConfiguration = false
    private var reloadLastFile = false
    private val currencyValues: HashMap<String, String>? = null
    private var translitChars: HashMap<String, String>? = null

    private inner class TimeZoneChangedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            LaunchActivity.applicationHandler.post(Runnable {
                if (!formatterMonth!!.timeZone.equals(TimeZone.getDefault())) {
                    getInstance().recreateFormatters()
                }
            })
        }
    }

    class LocaleInfo {
        var name: String? = null
        var nameEnglish: String? = null
        var shortName: String? = null
        var pathToFile: String? = null
        var version = 0
        var isBuiltIn = false
        val saveString: String
            get() = "$name|$nameEnglish|$shortName|$pathToFile|$version"

        /* fun getPathToFile(): File? {
             if (isRemote) {
                 return File(LaunchActivity.getFilesDirFixed(), "remote_$shortName.xml")
             }
             return if (!TextUtils.isEmpty(pathToFile)) File(pathToFile) else null
         }
 */
        val key: String?
            get() = if (pathToFile != null && "remote" != pathToFile) {
                "local_$shortName"
            } else shortName

        val isRemote: Boolean
            get() = "remote" == pathToFile

        val isLocal: Boolean
            get() = !TextUtils.isEmpty(pathToFile) && !isRemote

        companion object {
            fun createWithString(string: String?): LocaleInfo? {
                if (string == null || string.length == 0) {
                    return null
                }
                val args = string.split("\\|").toTypedArray()
                var localeInfo: LocaleInfo? = null
                if (args.size >= 4) {
                    localeInfo = LocaleInfo()
                    localeInfo.name = args[0]
                    localeInfo.nameEnglish = args[1]
                    localeInfo.shortName = args[2].toLowerCase()
                    localeInfo.pathToFile = args[3]
                    /*if (args.size >= 5) {
                        localeInfo.version = Utilities.parseInt(args[4])
                    }*/
                }
                return localeInfo
            }
        }
    }

    private val loadingRemoteLanguages = false
    var languages = ArrayList<LocaleInfo>()
    var remoteLanguages = ArrayList<LocaleInfo>()
    var languagesDict = HashMap<String?, LocaleInfo>()
    private val otherLanguages = ArrayList<LocaleInfo>()
    private fun getLanguageFromDict(key: String?): LocaleInfo? {
        return if (key == null) {
            null
        } else languagesDict[key.toLowerCase().replace("-", "_")]
    }

    private fun addRules(languages: Array<String>, rules: PluralRules) {
        for (language in languages) {
            allRules[language] = rules
        }
    }

    private fun stringForQuantity(quantity: Int): String {
        return when (quantity) {
            QUANTITY_ZERO -> "zero"
            QUANTITY_ONE -> "one"
            QUANTITY_TWO -> "two"
            QUANTITY_FEW -> "few"
            QUANTITY_MANY -> "many"
            else -> "other"
        }
    }

    val isCurrentLocalLocale: Boolean
        get() = currentLocaleInfeo!!.isLocal

    private fun getLocaleString(locale: Locale?): String {
        if (locale == null) {
            return "en"
        }
        val languageCode = locale.language
        val countryCode = locale.country
        val variantCode = locale.variant
        if (languageCode.length == 0 && countryCode.length == 0) {
            return "en"
        }
        val result = StringBuilder(11)
        result.append(languageCode)
        if (countryCode.length > 0 || variantCode.length > 0) {
            result.append('_')
        }
        result.append(countryCode)
        if (variantCode.length > 0) {
            result.append('_')
        }
        result.append(variantCode)
        return result.toString()
    }

    fun applyLanguageFile(file: File): Boolean {
        try {
            val stringMap = getLocaleFileStrings(file)
            val languageName = stringMap["LanguageName"]
            val languageNameInEnglish = stringMap["LanguageNameInEnglish"]
            val languageCode = stringMap["LanguageCode"]
            if (languageName != null && languageName.length > 0 && languageNameInEnglish != null && languageNameInEnglish.length > 0 && languageCode != null && languageCode.length > 0) {
                if (languageName.contains("&") || languageName.contains("|")) {
                    return false
                }
                if (languageNameInEnglish.contains("&") || languageNameInEnglish.contains("|")) {
                    return false
                }
                if (languageCode.contains("&") || languageCode.contains("|") || languageCode.contains("/") || languageCode.contains("\\")) {
                    return false
                }
                /* val finalFile = File(LaunchActivity.getFilesDirFixed(), "$languageCode.xml")
                 if (!AndroidUtilities.copyFile(file, finalFile)) {
                     return false
                 }*/
                var localeInfo = getLanguageFromDict(languageCode)
                if (localeInfo == null) {
                    localeInfo = LocaleInfo()
                    localeInfo.name = languageName
                    localeInfo.nameEnglish = languageNameInEnglish
                    localeInfo.shortName = languageCode.toLowerCase()
//                    localeInfo.pathToFile = finalFile.absolutePath
                    languages.add(localeInfo)
                    languagesDict[localeInfo.key] = localeInfo
                    otherLanguages.add(localeInfo)
                    saveOtherLanguages()
                }
                localeValues = stringMap
                applyLanguage(localeInfo, true, false, true, false)
                return true
            }
        } catch (e: Exception) {
        }
        return false
    }

    private fun saveOtherLanguages() {
        val preferences: SharedPreferences = LaunchActivity.applicationContext.getSharedPreferences("langconfig", Activity.MODE_PRIVATE)
        val editor = preferences.edit()
        val stringBuilder = StringBuilder()
        for (a in otherLanguages.indices) {
            val localeInfo = otherLanguages[a]
            val loc = localeInfo.saveString
            if (loc != null) {
                if (stringBuilder.length != 0) {
                    stringBuilder.append("&")
                }
                stringBuilder.append(loc)
            }
        }
        editor.putString("locales", stringBuilder.toString())
        stringBuilder.setLength(0)
        for (a in remoteLanguages.indices) {
            val localeInfo = remoteLanguages[a]
            val loc = localeInfo.saveString
            if (loc != null) {
                if (stringBuilder.length != 0) {
                    stringBuilder.append("&")
                }
                stringBuilder.append(loc)
            }
        }
        editor.putString("remote", stringBuilder.toString())
        editor.commit()
    }

    fun deleteLanguage(localeInfo: LocaleInfo): Boolean {
        if (localeInfo.pathToFile == null || localeInfo.isRemote) {
            return false
        }
        if (currentLocaleInfeo === localeInfo) {
            var info: LocaleInfo? = null
            if (systemDefaultLocale.language != null) {
                info = getLanguageFromDict(systemDefaultLocale.language)
            }
            if (info == null) {
                info = getLanguageFromDict(getLocaleString(systemDefaultLocale))
            }
            if (info == null) {
                info = getLanguageFromDict("en")
            }
            applyLanguage(info, true, false)
        }
        otherLanguages.remove(localeInfo)
        languages.remove(localeInfo)
        languagesDict.remove(localeInfo.shortName)
        val file = File(localeInfo.pathToFile)
        file.delete()
        saveOtherLanguages()
        return true
    }

    private fun loadOtherLanguages() {
        val preferences: SharedPreferences = LaunchActivity.applicationContext.getSharedPreferences("langconfig", Activity.MODE_PRIVATE)
        var locales = preferences.getString("locales", null)
        if (!TextUtils.isEmpty(locales)) {
            val localesArr = locales!!.split("&").toTypedArray()
            for (locale in localesArr) {
                val localeInfo = LocaleInfo.createWithString(locale)
                if (localeInfo != null) {
                    otherLanguages.add(localeInfo)
                }
            }
        }
        locales = preferences.getString("remote", null)
        if (!TextUtils.isEmpty(locales)) {
            val localesArr = locales!!.split("&").toTypedArray()
            for (locale in localesArr) {
                val localeInfo = LocaleInfo.createWithString(locale)
                localeInfo!!.shortName = localeInfo.shortName!!.replace("-", "_")
                if (localeInfo != null) {
                    remoteLanguages.add(localeInfo)
                }
            }
        }
    }

    private fun getLocaleFileStrings(file: File): HashMap<String, String> {
        return getLocaleFileStrings(file, false)
    }

    private fun getLocaleFileStrings(file: File, preserveEscapes: Boolean): HashMap<String, String> {
        var stream: FileInputStream? = null
        reloadLastFile = false
        try {
            if (!file.exists()) {
                return HashMap()
            }
            val stringMap = HashMap<String, String>()
            val parser = Xml.newPullParser()
            stream = FileInputStream(file)
            parser.setInput(stream, "UTF-8")
            var eventType = parser.eventType
            var name: String? = null
            var value: String? = null
            var attrName: String? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    name = parser.name
                    val c = parser.attributeCount
                    if (c > 0) {
                        attrName = parser.getAttributeValue(0)
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (attrName != null) {
                        value = parser.text
                        if (value != null) {
                            value = value.trim { it <= ' ' }
                            if (preserveEscapes) {
                                value = value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ")
                            } else {
                                value = value.replace("\\n", "\n")
                                value = value.replace("\\", "")
                                val old: String = value
                                value = value.replace("&lt;", "<")
                                if (!reloadLastFile && value != old) {
                                    reloadLastFile = true
                                }
                            }
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    value = null
                    attrName = null
                    name = null
                }
                if (name != null && name == "string" && value != null && attrName != null && value.length != 0 && attrName.length != 0) {
                    stringMap[attrName] = value
                    name = null
                    value = null
                    attrName = null
                }
                eventType = parser.next()
            }
            return stringMap
        } catch (e: Exception) {
            reloadLastFile = true
        } finally {
            try {
                stream?.close()
            } catch (e: Exception) {
            }
        }
        return HashMap()
    }

    @JvmOverloads
    fun applyLanguage(localeInfo: LocaleInfo?, override: Boolean, init: Boolean, fromFile: Boolean = false, force: Boolean = false) {
        if (localeInfo == null) {
            return
        }
//        val pathToFile = localeInfo.getPathToFile()
        val shortName = localeInfo.shortName
        try {
            val newLocale: Locale
            val args = localeInfo.shortName!!.split("_").toTypedArray()
            newLocale = if (args.size == 1) {
                Locale(localeInfo.shortName)
            } else {
                Locale(args[0], args[1])
            }
            if (override) {
                languageOverride = localeInfo.shortName
                val preferences: SharedPreferences = LaunchActivity.applicationContext.getSharedPreferences(Constants.SHARED_PREF_FILENAME, Activity.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString(Constants.LANG_SHARED_KEY, localeInfo.key)
                editor.apply()
            }
            /* if (pathToFile == null) {
                 localeValues.clear()
             } else if (!fromFile) {
                 localeValues = getLocaleFileStrings(pathToFile)
             }*/
            currentLocale = newLocale
            currentLocaleInfeo = localeInfo
            currentPluralRules = allRules[args[0]]
            if (currentPluralRules == null) {
                currentPluralRules = allRules[currentLocale!!.language]
            }
            if (currentPluralRules == null) {
                currentPluralRules = PluralRules_None()
            }
            changingConfiguration = true
            Locale.setDefault(currentLocale)
            val config = Configuration()
            config.locale = currentLocale
            LaunchActivity.applicationContext.resources.updateConfiguration(config, LaunchActivity.applicationContext.resources.displayMetrics)
            changingConfiguration = false
        } catch (e: Exception) {
            changingConfiguration = false
        }
        recreateFormatters()
    }

    private fun getStringInternal(key: String, res: Int): String {
        var value = localeValues[key]
        if (value == null) {
            try {
                value = LaunchActivity.applicationContext.getString(res)
            } catch (e: Exception) {
            }
        }
        if (value == null) {
            value = "LOC_ERR:$key"
        }
        return value
    }

    fun formatCurrencyString(amount: Long, type: String): String {
        var amount = amount
        var type = type
        type = type.toUpperCase()
        val customFormat: String
        val doubleAmount: Double
        val discount = amount < 0
        amount = Math.abs(amount)
        when (type) {
            "CLF" -> {
                customFormat = " %.4f"
                doubleAmount = amount / 10000.0
            }
            "BHD", "IQD", "JOD", "KWD", "LYD", "OMR", "TND" -> {
                customFormat = " %.3f"
                doubleAmount = amount / 1000.0
            }
            "BIF", "BYR", "CLP", "CVE", "DJF", "GNF", "ISK", "JPY", "KMF", "KRW", "MGA", "PYG", "RWF", "UGX", "UYI", "VND", "VUV", "XAF", "XOF", "XPF" -> {
                customFormat = " %.0f"
                doubleAmount = amount.toDouble()
            }
            "MRO" -> {
                customFormat = " %.1f"
                doubleAmount = amount / 10.0
            }
            else -> {
                customFormat = " %.2f"
                doubleAmount = amount / 100.0
            }
        }
        val сurrency = Currency.getInstance(type)
        if (сurrency != null) {
            val format = NumberFormat.getCurrencyInstance(if (currentLocale != null) currentLocale else systemDefaultLocale)
            format.currency = сurrency
            return (if (discount) "-" else "") + format.format(doubleAmount)
        }
        return (if (discount) "-" else "") + String.format(Locale.US, type + customFormat, doubleAmount)
    }

    fun formatCurrencyDecimalString(amount: Long, type: String, inludeType: Boolean): String {
        var amount = amount
        var type = type
        type = type.toUpperCase()
        val customFormat: String
        val doubleAmount: Double
        amount = Math.abs(amount)
        when (type) {
            "CLF" -> {
                customFormat = " %.4f"
                doubleAmount = amount / 10000.0
            }
            "BHD", "IQD", "JOD", "KWD", "LYD", "OMR", "TND" -> {
                customFormat = " %.3f"
                doubleAmount = amount / 1000.0
            }
            "BIF", "BYR", "CLP", "CVE", "DJF", "GNF", "ISK", "JPY", "KMF", "KRW", "MGA", "PYG", "RWF", "UGX", "UYI", "VND", "VUV", "XAF", "XOF", "XPF" -> {
                customFormat = " %.0f"
                doubleAmount = amount.toDouble()
            }
            "MRO" -> {
                customFormat = " %.1f"
                doubleAmount = amount / 10.0
            }
            else -> {
                customFormat = " %.2f"
                doubleAmount = amount / 100.0
            }
        }
        return String.format(Locale.US, if (inludeType) type else "" + customFormat, doubleAmount).trim { it <= ' ' }
    }

    fun onDeviceConfigurationChange(newConfig: Configuration) {
        if (changingConfiguration) {
            return
        }
        is24HourFormat = DateFormat.is24HourFormat(LaunchActivity.applicationContext)
        systemDefaultLocale = newConfig.locale
        if (languageOverride != null) {
            val toSet = currentLocaleInfeo
            currentLocaleInfeo = null
            applyLanguage(toSet, false, false)
        } else {
            val newLocale = newConfig.locale
            if (newLocale != null) {
                val d1 = newLocale.displayName
                val d2 = currentLocale!!.displayName
                if (d1 != null && d2 != null && d1 != d2) {
                    recreateFormatters()
                }
                currentLocale = newLocale
                currentPluralRules = allRules[currentLocale!!.language]
                if (currentPluralRules == null) {
                    currentPluralRules = allRules["en"]
                }
            }
        }
    }

    private fun createFormatter(locale: Locale?, format: String, defaultFormat: String): FastDateFormat {
        var format: String? = format
        if (format == null || format.length == 0) {
            format = defaultFormat
        }
        var formatter: FastDateFormat
        try {
            formatter = FastDateFormat.getInstance(format, locale)
        } catch (e: Exception) {
            format = defaultFormat
            formatter = FastDateFormat.getInstance(format, locale)
        }
        return formatter
    }

    fun recreateFormatters() {
        var locale = currentLocale
        if (locale == null) {
            locale = Locale.getDefault()
        }
        var lang = locale!!.language
        if (lang == null) {
            lang = "en"
        }
        lang = lang.toLowerCase()
        isRTL = lang.startsWith("ar") || lang.startsWith("he") || lang.startsWith("iw") || lang.startsWith("fa")
        nameDisplayOrder = if (lang == "ko") 2 else 1
        formatterMonth = createFormatter(locale, getStringInternal("formatterMonth", R.string.formatterMonth), "dd MMM")
        formatterYear = createFormatter(locale, getStringInternal("formatterYear", R.string.formatterYear), "dd.MM.yy")
        formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", R.string.formatterYearMax), "dd.MM.yyyy")
        chatDate = createFormatter(locale, getStringInternal("chatDate", R.string.chatDate), "d MMMM")
        chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", R.string.chatFullDate), "d MMMM yyyy")
        formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", R.string.formatterWeek), "EEE")
        formatterMonthYear = createFormatter(locale, getStringInternal("formatterMonthYear", R.string.formatterMonthYear), "MMMM yyyy")
        formatterDay = createFormatter(if (lang.toLowerCase() == "ar" || lang.toLowerCase() == "ko") locale else Locale.US, if (is24HourFormat) getStringInternal("formatterDay24H", R.string.formatterDay24H) else getStringInternal("formatterDay12H", R.string.formatterDay12H), if (is24HourFormat) "HH:mm" else "h:mm a")
        formatterStats = createFormatter(locale, if (is24HourFormat) getStringInternal("formatterStats24H", R.string.formatterStats24H) else getStringInternal("formatterStats12H", R.string.formatterStats12H), if (is24HourFormat) "MMM dd yyyy, HH:mm" else "MMM dd yyyy, h:mm a")
        formatterBannedUntil = createFormatter(locale, if (is24HourFormat) getStringInternal("formatterBannedUntil24H", R.string.formatterBannedUntil24H) else getStringInternal("formatterBannedUntil12H", R.string.formatterBannedUntil12H), if (is24HourFormat) "MMM dd yyyy, HH:mm" else "MMM dd yyyy, h:mm a")
        formatterBannedUntilThisYear = createFormatter(locale, if (is24HourFormat) getStringInternal("formatterBannedUntilThisYear24H", R.string.formatterBannedUntilThisYear24H) else getStringInternal("formatterBannedUntilThisYear12H", R.string.formatterBannedUntilThisYear12H), if (is24HourFormat) "MMM dd, HH:mm" else "MMM dd, h:mm a")
    }

    private fun escapeString(str: String): String {
        return if (str.contains("[CDATA")) {
            str
        } else str.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;")
    }

    fun getTranslitString(src: String): String {
        if (translitChars == null) {
            translitChars = HashMap(520)
            translitChars!!["ȼ"] = "c"
            translitChars!!["ᶇ"] = "n"
            translitChars!!["ɖ"] = "d"
            translitChars!!["ỿ"] = "y"
            translitChars!!["ᴓ"] = "o"
            translitChars!!["ø"] = "o"
            translitChars!!["ḁ"] = "a"
            translitChars!!["ʯ"] = "h"
            translitChars!!["ŷ"] = "y"
            translitChars!!["ʞ"] = "k"
            translitChars!!["ừ"] = "u"
            translitChars!!["ꜳ"] = "aa"
            translitChars!!["ĳ"] = "ij"
            translitChars!!["ḽ"] = "l"
            translitChars!!["ɪ"] = "i"
            translitChars!!["ḇ"] = "b"
            translitChars!!["ʀ"] = "r"
            translitChars!!["ě"] = "e"
            translitChars!!["ﬃ"] = "ffi"
            translitChars!!["ơ"] = "o"
            translitChars!!["ⱹ"] = "r"
            translitChars!!["ồ"] = "o"
            translitChars!!["ǐ"] = "i"
            translitChars!!["ꝕ"] = "p"
            translitChars!!["ý"] = "y"
            translitChars!!["ḝ"] = "e"
            translitChars!!["ₒ"] = "o"
            translitChars!!["ⱥ"] = "a"
            translitChars!!["ʙ"] = "b"
            translitChars!!["ḛ"] = "e"
            translitChars!!["ƈ"] = "c"
            translitChars!!["ɦ"] = "h"
            translitChars!!["ᵬ"] = "b"
            translitChars!!["ṣ"] = "s"
            translitChars!!["đ"] = "d"
            translitChars!!["ỗ"] = "o"
            translitChars!!["ɟ"] = "j"
            translitChars!!["ẚ"] = "a"
            translitChars!!["ɏ"] = "y"
            translitChars!!["л"] = "l"
            translitChars!!["ʌ"] = "v"
            translitChars!!["ꝓ"] = "p"
            translitChars!!["ﬁ"] = "fi"
            translitChars!!["ᶄ"] = "k"
            translitChars!!["ḏ"] = "d"
            translitChars!!["ᴌ"] = "l"
            translitChars!!["ė"] = "e"
            translitChars!!["ё"] = "yo"
            translitChars!!["ᴋ"] = "k"
            translitChars!!["ċ"] = "c"
            translitChars!!["ʁ"] = "r"
            translitChars!!["ƕ"] = "hv"
            translitChars!!["ƀ"] = "b"
            translitChars!!["ṍ"] = "o"
            translitChars!!["ȣ"] = "ou"
            translitChars!!["ǰ"] = "j"
            translitChars!!["ᶃ"] = "g"
            translitChars!!["ṋ"] = "n"
            translitChars!!["ɉ"] = "j"
            translitChars!!["ǧ"] = "g"
            translitChars!!["ǳ"] = "dz"
            translitChars!!["ź"] = "z"
            translitChars!!["ꜷ"] = "au"
            translitChars!!["ǖ"] = "u"
            translitChars!!["ᵹ"] = "g"
            translitChars!!["ȯ"] = "o"
            translitChars!!["ɐ"] = "a"
            translitChars!!["ą"] = "a"
            translitChars!!["õ"] = "o"
            translitChars!!["ɻ"] = "r"
            translitChars!!["ꝍ"] = "o"
            translitChars!!["ǟ"] = "a"
            translitChars!!["ȴ"] = "l"
            translitChars!!["ʂ"] = "s"
            translitChars!!["ﬂ"] = "fl"
            translitChars!!["ȉ"] = "i"
            translitChars!!["ⱻ"] = "e"
            translitChars!!["ṉ"] = "n"
            translitChars!!["ï"] = "i"
            translitChars!!["ñ"] = "n"
            translitChars!!["ᴉ"] = "i"
            translitChars!!["ʇ"] = "t"
            translitChars!!["ẓ"] = "z"
            translitChars!!["ỷ"] = "y"
            translitChars!!["ȳ"] = "y"
            translitChars!!["ṩ"] = "s"
            translitChars!!["ɽ"] = "r"
            translitChars!!["ĝ"] = "g"
            translitChars!!["в"] = "v"
            translitChars!!["ᴝ"] = "u"
            translitChars!!["ḳ"] = "k"
            translitChars!!["ꝫ"] = "et"
            translitChars!!["ī"] = "i"
            translitChars!!["ť"] = "t"
            translitChars!!["ꜿ"] = "c"
            translitChars!!["ʟ"] = "l"
            translitChars!!["ꜹ"] = "av"
            translitChars!!["û"] = "u"
            translitChars!!["æ"] = "ae"
            translitChars!!["и"] = "i"
            translitChars!!["ă"] = "a"
            translitChars!!["ǘ"] = "u"
            translitChars!!["ꞅ"] = "s"
            translitChars!!["ᵣ"] = "r"
            translitChars!!["ᴀ"] = "a"
            translitChars!!["ƃ"] = "b"
            translitChars!!["ḩ"] = "h"
            translitChars!!["ṧ"] = "s"
            translitChars!!["ₑ"] = "e"
            translitChars!!["ʜ"] = "h"
            translitChars!!["ẋ"] = "x"
            translitChars!!["ꝅ"] = "k"
            translitChars!!["ḋ"] = "d"
            translitChars!!["ƣ"] = "oi"
            translitChars!!["ꝑ"] = "p"
            translitChars!!["ħ"] = "h"
            translitChars!!["ⱴ"] = "v"
            translitChars!!["ẇ"] = "w"
            translitChars!!["ǹ"] = "n"
            translitChars!!["ɯ"] = "m"
            translitChars!!["ɡ"] = "g"
            translitChars!!["ɴ"] = "n"
            translitChars!!["ᴘ"] = "p"
            translitChars!!["ᵥ"] = "v"
            translitChars!!["ū"] = "u"
            translitChars!!["ḃ"] = "b"
            translitChars!!["ṗ"] = "p"
            translitChars!!["ь"] = ""
            translitChars!!["å"] = "a"
            translitChars!!["ɕ"] = "c"
            translitChars!!["ọ"] = "o"
            translitChars!!["ắ"] = "a"
            translitChars!!["ƒ"] = "f"
            translitChars!!["ǣ"] = "ae"
            translitChars!!["ꝡ"] = "vy"
            translitChars!!["ﬀ"] = "ff"
            translitChars!!["ᶉ"] = "r"
            translitChars!!["ô"] = "o"
            translitChars!!["ǿ"] = "o"
            translitChars!!["ṳ"] = "u"
            translitChars!!["ȥ"] = "z"
            translitChars!!["ḟ"] = "f"
            translitChars!!["ḓ"] = "d"
            translitChars!!["ȇ"] = "e"
            translitChars!!["ȕ"] = "u"
            translitChars!!["п"] = "p"
            translitChars!!["ȵ"] = "n"
            translitChars!!["ʠ"] = "q"
            translitChars!!["ấ"] = "a"
            translitChars!!["ǩ"] = "k"
            translitChars!!["ĩ"] = "i"
            translitChars!!["ṵ"] = "u"
            translitChars!!["ŧ"] = "t"
            translitChars!!["ɾ"] = "r"
            translitChars!!["ƙ"] = "k"
            translitChars!!["ṫ"] = "t"
            translitChars!!["ꝗ"] = "q"
            translitChars!!["ậ"] = "a"
            translitChars!!["н"] = "n"
            translitChars!!["ʄ"] = "j"
            translitChars!!["ƚ"] = "l"
            translitChars!!["ᶂ"] = "f"
            translitChars!!["д"] = "d"
            translitChars!!["ᵴ"] = "s"
            translitChars!!["ꞃ"] = "r"
            translitChars!!["ᶌ"] = "v"
            translitChars!!["ɵ"] = "o"
            translitChars!!["ḉ"] = "c"
            translitChars!!["ᵤ"] = "u"
            translitChars!!["ẑ"] = "z"
            translitChars!!["ṹ"] = "u"
            translitChars!!["ň"] = "n"
            translitChars!!["ʍ"] = "w"
            translitChars!!["ầ"] = "a"
            translitChars!!["ǉ"] = "lj"
            translitChars!!["ɓ"] = "b"
            translitChars!!["ɼ"] = "r"
            translitChars!!["ò"] = "o"
            translitChars!!["ẘ"] = "w"
            translitChars!!["ɗ"] = "d"
            translitChars!!["ꜽ"] = "ay"
            translitChars!!["ư"] = "u"
            translitChars!!["ᶀ"] = "b"
            translitChars!!["ǜ"] = "u"
            translitChars!!["ẹ"] = "e"
            translitChars!!["ǡ"] = "a"
            translitChars!!["ɥ"] = "h"
            translitChars!!["ṏ"] = "o"
            translitChars!!["ǔ"] = "u"
            translitChars!!["ʎ"] = "y"
            translitChars!!["ȱ"] = "o"
            translitChars!!["ệ"] = "e"
            translitChars!!["ế"] = "e"
            translitChars!!["ĭ"] = "i"
            translitChars!!["ⱸ"] = "e"
            translitChars!!["ṯ"] = "t"
            translitChars!!["ᶑ"] = "d"
            translitChars!!["ḧ"] = "h"
            translitChars!!["ṥ"] = "s"
            translitChars!!["ë"] = "e"
            translitChars!!["ᴍ"] = "m"
            translitChars!!["ö"] = "o"
            translitChars!!["é"] = "e"
            translitChars!!["ı"] = "i"
            translitChars!!["ď"] = "d"
            translitChars!!["ᵯ"] = "m"
            translitChars!!["ỵ"] = "y"
            translitChars!!["я"] = "ya"
            translitChars!!["ŵ"] = "w"
            translitChars!!["ề"] = "e"
            translitChars!!["ứ"] = "u"
            translitChars!!["ƶ"] = "z"
            translitChars!!["ĵ"] = "j"
            translitChars!!["ḍ"] = "d"
            translitChars!!["ŭ"] = "u"
            translitChars!!["ʝ"] = "j"
            translitChars!!["ж"] = "zh"
            translitChars!!["ê"] = "e"
            translitChars!!["ǚ"] = "u"
            translitChars!!["ġ"] = "g"
            translitChars!!["ṙ"] = "r"
            translitChars!!["ƞ"] = "n"
            translitChars!!["ъ"] = ""
            translitChars!!["ḗ"] = "e"
            translitChars!!["ẝ"] = "s"
            translitChars!!["ᶁ"] = "d"
            translitChars!!["ķ"] = "k"
            translitChars!!["ᴂ"] = "ae"
            translitChars!!["ɘ"] = "e"
            translitChars!!["ợ"] = "o"
            translitChars!!["ḿ"] = "m"
            translitChars!!["ꜰ"] = "f"
            translitChars!!["а"] = "a"
            translitChars!!["ẵ"] = "a"
            translitChars!!["ꝏ"] = "oo"
            translitChars!!["ᶆ"] = "m"
            translitChars!!["ᵽ"] = "p"
            translitChars!!["ц"] = "ts"
            translitChars!!["ữ"] = "u"
            translitChars!!["ⱪ"] = "k"
            translitChars!!["ḥ"] = "h"
            translitChars!!["ţ"] = "t"
            translitChars!!["ᵱ"] = "p"
            translitChars!!["ṁ"] = "m"
            translitChars!!["á"] = "a"
            translitChars!!["ᴎ"] = "n"
            translitChars!!["ꝟ"] = "v"
            translitChars!!["è"] = "e"
            translitChars!!["ᶎ"] = "z"
            translitChars!!["ꝺ"] = "d"
            translitChars!!["ᶈ"] = "p"
            translitChars!!["м"] = "m"
            translitChars!!["ɫ"] = "l"
            translitChars!!["ᴢ"] = "z"
            translitChars!!["ɱ"] = "m"
            translitChars!!["ṝ"] = "r"
            translitChars!!["ṽ"] = "v"
            translitChars!!["ũ"] = "u"
            translitChars!!["ß"] = "ss"
            translitChars!!["т"] = "t"
            translitChars!!["ĥ"] = "h"
            translitChars!!["ᵵ"] = "t"
            translitChars!!["ʐ"] = "z"
            translitChars!!["ṟ"] = "r"
            translitChars!!["ɲ"] = "n"
            translitChars!!["à"] = "a"
            translitChars!!["ẙ"] = "y"
            translitChars!!["ỳ"] = "y"
            translitChars!!["ᴔ"] = "oe"
            translitChars!!["ы"] = "i"
            translitChars!!["ₓ"] = "x"
            translitChars!!["ȗ"] = "u"
            translitChars!!["ⱼ"] = "j"
            translitChars!!["ẫ"] = "a"
            translitChars!!["ʑ"] = "z"
            translitChars!!["ẛ"] = "s"
            translitChars!!["ḭ"] = "i"
            translitChars!!["ꜵ"] = "ao"
            translitChars!!["ɀ"] = "z"
            translitChars!!["ÿ"] = "y"
            translitChars!!["ǝ"] = "e"
            translitChars!!["ǭ"] = "o"
            translitChars!!["ᴅ"] = "d"
            translitChars!!["ᶅ"] = "l"
            translitChars!!["ù"] = "u"
            translitChars!!["ạ"] = "a"
            translitChars!!["ḅ"] = "b"
            translitChars!!["ụ"] = "u"
            translitChars!!["к"] = "k"
            translitChars!!["ằ"] = "a"
            translitChars!!["ᴛ"] = "t"
            translitChars!!["ƴ"] = "y"
            translitChars!!["ⱦ"] = "t"
            translitChars!!["з"] = "z"
            translitChars!!["ⱡ"] = "l"
            translitChars!!["ȷ"] = "j"
            translitChars!!["ᵶ"] = "z"
            translitChars!!["ḫ"] = "h"
            translitChars!!["ⱳ"] = "w"
            translitChars!!["ḵ"] = "k"
            translitChars!!["ờ"] = "o"
            translitChars!!["î"] = "i"
            translitChars!!["ģ"] = "g"
            translitChars!!["ȅ"] = "e"
            translitChars!!["ȧ"] = "a"
            translitChars!!["ẳ"] = "a"
            translitChars!!["щ"] = "sch"
            translitChars!!["ɋ"] = "q"
            translitChars!!["ṭ"] = "t"
            translitChars!!["ꝸ"] = "um"
            translitChars!!["ᴄ"] = "c"
            translitChars!!["ẍ"] = "x"
            translitChars!!["ủ"] = "u"
            translitChars!!["ỉ"] = "i"
            translitChars!!["ᴚ"] = "r"
            translitChars!!["ś"] = "s"
            translitChars!!["ꝋ"] = "o"
            translitChars!!["ỹ"] = "y"
            translitChars!!["ṡ"] = "s"
            translitChars!!["ǌ"] = "nj"
            translitChars!!["ȁ"] = "a"
            translitChars!!["ẗ"] = "t"
            translitChars!!["ĺ"] = "l"
            translitChars!!["ž"] = "z"
            translitChars!!["ᵺ"] = "th"
            translitChars!!["ƌ"] = "d"
            translitChars!!["ș"] = "s"
            translitChars!!["š"] = "s"
            translitChars!!["ᶙ"] = "u"
            translitChars!!["ẽ"] = "e"
            translitChars!!["ẜ"] = "s"
            translitChars!!["ɇ"] = "e"
            translitChars!!["ṷ"] = "u"
            translitChars!!["ố"] = "o"
            translitChars!!["ȿ"] = "s"
            translitChars!!["ᴠ"] = "v"
            translitChars!!["ꝭ"] = "is"
            translitChars!!["ᴏ"] = "o"
            translitChars!!["ɛ"] = "e"
            translitChars!!["ǻ"] = "a"
            translitChars!!["ﬄ"] = "ffl"
            translitChars!!["ⱺ"] = "o"
            translitChars!!["ȋ"] = "i"
            translitChars!!["ᵫ"] = "ue"
            translitChars!!["ȡ"] = "d"
            translitChars!!["ⱬ"] = "z"
            translitChars!!["ẁ"] = "w"
            translitChars!!["ᶏ"] = "a"
            translitChars!!["ꞇ"] = "t"
            translitChars!!["ğ"] = "g"
            translitChars!!["ɳ"] = "n"
            translitChars!!["ʛ"] = "g"
            translitChars!!["ᴜ"] = "u"
            translitChars!!["ф"] = "f"
            translitChars!!["ẩ"] = "a"
            translitChars!!["ṅ"] = "n"
            translitChars!!["ɨ"] = "i"
            translitChars!!["ᴙ"] = "r"
            translitChars!!["ǎ"] = "a"
            translitChars!!["ſ"] = "s"
            translitChars!!["у"] = "u"
            translitChars!!["ȫ"] = "o"
            translitChars!!["ɿ"] = "r"
            translitChars!!["ƭ"] = "t"
            translitChars!!["ḯ"] = "i"
            translitChars!!["ǽ"] = "ae"
            translitChars!!["ⱱ"] = "v"
            translitChars!!["ɶ"] = "oe"
            translitChars!!["ṃ"] = "m"
            translitChars!!["ż"] = "z"
            translitChars!!["ĕ"] = "e"
            translitChars!!["ꜻ"] = "av"
            translitChars!!["ở"] = "o"
            translitChars!!["ễ"] = "e"
            translitChars!!["ɬ"] = "l"
            translitChars!!["ị"] = "i"
            translitChars!!["ᵭ"] = "d"
            translitChars!!["ﬆ"] = "st"
            translitChars!!["ḷ"] = "l"
            translitChars!!["ŕ"] = "r"
            translitChars!!["ᴕ"] = "ou"
            translitChars!!["ʈ"] = "t"
            translitChars!!["ā"] = "a"
            translitChars!!["э"] = "e"
            translitChars!!["ḙ"] = "e"
            translitChars!!["ᴑ"] = "o"
            translitChars!!["ç"] = "c"
            translitChars!!["ᶊ"] = "s"
            translitChars!!["ặ"] = "a"
            translitChars!!["ų"] = "u"
            translitChars!!["ả"] = "a"
            translitChars!!["ǥ"] = "g"
            translitChars!!["р"] = "r"
            translitChars!!["ꝁ"] = "k"
            translitChars!!["ẕ"] = "z"
            translitChars!!["ŝ"] = "s"
            translitChars!!["ḕ"] = "e"
            translitChars!!["ɠ"] = "g"
            translitChars!!["ꝉ"] = "l"
            translitChars!!["ꝼ"] = "f"
            translitChars!!["ᶍ"] = "x"
            translitChars!!["х"] = "h"
            translitChars!!["ǒ"] = "o"
            translitChars!!["ę"] = "e"
            translitChars!!["ổ"] = "o"
            translitChars!!["ƫ"] = "t"
            translitChars!!["ǫ"] = "o"
            translitChars!!["i̇"] = "i"
            translitChars!!["ṇ"] = "n"
            translitChars!!["ć"] = "c"
            translitChars!!["ᵷ"] = "g"
            translitChars!!["ẅ"] = "w"
            translitChars!!["ḑ"] = "d"
            translitChars!!["ḹ"] = "l"
            translitChars!!["ч"] = "ch"
            translitChars!!["œ"] = "oe"
            translitChars!!["ᵳ"] = "r"
            translitChars!!["ļ"] = "l"
            translitChars!!["ȑ"] = "r"
            translitChars!!["ȭ"] = "o"
            translitChars!!["ᵰ"] = "n"
            translitChars!!["ᴁ"] = "ae"
            translitChars!!["ŀ"] = "l"
            translitChars!!["ä"] = "a"
            translitChars!!["ƥ"] = "p"
            translitChars!!["ỏ"] = "o"
            translitChars!!["į"] = "i"
            translitChars!!["ȓ"] = "r"
            translitChars!!["ǆ"] = "dz"
            translitChars!!["ḡ"] = "g"
            translitChars!!["ṻ"] = "u"
            translitChars!!["ō"] = "o"
            translitChars!!["ľ"] = "l"
            translitChars!!["ẃ"] = "w"
            translitChars!!["ț"] = "t"
            translitChars!!["ń"] = "n"
            translitChars!!["ɍ"] = "r"
            translitChars!!["ȃ"] = "a"
            translitChars!!["ü"] = "u"
            translitChars!!["ꞁ"] = "l"
            translitChars!!["ᴐ"] = "o"
            translitChars!!["ớ"] = "o"
            translitChars!!["ᴃ"] = "b"
            translitChars!!["ɹ"] = "r"
            translitChars!!["ᵲ"] = "r"
            translitChars!!["ʏ"] = "y"
            translitChars!!["ᵮ"] = "f"
            translitChars!!["ⱨ"] = "h"
            translitChars!!["ŏ"] = "o"
            translitChars!!["ú"] = "u"
            translitChars!!["ṛ"] = "r"
            translitChars!!["ʮ"] = "h"
            translitChars!!["ó"] = "o"
            translitChars!!["ů"] = "u"
            translitChars!!["ỡ"] = "o"
            translitChars!!["ṕ"] = "p"
            translitChars!!["ᶖ"] = "i"
            translitChars!!["ự"] = "u"
            translitChars!!["ã"] = "a"
            translitChars!!["ᵢ"] = "i"
            translitChars!!["ṱ"] = "t"
            translitChars!!["ể"] = "e"
            translitChars!!["ử"] = "u"
            translitChars!!["í"] = "i"
            translitChars!!["ɔ"] = "o"
            translitChars!!["с"] = "s"
            translitChars!!["й"] = "i"
            translitChars!!["ɺ"] = "r"
            translitChars!!["ɢ"] = "g"
            translitChars!!["ř"] = "r"
            translitChars!!["ẖ"] = "h"
            translitChars!!["ű"] = "u"
            translitChars!!["ȍ"] = "o"
            translitChars!!["ш"] = "sh"
            translitChars!!["ḻ"] = "l"
            translitChars!!["ḣ"] = "h"
            translitChars!!["ȶ"] = "t"
            translitChars!!["ņ"] = "n"
            translitChars!!["ᶒ"] = "e"
            translitChars!!["ì"] = "i"
            translitChars!!["ẉ"] = "w"
            translitChars!!["б"] = "b"
            translitChars!!["ē"] = "e"
            translitChars!!["ᴇ"] = "e"
            translitChars!!["ł"] = "l"
            translitChars!!["ộ"] = "o"
            translitChars!!["ɭ"] = "l"
            translitChars!!["ẏ"] = "y"
            translitChars!!["ᴊ"] = "j"
            translitChars!!["ḱ"] = "k"
            translitChars!!["ṿ"] = "v"
            translitChars!!["ȩ"] = "e"
            translitChars!!["â"] = "a"
            translitChars!!["ş"] = "s"
            translitChars!!["ŗ"] = "r"
            translitChars!!["ʋ"] = "v"
            translitChars!!["ₐ"] = "a"
            translitChars!!["ↄ"] = "c"
            translitChars!!["ᶓ"] = "e"
            translitChars!!["ɰ"] = "m"
            translitChars!!["е"] = "e"
            translitChars!!["ᴡ"] = "w"
            translitChars!!["ȏ"] = "o"
            translitChars!!["č"] = "c"
            translitChars!!["ǵ"] = "g"
            translitChars!!["ĉ"] = "c"
            translitChars!!["ю"] = "yu"
            translitChars!!["ᶗ"] = "o"
            translitChars!!["ꝃ"] = "k"
            translitChars!!["ꝙ"] = "q"
            translitChars!!["г"] = "g"
            translitChars!!["ṑ"] = "o"
            translitChars!!["ꜱ"] = "s"
            translitChars!!["ṓ"] = "o"
            translitChars!!["ȟ"] = "h"
            translitChars!!["ő"] = "o"
            translitChars!!["ꜩ"] = "tz"
            translitChars!!["ẻ"] = "e"
            translitChars!!["о"] = "o"
        }
        val dst = StringBuilder(src.length)
        val len = src.length
        for (a in 0 until len) {
            val ch = src.substring(a, a + 1)
            val tch = translitChars!![ch]
            if (tch != null) {
                dst.append(tch)
            } else {
                dst.append(ch)
            }
        }
        return dst.toString()
    }

    fun getCurrentLocaleInfo(): LocaleInfo {
        return currentLocaleInfeo!!
    }

    abstract class PluralRules {
        abstract fun quantityForNumber(n: Int): Int
    }

    class PluralRules_Zero : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 0 || count == 1) {
                QUANTITY_ONE
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Welsh : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 0) {
                QUANTITY_ZERO
            } else if (count == 1) {
                QUANTITY_ONE
            } else if (count == 2) {
                QUANTITY_TWO
            } else if (count == 3) {
                QUANTITY_FEW
            } else if (count == 6) {
                QUANTITY_MANY
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Two : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 1) {
                QUANTITY_ONE
            } else if (count == 2) {
                QUANTITY_TWO
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Tachelhit : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count >= 0 && count <= 1) {
                QUANTITY_ONE
            } else if (count >= 2 && count <= 10) {
                QUANTITY_FEW
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Slovenian : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            val rem100 = count % 100
            return if (rem100 == 1) {
                QUANTITY_ONE
            } else if (rem100 == 2) {
                QUANTITY_TWO
            } else if (rem100 >= 3 && rem100 <= 4) {
                QUANTITY_FEW
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Romanian : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            val rem100 = count % 100
            return if (count == 1) {
                QUANTITY_ONE
            } else if (count == 0 || rem100 >= 1 && rem100 <= 19) {
                QUANTITY_FEW
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Polish : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            val rem100 = count % 100
            val rem10 = count % 10
            return if (count == 1) {
                QUANTITY_ONE
            } else if (rem10 >= 2 && rem10 <= 4 && !(rem100 >= 12 && rem100 <= 14) && !(rem100 >= 22 && rem100 <= 24)) {
                QUANTITY_FEW
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_One : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 1) QUANTITY_ONE else QUANTITY_OTHER
        }
    }

    class PluralRules_None : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return QUANTITY_OTHER
        }
    }

    class PluralRules_Maltese : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            val rem100 = count % 100
            return if (count == 1) {
                QUANTITY_ONE
            } else if (count == 0 || rem100 >= 2 && rem100 <= 10) {
                QUANTITY_FEW
            } else if (rem100 >= 11 && rem100 <= 19) {
                QUANTITY_MANY
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Macedonian : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count % 10 == 1 && count != 11) {
                QUANTITY_ONE
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Lithuanian : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            val rem100 = count % 100
            val rem10 = count % 10
            return if (rem10 == 1 && !(rem100 >= 11 && rem100 <= 19)) {
                QUANTITY_ONE
            } else if (rem10 >= 2 && rem10 <= 9 && !(rem100 >= 11 && rem100 <= 19)) {
                QUANTITY_FEW
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Latvian : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 0) {
                QUANTITY_ZERO
            } else if (count % 10 == 1 && count % 100 != 11) {
                QUANTITY_ONE
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Langi : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 0) {
                QUANTITY_ZERO
            } else if (count > 0 && count < 2) {
                QUANTITY_ONE
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_French : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count >= 0 && count < 2) {
                QUANTITY_ONE
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Czech : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 1) {
                QUANTITY_ONE
            } else if (count >= 2 && count <= 4) {
                QUANTITY_FEW
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Breton : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            return if (count == 0) {
                QUANTITY_ZERO
            } else if (count == 1) {
                QUANTITY_ONE
            } else if (count == 2) {
                QUANTITY_TWO
            } else if (count == 3) {
                QUANTITY_FEW
            } else if (count == 6) {
                QUANTITY_MANY
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Balkan : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            val rem100 = count % 100
            val rem10 = count % 10
            return if (rem10 == 1 && rem100 != 11) {
                QUANTITY_ONE
            } else if (rem10 >= 2 && rem10 <= 4 && !(rem100 >= 12 && rem100 <= 14)) {
                QUANTITY_FEW
            } else if (rem10 == 0 || rem10 >= 5 && rem10 <= 9 || rem100 >= 11 && rem100 <= 14) {
                QUANTITY_MANY
            } else {
                QUANTITY_OTHER
            }
        }
    }

    class PluralRules_Arabic : PluralRules() {
        override fun quantityForNumber(count: Int): Int {
            val rem100 = count % 100
            return if (count == 0) {
                QUANTITY_ZERO
            } else if (count == 1) {
                QUANTITY_ONE
            } else if (count == 2) {
                QUANTITY_TWO
            } else if (rem100 >= 3 && rem100 <= 10) {
                QUANTITY_FEW
            } else if (rem100 >= 11 && rem100 <= 99) {
                QUANTITY_MANY
            } else {
                QUANTITY_OTHER
            }
        }
    }

    companion object {
        const val QUANTITY_OTHER = 0x0000
        const val QUANTITY_ZERO = 0x0001
        const val QUANTITY_ONE = 0x0002
        const val QUANTITY_TWO = 0x0004
        const val QUANTITY_FEW = 0x0008
        const val QUANTITY_MANY = 0x0010
        var isRTL = false
        var nameDisplayOrder = 1
        private var is24HourFormat = false
        var Instanc: LocaleController? = null

        fun getInstance(): LocaleController {
            var localInstanc = Instanc
            if (localInstanc == null) {
                synchronized(LocaleController::class.java) {
                    localInstanc = Instanc
                    if (localInstanc == null) {
                        localInstanc = LocaleController()
                        Instanc = localInstanc
                    }
                }
            }
            return localInstanc!!
        }
        /*fun getSystemDefaultLocale(): Locale? {
            return systemDefaultLocale
        }
        val systemLocaleStringIso639: String
            get() {
//                val locale: Locale = getInstance().getSystemDefaultLocale() ?: return "en"
                val languageCode = locale.language
                val countryCode = locale.country
                val variantCode = locale.variant
                if (languageCode.length == 0 && countryCode.length == 0) {
                    return "en"
                }
                val result = StringBuilder(11)
                result.append(languageCode)
                if (countryCode.length > 0 || variantCode.length > 0) {
                    result.append('-')
                }
                result.append(countryCode)
                if (variantCode.length > 0) {
                    result.append('_')
                }
                result.append(variantCode)
                return result.toString()
            }*/

        val localeStringIso639: String
            get() {
                val locale: Locale = getInstance().currentLocale ?: return "en"
                val languageCode = locale.language
                val countryCode = locale.country
                val variantCode = locale.variant
                if (languageCode.length == 0 && countryCode.length == 0) {
                    return "en"
                }
                val result = StringBuilder(11)
                result.append(languageCode)
                if (countryCode.length > 0 || variantCode.length > 0) {
                    result.append('-')
                }
                result.append(countryCode)
                if (variantCode.length > 0) {
                    result.append('_')
                }
                result.append(variantCode)
                return result.toString()
            }

        fun getLocaleAlias(code: String?): String? {
            if (code == null) {
                return null
            }
            when (code) {
                "in" -> return "id"
                "iw" -> return "he"
                "jw" -> return "jv"
                "no" -> return "nb"
                "tl" -> return "fil"
                "ji" -> return "yi"
                "id" -> return "in"
                "he" -> return "iw"
                "jv" -> return "jw"
                "nb" -> return "no"
                "fil" -> return "tl"
                "yi" -> return "ji"
            }
            return null
        }

        val currentLanguageName: String
            get() = getString("LanguageName", R.string.LanguageName)

        fun getString(key: String, res: Int): String {
            return getInstance().getStringInternal(key, res)
        }

        fun getStringResourceByName(aString: String): String {
            val packageName: String = LaunchActivity.applicationContext.packageName
            val resId: Int = LaunchActivity.applicationContext.resources.getIdentifier(aString, "string", packageName)
            return getString(aString, resId)
        }

        fun getPluralString(key: String?, plural: Int): String {
            if (key == null || key.length == 0 || getInstance().currentPluralRules == null) {
                return "LOC_ERR:$key"
            }
            var param: String = getInstance().stringForQuantity(getInstance().currentPluralRules!!.quantityForNumber(plural))
            param = key + "_" + param
            val resourceId: Int = LaunchActivity.applicationContext.resources.getIdentifier(param, "string", LaunchActivity.applicationContext.packageName)
            return getString(param, resourceId)
        }

        fun formatPluralString(key: String?, plural: Int): String {
            if (key == null || key.length == 0 || getInstance().currentPluralRules == null) {
                return "LOC_ERR:$key"
            }
            var param: String = getInstance().stringForQuantity(getInstance().currentPluralRules!!.quantityForNumber(plural))
            param = key + "_" + param
            val resourceId: Int = LaunchActivity.applicationContext.resources.getIdentifier(param, "string", LaunchActivity.applicationContext.packageName)
            return formatString(param, resourceId, plural)
        }

        fun formatString(key: String, res: Int, vararg args: Any?): String {
            return try {
                var value: String = getInstance().localeValues.get(key)!!
                if (value == null) {
                    value = LaunchActivity.applicationContext.getString(res)
                }
                if (getInstance().currentLocale != null) {
                    String.format(getInstance().currentLocale!!, value, *args)
                } else {
                    String.format(value, *args)
                }
            } catch (e: Exception) {
                "LOC_ERR: $key"
            }
        }

        fun formatTTLString(ttl: Int): String {
            return if (ttl < 60) {
                formatPluralString("Seconds", ttl)
            } else if (ttl < 60 * 60) {
                formatPluralString("Minutes", ttl / 60)
            } else if (ttl < 60 * 60 * 24) {
                formatPluralString("Hours", ttl / 60 / 60)
            } else if (ttl < 60 * 60 * 24 * 7) {
                formatPluralString("Days", ttl / 60 / 60 / 24)
            } else {
                val days = ttl / 60 / 60 / 24
                if (ttl % 7 == 0) {
                    formatPluralString("Weeks", days / 7)
                } else {
                    String.format("%s %s", formatPluralString("Weeks", days / 7), formatPluralString("Days", days % 7))
                }
            }
        }

        fun formatStringSimple(string: String, vararg args: Any?): String {
            return try {
                if (getInstance().currentLocale != null) {
                    String.format(getInstance().currentLocale!!, string, *args)
                } else {
                    String.format(string, *args)
                }
            } catch (e: Exception) {
                "LOC_ERR: $string"
            }
        }

        fun formatCallDuration(duration: Int): String {
            return if (duration > 3600) {
                var result = formatPluralString("Hours", duration / 3600)
                val minutes = duration % 3600 / 60
                if (minutes > 0) {
                    result += ", " + formatPluralString("Minutes", minutes)
                }
                result
            } else if (duration > 60) {
                formatPluralString("Minutes", duration / 60)
            } else {
                formatPluralString("Seconds", duration)
            }
        }

        fun formatDateChat(date: Long): String {
            var date = date
            try {
                val rightNow = Calendar.getInstance()
                date *= 1000
                rightNow.timeInMillis = date
                return if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                    getInstance().chatDate!!.format(date)
                } else getInstance().chatFullDate!!.format(date)
            } catch (e: Exception) {
            }
            return "LOC_ERR: formatDateChat"
        }

        fun formatDate(date: Long): String {
            var date = date
            try {
                date *= 1000
                val rightNow = Calendar.getInstance()
                val day = rightNow[Calendar.DAY_OF_YEAR]
                val year = rightNow[Calendar.YEAR]
                rightNow.timeInMillis = date
                val dateDay = rightNow[Calendar.DAY_OF_YEAR]
                val dateYear = rightNow[Calendar.YEAR]
                return if (dateDay == day && year == dateYear) {
                    getInstance().formatterDay!!.format(Date(date))
                } else if (dateDay + 1 == day && year == dateYear) {
                    getString("Yesterday", R.string.Yesterday)
                } else if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                    getInstance().formatterMonth!!.format(Date(date))
                } else {
                    getInstance().formatterYear!!.format(Date(date))
                }
            } catch (e: Exception) {
            }
            return "LOC_ERR: formatDate"
        }

        fun formatLocationLeftTime(time: Int): String {
            var time = time
            val text: String
            val hours = time / 60 / 60
            time -= hours * 60 * 60
            val minutes = time / 60
            time -= minutes * 60
            text = if (hours != 0) {
                String.format("%dh", hours + if (minutes > 30) 1 else 0)
            } else if (minutes != 0) {
                String.format("%d", minutes + if (time > 30) 1 else 0)
            } else {
                String.format("%d", time)
            }
            return text
        }

        fun isRTLCharacter(ch: Char): Boolean {
            return Character.getDirectionality(ch) == Character.DIRECTIONALITY_RIGHT_TO_LEFT || Character.getDirectionality(ch) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC || Character.getDirectionality(ch) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING || Character.getDirectionality(ch) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
        }

        fun formatDateForBan(date: Long): String {
            var date = date
            try {
                date *= 1000
                val rightNow = Calendar.getInstance()
                val year = rightNow[Calendar.YEAR]
                rightNow.timeInMillis = date
                val dateYear = rightNow[Calendar.YEAR]
                return if (year == dateYear) {
                    getInstance().formatterBannedUntilThisYear!!.format(Date(date))
                } else {
                    getInstance().formatterBannedUntil!!.format(Date(date))
                }
            } catch (e: Exception) {
            }
            return "LOC_ERR"
        }

        fun stringForMessageListDate(date: Long): String {
            var date = date
            try {
                date *= 1000
                val rightNow = Calendar.getInstance()
                val day = rightNow[Calendar.DAY_OF_YEAR]
                rightNow.timeInMillis = date
                val dateDay = rightNow[Calendar.DAY_OF_YEAR]
                return if (Math.abs(System.currentTimeMillis() - date) >= 31536000000L) {
                    getInstance().formatterYear!!.format(Date(date))
                } else {
                    val dayDiff = dateDay - day
                    if (dayDiff == 0 || dayDiff == -1 && System.currentTimeMillis() - date < 60 * 60 * 8 * 1000) {
                        getInstance().formatterDay!!.format(Date(date))
                    } else if (dayDiff > -7 && dayDiff <= -1) {
                        getInstance().formatterWeek!!.format(Date(date))
                    } else {
                        getInstance().formatterMonth!!.format(Date(date))
                    }
                }
            } catch (e: Exception) {
            }
            return "LOC_ERR"
        }

        fun formatShortNumber(number: Int, rounded: IntArray?): String {
            var number = number
            val K = StringBuilder()
            var lastDec = 0
            val KCount = 0
            while (number / 1000 > 0) {
                K.append("K")
                lastDec = number % 1000 / 100
                number /= 1000
            }
            if (rounded != null) {
                var value = number + lastDec / 10.0
                for (a in 0 until K.length) {
                    value *= 1000.0
                }
                rounded[0] = value.toInt()
            }
            if (lastDec != 0 && K.length > 0) {
                return if (K.length == 2) {
                    String.format(Locale.US, "%d.%dM", number, lastDec)
                } else {
                    String.format(Locale.US, "%d.%d%s", number, lastDec, K.toString())
                }
            }
            return if (K.length == 2) {
                String.format(Locale.US, "%dM", number)
            } else {
                String.format(Locale.US, "%d%s", number, K.toString())
            }
        }
    }

    init {
        addRules(arrayOf("bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb",
                "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku",
                "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te",
                "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast"), PluralRules_One())
        addRules(arrayOf("cs", "sk"), PluralRules_Czech())
        addRules(arrayOf("ff", "fr", "kab"), PluralRules_French())
        addRules(arrayOf("hr", "ru", "sr", "uk", "be", "bs", "sh"), PluralRules_Balkan())
        addRules(arrayOf("lv"), PluralRules_Latvian())
        addRules(arrayOf("lt"), PluralRules_Lithuanian())
        addRules(arrayOf("pl"), PluralRules_Polish())
        addRules(arrayOf("ro", "mo"), PluralRules_Romanian())
        addRules(arrayOf("sl"), PluralRules_Slovenian())
        addRules(arrayOf("ar"), PluralRules_Arabic())
        addRules(arrayOf("mk"), PluralRules_Macedonian())
        addRules(arrayOf("cy"), PluralRules_Welsh())
        addRules(arrayOf("br"), PluralRules_Breton())
        addRules(arrayOf("lag"), PluralRules_Langi())
        addRules(arrayOf("shi"), PluralRules_Tachelhit())
        addRules(arrayOf("mt"), PluralRules_Maltese())
        addRules(arrayOf("ga", "se", "sma", "smi", "smj", "smn", "sms"), PluralRules_Two())
        addRules(arrayOf("ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"), PluralRules_Zero())
        addRules(arrayOf("az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to",
                "tr", "vi", "wo", "yo", "zh", "bo", "dz", "id", "jv", "jw", "ka", "km", "kn", "ms", "th", "in"), PluralRules_None())
        var localeInfo = LocaleInfo()
        localeInfo.name = "English"
        localeInfo.nameEnglish = "English"
        localeInfo.shortName = "en"
        localeInfo.pathToFile = null
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo

        val fa_localeInfo = LocaleInfo()
        fa_localeInfo.name = "فارسی"
        fa_localeInfo.nameEnglish = "Persian"
        fa_localeInfo.shortName = "fa"
        fa_localeInfo.pathToFile = null
        fa_localeInfo.isBuiltIn = true
        languages.add(fa_localeInfo)
        languagesDict[fa_localeInfo.shortName] = fa_localeInfo
/*
        localeInfo = LocaleInfo()
        localeInfo.name = "Italiano"
        localeInfo.nameEnglish = "Italian"
        localeInfo.shortName = "it"
        localeInfo.pathToFile = null
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo
        localeInfo = LocaleInfo()
        localeInfo.name = "Español"
        localeInfo.nameEnglish = "Spanish"
        localeInfo.shortName = "es"
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo
        localeInfo = LocaleInfo()
        localeInfo.name = "Deutsch"
        localeInfo.nameEnglish = "German"
        localeInfo.shortName = "de"
        localeInfo.pathToFile = null
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo

        localeInfo = LocaleInfo()
        localeInfo.name = "Nederlands"
        localeInfo.nameEnglish = "Dutch"
        localeInfo.shortName = "nl"
        localeInfo.pathToFile = null
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo*/

        localeInfo = LocaleInfo()
        localeInfo.name = "العربية"
        localeInfo.nameEnglish = "Arabic"
        localeInfo.shortName = "ar"
        localeInfo.pathToFile = null
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo
/*
        localeInfo = LocaleInfo()
        localeInfo.name = "Português (Brasil)"
        localeInfo.nameEnglish = "Portuguese (Brazil)"
        localeInfo.shortName = "pt_br"
        localeInfo.pathToFile = null
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo*/

        localeInfo = LocaleInfo()
        localeInfo.name = "한국어"
        localeInfo.nameEnglish = "Korean"
        localeInfo.shortName = "ko"
        localeInfo.pathToFile = null
        localeInfo.isBuiltIn = true
        languages.add(localeInfo)
        languagesDict[localeInfo.shortName] = localeInfo

        loadOtherLanguages()
        for (a in otherLanguages.indices) {
            val locale = otherLanguages[a]
            languages.add(locale)
            languagesDict[locale.key] = locale
        }
        for (a in remoteLanguages.indices) {
            val locale = remoteLanguages[a]
            val existingLocale = getLanguageFromDict(locale.key)
            if (existingLocale != null) {
                existingLocale.pathToFile = locale.pathToFile
                existingLocale.version = locale.version
            } else {
                languages.add(locale)
                languagesDict[locale.key] = locale
            }
        }
        systemDefaultLocale = Locale.getDefault()
        is24HourFormat = DateFormat.is24HourFormat(LaunchActivity.applicationContext)
        var currentInfo: LocaleInfo? = null
        var override = false
        try {
            val preferences = LaunchActivity.sharedPreferences!!
            val editor = preferences.edit()
            editor.putString(Constants.LANG_SHARED_KEY, /*"en"*/fa_localeInfo.key)
            editor.apply()
            val lang = preferences.getString(Constants.LANG_SHARED_KEY, null)
            if (lang != null) {
                currentInfo = getLanguageFromDict(lang)
                if (currentInfo != null) {
                    override = true
                }
            }
            if (currentInfo == null && systemDefaultLocale.language != null) {
                currentInfo = getLanguageFromDict(systemDefaultLocale.language)
            }
            if (currentInfo == null) {
                currentInfo = getLanguageFromDict(getLocaleString(systemDefaultLocale))
                if (currentInfo == null) {
                    currentInfo = getLanguageFromDict("en")
                }
            }
            applyLanguage(currentInfo, override, true)
        } catch (e: Exception) {
        }
        try {
            val timezoneFilter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            LaunchActivity.applicationContext.registerReceiver(TimeZoneChangedReceiver(), timezoneFilter)
        } catch (e: Exception) {
        }
    }
}