package by.carkva_gazeta.malitounik2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Divider,
    secondary = PrimaryText,
    onSecondary = PrimaryTextBlack,
    tertiary = PrimaryTextBlack,
    onTertiary = Primary,
    background = PrimaryTextBlack,
    /*onPrimary = PrimaryTextBlack,
    onPrimaryContainer = Primary,
    onTertiary = Divider*/
    /* Other default colors to override
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlack,
    onPrimary = BackgroundTolBarDark,
    secondary = PrimaryTextBlack,
    onSecondary = PrimaryTextBlack,
    tertiary = BackgroundTolBarDark,
    onTertiary = BackgroundTolBarDark,
    background = BackgroundDark,
    /*onPrimary = PrimaryTextBlack,
    onPrimaryContainer = Primary,
    onSecondary = PrimaryText,
    onTertiary = Divider*/
)

@Composable
fun MalitounikTheme(
    darkTheme : Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    //dynamicColor : Boolean = true,
    content : @Composable () -> Unit
) {
    val colorScheme = when {
        /*dynamicColor && Build.VERSION.SDK_INT>=Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    /*val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }*/
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}