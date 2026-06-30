# Prototyp 4: GUI
Erstellen Sie die graphische Oberfläche für die Geschäftslogik.

## Abgabeanforderungen
Die Abgabe hat als zip-Datei zu erfolgen, die ein lauffähiges IntelliJ-IDEA-Projekt enthält. Sie sollte die befüllte Checkliste im root des Projektes (neben der iml-Datei) enthalten in der der erreichte Stand bezüglich des Bewertungsschemas vermerkt ist.

Änderungen an der Checkliste sind grundsätzlich nicht zulässig. Davon ausgenommen ist das Befüllen der Checkboxen und ergänzende Anmerkungen die _kursiv gesetzt_ sind.

## Quellen
Zulässige Quellen sind suchmaschinen-indizierte Internetseiten und LLMs. Werden mehr als drei zusammenhängende Anweisungen übernommen ist die Quelle in den Kommentaren anzugeben. Ausgeschlossen sind Quellen, die auch in dieser LV abgegeben werden oder wurden. Zulässig sind außerdem die über moodle bereitgestellten Materialien, diese können für die LV ohne Quellenangabe verwendet werden.
Flüchtige Quellen, wie LLMs, sind nachvollziehbar zu dokumentieren.

## Bewertung
0 Punkte wenn die grundsätzlichen Anforderungen nicht erfüllt sind. 1 Punkt für die Erfüllung der Basisanforderung und darauf aufbauend je ein Punkt für die nummerierten Anforderungen.

### grundsätzliche Anforderungen
- [x] Quellen angegeben (In den Kommentaren im Code angeben, dass LLMs als Hilfe genutzt wurden)
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
- [x] Benutzeroberfläche und Geschäftslogik korrekt aufgeteilt (mindestens 2-Schichten-Architektur)
- [x] GUI-Implementierung für CRUD für einen Typ

### 1 Ausbau
- [x] Auflistungen sind immer sichtbar und werden automatisch aktualisiert
- [x] sortierbare Darstellung gemäß Anforderungen

### 2 FXML
abhängig von 1
- [x] FXML verwendet
- [x] data binding verwendet
- [x] skalierbare Darstellung


### 3 Nebenläufigkeit
abhängig von 2
- [x] Benutzeroberfläche wird beim Einfügen nicht gesperrt

### 4 drag&drop
abhängig von 2
- [x] Austausch der Abrufadressen mittels drag&drop

