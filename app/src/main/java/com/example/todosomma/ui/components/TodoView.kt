package com.example.todosomma.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todosomma.R
import com.example.todosomma.data.model.Todo
import com.example.todosomma.util.DateUtil
import java.util.Calendar


@Composable
fun TodoView(
    todo: Todo?,
    onCreateOrUpdate: (todoId: String?, title: String, description: String?, dueDate: Long) -> Unit,
    onDelete: (todo: Todo) -> Unit,
    onCancel: () -> Unit
) {

    val header =
        if (todo != null) stringResource(R.string.update_task) else stringResource(R.string.create_new_task)
    val actionLabel =
        if (todo != null) stringResource(R.string.update) else stringResource(R.string.create)

    val initialTitle = todo?.title ?: ""
    var title by rememberSaveable { mutableStateOf(initialTitle) }

    val initialDescription = todo?.description ?: ""
    var description by rememberSaveable { mutableStateOf(initialDescription) }

    val initDateTimeMillis = todo?.dueDate ?: 0
    var dueDateTimeInMillis by rememberSaveable { mutableLongStateOf(initDateTimeMillis) }


    var isTitleError by remember { mutableStateOf(false) }
    var titleErrorMessage: String? by remember { mutableStateOf(null) }
    var isDescError by remember { mutableStateOf(false) }
    var descErrorMessage: String? by remember { mutableStateOf(null) }

    var dateTimeErrorMessage: String? by remember { mutableStateOf(null) }


    val titleRegex = Regex("^[A-Za-z0-9.,!?()'\\s]{3,50}$")
    val descriptionRegex = Regex("^[A-Za-z0-9.,!?()'\\s\\n-]{5,500}$")

    Column(modifier = Modifier.padding(all = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                header,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { onCancel() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.Gray
                )
            }
        }

        CustomTextInputField(
            placeHolderText = stringResource(R.string.title),
            valueText = title,
            errorText = titleErrorMessage,
            hasError = isTitleError,
            iconSize = 34,
            fontSize = 20,
            fontWeight = FontWeight.Medium,
            leadingIcon = Icons.Default.TaskAlt
        ) {
            title = it
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        DateTimePickerButton(
            initDateTimeMillis = initDateTimeMillis,
            errorMessage = dateTimeErrorMessage,
            onDateChanged = { timeInMillis ->
                dueDateTimeInMillis = timeInMillis
            })
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        CustomTextInputField(
            placeHolderText = stringResource(R.string.description),
            valueText = description,
            errorText = descErrorMessage,
            hasError = isDescError,
            iconSize = 32,
            fontSize = 16,
            fontWeight = FontWeight.Normal,
            leadingIcon = Icons.Default.EditNote
        ) {
            description = it
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            todo?.let {
                IconButton(
                    onClick = {
                        onDelete(todo)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .border(
                            shape = RoundedCornerShape(36.dp),
                            border = BorderStroke(0.dp, Color.LightGray)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {

                titleErrorMessage = when {
                    title.isBlank() -> "Title cannot be empty"
                    title.length < 3 -> "Title must be at least 3 characters long."
                    !title.matches(titleRegex) -> "Title can only include letters, numbers, and basic punctuation."
                    else -> null
                }

                isTitleError = titleErrorMessage != null

                descErrorMessage = when {
                    description.isBlank() -> "Description cannot be empty"
                    description.length < 5 -> "Description must be at least 5 characters long."
                    !description.matches(descriptionRegex) -> "Description can only include letters, numbers, and basic punctuation."
                    else -> null
                }

                dateTimeErrorMessage =
                    if (dueDateTimeInMillis == 0L) "Date & Time required" else null

                isDescError = descErrorMessage != null

                if (!isTitleError && !isDescError && dateTimeErrorMessage == null) {
                    onCreateOrUpdate(
                        todo?.id,
                        title,
                        description,
                        dueDateTimeInMillis,
                    )
                }
            }
            ) {
                Text(actionLabel, modifier = Modifier.padding(start = 22.dp, end = 22.dp))
            }
        }
    }
}


@Composable
internal fun CustomTextInputField(
    placeHolderText: String,
    valueText: String,
    errorText: String?,
    hasError: Boolean,
    iconSize: Int,
    fontSize: Int,
    fontWeight: FontWeight,
    leadingIcon: ImageVector,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = valueText,
        isError = hasError,
        onValueChange = {
            onValueChange(it)
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Leading Icon",
                tint = Color.Black,
                modifier = Modifier
                    .size(iconSize.dp)
            )
        },
        textStyle = TextStyle.Default.copy(
            fontSize = fontSize.sp,
            fontWeight = fontWeight
        ),
        placeholder = {
            Text(
                text = placeHolderText,
                color = Color.Gray,
                fontSize = fontSize.sp
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
    )
    errorText?.let {
        Text(
            text = it,
            fontSize = 12.sp,
            color = Color.Red,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
        )
    }
}

@Composable
internal fun DateTimePickerButton(
    initDateTimeMillis: Long,
    errorMessage: String?,
    onDateChanged: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var initialDateLabel = stringResource(R.string.add_due_date_and_time)
    if (initDateTimeMillis != 0L) {
        calendar.timeInMillis = initDateTimeMillis
        initialDateLabel = DateUtil.formatDateTimeMillis(initDateTimeMillis)
    } else {
        calendar.timeInMillis = System.currentTimeMillis()
    }

    var dueDateTime by remember { mutableStateOf(initialDateLabel) }


    val textColor =
        if (dueDateTime.contentEquals(stringResource(R.string.add_due_date_and_time))) Color.Gray else Color.Black

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val timePicker = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    val formattedDate = DateUtil.formatDateTimeMillis(calendar.timeInMillis)
                    dueDateTime = context.getString(R.string.due_date_time, formattedDate)

                    onDateChanged(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )

            timePicker.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = calendar.timeInMillis // Prevent past dates
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = if (errorMessage != null) colorResource(id = R.color.light_gray) else Color.Transparent)
                .clickable {
                    datePicker.show()
                }
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendar Icon",
                tint = Color.Black,
                modifier = Modifier
                    .padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                    .size(32.dp)
            )
            Text(
                text = dueDateTime,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.padding(16.dp)
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
            )
        }
    }
}

