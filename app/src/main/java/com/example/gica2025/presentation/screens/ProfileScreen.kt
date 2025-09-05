package com.example.gica2025.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gica2025.data.repository.AuthRepository
import com.example.gica2025.presentation.components.EditProfileDialog
import com.example.gica2025.presentation.viewmodel.ProfileViewModel
import com.example.gica2025.ui.theme.GradientEnd
import com.example.gica2025.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authRepository: AuthRepository,
    onLogout: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel { ProfileViewModel(authRepository) }
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }
    
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            GradientStart.copy(alpha = 0.1f),
            GradientEnd.copy(alpha = 0.1f)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .verticalScroll(rememberScrollState())
    ) {
        // Error Snackbar
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }

        // Success Snackbar
        if (uiState.updateSuccess) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.updateMessage ?: "Perfil actualizado correctamente",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearSuccess() }) {
                        Text("OK", color = Color(0xFF4CAF50))
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.user?.let { user ->
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Header con Avatar y Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mi Perfil",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Row {
                            IconButton(
                                onClick = { viewModel.loadProfile() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualizar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            FilledTonalButton(
                                onClick = { viewModel.startEditing() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Avatar y info básica
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar con imagen real o inicial
                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!user.avatarUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = user.avatarUrl,
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(GradientStart, GradientEnd)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.displayName.firstOrNull()?.uppercase() ?: "U",
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = user.displayName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "@${user.username}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Roles Badge
                            user.roles?.let { rolesList ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rolesList.forEach { role ->
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
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Badge,
                                                    contentDescription = "Rol",
                                                    modifier = Modifier.size(16.dp)
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
                            }

                            // Completion percentage
                            user.completionPercentage?.let { percentage ->
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Perfil completado: ",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "$percentage%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { percentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Información detallada
                    ProfileInfoSection(
                        title = "Información Personal",
                        icon = Icons.Default.Person,
                        items = listOf(
                            ProfileInfoItem(Icons.Default.Email, "Email", user.email),
                            ProfileInfoItem(Icons.Default.Person, "Nombre completo", "${user.firstName} ${user.lastName}".trim())
                        )
                    )

                    // Profile data si existe
                    user.profileData?.let { profileData ->
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileInfoSection(
                            title = "Información de Contacto",
                            icon = Icons.Default.Phone,
                            items = listOfNotNull(
                                profileData.phone?.let { ProfileInfoItem(Icons.Default.Phone, "Teléfono", it) },
                                profileData.address?.let { ProfileInfoItem(Icons.Default.LocationOn, "Dirección", it) },
                                profileData.city?.let { ProfileInfoItem(Icons.Default.LocationOn, "Ciudad", "${it}${profileData.region?.let { ", $it" } ?: ""}") },
                                profileData.country?.let { ProfileInfoItem(Icons.Default.LocationOn, "País", it) }
                            )
                        )
                    }

                    // Device info si existe
                    user.deviceInfo?.let { deviceInfo ->
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileInfoSection(
                            title = "Información del Dispositivo",
                            icon = Icons.Default.AccountCircle,
                            items = listOf(
                                ProfileInfoItem(Icons.Default.AccountCircle, "Plataforma", "${deviceInfo.platform} ${deviceInfo.type}"),
                                ProfileInfoItem(Icons.Default.AccountCircle, "App Version", deviceInfo.appVersion ?: "N/A"),
                                ProfileInfoItem(Icons.Default.AccountCircle, "Device ID", deviceInfo.deviceId)
                            )
                        )
                    }

                    // Login history si existe
                    user.loginHistory?.let { history ->
                        if (history.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileInfoSection(
                                title = "Historial de Acceso",
                                icon = Icons.Default.History,
                                items = history.take(3).map { login ->
                                    ProfileInfoItem(
                                        Icons.Default.History,
                                        "Último acceso",
                                        "${login.timestamp.take(16)} desde ${login.ipAddress}"
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón de cerrar sesión
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "CERRAR SESIÓN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("CONFIRMAR")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("CANCELAR")
                }
            }
        )
    }

    // Edit Profile Dialog
    if (uiState.isEditing) {
        uiState.user?.let { user ->
            EditProfileDialog(
                user = user,
                onDismiss = { viewModel.cancelEditing() },
                onSave = { profileUpdateRequest ->
                    viewModel.updateProfile(profileUpdateRequest)
                },
                isLoading = uiState.isLoading
            )
        }
    }
}

data class ProfileInfoItem(
    val icon: ImageVector,
    val label: String,
    val value: String
)

@Composable
private fun ProfileInfoSection(
    title: String,
    icon: ImageVector,
    items: List<ProfileInfoItem>
) {
    if (items.isEmpty()) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = item.value,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}