# Play Billing setup (BM3)

Product ID in code: `premium_lifetime` (one-time in-app product).

## Play Console
1. App with applicationId `com.buckmanager.app`
2. Monetize with Play → Products → In-app products → create `premium_lifetime`
3. Set price (e.g. IDR 15.000), activate
4. Upload a signed build to internal testing (Billing needs Play)
5. Add license testers for sandbox buys

## App behavior
- Purchase button opens Play Billing flow (no more free unlock)
- Owned purchases restored on startup via `BillingManager.refreshPurchases()`
- `watchAd()` is a stub until AdMob is wired (does not grant premium)

## Local debug
Sideloaded debug APKs often cannot talk to Play Billing. Use an internal-test track install for real purchase tests.
