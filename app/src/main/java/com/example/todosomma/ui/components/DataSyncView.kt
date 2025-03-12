package com.example.todosomma.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todosomma.R


@Composable
fun DataSyncView(isSyncing: Boolean, progress: Int, max: Int, onUpload: () -> Unit) {

    if (isSyncing) {
        SyncProgress(progress, max)
    } else {
        PendingUploadView(onUpload = onUpload)
    }
}

@Composable
internal fun SyncProgress(progress: Int, max: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(36.dp))
            .background(
                color = Color.Magenta,
                shape = RoundedCornerShape(36.dp)
            )
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp),
                trackColor = Color.White
            )
            Text(
                stringResource(R.string.syncing),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
        }
        Text("$progress/$max", fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
internal fun PendingUploadView(onUpload: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)

            .shadow(elevation = 16.dp, shape = RoundedCornerShape(36.dp))
            .background(
                color = Color.Magenta,
                shape = RoundedCornerShape(36.dp)
            )
            .padding(start = 16.dp, end = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            )
            Text(
                stringResource(R.string.some_tasks_are_pending_sync),
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

        }
        Button(
            onClick = onUpload,
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.LightGray
            )
        ) { Text(stringResource(R.string.sync), fontWeight = FontWeight.Bold) }
    }
}