/*
 * PATCH: Initialize offline-local mode in MainActivity.onCreate()
 * Replace the server initialization code with local database bootstrap.
 * Add this after line 172 (DATABASE section):
 */

    // OFFLINE MODE - Initialize local database for local-only operation
    OfflineModeUtil.initializeOfflineModeIfNeeded(sharedPrefs);
    AppDatabase localDb = AppDatabase.getAppDatabase(this);
    DatabaseInitializer.initializeIfEmpty(localDb);
    Log.i(TAG, "Offline mode enabled: Local database initialized");

/*
 * Replace line 154 (netUtil.createWebSocketClient()) with comment:
 */
    // netUtil.createWebSocketClient(); // DISABLED IN OFFLINE MODE

/*
 * Replace line 200 (updateGrocyApi()) with comment:
 */
    // updateGrocyApi(); // DISABLED IN OFFLINE MODE - Not needed for local-only operation
    grocyApi = new GrocyApi(getApplication()); // Initialize with default (empty) settings

/*
 * Replace lines 250-258 (CONFIG CHECK) with:
 */
    // Skip server compatibility check in offline mode
    Log.d(TAG, "Skipping server configuration in offline mode");
