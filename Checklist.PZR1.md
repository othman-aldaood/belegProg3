# Beleg SS 26 PZR1 (100)
Checkboxen befüllen und _kursiv_ gesetzten Text durch entsprechende Angaben ersetzten.
Bei keiner Angabe wird nur Entwurf, Testqualität, Testabdeckung GL, Fehlerfreiheit und Basisfunktionalität bewertet.
Die Zahl in der Klammer sind die jeweiligen Punkte für die Bewertung.
Die empfohlenen Realisierungen zum Bestehen der Prüfung sind **fett** gesetzt.
Ergänzende Anmerkungen bitte immer _kursiv_ setzen. Andere Änderungen, außer Befüllen der Checkboxen, sind nicht zulässig.

## Voraussetzungen für die Bewertung
- zur Prüfung via LSF angemeldet
- Prüfungszulassung erreicht

## Voraussetzungen für das Bestehen
- [x] Quellen angegeben
- [x] Abgabe als zip-Archiv mit dem Projekt im root
- [x] IntelliJ-Projekt (kein Gradle, Maven o.ä.)
- [x] keine weiteren Bibliotheken außer JUnit5, Mockito und JavaFX (und deren Abhängigkeiten)
- [x] keine Umlaute, Sonderzeichen, etc. in Datei- und Pfadnamen
- [x] kompilierbar
- [x] Trennung zwischen Test- und Produktiv-Code
- [x] implementierte main-Methoden nur im default package des Moduls belegProg3, nicht in den Submodulen
  - [x] CLI
  - [x] alternativ konfiguriertes CLI
  - [x] je eine für jede Simulation
  - [x] GUI
  - [x] Server
- [x] keine vorgetäuschte Funktionalität (inkl. leere und trivial tautologische Tests)
- [x] ausführbar

## Prototypen (max. 10)
- GL
- CLI
- Sim
- GUI
- I/O
- Net

## Entwurf (8)
- [x] **Benennung** (1)
- [x] **Zuständigkeit** (2)
- [x] **Paketierung** (1)
- [x] **Schichtenaufteilung (via modules)** (2)
  - _module: contract, domainLogic, cli, sim, gui, io, net, log_
- [x] **keine Verwendung von reflection inkl. down casts** (1)
  - nur zulässig beim Lesen aus streams und GUI
  - _casts ausschliesslich beim Lesen aus streams (JOS/JBP in JOSPersistence bzw. JBPPersistence)_
- [x] keine Duplikate (außer in den Tests und Setups) (1)

## Tests (28)
- [x] **Testqualität** (7)
- [x] **Testabdeckung GL inkl. Abhängigkeiten (100% additiv)** (6) _100% Klassen (5/5), 100% Methoden (100/100), 100% Zeilen (321/321), 100% Zweige (138/138)_
- [x] Testabdeckung Rest (beteiligte Methoden jeweils 100% additiv) (5)
  - [x] Einfügen von Kund*innen über das CLI _ConsoleClient (ConsoleClientTest)_
  - [x] Anzeigen von Kund*innen über das CLI _ConsoleClient (ConsoleClientTest)_
  - [x] ein Beobachter _WarehouseManager mit CapacityObserver, HazardObserver und ChangeObserver (WarehouseManagerTest); LoggingChangeObserver (LoggingChangeObserverTest)_
  - [x] deterministische Funktionalität der Simulationen _Produzent, Konsument, Produzent3, Konsument3, Updater3, RandomCargoGenerator (ProduzentTest, KonsumentTest, Produzent3Test, Konsument3Test, Updater3Test, RandomCargoGeneratorTest)_
  - [x] Speichern und Laden via JOS oder JBP ohne UI _JOSPersistence, JBPPersistence, WarehouseSnapshot (JOSPersistenceTest, JBPPersistenceTest)_
- [x] **mindestens 5 Unittests, die Mockito verwenden** (5)
  - _u.a. in WarehouseManagerTest, ConsoleClientTest, CommandProcessorTest, LoggingCommandListenerTest, ProduzentTest_
- [x] mindestens 4 Spy- / Verhaltens-Tests (4)
  - _Verhaltenstests mit Mockito.verify, u.a. in CommandProcessorTest (4), ConsoleClientTest (3), LoggingCommandListenerTest (8), WarehouseManagerTest_
- [x] **keine unbeabsichtigt fehlschlagenden Test** (1)

## Fehlerfreiheit (10)
- [x] **Kapselung** (5)
  - _die GL gibt ausschliesslich tiefe Kopien heraus (copy-Methoden der Cargo-Implementierungen); es gibt keine oeffentlichen setter am WarehouseManager_
- [x] **keine Ablauffehler** (5)

## Basisfunktionalität (12)
- [x] **CRUD** (2)
- [x] **CLI** (2)
  - Syntax gemäß Anforderungen
- [x] **Simulation** (2)
  - ohne race conditions
- [x] **GUI** (2)
- [x] **I/O** (2)
  - in CLI oder GUI integriert
  - _im CLI im Persistenzmodus (:p) integriert_
- [x] **Net** (2)

## Funktionalität (22)
- [x] vollständige GL (2)
- [x] threadsichere GL (1)
- [x] vollständiges CLI (1)
- [x] alternativ konfiguriertes CLI (1)
  - _in AlternativeMain deaktiviert: Loeschen von Kund*innen und Auflisten der Gefahrenstoffe; nur ein Beobachter aktiv (Kapazitaetswarnung)_
- [x] ausdifferenziertes event-System mit mindestens 3 events (2)
  - _CargoCommandListener, GLFeedbackListener, PersistenceCommandListener, CapacityObserver, HazardObserver, ChangeObserver_
- [x] observer (2)
  - _CapacityObserver (ab 90% Kapazitaet) und HazardObserver (Aenderung der Gefahrenstoffe); die Beobachter erhalten nur das Signal und fragen den Zustand selbst ab_
- [x] bzgl. den Anforderungen angemessene Typen der collections (2)
- [x] Simulation 2 (1)
- [x] Simulation 3 (1)
- [x] skalierbare GUI (1)
- [x] vollständige GUI (1)
- [x] FXML und data binding verwendet (1)
- [x] Änderung der storageLocation mittels drag&drop (1)
- [x] Einfügen via GUI erfolgt nebenläufig (1)
- [x] sowohl JBP als auch JOS (2)
- [x] sowohl TCP als auch UDP (1)
- [x] Server unterstützt konkurrierende Clients für TCP oder UDP (1)
  - _fuer TCP (eigener Thread je Client) und fuer UDP (datagrammweise Bearbeitung)_

## zusätzliche Anforderungen (10)
- [x] Logeinträge für Nachrichten an die GL (2)
  - _LoggingCommandListener im module log_
- [x] Logeinträge für Änderungen an der GL (2)
  - _LoggingChangeObserver im module log_
- [x] Trennung zwischen bestehender Implementierung und Log (2)
  - _das Log wird nur im setup der main-Methode eingehangen; keine andere Klasse kennt das module log_
- [x] geschützter Zugriff auf die Logdatei inkl. Threadsicherheit (2)
  - _LogWriter besitzt die Datei log.txt und schreibt synchronisiert_
- [x] Mehrsprachigkeit (1)
  - _Aktivierung ueber das Kommandozeilenargument DE oder EN; Deutsch vollstaendig, Englisch prototypisch mit vier Eintraegen_
- [x] erweiterbare Mehrsprachigkeit (1)
  - _die Texte liegen in Properties-Dateien (LogTexts_&lt;Sprachkuerzel&gt;.properties); eine weitere Sprache erfordert nur eine weitere Datei, keine Codeaenderung_
