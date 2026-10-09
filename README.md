# EMG Companion

Kotlin Multiplatform MVP für eine EMG-Messwertanzeige auf Android und iOS. Die Demo-Quelle liefert synthetische Werte. Die App ist kein Medizinprodukt und nicht für medizinische Entscheidungen bestimmt.

Veröffentlicht wird der Quellcode; vorgebaute APK- und IPA-Dateien werden nicht verteilt.

## Architektur

```text
Eingaben: Compose UI → ViewModel → MeasurementRepository → MeasurementDataSource
Zustand:  Compose UI ← StateFlow<EmgUiState> ← ViewModel ← Repository ← DataSource
```

- `composeApp` enthält die gemeinsame Compose-Oberfläche, den MVVM-Zustand und die Datenzugriffe. Die UI sendet Aktionen ans ViewModel und rendert dessen unveränderlichen `StateFlow`-Zustand.
- Der iOS-Host bindet den Compose-Controller an SwiftUI und reicht dessen Layout-Vorschläge weiter. `UILaunchScreen` aktiviert die native Geräteauflösung; ohne diesen Eintrag lief die App im 320 × 480-pt-Kompatibilitätsmodus.
- Das Repository wählt eine plattformneutrale Datenquelle aus. BLE-APIs bleiben in `KableBleMeasurementDataSource`; sie gelangen nicht in die Business-Logik.
- `StubMeasurementDataSource` erzeugt reproduzierbare Demo-Werte. `KableBleMeasurementDataSource` kapselt Scan, GATT-Verbindung, Notifications und Abbruch.
- Die Fallstudie enthält keine freigegebene Geräte- oder Paket-Spezifikation. Daher ist die BLE-Quelle nicht konfiguriert und wechselt bei einem Fehler nicht still zur Demo.

Kable wird als BLE-Transportbibliothek verwendet; eine eigene Plattform-BLE-Implementierung würde dieselben Scan- und GATT-Funktionen doppelt abbilden. Messprotokoll, Gerätekonfiguration und Einheiten bleiben ausdrücklich projektspezifisch.

## Lokal starten

Voraussetzungen: JDK 17. Für Android werden Android Studio und Android SDK 37 benötigt; für iOS ein Mac mit Xcode und installierter iOS-Simulator-Runtime.

**Android:** Projekt in Android Studio öffnen, Gradle Sync abwarten und `androidApp` ausführen. Ein Debug-APK lässt sich mit `./gradlew :androidApp:assembleDebug` bauen.

**iOS:** `iosApp/iosApp.xcodeproj` öffnen, das Scheme **EMGCompanion** und ein iPhone-Simulatorgerät wählen und **Run** starten. Xcode baut das gemeinsame Kotlin-Framework über Gradle; dafür müssen JDK 17 und Gradle-Abhängigkeiten verfügbar sein. Für den Simulator ist keine Apple-Signierung nötig.

Die gemeinsamen Unit-Tests startest du mit:

```bash
./gradlew :composeApp:allTests
```

Die Tests decken Stub-Daten, Repository-Auswahl, Zustandsabbildung, Pufferbegrenzung und Abbruch ab. Ein BLE-Hardwaretest ist ohne Geräteprotokoll und Testperipheral noch nicht möglich.
