package com.amarchaud.ui.screen.detail.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarchaud.ui.screen.detail.UserDetailAction
import com.amarchaud.ui.screen.detail.models.UserDetailUiModel
import com.amarchaud.ui.theme.spacing
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import paginationdemomultiplatform.ui.generated.resources.Res
import paginationdemomultiplatform.ui.generated.resources.action_call
import paginationdemomultiplatform.ui.generated.resources.action_email
import paginationdemomultiplatform.ui.generated.resources.action_map
import paginationdemomultiplatform.ui.generated.resources.ic_mail
import paginationdemomultiplatform.ui.generated.resources.ic_phone
import paginationdemomultiplatform.ui.generated.resources.ic_place

/** Email / Call / Map shortcuts, disabled when the information is missing. */
@Composable
internal fun QuickActions(
    user: UserDetailUiModel,
    onAction: (UserDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxl, Alignment.CenterHorizontally),
    ) {
        QuickAction(
            icon = Res.drawable.ic_mail,
            label = stringResource(Res.string.action_email),
            enabled = user.emailUri.isNotEmpty(),
            onClick = { onAction(UserDetailAction.EmailClicked) },
        )
        QuickAction(
            icon = Res.drawable.ic_phone,
            label = stringResource(Res.string.action_call),
            enabled = user.phoneUri.isNotEmpty(),
            onClick = { onAction(UserDetailAction.PhoneClicked) },
        )
        QuickAction(
            icon = Res.drawable.ic_place,
            label = stringResource(Res.string.action_map),
            enabled = user.mapsUri.isNotEmpty(),
            onClick = { onAction(UserDetailAction.AddressClicked) },
        )
    }
}

@Composable
private fun QuickAction(
    icon: DrawableResource,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            modifier = Modifier.size(56.dp),
            onClick = onClick,
            enabled = enabled,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(icon),
                contentDescription = label,
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.s))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            },
        )
    }
}
