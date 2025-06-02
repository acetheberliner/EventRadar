# TODO LIST

- MapActivty markers not displaying [SOLVED]
- Reset search to general view [DONE]
- Input type date for date [DONE]
- Better color choices [DONE]
- Map crash on second opening [SOLVED]
- Don't auto-show keyboard on landing
- Modern app icon
- Favourite order by date
- Auto zoom on searched city
- Reset search button height adj.
- UX/UI improvements

- Event details:
  - Title [ALREADY DONE]
  - Date [ALREADY DONE]
  - City [ALREADY DONE]
  - Description
  - External Link
  - Contacts

- (?) Filter events expired
- (?) Page animations (fade-in/out)
- (?) Next events from now on button

- Clean project
- Clean comments

ricapitolando: per ora l'app permette di vedere una lista di eventi eterogenea caratterizzati da un nome, un luogo e una data, di aprirle per visualizzarne una pagina di dettaglio
anche se di base non c'è ancora scritto nulla di preciso su ogni evento, bisognerà aggiungere questa colonna al db e una sezione dedicata nell'xml, giusto per aggiungere 
completezza. e questa è la api 1/2 richiesta. poi l'utente ha la possibilità di cercare e filtrare gli eventi in base a una data e un luogo (api call 2/2) visualizzando una lista
di card filtrata. nella vista di dettaglio di ogni evento è possibile salvarlo nei preferiti locali dell'app accessibili dal button presente nell'interfaccia della homepage che elenca
tutti gli eventi salvati come preferiti e permette di rimuovere la preferenza. inoltre se per una città in quella data non ci sono eventi si ha una chiamata api di fallback
(api call 3/2) ad altri eventi della stessa città i date diverse. infine vi è la possibilità di visualizzare su mappa google questi eventi tramite marker inseriti dinamicamente dall'app
cliccando sui quali si ottengono informazioni relative al titolo dell'evento e alla data.