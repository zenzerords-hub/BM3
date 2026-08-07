package com.example.buckmanager.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)
}

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings")
    suspend fun getAllSettings(): List<SettingEntity>

    @Query("SELECT * FROM settings WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): SettingEntity?

    @Query("SELECT * FROM settings WHERE key = :key LIMIT 1")
    fun getSettingFlow(key: String): Flow<SettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteSetting(key: String)
}

@Dao
interface RecurringBillDao {
    @Query("SELECT * FROM recurring_bills WHERE isActive = 1 ORDER BY dayOfMonth ASC")
    fun getActiveBillsFlow(): Flow<List<RecurringBillEntity>>

    @Query("SELECT * FROM recurring_bills ORDER BY id DESC")
    suspend fun getAllBills(): List<RecurringBillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: RecurringBillEntity): Long

    @Query("DELETE FROM recurring_bills WHERE id = :id")
    suspend fun deleteBill(id: Long)
}

@Database(
    entities = [TransactionEntity::class, SettingEntity::class, RecurringBillEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun settingDao(): SettingDao
    abstract fun recurringBillDao(): RecurringBillDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "buckmanager.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
