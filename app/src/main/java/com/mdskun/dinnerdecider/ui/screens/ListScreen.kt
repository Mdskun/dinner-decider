package com.mdskun.dinnerdecider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdskun.dinnerdecider.R
import com.mdskun.dinnerdecider.ui.component.TopBar
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
    var showResetDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle delete snackbar with Undo action
    val lastDeleted = viewModel.lastDeletedItem
    val deletedMessage = stringResource(R.string.dinner_deleted, lastDeleted?.second ?: "")
    val undoLabel = stringResource(R.string.undo)

    LaunchedEffect(lastDeleted) {
        if (lastDeleted != null) {
            val result = snackbarHostState.showSnackbar(
                message = deletedMessage,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearLastDeletedItem()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar
            TopBar(
                title = stringResource(R.string.nav_dinner_list),
                subtitle = stringResource(R.string.meals_saved, viewModel.dinners.size),
                onBack = { viewModel.goBack() }
            )

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
            ) {
                // Add New Item Input Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = newItem,
                        onValueChange = {
                            newItem = it
                            if (viewModel.duplicateError != null) {
                                viewModel.clearDuplicateError()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (newItem.isNotBlank()) {
                                    if (viewModel.addDinner(newItem)) {
                                        newItem = ""
                                        focusManager.clearFocus()
                                    }
                                }
                            }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Box {
                                if (newItem.isEmpty()) {
                                    Text(
                                        stringResource(R.string.add_dinner_hint),
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W600,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                if (viewModel.addDinner(newItem)) {
                                    newItem = ""
                                    focusManager.clearFocus()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            stringResource(R.string.add_button),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W800
                        )
                    }
                }

                // Duplicate Error Message
                if (viewModel.duplicateError != null) {
                    Text(
                        text = viewModel.duplicateError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dinner Items List
                if (viewModel.dinners.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.empty_list_message),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
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
                                    if (viewModel.duplicateError != null) {
                                        viewModel.clearDuplicateError()
                                    }
                                },
                                onEditValueChange = {
                                    editValue = it
                                    if (viewModel.duplicateError != null) {
                                        viewModel.clearDuplicateError()
                                    }
                                },
                                onSaveEdit = {
                                    if (editValue.isNotBlank() && editValue != dinner) {
                                        if (viewModel.updateDinner(index, editValue)) {
                                            editingIndex = -1
                                            focusManager.clearFocus()
                                        }
                                    } else {
                                        editingIndex = -1
                                        focusManager.clearFocus()
                                    }
                                },
                                onCancelEdit = {
                                    editingIndex = -1
                                    focusManager.clearFocus()
                                    if (viewModel.duplicateError != null) {
                                        viewModel.clearDuplicateError()
                                    }
                                },
                                onDelete = {
                                    viewModel.deleteDinner(index)
                                    if (editingIndex == index) {
                                        editingIndex = -1
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { showResetDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    stringResource(R.string.reset_list_button),
                                    fontWeight = FontWeight.W700,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Reset List Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    stringResource(R.string.reset_dialog_title),
                    fontWeight = FontWeight.W800
                )
            },
            text = {
                Text(
                    stringResource(R.string.reset_dialog_message),
                    fontWeight = FontWeight.W600
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaultList()
                        showResetDialog = false
                    }
                ) {
                    Text(
                        stringResource(R.string.confirm),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.W800
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        fontWeight = FontWeight.W600
                    )
                }
            }
        )
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

    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    val editDesc = stringResource(R.string.edit_dinner_description)
    val deleteDesc = stringResource(R.string.delete_dinner_description)
    val saveDesc = stringResource(R.string.save_edit_description)
    val cancelDesc = stringResource(R.string.cancel_edit_description)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color dot indicator
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(10.dp))

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
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onSaveEdit() }
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )

            // Save IconButton (48dp touch target)
            IconButton(
                onClick = onSaveEdit,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = saveDesc }
            ) {
                Text(text = "✅", fontSize = 18.sp)
            }

            // Cancel IconButton (48dp touch target)
            IconButton(
                onClick = onCancelEdit,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = cancelDesc }
            ) {
                Text(text = "❌", fontSize = 18.sp)
            }
        } else {
            Text(
                text = dinner,
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // Edit IconButton (48dp touch target)
            IconButton(
                onClick = onStartEdit,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = editDesc }
            ) {
                Text(text = "✏️", fontSize = 18.sp)
            }

            // Delete IconButton (48dp touch target)
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = deleteDesc }
            ) {
                Text(text = "🗑️", fontSize = 18.sp)
            }
        }
    }
}