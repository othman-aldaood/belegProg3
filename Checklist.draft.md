# Beleg SS 26 draft (100)
Checkboxen befüllen und _kursiv_ gesetzten Text durch entsprechende Angaben ersetzten.
Bei keiner Angabe wird nur Entwurf, Testqualität, Testabdeckung GL, Fehlerfreiheit und Basisfunktionalität bewertet.
Die Zahl in der Klammer sind die jeweiligen Punkte für die Bewertung.
Die empfohlenen Realisierungen zum Bestehen der Prüfung sind **fett** gesetzt.
Ergänzende Anmerkungen bitte immer _kursiv_ setzen. Andere Änderungen, außer Befüllen der Checkboxen, sind nicht zulässig.

## Voraussetzungen für die Bewertung
- zur Prüfung via LSF angemeldet
- Prüfungszulassung erreicht

## Voraussetzungen für das Bestehen
- [ ] Quellen angegeben
- [ ] Abgabe als zip-Archiv mit dem Projekt im root
- [ ] IntelliJ-Projekt (kein Gradle, Maven o.ä.)
- [ ] keine weiteren Bibliotheken außer JUnit5, Mockito und JavaFX (und deren Abhängigkeiten)
- [ ] keine Umlaute, Sonderzeichen, etc. in Datei- und Pfadnamen
- [ ] kompilierbar
- [ ] Trennung zwischen Test- und Produktiv-Code
- [ ] implementierte main-Methoden nur im default package des Moduls belegProg3, nicht in den Submodulen
  - [ ] CLI
  - [ ] alternativ konfiguriertes CLI
  - [ ] je eine für jede Simulation
  - [ ] GUI
  - [ ] Server
- [ ] keine vorgetäuschte Funktionalität (inkl. leere und trivial tautologische Tests)
- [ ] ausführbar

## Prototypen (max. 10)
- GL
- CLI
- Sim
- GUI
- I/O
- Net

## Entwurf (8)
- [ ] **Benennung** (1)
- [ ] **Zuständigkeit** (2)
- [ ] **Paketierung** (1)
- [ ] **Schichtenaufteilung (via modules)** (2)
- [ ] **keine Verwendung von reflection inkl. down casts** (1)
  - nur zulässig beim Lesen aus streams und GUI
- [ ] keine Duplikate (außer in den Tests und Setups) (1)

## Tests (28)
- [ ] **Testqualität** (7)
- [ ] **Testabdeckung GL inkl. Abhängigkeiten (100% additiv)** (6) _Abdeckung in Prozent angeben_
- [ ] Testabdeckung Rest (beteiligte Methoden jeweils 100% additiv) (5)
  - [ ] Einfügen von Kund*innen über das CLI _getestete Klassen angeben_
  - [ ] Anzeigen von Kund*innen über das CLI _getestete Klassen angeben_
  - [ ] ein Beobachter _getestete Klassen angeben_
  - [ ] deterministische Funktionalität der Simulationen _getestete Klassen angeben_
  - [ ] Speichern und Laden via JOS oder JBP ohne UI_getestete Klassen angeben_
- [ ] **mindestens 5 Unittests, die Mockito verwenden** (5)
- [ ] mindestens 4 Spy- / Verhaltens-Tests (4)
- [ ] **keine unbeabsichtigt fehlschlagenden Test** (1)

## Fehlerfreiheit (10)
- [ ] **Kapselung** (5)
- [ ] **keine Ablauffehler** (5)

## Basisfunktionalität (12)
- [ ] **CRUD** (2)
- [ ] **CLI** (2)
  - Syntax gemäß Anforderungen
- [ ] **Simulation** (2)
  - ohne race conditions
- [ ] **GUI** (2)
- [ ] **I/O** (2)
  - in CLI oder GUI integriert
- [ ] **Net** (2)

## Funktionalität (22)
- [ ] vollständige GL (2)
- [ ] threadsichere GL (1)
- [ ] vollständiges CLI (1)
- [ ] alternativ konfiguriertes CLI (1)
  - _hier oder im source code angeben welche Funktionalität im alternativ konfigurierten CLI deaktiviert_
- [ ] ausdifferenziertes event-System mit mindestens 3 events (2)
- [ ] observer (2)
- [ ] bzgl. den Anforderungen angemessene Typen der collections (2)
- [ ] Simulation 2 (1)
- [ ] Simulation 3 (1)
- [ ] skalierbare GUI (1)
- [ ] vollständige GUI (1)
- [ ] FXML und data binding verwendet (1)
- [ ] Änderung der storageLocation mittels drag&drop (1)
- [ ] Einfügen via GUI erfolgt nebenläufig (1)
- [ ] sowohl JBP als auch JOS (2)
- [ ] sowohl TCP als auch UDP (1)
- [ ] Server unterstützt konkurrierende Clients für TCP oder UDP (1)

## zusätzliche Anforderungen (10)
