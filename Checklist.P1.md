# Prototyp 1: GL
Erstellen Sie die Geschäftslogik des Belegs prototypisch und testen Sie exemplarisch. Für diesen Prototyp müssen nicht alle Anforderungen realisiert werden aber mindestens Einfügen, Auflisten, Ändern (Inspektionsdatum) und Entfernen (CRUD) für mindestens einen Typ von den im Vertrag vordefinierten Frachtstücken, z.B. DryBulkCargo.

Weitere Informationen stehen im Anforderungsdokument unter der Überschrift GL.

## Abgabeanforderungen
Die Abgabe hat als zip-Datei zu erfolgen, die ein lauffähiges IntelliJ-IDEA-Projekt enthält. Dafür kann der Projektordner direkt in das zip eingepackt werden. Sie sollte die befüllte Checkliste im root des Projektes (neben der iml-Datei) enthalten in der der erreichte Stand bezüglich des Bewertungsschemas vermerkt ist.

Änderungen an der Checkliste sind grundsätzlich nicht zulässig. Davon ausgenommen ist das Befüllen der Checkboxen und ergänzende Anmerkungen die _kursiv gesetzt_ sind.

## Quellen
Zulässige Quellen sind suchmaschinen-indizierte Internetseiten und LLMs. Werden mehr als drei zusammenhängende Anweisungen übernommen ist die Quelle in den Kommentaren anzugeben. Ausgeschlossen sind Quellen, die auch in dieser LV abgegeben werden oder wurden. Zulässig sind außerdem die über moodle bereitgestellten Materialien, diese können für die LV ohne Quellenangabe verwendet werden.
Flüchtige Quellen, wie LLMs, sind nachvollziehbar zu dokumentieren.

## Bewertung
0 Punkte wenn die grundsätzlichen Anforderungen nicht erfüllt sind. 1 Punkt für die Erfüllung der Basisanforderung und darauf aufbauend je ein Punkt für die nummerierten Anforderungen.

### grundsätzliche Anforderungen
- [X] Quellen angegeben
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
- [x] CRUD für einen Typ
- [x] mindestens ein Test

### 1 Ausbau GL
- [x] Einfügen der Frachtstücke vollständig implementiert (siehe Anforderungsdokument)
- [x] Unterstützung von mindestens zwei Frachttypen

### 2 Testabdeckung
abhängig von 1
- [x] Einfügen der Frachtstücke vollständig getestet (mindestens Pfadabdeckung)

### 3 Stellvertretertests
abhängig von 1
- [x] mindestens zwei Tests mit Mockito

### 4 vollständige GL
abhängig von 1
- [x] GL erfüllt alle Anforderungen lt. Anforderungsdokument

