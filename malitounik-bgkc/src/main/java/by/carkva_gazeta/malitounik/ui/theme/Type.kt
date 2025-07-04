package by.carkva_gazeta.malitounik.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import by.carkva_gazeta.malitounik.R

// Set of Material typography styles to start with
/*val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)*/
val displayFontFamily = FontFamily(
    Font(R.font.roboto_condensed_regular),
    Font(R.font.roboto_condensed_bold, weight = FontWeight.Bold),
    Font(R.font.roboto_condensed_italic, style = FontStyle.Italic),
    Font(R.font.roboto_condensed_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic)
)
val baseline = Typography()

/*fun getPersonalizedTypography(fontSize: Float): Typography {
    val fontSizePrefs = fontSize - 18
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = displayFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (18 + fontSizePrefs).sp,
            lineHeight = ((18 + fontSizePrefs) * 1.15).sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = displayFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (22 + fontSizePrefs).sp,
            lineHeight = ((22 + fontSizePrefs) * 1.15).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = displayFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 + fontSizePrefs).sp,
            lineHeight = ((18 + fontSizePrefs) * 1.15).sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = displayFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (12 + fontSizePrefs).sp,
            lineHeight = ((12 + fontSizePrefs) * 1.15).sp,
            letterSpacing = 0.5.sp
        ),
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = displayFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = displayFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = displayFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = displayFontFamily)
    )
}*/

/*bodyLarge = TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = Settings.fontInterface.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )*/

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = displayFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = displayFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = displayFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = displayFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = displayFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = displayFontFamily)
)
/* val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

)*/