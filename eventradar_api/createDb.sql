CREATE DATABASE eventradar;
USE eventradar;

CREATE TABLE events (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    location VARCHAR(100) NOT NULL,
    imageUrl TEXT,
    description TEXT,
    externalLink TEXT,
    contacts TEXT
);

INSERT INTO events VALUES
('1', 'Fiera del Libro', '2025-05-07', 'Cesena',
 'https://images.unsplash.com/photo-1661009540490-315d16770038?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
 'Grande fiera del libro con autori e presentazioni.', 
 'https://www.fieralibrocesena.it', 
 'info@fieralibrocesena.it'),

('2', 'Concerto Jazz', '2025-05-07', 'Milano',
 'https://www.discoveringbellano.eu/wp-content/uploads/2022/07/jazz_sito.jpg',
 'Concerto dal vivo con i migliori musicisti jazz italiani.', 
 'https://www.milanojazzfestival.it', 
 'contatti@milanojazzfestival.it'),

('3', 'Sagra della Pizza', '2025-05-07', 'Napoli',
 'https://www.virtuquotidiane.it/wp-content/uploads/2017/06/pizzaforno-980x540.png',
 'Evento gastronomico dedicato alla pizza napoletana.', 
 'https://www.sagradellapizza.it', 
 'info@sagradellapizza.it'),

('4', 'Workshop Kotlin', '2025-05-08', 'Bologna',
 'https://mtmk.ams3.cdn.digitaloceanspaces.com/laravel/1908/public/posts/April2023/SwdWFW3XYV494I9cazv1.jpg',
 'Corso pratico di sviluppo app Android con Kotlin.', 
 'https://www.kotlinworkshopbologna.it', 
 'support@kotlinworkshopbologna.it'),

('5', 'Festival Teatro', '2025-05-08', 'Roma',
 'https://realefoundation.org/images/content/29a46d21.jpg',
 'Festival di teatro con spettacoli e laboratori.', 
 'https://www.festivalteatroroma.it', 
 'info@festivalteatroroma.it');
