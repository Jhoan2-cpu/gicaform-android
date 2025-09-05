package com.example.gica2025.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gica2025.data.model.ProfileUpdateRequest
import com.example.gica2025.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (ProfileUpdateRequest) -> Unit,
    isLoading: Boolean = false
) {
    var displayName by remember { mutableStateOf(user.displayName) }
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.profileData?.phone ?: "") }
    var address by remember { mutableStateOf(user.profileData?.address ?: "") }
    var dni by remember { mutableStateOf(user.profileData?.dni ?: "") }
    var city by remember { mutableStateOf(user.profileData?.city ?: "") }
    var region by remember { mutableStateOf(user.profileData?.region ?: "") }
    var country by remember { mutableStateOf(user.profileData?.country ?: "") }
    var reference by remember { mutableStateOf(user.profileData?.reference ?: "") }

    val isFormValid = displayName.isNotEmpty() && 
                     firstName.isNotEmpty() && 
                     lastName.isNotEmpty() && 
                     email.isNotEmpty() && 
                     email.contains("@")

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Editar Perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información Básica
                Text(
                    text = "📋 Información Básica",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Nombre a mostrar") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Nombre a mostrar")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = displayName.isEmpty()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("Nombres") },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        isError = firstName.isEmpty()
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Apellidos") },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        isError = lastName.isEmpty()
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = email.isEmpty() || !email.contains("@")
                )

                // Información de Contacto
                Text(
                    text = "📞 Información de Contacto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Teléfono")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text("DNI/Documento") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                // Ubicación
                Text(
                    text = "🌍 Ubicación",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = "Dirección")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ciudad") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = "Ciudad")
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    )

                    OutlinedTextField(
                        value = region,
                        onValueChange = { region = it },
                        label = { Text("Región/Estado") },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    )
                }

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("País") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = "País")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("Referencia de dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    placeholder = { Text("Ej: Al frente del parque, cerca del mercado...") }
                )

                // Loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            Text(
                                text = "Guardando cambios...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updateRequest = ProfileUpdateRequest(
                        id = user.id,
                        username = user.username, // Username no se puede cambiar
                        email = email,
                        displayName = displayName,
                        firstName = firstName,
                        lastName = lastName,
                        phone = phone.takeIf { it.isNotEmpty() },
                        address = address.takeIf { it.isNotEmpty() },
                        dni = dni.takeIf { it.isNotEmpty() },
                        city = city.takeIf { it.isNotEmpty() },
                        region = region.takeIf { it.isNotEmpty() },
                        country = country.takeIf { it.isNotEmpty() },
                        reference = reference.takeIf { it.isNotEmpty() }
                    )
                    onSave(updateRequest)
                },
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar Cambios")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}