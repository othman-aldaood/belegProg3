# Prototyp 5: I/O
Realisieren Sie die Funktionalität den Zustand der Geschäftslogik zu laden und zu speichern.
Erstellen Sie zur Demonstration eine main-Methode die eine Verwaltung mit Frachtstücke befüllt und den Zustand ausgibt. Anschließend soll sie gespeichert und geladen werden und der Zustand erneut ausgegeben werden.

Änderungen am Vertrag sind ab jetzt zulässig. Dabei müssen Funktionsumfang, Kapselung und die Erweiterbarkeit erhalten bleiben.

Weitere Informationen stehen im Anforderungsdokument unter der Überschrift I/O.

## Abgabeanforderungen
Die Abgabe hat als zip-Datei zu erfolgen, die ein lauffähiges IntelliJ-IDEA-Projekt enthält. Sie sollte die befüllte Checkliste im root des Projektes (neben der iml-Datei) enthalten in der der erreichte Stand bezüglich des Bewertungsschemas vermerkt ist.

Änderungen an der Checkliste sind grundsätzlich nicht zulässig. Davon ausgenommen ist das Befüllen der Checkboxen und ergänzende Anmerkungen die _kursiv gesetzt_ sind.

## Quellen
Zulässige Quellen sind suchmaschinen-indizierte Internetseiten und LLMs. Werden mehr als drei zusammenhängende Anweisungen übernommen ist die Quelle in den Kommentaren anzugeben. Ausgeschlossen sind Quellen, die auch in dieser LV abgegeben werden oder wurden. Zulässig sind außerdem die über moodle bereitgestellten Materialien, diese können für die LV ohne Quellenangabe verwendet werden.
Flüchtige Quellen, wie LLMs, sind nachvollziehbar zu dokumentieren.

## Bewertung
0 Punkte wenn die grundsätzlichen Anforderungen nicht erfüllt sind. 1 Punkt für die Erfüllung der Basisanforderung und darauf aufbauend je ein Punkt für die nummerierten Anforderungen.

### grundsätzliche Anforderungen
- [x] Quellen angegeben _(LLM als Hilfe genutzt um ProjektStrukturظFehler zu heben, Code Kommenta)_
- [x] Abgabe als zip-Archiv mit dem Projekt im root
- [x] IntelliJ-Projekt (kein Gradle, Maven o.ä.)
- [x] keine weiteren Bibliotheken außer JUnit5, Mockito und JavaFX (und deren Abhängigkeiten)
- [x] keine Umlaute, Sonderzeichen, etc. in Datei- und Pfadnamen
- [x] kompilierbar
- [x] Trennung zwischen Test- und Produktiv-Code
- [x] geforderte main-Methoden nur im default package des Moduls belegProg3, nicht in den Submodulen
- [x] keine vorgetäuschte Funktionalität (inkl. leere Tests)
- [x] ausführbar

### Basisanforderung
- [x] Speichern und Laden der Geschäftslogik mit JOS oder JBP
- [x] main-Methode zur Demonstration der Persistierung _(MainIO)_

### 1 Integration
- [x] Persistierung und Geschäftslogik korrekt aufgeteilt _(eigenes Modul io, Verdrahtung ueber PersistenceCommandListener im setup)_
- [x] Einbindung der Persistierung im CLI oder GUI _(CLI, Persistenzmodus :p mit save/load [JOS|JBP])_

### 2 Mockito
abhängig von 1
- [x] Stellvertreter-Tests für das Speichern und Laden _(ConsoleClientTest, JOSPersistenceTest, JBPPersistenceTest)_

### 3 beide Technologien
abhängig von 1
- [x] Speichern und Laden der Geschäftslogik mit JOS **und** JBP
