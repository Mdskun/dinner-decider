package com.mdskun.dinnerdecider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdskun.dinnerdecider.ui.theme.*
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel

@Composable
fun ListScreen(
    viewModel: DinnerViewModel,
    modifier: Modifier = Modifier
) {
    var newItem by remember { mutableStateOf("") }
    var editingIndex by remember { mutableIntStateOf(-1) }
    var editValue by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.background(WarmBackground)
    ) {
        // Top Bar
        TopBar(
            title = "Dinner List",
            subtitle = "${viewModel.dinners.size} meals",
            onBack = { viewModel.goBack() }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
        ) {
            // Add new item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .border(2.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600,
                        color = DarkText
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newItem.isNotBlank()) {
                                viewModel.addDinner(newItem)
                                newItem = ""
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    cursorBrush = SolidColor(AccentColor),
                    decorationBox = { innerTextField ->
                        Box {
                            if (newItem.isEmpty()) {
                                Text(
                                    "Add a new dinner…",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.W600,
                                        color = LightSubtitle
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Button(
                    onClick = {
                        if (newItem.isNotBlank()) {
                            viewModel.addDinner(newItem)
                            newItem = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentColor,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Add",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W800
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List
            if (viewModel.dinners.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No dinners yet. Add some above! 🍽️",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600,
                        color = LightSubtitle
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(viewModel.dinners) { index, dinner ->
                        EditableDinnerItem(
                            dinner = dinner,
                            color = WheelColors[index % WheelColors.size],
                            isEditing = editingIndex == index,
                            editValue = if (editingIndex == index) editValue else dinner,
                            onStartEdit = {
                                editingIndex = index
                                editValue = dinner
                            },
                            onEditValueChange = { editValue = it },
                            onSaveEdit = {
                                if (editValue.isNotBlank() && editValue != dinner) {
                                    viewModel.updateDinner(index, editValue)
                                }
                                editingIndex = -1
                                focusManager.clearFocus()
                            },
                            onCancelEdit = {
                                editingIndex = -1
                                focusManager.clearFocus()
                            },
                            onDelete = {
                                viewModel.deleteDinner(index)
                                if (editingIndex == index) {
                                    editingIndex = -1
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditableDinnerItem(
    dinner: String,
    color: androidx.compose.ui.graphics.Color,
    isEditing: Boolean,
    editValue: String,
    onStartEdit: () -> Unit,
    onEditValueChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    // Request focus when editing starts
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.5.dp, CardBorder.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Text or Edit field
        if (isEditing) {
            BasicTextField(
                value = editValue,
                onValueChange = onEditValueChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    color = DarkText
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onSaveEdit() }
                ),
                cursorBrush = SolidColor(AccentColor)
            )

            // Save button when editing
            Text(
                text = "✅",
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(onClick = onSaveEdit)
                    .padding(4.dp)
            )

            // Cancel button when editing
            Text(
                text = "❌",
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(onClick = onCancelEdit)
                    .padding(4.dp)
            )
        } else {
            Text(
                text = dinner,
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                color = DarkText,
                modifier = Modifier.weight(1f)
            )

            // Edit button
            Text(
                text = "✏️",
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(onClick = onStartEdit)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Delete button
            Text(
                text = "🗑️",
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(onClick = onDelete)
                    .padding(4.dp)
            )
        }
    }
}