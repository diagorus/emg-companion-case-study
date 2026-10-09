# EMG Companion

Kotlin-Multiplatform-App zur Anzeige von EMG-Messwerten unter Android und iOS. Die Demo-Quelle erzeugt synthetische Werte.

## Architektur

```text
Nutzeraktionen: Compose UI → ViewModel → Repository → Datenquelle
Messwerte:      Compose UI ← ViewModel ← Repository ← Datenquelle
```

Die Oberfläche zeigt den Zustand des ViewModels an und leitet Nutzeraktionen an es weiter. Das Repository wählt die Demo- oder BLE-Datenquelle. Kable dient als BLE-Transport. Da die Fallstudie kein konkretes Geräteprotokoll vorgibt, ist die BLE-Quelle noch nicht für ein bestimmtes Gerät konfiguriert.

## Screenshots

Die Aufnahmen zeigen Start, laufende Simulation, fehlende BLE-Gerätekonfiguration und den Stopp mit erhaltenem Signalverlauf.

| Zustand | Android | iOS |
| --- | --- | --- |
| Start | <a href="docs/screenshots/android/start.png"><img src="docs/screenshots/android/start.png" alt="Android-App im Startzustand" width="260"></a> | <a href="docs/screenshots/ios/start.png"><img src="docs/screenshots/ios/start.png" alt="iOS-App im Startzustand" width="260"></a> |
| Simulation läuft | <a href="docs/screenshots/android/live.png"><img src="docs/screenshots/android/live.png" alt="Android-App mit laufender Simulation" width="260"></a> | <a href="docs/screenshots/ios/live.png"><img src="docs/screenshots/ios/live.png" alt="iOS-App mit laufender Simulation" width="260"></a> |
| BLE-Gerät nicht konfiguriert | <a href="docs/screenshots/android/ble-error.png"><img src="docs/screenshots/android/ble-error.png" alt="Android-App mit BLE-Konfigurationsfehler" width="260"></a> | <a href="docs/screenshots/ios/ble-error.png"><img src="docs/screenshots/ios/ble-error.png" alt="iOS-App mit BLE-Konfigurationsfehler" width="260"></a> |
| Gestoppt, Verlauf erhalten | <a href="docs/screenshots/android/stopped.png"><img src="docs/screenshots/android/stopped.png" alt="Android-App nach dem Stoppen mit erhaltenem Verlauf" width="260"></a> | <a href="docs/screenshots/ios/stopped.png"><img src="docs/screenshots/ios/stopped.png" alt="iOS-App nach dem Stoppen mit erhaltenem Verlauf" width="260"></a> |

## Lokal starten

Voraussetzungen: JDK 17, Android Studio mit Android SDK 37 sowie für iOS macOS, Xcode und eine iOS-Simulator-Runtime.

**Android:** Projekt in Android Studio öffnen, Gradle Sync abwarten und `androidApp` ausführen.

**iOS:** `iosApp/iosApp.xcodeproj` öffnen, das Scheme **EMGCompanion** und einen iPhone-Simulator wählen, dann **Run** starten.

Die gemeinsamen Unit-Tests startest du mit:

```bash
./gradlew :composeApp:allTests
```
