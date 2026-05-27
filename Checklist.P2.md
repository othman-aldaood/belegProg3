# Prototyp 2: CLI
Erstellen Sie ein prototypisches CLI für den Beleg mit 4 Befehlen (und ggf. ohne Modi): ein Befehl zum Einfügen eines (vordefinierten) Frachtstücks, ein Befehl zum Anzeigen der Frachtstücke, ein Befehl zum Ändern des Inspektionsdatums eines Frachtstücks und ein Befehl zum Entfernen eines Frachtstücks.

Weitere Informationen stehen im Anforderungsdokument unter der Überschrift CLI. Der Persistenzmodus sowie die Ausführung als Client bzw. Server sind nicht Teil dieses Prototyps.

## Abgabeanforderungen
Die Abgabe hat als zip-Datei zu erfolgen, die ein lauffähiges IntelliJ-IDEA-Projekt enthält. Dafür kann der Projektordner direkt in das zip eingepackt werden. Sie sollte die befüllte Checkliste im root des Projektes (neben der iml-Datei) enthalten in der der erreichte Stand bezüglich des Bewertungsschemas vermerkt ist.

Änderungen an der Checkliste sind grundsätzlich nicht zulässig. Davon ausgenommen ist das Befüllen der Checkboxen und ergänzende Anmerkungen die _kursiv gesetzt_ sind.

## Quellen
Zulässige Quellen sind suchmaschinen-indizierte Internetseiten und LLMs. Werden mehr als drei zusammenhängende Anweisungen übernommen ist die Quelle in den Kommentaren anzugeben. Ausgeschlossen sind Quellen, die auch in dieser LV abgegeben werden oder wurden. Zulässig sind außerdem die über moodle bereitgestellten Materialien, diese können für die LV ohne Quellenangabe verwendet werden.
Flüchtige Quellen, wie LLMs, sind nachvollziehbar zu dokumentieren.

## Bewertung
0 Punkte wenn die grundsätzlichen Anforderungen nicht erfüllt sind. 1 Punkt für die Erfüllung der Basisanforderung und darauf aufbauend je ein Punkt für die nummerierten Anforderungen.
### grundsätzliche Anforderungen
- [x] Quellen angegeben _(LLM genutzt in den Kommentaren dokumentiert. Weitere Dokumentationen: https://docs.oracle.com/en/java/, https://www.w3schools.com/java/default.asp, https://junit.org/)_
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
- [x] CLI-Implementierung für CRUD für einen Typ
- [x] Dokumentation des Befehlssatzes inkl. Beispiele, falls abweichend zu Anforderungsdokument

### 1 einfaches event-System
- [x] event-System für die Kommunikation vom CLI zur GL realisiert

### 2 Beobachtermuster
- [x] ein Beobachter gemäß Anforderungen realisiert

### 3 erweitertes event-System
abhängig von 1
- [x] event-System für die Kommunikation von der GL zum CLI realisiert

### 4 getestetes event-System
abhängig von 3
- [x] für einen geforderten Anwendungsfall alle beteiligten Methoden getestet, von der Eingabe auf System.in bis zur Ausgabe auf System.out