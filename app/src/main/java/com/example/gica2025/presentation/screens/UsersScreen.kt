package com.example.gica2025.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gica2025.data.model.UserItem
import com.example.gica2025.presentation.viewmodel.UsersViewModel
import com.example.gica2025.ui.theme.GradientEnd
import com.example.gica2025.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)


@Preview
@Composable
fun UsersScreen(
    token: String? = null
) {
    val viewModel: UsersViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            GradientStart.copy(alpha = 0.1f),
            GradientEnd.copy(alpha = 0.1f)
        )
    )
    
    LaunchedEffect(token) {
        token?.let { 
            viewModel.loadUsers(it) 
        }
    }
    
    uiState.actionMessage?.let { message ->
        LaunchedEffect(message) {
            viewModel.clearActionMessage()
        }
        
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { viewModel.clearActionMessage() }) {
                    Text("OK")
                }
            }
        ) {
            Text(message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Gestión de Usuarios",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Administra los usuarios del sistema",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = { 
                        token?.let { viewModel.retryAllUsers(it) }
                    },
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reintentar todos",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Barra de búsqueda
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Buscar usuarios...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de usuarios
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar usuarios",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            uiState.users.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay usuarios disponibles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.users) { user ->
                        UserCard(
                            user = user,
                            isRetryLoading = uiState.retryLoading.contains(user.wp_user_id),
                            isDeleteLoading = uiState.deleteLoading.contains(user.wp_user_id),
                            onRetryClick = { 
                                token?.let { viewModel.retryUser(user.wp_user_id, it) }
                            },
                            onDeleteClick = {
                                token?.let { viewModel.deleteUser(user, it) }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats placeholder
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.users.size}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Usuarios Totales",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.users.count { it.has_external_registration }}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Con Registro Externo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: UserItem,
    isRetryLoading: Boolean = false,
    isDeleteLoading: Boolean = false,
    onRetryClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Usuario",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = user.display_name.ifEmpty { user.username },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (user.first_name.isNotEmpty() || user.last_name.isNotEmpty()) {
                            Text(
                                text = "${user.first_name} ${user.last_name}".trim(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (user.has_external_registration) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = if (user.has_external_registration) "Registrado" else "Solo WP",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (user.has_external_registration) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = user.email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Text(
                text = "ID: ${user.wp_user_id} • Registrado: ${user.wp_registered_date.take(10)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            
            user.external_registration?.let { external ->
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Registro Externo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Doc: ${external.document_type.uppercase()} ${external.document_number}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Text(
                    text = "Estado: ${external.status_label} • Tel: ${external.phone}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (user.has_external_registration) {
                Spacer(modifier = Modifier.height(12.dp))
                val isRegistered = user.external_registration?.status_label?.equals("Registrado", ignoreCase = true) == true
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isRegistered) {
                        Button(
                            onClick = onRetryClick,
                            enabled = !isRetryLoading && !isDeleteLoading,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            if (isRetryLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reintentar",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reintentar", fontSize = 12.sp)
                        }
                    }
                    
                    Button(
                        onClick = onDeleteClick,
                        enabled = !isRetryLoading && !isDeleteLoading,
                        modifier = if (isRegistered) Modifier.fillMaxWidth() else Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        if (isDeleteLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Eliminar", 
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}