package com.example.gica2025.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gica2025.data.model.User
import com.example.gica2025.ui.theme.GradientEnd
import com.example.gica2025.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar y nombre
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
            if (user != null) {
                Text(
                    text = user.displayName.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user?.displayName ?: "Usuario",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "@${user?.username ?: "usuario"}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Información del usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Información Personal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // ID de Usuario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "ID",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "ID de Usuario",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = user?.id?.toString() ?: "N/A",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Divider()

                // Email
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Correo Electrónico",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = user?.email ?: "No disponible",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Información de desarrollo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = "🚧 Esta sección está en desarrollo. Próximamente se agregará más funcionalidad para la gestión del perfil del usuario.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

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

    // Diálogo de confirmación
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
}