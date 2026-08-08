package com.example.buckmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.buckmanager.data.AppDatabase
import com.example.buckmanager.data.SettingEntity
import com.example.buckmanager.data.TransactionEntity
import com.example.buckmanager.model.*
import com.example.buckmanager.widget.GoalAppWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class BuckViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "buckmanager.db"
    )
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .fallbackToDestructiveMigration()
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    // State Flows
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions.asStateFlow()

    private val _currencySymbol = MutableStateFlow(com.example.buckmanager.model.CurrencyConfig.symbol)
    val currencySymbol: StateFlow<String> = _currencySymbol

    fun refreshCurrency() {
        _currencySymbol.value = com.example.buckmanager.model.CurrencyConfig.symbol
    }

    fun createLocalSnapshot(context: android.content.Context) {
        viewModelScope.launch {
            com.example.buckmanager.utils.BackupUtils.createLocalSnapshot(context)
        }
    }

    fun exportToJson(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch {
            com.example.buckmanager.utils.BackupUtils.exportToJson(context, db, uri)
        }
    }

    private val _envelopes = MutableStateFlow<List<Envelope>>(defaultEnvelopes())
    val envelopes: StateFlow<List<Envelope>> = _envelopes.asStateFlow()

    private val _globalBackground = MutableStateFlow(GlobalBackgroundConfig())
    val globalBackground: StateFlow<GlobalBackgroundConfig> = _globalBackground.asStateFlow()

    private val _headerCardsConfig = MutableStateFlow(HeaderCardsConfig())
    val headerCardsConfig: StateFlow<HeaderCardsConfig> = _headerCardsConfig.asStateFlow()

    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    fun completeOnboarding() {
        _hasSeenOnboarding.value = true
        viewModelScope.launch(Dispatchers.IO) {
            saveSetting("has_seen_onboarding", "true")
        }
    }

    private val _fundGoal = MutableStateFlow(FundGoalConfig())
    val fundGoal: StateFlow<FundGoalConfig> = _fundGoal.asStateFlow()

    private val _monetization = MutableStateFlow(MonetizationState())
    val monetization: StateFlow<MonetizationState> = _monetization.asStateFlow()

    private val _streakData = MutableStateFlow(StreakData())
    val streakData: StateFlow<StreakData> = _streakData.asStateFlow()


    private val _isEditLocked = MutableStateFlow(true)
    val isEditLocked: StateFlow<Boolean> = _isEditLocked.asStateFlow()

    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    fun clearUserNotice() {
        _userNotice.value = null
    }

    fun showNotice(message: String) {
        _userNotice.value = message
    }

    fun toggleEditLock() {
        val newState = !_isEditLocked.value
        _isEditLocked.value = newState
        viewModelScope.launch(Dispatchers.IO) {
            saveSetting("edit_locked", newState.toString())
        }
    }
    private val _isCustomizationLocked = MutableStateFlow(false)
    val isCustomizationLocked: StateFlow<Boolean> = _isCustomizationLocked.asStateFlow()

    private val _hideBalances = MutableStateFlow(false)
    val hideBalances: StateFlow<Boolean> = _hideBalances.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(true)
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isThemeCustomized = MutableStateFlow(false)
    val isThemeCustomized: StateFlow<Boolean> = _isThemeCustomized.asStateFlow()

    init {
        loadAllData()
    }

    private fun defaultEnvelopes(): List<Envelope> = defaultEnvelopesLight()

    private fun defaultEnvelopesDark(): List<Envelope> = listOf(
        Envelope(id = "main", name = "Main Envelope", percentage = 0, colorHex = "#9CA3AF", iconName = "wallet", backgroundColorHex = "#202532", orderIndex = 0, labelColorHex = "#9CA3AF", valueColorHex = "#FFFFFF", descriptionColorHex = "#6B7280"),
        Envelope(id = "needs", name = "Needs", percentage = 50, colorHex = "#38BDF8", iconName = "home", backgroundColorHex = "#162338", orderIndex = 1, labelColorHex = "#38BDF8", valueColorHex = "#FFFFFF", descriptionColorHex = "#9CA3AF"),
        Envelope(id = "wants", name = "Wants", percentage = 30, colorHex = "#FBBF24", iconName = "game-controller", backgroundColorHex = "#282118", orderIndex = 2, labelColorHex = "#FBBF24", valueColorHex = "#FFFFFF", descriptionColorHex = "#9CA3AF"),
        Envelope(id = "savings", name = "Savings", percentage = 20, colorHex = "#34D399", iconName = "chart", backgroundColorHex = "#142A22", orderIndex = 3, labelColorHex = "#34D399", valueColorHex = "#FFFFFF", descriptionColorHex = "#9CA3AF")
    )

    private fun defaultEnvelopesLight(): List<Envelope> = listOf(
        Envelope(id = "main", name = "Main Envelope", percentage = 0, colorHex = "#64748B", iconName = "wallet", backgroundColorHex = "#F1F5F9", orderIndex = 0, labelColorHex = "#475569", valueColorHex = "#0F172A", descriptionColorHex = "#94A3B8"),
        Envelope(id = "needs", name = "Needs", percentage = 50, colorHex = "#1D2A96", iconName = "home", backgroundColorHex = "#EBF0FF", orderIndex = 1, labelColorHex = "#1D2A96", valueColorHex = "#0F172A", descriptionColorHex = "#64748B"),
        Envelope(id = "wants", name = "Wants", percentage = 30, colorHex = "#EC407A", iconName = "game-controller", backgroundColorHex = "#FCE4EC", orderIndex = 2, labelColorHex = "#EC407A", valueColorHex = "#0F172A", descriptionColorHex = "#64748B"),
        Envelope(id = "savings", name = "Savings", percentage = 20, colorHex = "#10B981", iconName = "chart", backgroundColorHex = "#E6F4EA", orderIndex = 3, labelColorHex = "#059669", valueColorHex = "#0F172A", descriptionColorHex = "#64748B")
    )

    fun loadAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Load transactions
            val txList = db.transactionDao().getAllTransactions()
            _transactions.value = txList


            // Load settings
            val settings = db.settingDao().getAllSettings().associate { it.key to it.value }
            _hasSeenOnboarding.value = settings["has_seen_onboarding"] == "true"

            // Envelopes config
            settings["envelopes_config"]?.let { envStr ->
                try {
                    val parsed = json.decodeFromString<List<Envelope>>(envStr)
                    _envelopes.value = parsed.sortedBy { it.orderIndex }
                } catch (e: Exception) {
                    _envelopes.value = defaultEnvelopes()
                }
            } ?: run {
                saveSetting("envelopes_config", json.encodeToString(defaultEnvelopes()))
            }

            // Global background
            settings["global_background_config"]?.let { bgStr ->
                try {
                    _globalBackground.value = json.decodeFromString(bgStr)
                } catch (e: Exception) {}
            }

            // Header cards
            settings["header_cards_config"]?.let { hcStr ->
                try {
                    _headerCardsConfig.value = json.decodeFromString(hcStr)
                } catch (e: Exception) {}
            }

            // Fund goal (synchronous load to ensure it's available for applyThemePreset)
            settings["fund_goal_config"]?.let { fgStr ->
                try {
                    _fundGoal.value = json.decodeFromString<FundGoalConfig>(fgStr)
                } catch (e: Exception) {}
            }

            // Fund goal (observed continuously to sync with widget)
            viewModelScope.launch(Dispatchers.IO) {
                db.settingDao().getSettingFlow("fund_goal_config").collect { entity ->
                    entity?.value?.let { fgStr ->
                        try {
                            val fg = json.decodeFromString<FundGoalConfig>(fgStr)
                            _fundGoal.value = fg
                            GoalAppWidgetProvider.saveGoalToPrefs(getApplication(), fg)
                        } catch (e: Exception) {}
                    }
                }
            }

            // Monetization
            settings["monetization"]?.let { monStr ->
                try {
                    val mon = json.decodeFromString<MonetizationState>(monStr)
                    _monetization.value = mon
                    _isCustomizationLocked.value = false
                } catch (e: Exception) {}
            }

            // Streak
            settings["streak_data"]?.let { streakStr ->
                try {
                    _streakData.value = json.decodeFromString(streakStr)
                } catch (e: Exception) {}
            }

            // User Email
            _userEmail.value = settings["user_email"]
            _notificationEnabled.value = settings["notification_enabled"]?.toBoolean() ?: true
            _isDarkMode.value = (settings["app_theme_mode"] ?: "light") == "dark"
            _isThemeCustomized.value = settings["is_theme_customized"]?.toBoolean() ?: false
            
            if (!_isThemeCustomized.value) {
                applyThemePreset(_isDarkMode.value)
            }
        }
    }

    // Calculated fields
    fun getTotalIncome(): Double = _transactions.value.filter { it.type == "income" }.sumOf { it.amount }
    fun getTotalExpense(): Double = _transactions.value.filter { it.type == "expense" }.sumOf { it.amount }
    fun getNetWorth(): Double = getTotalIncome() - getTotalExpense()

    fun getEnvelopeStats(envelopeId: String): EnvelopeStats {
        val env = _envelopes.value.find { it.id == envelopeId } ?: return EnvelopeStats()
        
        // Pool is Total Income minus any "Unallocated" (main) expenses like Goal deposits.
        // This prevents double-deduction when a user makes an expense from a specific envelope.
        val totalIncome = getTotalIncome()
        val unallocatedExpenses = _transactions.value.filter { it.type == "expense" && it.category == "main" }.sumOf { it.amount }
        val pool = (totalIncome - unallocatedExpenses).coerceAtLeast(0.0)
        
        val allocated = pool * (env.percentage / 100.0)
        
        // Main expenses already reduced the pool, so they are not counted as 'spent' against its own percentage
        val spent = if (envelopeId == "main") 0.0 else _transactions.value.filter { it.type == "expense" && it.category == envelopeId }.sumOf { it.amount }
        
        return EnvelopeStats(allocated = allocated, spent = spent, remaining = allocated - spent)
    }

    // Actions
    fun addTransaction(type: String, amount: Double, category: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val tx = TransactionEntity(
                type = type,
                amount = amount,
                category = category,
                date = dateIso,
                description = description.ifBlank { if (type == "income") "Income" else "Expense" }
            )
            db.transactionDao().insertTransaction(tx)
            _transactions.value = db.transactionDao().getAllTransactions()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.transactionDao().deleteTransaction(id)
            _transactions.value = db.transactionDao().getAllTransactions()
        }
    }

    private suspend fun markCustomized() {
        if (!_isThemeCustomized.value) {
            _isThemeCustomized.value = true
            saveSetting("is_theme_customized", "true")
        }
    }

    fun toggleThemeMode(isDark: Boolean) {
        if (_isThemeCustomized.value) return // Disabled when custom modifications exist
        viewModelScope.launch(Dispatchers.IO) {
            _isDarkMode.value = isDark
            saveSetting("app_theme_mode", if (isDark) "dark" else "light")
            applyThemePreset(isDark)
        }
    }

    fun resetCustomizationToDefaultTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            _isThemeCustomized.value = false
            saveSetting("is_theme_customized", "false")
            applyThemePreset(_isDarkMode.value)
        }
    }

    private suspend fun applyThemePreset(isDark: Boolean) {
        if (isDark) {
            val bg = GlobalBackgroundConfig(backgroundColorHex = "#0F1117", textColorHex = "#FFFFFF", appNameColorHex = "#FFFFFF", titleColorHex = "#FFFFFF", budgetEnvelopesColorHex = "#FFFFFF")
            val hc = HeaderCardsConfig(
                netWorth = HeaderCardConfig(backgroundColorHex = "#181C26", radiusTopLeft = 28, radiusTopRight = 28, radiusBottomRight = 28, radiusBottomLeft = 28, labelColorHex = "#F5B041", valueColorHex = "#FFFFFF", borderTop = 1, borderRight = 1, borderBottom = 1, borderLeft = 1),
                income = HeaderCardConfig(backgroundColorHex = "#132A22", radiusTopLeft = 20, radiusTopRight = 20, radiusBottomRight = 20, radiusBottomLeft = 20, labelColorHex = "#9CA3AF", valueColorHex = "#34D399", borderTop = 1, borderRight = 1, borderBottom = 1, borderLeft = 1),
                expense = HeaderCardConfig(backgroundColorHex = "#2E1A24", radiusTopLeft = 20, radiusTopRight = 20, radiusBottomRight = 20, radiusBottomLeft = 20, labelColorHex = "#9CA3AF", valueColorHex = "#FB7185", borderTop = 1, borderRight = 1, borderBottom = 1, borderLeft = 1)
            )
            val fg = _fundGoal.value.copy(backgroundColorHex = "#181C26", labelColorHex = "#F5B041", valueColorHex = "#FFFFFF", borderColorHex = "#38BDF8")
            
            val envs = _envelopes.value.map { env ->
                when(env.id) {
                    "main" -> env.copy(colorHex = "#9CA3AF", backgroundColorHex = "#202532", labelColorHex = "#9CA3AF", valueColorHex = "#FFFFFF", descriptionColorHex = "#6B7280")
                    "needs" -> env.copy(colorHex = "#38BDF8", backgroundColorHex = "#E0F2FE", labelColorHex = "#0284C7", valueColorHex = "#F8FAFC", descriptionColorHex = "#94A3B8")
                    "wants" -> env.copy(colorHex = "#F472B6", backgroundColorHex = "#FDF2F8", labelColorHex = "#DB2777", valueColorHex = "#F8FAFC", descriptionColorHex = "#94A3B8")
                    "savings" -> env.copy(colorHex = "#34D399", backgroundColorHex = "#ECFDF5", labelColorHex = "#059669", valueColorHex = "#F8FAFC", descriptionColorHex = "#94A3B8")
                    else -> env
                }
            }

            _globalBackground.value = bg
            _headerCardsConfig.value = hc
            _fundGoal.value = fg
            _envelopes.value = envs

            saveSetting("global_background_config", json.encodeToString(bg))
            saveSetting("header_cards_config", json.encodeToString(hc))
            saveSetting("fund_goal_config", json.encodeToString(fg))
            saveSetting("envelopes_config", json.encodeToString(envs))
        } else {
            val bg = GlobalBackgroundConfig(backgroundColorHex = "#F0F4FA", textColorHex = "#0F172A", appNameColorHex = "#000000", titleColorHex = "#000000", budgetEnvelopesColorHex = "#000000")
            val hc = HeaderCardsConfig(
                netWorth = HeaderCardConfig(backgroundColorHex = "#1D2A96", radiusTopLeft = 28, radiusTopRight = 28, radiusBottomRight = 28, radiusBottomLeft = 28, labelColorHex = "#FFB300", valueColorHex = "#FFFFFF", borderTop = 0, borderRight = 0, borderBottom = 0, borderLeft = 0),
                income = HeaderCardConfig(backgroundColorHex = "#ECFDF5", radiusTopLeft = 20, radiusTopRight = 20, radiusBottomRight = 20, radiusBottomLeft = 20, labelColorHex = "#047857", valueColorHex = "#059669", borderTop = 1, borderRight = 1, borderBottom = 1, borderLeft = 1),
                expense = HeaderCardConfig(backgroundColorHex = "#FCE4EC", radiusTopLeft = 20, radiusTopRight = 20, radiusBottomRight = 20, radiusBottomLeft = 20, labelColorHex = "#C2185B", valueColorHex = "#EC407A", borderTop = 1, borderRight = 1, borderBottom = 1, borderLeft = 1)
            )
            val fg = _fundGoal.value.copy(backgroundColorHex = "#FFFFFF", labelColorHex = "#1D2A96", valueColorHex = "#0F172A", borderColorHex = "#2563EB")
            
            val envs = _envelopes.value.map { env ->
                when(env.id) {
                    "main" -> env.copy(colorHex = "#64748B", backgroundColorHex = "#F1F5F9", labelColorHex = "#475569", valueColorHex = "#0F172A", descriptionColorHex = "#94A3B8")
                    "needs" -> env.copy(colorHex = "#2563EB", backgroundColorHex = "#EFF6FF", labelColorHex = "#2563EB", valueColorHex = "#0F172A", descriptionColorHex = "#64748B")
                    "wants" -> env.copy(colorHex = "#EC407A", backgroundColorHex = "#FCE4EC", labelColorHex = "#EC407A", valueColorHex = "#0F172A", descriptionColorHex = "#64748B")
                    "savings" -> env.copy(colorHex = "#10B981", backgroundColorHex = "#E6F4EA", labelColorHex = "#059669", valueColorHex = "#0F172A", descriptionColorHex = "#64748B")
                    else -> env
                }
            }

            _globalBackground.value = bg
            _headerCardsConfig.value = hc
            _fundGoal.value = fg
            _envelopes.value = envs

            saveSetting("global_background_config", json.encodeToString(bg))
            saveSetting("header_cards_config", json.encodeToString(hc))
            saveSetting("fund_goal_config", json.encodeToString(fg))
            saveSetting("envelopes_config", json.encodeToString(envs))
        }
    }

    fun updateEnvelope(updated: Envelope) {
        viewModelScope.launch(Dispatchers.IO) {
            markCustomized()
            val newList = _envelopes.value.map { if (it.id == updated.id) updated else it }
            _envelopes.value = newList
            saveSetting("envelopes_config", json.encodeToString(newList))
        }
    }

    fun addEnvelope(newEnv: Envelope) {
        viewModelScope.launch(Dispatchers.IO) {
            markCustomized()
            val envWithId = newEnv.copy(
                id = "custom_${System.currentTimeMillis()}",
                orderIndex = _envelopes.value.size
            )
            val newList = _envelopes.value + envWithId
            _envelopes.value = newList
            saveSetting("envelopes_config", json.encodeToString(newList))
        }
    }

    fun deleteEnvelope(id: String) {
        if (id == "main") return // Protect main envelope
        viewModelScope.launch(Dispatchers.IO) {
            val listWithoutDeleted = _envelopes.value.filter { it.id != id }
            val otherSum = listWithoutDeleted.filter { it.id != "main" }.sumOf { it.percentage }
            val newList = listWithoutDeleted.map { env ->
                if (env.id == "main") env.copy(percentage = (100 - otherSum).coerceAtLeast(0)) else env
            }
            _envelopes.value = newList
            saveSetting("envelopes_config", json.encodeToString(newList))
        }
    }

    fun updateAllEnvelopes(newList: List<Envelope>) {
        viewModelScope.launch(Dispatchers.IO) {
            val otherSum = newList.filter { it.id != "main" }.sumOf { it.percentage }
            val finalList = newList.map { env ->
                if (env.id == "main") env.copy(percentage = (100 - otherSum).coerceAtLeast(0)) else env
            }
            _envelopes.value = finalList
            saveSetting("envelopes_config", json.encodeToString(finalList))
        }
    }

    fun applyAllocationStrategy(percentages: Map<String, Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            val otherSum = percentages.filterKeys { it != "main" }.values.sum()
            val mainPercent = (100 - otherSum).coerceAtLeast(0)
            
            val newList = _envelopes.value.map { env ->
                if (env.id == "main") {
                    env.copy(percentage = mainPercent)
                } else {
                    val newPercentage = percentages[env.id]
                    if (newPercentage != null) env.copy(percentage = newPercentage) else env
                }
            }
            _envelopes.value = newList
            saveSetting("envelopes_config", json.encodeToString(newList))
        }
    }

    fun updateBackground(config: GlobalBackgroundConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            markCustomized()
            _globalBackground.value = config
            saveSetting("global_background_config", json.encodeToString(config))
        }
    }

    fun updateHeaderCard(cardKey: String, config: HeaderCardConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            markCustomized()
            val current = _headerCardsConfig.value
            val newCards = when (cardKey) {
                "netWorth" -> current.copy(netWorth = config)
                "income" -> current.copy(income = config)
                "expense" -> current.copy(expense = config)
                else -> current
            }
            _headerCardsConfig.value = newCards
            saveSetting("header_cards_config", json.encodeToString(newCards))
        }
    }

    fun updateFundGoal(config: FundGoalConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            markCustomized()
            _fundGoal.value = config
            saveSetting("fund_goal_config", json.encodeToString(config))
            GoalAppWidgetProvider.saveGoalToPrefs(getApplication(), config)
        }
    }

    fun depositToGoal(amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentGoal = _fundGoal.value
            val newAmount = (currentGoal.currentAmount + amount).coerceAtLeast(0.0)
            val updatedGoal = currentGoal.copy(currentAmount = newAmount)
            _fundGoal.value = updatedGoal
            saveSetting("fund_goal_config", json.encodeToString(updatedGoal))
            GoalAppWidgetProvider.saveGoalToPrefs(getApplication(), updatedGoal)
            
            // Automatically log an expense to deduct from Main Envelope (main)
            addTransaction(
                type = "expense",
                amount = amount,
                category = "main",
                description = "Deposit to My Goal"
            )
        }
    }

    fun hasPremium(): Boolean {
        val mon = _monetization.value
        return mon.isPremium || mon.premiumExpiryDate > System.currentTimeMillis() || mon.adTickets > 0
    }

    fun updateMonetization(mon: MonetizationState) {
        viewModelScope.launch(Dispatchers.IO) {
            _monetization.value = mon
            val now = System.currentTimeMillis()
            _isCustomizationLocked.value = !(mon.isPremium || mon.premiumExpiryDate > now || mon.adTickets > 0)
            saveSetting("monetization", json.encodeToString(mon))
        }
    }

    fun unlockTemporaryCustomization() {
        val currentMon = _monetization.value
        if (currentMon.adTickets > 0) {
            val newMon = currentMon.copy(adTickets = currentMon.adTickets - 1)
            updateMonetization(newMon)
            _isCustomizationLocked.value = false
            _isEditLocked.value = false
            _userNotice.value = "✨ Customization Unlocked! Edit Mode is Active."
        }
    }

    fun watchAd() {
        val currentMon = _monetization.value
        val newTickets = currentMon.adTickets + 1
        if (newTickets >= 3) {
            val oneDayMillis = 24L * 60 * 60 * 1000
            val newMon = currentMon.copy(
                adTickets = 0,
                premiumExpiryDate = maxOf(System.currentTimeMillis(), currentMon.premiumExpiryDate) + oneDayMillis
            )
            updateMonetization(newMon)
            _isEditLocked.value = false
            _userNotice.value = "🎉 24-Hour Premium Unlocked! Edit Mode is Active."
        } else {
            updateMonetization(currentMon.copy(adTickets = newTickets))
            _userNotice.value = "📺 Ad Watched! ($newTickets/3 tickets). Watch ${3 - newTickets} more to unlock Premium!"
        }
    }

    fun purchaseLifetimePremium() {
        updateMonetization(_monetization.value.copy(isPremium = true))
        _isEditLocked.value = false
        _userNotice.value = "🎉 Lifetime Premium Unlocked! Edit Mode is Active. Enjoy full customization!"
    }

    fun checkAndUpdateStreak(): Pair<Boolean, Int> {
        val currentMon = _monetization.value
        if (currentMon.isPremium) {
             val newData = StreakData(currentStreak = 0, lastLoginDate = null, ticketsClaimed = 0)
             _streakData.value = newData
             viewModelScope.launch(Dispatchers.IO) { saveSetting("streak_data", json.encodeToString(newData)) }
             return Pair(false, 0)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = sdf.format(Date())
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterday = sdf.format(cal.time)
        val currentStreak = _streakData.value
        if (currentStreak.lastLoginDate == today) {
            return Pair(false, currentStreak.currentStreak)
        }
        var newStreak = if (currentStreak.lastLoginDate == yesterday) currentStreak.currentStreak + 1 else 1
        var rewardEarned = false
        var ticketsClaimed = currentStreak.ticketsClaimed
        if (newStreak >= 7) {
            rewardEarned = true
            newStreak = 0
            ticketsClaimed += 1
            val newMon = currentMon.copy(isPremium = true)
            updateMonetization(newMon)
        }
        val newData = StreakData(currentStreak = newStreak, lastLoginDate = today, ticketsClaimed = ticketsClaimed)
        _streakData.value = newData
        viewModelScope.launch(Dispatchers.IO) {
            saveSetting("streak_data", json.encodeToString(newData))
        }
        return Pair(rewardEarned, newStreak)
    }


    fun toggleHideBalances() { _hideBalances.value = !_hideBalances.value }

    fun toggleNotification(enabled: Boolean) {
        _notificationEnabled.value = enabled
        viewModelScope.launch(Dispatchers.IO) {
            saveSetting("notification_enabled", enabled.toString())
        }
    }

    fun setUserEmail(email: String?) {
        _userEmail.value = email
        viewModelScope.launch(Dispatchers.IO) {
            if (email != null) {
                saveSetting("user_email", email)
            } else {
                db.settingDao().deleteSetting("user_email")
            }
        }
    }

    // Backup & Restore
    fun backupData(): String {
        val payload = BackupPayload(
            exportDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()),
            userEmail = _userEmail.value,
            transactions = _transactions.value.map {
                BackupTransactionDto(it.type, it.amount, it.category, it.date, it.description)
            },
            envelopes = _envelopes.value,
            globalBackground = _globalBackground.value,
            headerCardsConfig = _headerCardsConfig.value,
            fundGoal = _fundGoal.value
        )
        return json.encodeToString(payload)
    }

    fun restoreData(jsonString: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val payload = json.decodeFromString<BackupPayload>(jsonString)
                
                // Clear existing transactions
                db.transactionDao().getAllTransactions().forEach { db.transactionDao().deleteTransaction(it.id) }

                // Insert transactions
                payload.transactions.forEach { tx ->
                    db.transactionDao().insertTransaction(
                        TransactionEntity(
                            type = tx.type,
                            amount = tx.amount,
                            category = tx.category,
                            date = tx.date,
                            description = tx.description
                        )
                    )
                }


                // Save configurations
                if (payload.envelopes.isNotEmpty()) {
                    saveSetting("envelopes_config", json.encodeToString(payload.envelopes))
                }
                saveSetting("global_background_config", json.encodeToString(payload.globalBackground))
                saveSetting("header_cards_config", json.encodeToString(payload.headerCardsConfig))
                saveSetting("fund_goal_config", json.encodeToString(payload.fundGoal))
                if (!payload.userEmail.isNullOrBlank()) {
                    saveSetting("user_email", payload.userEmail)
                }

                loadAllData()
                onResult(true, "Data successfully restored!")
            } catch (e: Exception) {
                onResult(false, "Invalid backup format or corrupt JSON data.")
            }
        }
    }

    // Share Customization
    fun exportCustomizationJson(): String {
        val payload = SharedCustomizationPayload(
            globalBackground = _globalBackground.value,
            headerCardsConfig = _headerCardsConfig.value,
            envelopes = _envelopes.value
        )
        return json.encodeToString(payload)
    }

    fun importCustomizationJson(jsonString: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                markCustomized()
                val payload = json.decodeFromString<SharedCustomizationPayload>(jsonString)
                saveSetting("global_background_config", json.encodeToString(payload.globalBackground))
                saveSetting("header_cards_config", json.encodeToString(payload.headerCardsConfig))
                if (payload.envelopes.isNotEmpty()) {
                    saveSetting("envelopes_config", json.encodeToString(payload.envelopes))
                }
                loadAllData()
                onResult(true, "Custom theme applied successfully!")
            } catch (e: Exception) {
                onResult(false, "Invalid theme customization code.")
            }
        }
    }

    private suspend fun saveSetting(key: String, value: String) {
        db.settingDao().insertSetting(SettingEntity(key, value))
    }
}
