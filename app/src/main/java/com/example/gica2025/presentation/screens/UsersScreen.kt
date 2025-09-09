package com.example.gica2025.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gica2025.data.model.User
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
    var selectedUserProfile by remember { mutableStateOf<User?>(null) }
    
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

        Spacer(modifier = Modifier.height(24.dp))

        // Stats moved to top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Usuarios totales",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.totalUsers}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Usuarios Totales",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Administradores",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.users.count { it.roles?.contains("administrator") == true }}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Administradores",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filter and sort controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Role filter
            var roleFilterExpanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = roleFilterExpanded,
                onExpandedChange = { roleFilterExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = when (uiState.selectedRole) {
                        "administrator" -> "Administradores"
                        "subscriber" -> "Suscriptores"
                        else -> "Todos los roles"
                    },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Filtrar por rol") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtrar"
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                ExposedDropdownMenu(
                    expanded = roleFilterExpanded,
                    onDismissRequest = { roleFilterExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos los roles") },
                        onClick = {
                            viewModel.updateRoleFilter(null)
                            roleFilterExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Administradores") },
                        onClick = {
                            viewModel.updateRoleFilter("administrator")
                            roleFilterExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Suscriptores") },
                        onClick = {
                            viewModel.updateRoleFilter("subscriber")
                            roleFilterExpanded = false
                        }
                    )
                }
            }
            
            // Sort button
            var sortExpanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it },
                modifier = Modifier.width(120.dp)
            ) {
                OutlinedButton(
                    onClick = { sortExpanded = true },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Ordenar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ordenar",
                        fontSize = 12.sp
                    )
                }
                
                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nombre A-Z") },
                        onClick = {
                            viewModel.updateSorting("name", "asc")
                            sortExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Nombre Z-A") },
                        onClick = {
                            viewModel.updateSorting("name", "desc")
                            sortExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Más recientes") },
                        onClick = {
                            viewModel.updateSorting("date", "desc")
                            sortExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Más antiguos") },
                        onClick = {
                            viewModel.updateSorting("date", "asc")
                            sortExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Users list with improved display
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando usuarios...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            uiState.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al cargar usuarios",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 18.sp
                        )
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { token?.let { viewModel.retryAllUsers(it) } },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Reintentar")
                        }
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
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Sin usuarios",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay usuarios disponibles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Los usuarios aparecerán aquí cuando se registren",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredUsers) { user ->
                        UserCard(
                            user = user,
                            isDeleteLoading = uiState.deleteLoading.contains(user.id),
                            onRetryClick = { 
                                selectedUserProfile = user
                            },
                            onDeleteClick = {
                                token?.let { viewModel.deleteUser(user, it) }
                            }
                        )
                    }
                }
            }
        }
    }
    
    // User Profile Dialog
    selectedUserProfile?.let { user ->
        UserProfileDialog(
            user = user,
            onDismiss = { selectedUserProfile = null }
        )
    }
}

@Composable
private fun UserCard(
    user: User,
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
                            text = user.displayName.ifEmpty { user.username },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (user.firstName.isNotEmpty() || user.lastName.isNotEmpty()) {
                            Text(
                                text = "${user.firstName} ${user.lastName}".trim(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                user.roles?.let { rolesList ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rolesList.forEach { role ->
                            AssistChip(
                                onClick = { },
                                label = { 
                                    Text(
                                        text = when(role) {
                                            "administrator" -> "Admin"
                                            "subscriber" -> "Suscriptor"
                                            else -> role.replaceFirstChar { it.uppercase() }
                                        },
                                        fontSize = 10.sp
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (role == "administrator") 
                                        MaterialTheme.colorScheme.primaryContainer
                                    else 
                                        MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                } ?: run {
                    // Fallback cuando no hay roles
                    AssistChip(
                        onClick = { },
                        label = { 
                            Text(
                                text = "Usuario",
                                fontSize = 10.sp
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.height(24.dp)
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
                text = "ID: ${user.id} • Registrado: ${user.registered?.take(10) ?: "N/A"}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            
            user.externalRegistration?.let { external ->
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Registro Externo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Doc: ${external.documentType.uppercase()} ${external.documentNumber}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = "Estado: ${external.statusLabel} • Tel: ${external.phone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Botones de gestión
            Spacer(modifier = Modifier.height(12.dp))
            
            val isAdmin = user.roles?.contains("administrator") == true
            val canDelete = !isAdmin // No permitir eliminar administradores
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Ver Perfil Completo
                OutlinedButton(
                    onClick = onRetryClick,
                    enabled = !isDeleteLoading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Ver Perfil",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver Perfil", fontSize = 12.sp)
                }
                
                // Botón Eliminar (solo si no es administrador)
                if (canDelete) {
                    Button(
                        onClick = onDeleteClick,
                        enabled = !isDeleteLoading,
                        modifier = Modifier.weight(1f),
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
                } else {
                    // Placeholder para mantener el diseño balanceado
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "Administrador Protegido",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserProfileDialog(
    user: User,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil de usuario",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Perfil de Usuario",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Basic Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información Básica",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ProfileInfoRow("Nombre de Usuario", user.username)
                        ProfileInfoRow("Nombre para Mostrar", user.displayName.ifEmpty { "No especificado" })
                        ProfileInfoRow("Nombre", user.firstName.ifEmpty { "No especificado" })
                        ProfileInfoRow("Apellido", user.lastName.ifEmpty { "No especificado" })
                        ProfileInfoRow("Email", user.email)
                        ProfileInfoRow("ID", user.id.toString())
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Roles Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Roles y Permisos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        user.roles?.let { rolesList ->
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(rolesList) { role ->
                                    AssistChip(
                                        onClick = { },
                                        label = { 
                                            Text(
                                                text = when(role) {
                                                    "administrator" -> "Administrador"
                                                    "subscriber" -> "Suscriptor"
                                                    else -> role.replaceFirstChar { it.uppercase() }
                                                },
                                                fontSize = 12.sp
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = if (role == "administrator") 
                                                MaterialTheme.colorScheme.primaryContainer
                                            else 
                                                MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                }
                            }
                        } ?: Text(
                            text = "Sin roles asignados",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Registration Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información de Registro",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ProfileInfoRow("Fecha de Registro", user.registered?.take(19)?.replace("T", " ") ?: "No disponible")
                        
                        user.externalRegistration?.let { external ->
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Registro Externo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            ProfileInfoRow("Tipo de Documento", external.documentType.uppercase())
                            ProfileInfoRow("Número de Documento", external.documentNumber)
                            ProfileInfoRow("Teléfono", external.phone)
                            ProfileInfoRow("Estado", external.statusLabel)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        modifier = Modifier.fillMaxWidth(0.95f)
    )
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End
        )
    }
}