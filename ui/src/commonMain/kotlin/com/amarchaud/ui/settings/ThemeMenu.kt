package com.amarchaud.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import paginationdemomultiplatform.ui.generated.resources.Res
import paginationdemomultiplatform.ui.generated.resources.ic_palette
import paginationdemomultiplatform.ui.generated.resources.material_you
import paginationdemomultiplatform.ui.generated.resources.theme_options

/** Palette icon of the top bar, opening a menu with the "Material You" option. */
@Composable
fun ThemeMenu(
    materialYouEnabled: Boolean,
    onMaterialYouToggled: (Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(Res.drawable.ic_palette),
                contentDescription = stringResource(Res.string.theme_options),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.material_you),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                leadingIcon = {
                    // the whole row is clickable: the checkbox only reflects the state
                    Checkbox(checked = materialYouEnabled, onCheckedChange = null)
                },
                onClick = {
                    onMaterialYouToggled(!materialYouEnabled)
                    expanded = false
                },
            )
        }
    }
}
