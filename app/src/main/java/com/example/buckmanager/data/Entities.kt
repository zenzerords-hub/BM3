package com.example.buckmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "income" or "expense"
    val amount: Double,
    val category: String, // envelope id or "income"
    val date: String, // ISO String
    val description: String
)

@Serializable
@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Serializable
@Entity(tableName = "recurring_bills")
data class RecurringBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val category: String,
    val dayOfMonth: Int,
    val isActive: Boolean = true,
    val lastProcessed: String? = null,
    val createdAt: String? = null
)
