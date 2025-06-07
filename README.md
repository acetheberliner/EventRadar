<img src="https://github.com/user-attachments/assets/b8c493c0-3d49-411d-8d6a-046097e24ffd" alt="Event Radar" width="80" /> **Event Radar**

---

## EventRadar è un'applicazione Android sviluppata per aiutare gli utenti a scoprire eventi di vario tipo (culturali, musicali e pubblici) in Italia, 
in base a una località e a una data specifica inserita. L’app consente di visualizzare i risultati sia in forma di lista interattiva che tramite una mappa con marker geolocalizzati.

### Il progetto è stato realizzato come progetto individuale.

---

## Funzionalità principali implementate
- Ricerca eventi tramite chiamate a servizi API remoti
- Visualizzazione degli eventi in una lista interattiva con immagini e dettagli
- Vista dettagliata di ogni evento con:
  - Nome
  - Data
  - Città
  - Descrizione
  - Link cliccabile
  - Contatti
    
- Mappa interattiva con geocodifica automatica dei luoghi e posizionamento dei marker
- Gestione avanzata dei marker:
  - Marker raggruppati per città
  - Marker giallo se almeno un evento della città è tra i preferiti, altrimenti rosso
  - Marker che visualizza il numero di eventi e i dettagli relativi
    
- Salvataggio degli eventi preferiti consultabili in una sezione dedicata
- Possibilità di rimuovere i preferiti
- Ordinamento dei preferiti per data tramite switch
- Filtro per mostrare solo eventi futuri
- Filtro "Solo eventi futuri" anche nella lista generale
- Evidenziazione degli eventi passati nella lista
- Ricerca con fallback intelligente:
  - Se non ci sono eventi per una data specifica, vengono mostrati altri eventi della stessa città in date differenti
    
- Gestione dei permessi per localizzazione
- Interfaccia coerente, user-friendly e ottimizzata per smartphone
- Persistenza locale dei dati preferiti tramite Room DB

---

## Requisiti per l’esecuzione

- Android Studio (API 27+)
- XAMPP per ambiente backend locale (PHP + MySQL)

## Come avviare il progetto

- Avviare XAMPP e attivare Apache e MySQL
- Accedere a phpMyAdmin (http://localhost/phpmyadmin)
- Eseguire il file createSb.sql fornito con il progetto per creare e popolare il database con la tabella events
- Copiare la cartella api/ fornita in:
  - C:\xampp\htdocs\eventradar-api\
- Verificare che l'API sia accessibile via browser:
  - http://localhost/eventradar-api/get_events.php
- Avviare l'emulatore o un dispositivo reale e installare l'app

---

## Note tecniche

- Architettura: MVVM
- Database locale: Room
- Comunicazione con API: Retrofit
- Gestione immagini: Glide
- Mappa: Google Maps SDK
- Geocoding: Google Maps Geocoding API
- Persistenza: LiveData + Room
