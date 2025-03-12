package com.example.todosomma.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todosomma.R


@Composable
fun EmptyScreenPlaceHolderView(onCreateTap: () -> Unit, onDownloadTap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onDownloadTap()
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = "Download",
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                )
                Text(
                    text = stringResource(R.string.download_tasks),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .width(32.dp)
                        .padding(end = 8.dp)
                        .background(color = colorResource(R.color.white_70))
                )
                Text(
                    text = stringResource(R.string.or),
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )

                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .width(32.dp)
                        .padding(start = 8.dp)
                        .background(color = colorResource(R.color.white_70))
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onCreateTap()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Open",
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                )
                Text(
                    text = stringResource(R.string.start_adding_tasks),
                    fontSize = 16.sp,
                )
            }
        }
    }
}
