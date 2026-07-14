# Prototyp 6: Netzwerk
Erweitern Sie das CLI als Client-Server-Lösung. Der Client soll dabei die Oberfläche zur Bedienung realisieren und der Server die Geschäftslogik enthalten.

Client und Server haben jeweils eine eigene main-Methode (IntelliJ kann mehrere Applikationen parallel ausführen).

Weitere Informationen stehen im Anforderungsdokument unter der Überschrift Net.

## Abgabeanforderungen
Die Abgabe hat als zip-Datei zu erfolgen, die ein lauffähiges IntelliJ-IDEA-Projekt enthält. Sie sollte die befüllte Checkliste im root des Projektes (neben der iml-Datei) enthalten in der der erreichte Stand bezüglich des Bewertungsschemas vermerkt ist.

Änderungen an der Checkliste sind grundsätzlich nicht zulässig. Davon ausgenommen ist das Befüllen der Checkboxen und ergänzende Anmerkungen die _kursiv gesetzt_ sind.

## Quellen
Zulässige Quellen sind suchmaschinen-indizierte Internetseiten und LLMs. Werden mehr als drei zusammenhängende Anweisungen übernommen ist die Quelle in den Kommentaren anzugeben. Ausgeschlossen sind Quellen, die auch in dieser LV abgegeben werden oder wurden. Zulässig sind außerdem die über moodle bereitgestellten Materialien, diese können für die LV ohne Quellenangabe verwendet werden.
Flüchtige Quellen, wie LLMs, sind nachvollziehbar zu dokumentieren.

## Bewertung
0 Punkte wenn die grundsätzlichen Anforderungen nicht erfüllt sind. 1 Punkt für die Erfüllung der Basisanforderung und darauf aufbauend je ein Punkt für die nummerierten Anforderungen.

### grundsätzliche Anforderungen
- [x] Quellen angegeben _(LLM als Hilfe genutzt,Code Kommntar, Test fehler zu anaylsieren )_
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
- [x] Trennung zwischen Oberfläche (Client) und Geschäftslogik (Server) _(ServerMain haelt die GL, Main als Client, eigene Prozesse)_
- [x] TCP- oder UDP-Implementierung für CRUD für einen Typ _(textbasiertes Protokoll ueber CommandProcessor im Modul net)_

### 1 Mockito
- [x] je ein Stellvertreter-Test für Einfügen und Anzeigen pro implementierten Server _(CommandProcessorTest; TCP- und UDP-Server verwenden denselben CommandProcessor)_

### 2 beide Technologien
- [x] Implementierung von Client und Server für TCP und UDP _(UDP mit Request-IDs, Timeout und erneutem Senden gegen Paketverlust und Umsortierung)_

### 3 Nebenläufigkeit
- [x] Unterstützung mehrerer konkurierender Clients (TCP oder UDP) _(TCP: ein Thread pro Client, gemeinsame GL)_