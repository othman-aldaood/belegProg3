# Quellenverzeichnis

Das Projekt ist in Eigenleistung über die Prototypen 1-6 entstanden. Die
folgenden Quellen wurden zum Lernen und als Nachschlagewerk verwendet; die
LLM-Verwendung ist im letzten Abschnitt nachvollziehbar dokumentiert.

## Kurse und Tutorials

- JetBrains IntelliJ IDEA Hilfe, GUI-Design:
  https://www.jetbrains.com/help/idea/design-gui-using-swing.html
- LinkedIn Learning, "JavaFX Grundkurs":
  https://www.linkedin.com/learning/javafx-grundkurs
- LinkedIn Learning, "TCP und UDP":
  https://www.linkedin.com/learning/tcp-und-udp
- LinkedIn Learning, "JUnit: Tests erstellen und ausführen":
  https://www.linkedin.com/learning/junit-tests-erstellen-und-ausfuhren

## Moodle-Materialien
Die über Moodle bereitgestellten Materialien der Lehrveranstaltung (Folien und
Beispiele) wurden verwendet; sie dürfen laut Aufgabenstellung ohne gesonderte
Quellenangabe genutzt werden. Einzelne Verweise (z.B. "Folie 58", "Folie 63")
sind im Quellcode kommentiert.

## Java-Dokumentation
Oracle Java SE Dokumentation: https://docs.oracle.com/en/java/

Verwendet für die Standard-API, insbesondere: Collections, Concurrency
(synchronized, wait/notify, ScheduledExecutorService), Java I/O
(ObjectOutputStream/ObjectInputStream, XMLEncoder/XMLDecoder), Netzwerk
(Socket/ServerSocket, DatagramSocket) und JavaFX (TableView, Properties,
Task, Drag&Drop).

## LLM-Verwendung (flüchtige Quelle)
Gemäß Aufgabenstellung wird die Verwendung von LLMs als Quelle nachvollziehbar
dokumentiert. Als Hilfsmittel wurde Claude (Anthropic) verwendet; Screenshots
der Sitzungen liegen im Ordner "Quellen" bei. Mit LLM-Unterstützung erstellt
bzw. überarbeitet wurden insbesondere:

- domainLogic/tst: WarehouseManagerTest, CustomerImplTest, DryBulkCargoImplTest,
  UnitisedCargoImplTest, DryBulkAndUnitisedCargoImplTest
- domainLogic/src: copy-Methoden der Cargo-Implementierungen (Prototype-Muster),
  Wiederherstellungs-Konstruktor und defensive Kopien im WarehouseManager,
  Beobachterlisten und Benachrichtigungen
- contract/src/events: HazardObserver, ChangeObserver
- io/src: WarehouseSnapshot; Umstellung der JBP-Persistenz auf das Snapshot-
  Transferobjekt; io/tst: JOSPersistenceTest, JBPPersistenceTest (Roundtrips)
- cli: zweiter Beobachter im ConsoleClient; cli/tst: Ergänzungen im
  ConsoleClientTest
- sim: Überarbeitung von Konsument, Konsument3, Updater3, Produzent3;
  RandomCargoGeneratorTest, Produzent3Test
- gui: Überarbeitung von WarehouseController und Warehouse.fxml
- Root: Anpassungen im setup von Main, MainIO, AlternativeMain, Simulation1-3

Alle Inhalte wurden geprüft, getestet und verantwortet abgegeben.
