package com.aicodeassistant.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicodeassistant.domain.model.Permission
import com.aicodeassistant.domain.model.PermissionStatus
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun PermissionItem(
    permission: Permission,
    onRequest: (Permission) -> Unit
) {
    val statusColor = when (permission.status) {
        PermissionStatus.GRANTED -> AICodeAssistant.colorScheme.primary
        PermissionStatus.DENIED -> AICodeAssistant.colorScheme.error
        PermissionStatus.REQUESTED -> AICodeAssistant.colorScheme.warning
        PermissionStatus.PERMANENTLY_DENIED -> AICodeAssistant.colorScheme.onSurfaceVariant
    }

    val statusLabel = when (permission.status) {
        PermissionStatus.GRANTED -> "已授权"
        PermissionStatus.DENIED -> "已拒绝"
        PermissionStatus.REQUESTED -> "请求中"
        PermissionStatus.PERMANENTLY_DENIED -> "永久拒绝"
    }

    ListItem(
        modifier = Modifier.fillMaxWidth(),
        headContent = {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getPermissionIcon(permission.permissionName),
                    contentDescription = permission.displayName,
                    tint = AICodeAssistant.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = permission.displayName,
                        fontWeight = FontWeight.Medium,
                        color = AICodeAssistant.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .background(statusColor.copy(alpha = 0.15f))
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 10.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = permission.rationale,
                    fontSize = 12.sp,
                    color = AICodeAssistant.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingContent = {
            if (permission.status != PermissionStatus.GRANTED) {
                Button(onClick = { onRequest(permission) }) {
                    Text("请求权限")
                }
            } else {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Outlined.CheckCircle,
                    contentDescription = "已授权",
                    tint = AICodeAssistant.colorScheme.primary,
                    modifier = Modifier.size(24.dp).padding(16.dp)
                )
            }
        }
    )
}

fun getPermissionIcon(permissionName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (permissionName) {
        "android.permission.CAMERA" -> androidx.compose.material.icons.Icons.Outlined.CameraAlt
        "android.permission.RECORD_AUDIO" -> androidx.compose.material.icons.Icons.Outlined.Mic
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION" -> androidx.compose.material.icons.Icons.Outlined.LocationOn
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS" -> androidx.compose.material.icons.Icons.Outlined.Contacts
        "android.permission.SEND_SMS",
        "android.permission.READ_SMS" -> androidx.compose.material.icons.Icons.Outlined.Message
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE" -> androidx.compose.material.icons.Icons.Outlined.Folder
        "android.permission.POST_NOTIFICATIONS" -> androidx.compose.material.icons.Icons.Outlined.Notifications
        else -> androidx.compose.material.icons.Icons.Outlined.Security
    }
}
