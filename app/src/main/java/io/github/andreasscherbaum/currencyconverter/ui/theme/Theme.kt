package io.github.andreasscherbaum.currencyconverter.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EuroBlueLight,
    onPrimary = Color.White,
    primaryContainer = EuroBlueContainer,
    onPrimaryContainer = EuroBlueLight,
    secondary = EuroBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = EuroYellow,
    onSecondaryContainer = EuroBlueLight,
    tertiary = EuroYellow,
    background = Background,
    onBackground = Color.Black,
    surface = Surface,
    onSurface = Color.Black,
    surfaceVariant = CalculatorButton,
    onSurfaceVariant = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = EuroBlueDark,
    onPrimary = Color.Black,
    primaryContainer = EuroBlueContainerDark,
    onPrimaryContainer = EuroBlueDark,
    secondary = EuroBlueSecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = EuroYellowDark,
    onSecondaryContainer = Color.Black,
    tertiary = EuroYellowDark,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = CalculatorButtonDark,
    onSurfaceVariant = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun CurrencyConverterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
