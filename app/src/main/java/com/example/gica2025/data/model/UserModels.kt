package com.example.gica2025.data.model

data class UsersResponse(
    val success: Boolean,
    val data: UsersData,
    val message: String
)

data class UsersData(
    val users: List<UserItem>,
    val pagination: Pagination,
    val filters: Filters,
    val stats: Stats
)

data class UserItem(
    val wp_user_id: Int,
    val username: String,
    val email: String,
    val display_name: String,
    val first_name: String,
    val last_name: String,
    val wp_registered_date: String,
    val has_external_registration: Boolean,
    val external_registration: ExternalRegistration?
)

data class ExternalRegistration(
    val id: Int,
    val document_type: String,
    val document_number: String,
    val country: String,
    val phone: String,
    val mobile: String,
    val backend_status: String,
    val backend_response: BackendResponse?,
    val error_message: String?,
    val retry_count: Int,
    val registration_date: String,
    val last_update: String,
    val status_label: String
)

data class BackendResponse(
    val id: Int,
    val user_id: Int,
    val title: String,
    val first_name: String,
    val paternal_surname: String,
    val maternal_surname: String,
    val document_type: String,
    val document_number: String,
    val document_country: String,
    val birth_date: String?,
    val gender: String?,
    val nationality: String,
    val region: String,
    val city: String,
    val address: String?,
    val postal_code: String?,
    val phone_country_code: String,
    val phone_area_code: String,
    val phone_number: String,
    val mobile_country_code: String,
    val mobile_area_code: String,
    val mobile_number: String,
    val email: String,
    val email_verified: Int,
    val secondary_email: String,
    val profession: String?,
    val specialization: String?,
    val experience_years: Int,
    val education_level: String?,
    val languages: List<String>,
    val skills: List<String>,
    val billing_document_type: String,
    val billing_name: String,
    val billing_ruc: String?,
    val billing_country: String,
    val billing_region: String,
    val billing_city: String,
    val billing_address: String?,
    val payment_plan: String,
    val invoice_type: String,
    val referral_source: String,
    val assigned_seller: String,
    val seller_office: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)

data class Pagination(
    val current_page: Int,
    val per_page: Int,
    val total_items: Int,
    val total_pages: Int,
    val has_more: Boolean
)

data class Filters(
    val status: String,
    val search: String?
)

data class Stats(
    val total_wp_users: Int,
    val wp_only_users: Int,
    val external_registrations: ExternalRegistrationStats,
    val success_rate: Double
)

data class ExternalRegistrationStats(
    val total: Int,
    val pending: Int,
    val registered: Int,
    val failed: Int
)

data class DeleteUserResponse(
    val success: Boolean,
    val data: DeleteUserData?,
    val message: String
)

data class DeleteUserData(
    val user_id: Int?,
    val wp_user_id: Int?,
    val deleted_wp_user: Boolean,
    val action: String
)

data class DeleteUserRequest(
    val user_id: Int?,
    val delete_wp_user: Boolean,
    val wp_user_id: Int?
)

data class RetryResponse(
    val success: Boolean,
    val message: String
)