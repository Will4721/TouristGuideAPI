INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (1, 'Tivoli', 'Forlystelsespark i København', 'I hjertet af København ligger Tivoli – verdens tredjeældste forlystelsespark. Siden 1843 har Tivoli budt velkommen til en verden fyldt med fantastiske oplevelser i den eventyrlige have. 

Glæd dig til 30 sjove forlystelser, tusindvis af farverige blomster, et væld af fede koncerter, betagende ballet- og teaterforestillinger, lækre madoplevelser på flere end 30 spisesteder og skab uforglemmelige minder for hele familien.', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (2, 'Den Lille Havfrue', 'Berømt statue', 'The Little Mermaid (Danish: Den lille Havfrue) is a bronze statue by Edvard Eriksen, depicting a mermaid becoming human. The sculpture is displayed on a rock by the waterside at the Langelinie promenade in Copenhagen, Denmark.[a] It is 1.25 metres (4.1 ft) tall[2] and weighs 175 kilograms (385 lb).[3]

Based on the 1837 fairy tale of the same name by Danish author Hans Christian Andersen, the small and unimposing statue is a Copenhagen icon and has been a major tourist attraction since its unveiling in 1913. In recent decades it has become a popular target for defacement by vandals and political activists.

Mermaid is among iconic statues that symbolise cities. Others include: Manneken Pis in Brussels;[4] the Statue of Liberty in New York; Christ the Redeemer in Rio de Janeiro; the statue of Pania of the Reef in Napier; Smok Wawelski (Wawel Dragon) in Kraków, Poland; or Nelson''s Column and Eros in London.', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (3, 'Parken', 'Stadion', 'Parken (tidligere Københavns Idrætspark eller blot Idrætsparken) er et fodboldstadion på Per Henrik Lings Allé 2 på Østerbro i København. Parken fungerer både som Danmarks nationalstadion for det danske landshold i fodbold og hjemmebane for fodboldklubben F.C. København. Som følge af en sponsoraftale med TeliaSonera havde stadionet fra juli 2014 til 31. august 2020 navnet Telia Parken.

Parken ejes af det børsnoterede PARKEN Sport & Entertainment A/S, der står for at udleje Parken til både fodboldkampe og arrangementer inden for underholdningsbranchen herunder koncerter. Grunden under parken ejes af Københavns Kommune med en hjemfaldspligt på grunden med udløb i 2060.

Parken opfylder den højeste UEFA stadionkategori (4). Stadionet har dog ikke nok kapacitet til at kunne bruges som primært stadion til slutrunder. Fx kræves der et stadion med en kapacitet på 60.000 for at kunne afvikle EM i fodbold i 2028.', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (4, 'Lousiana', 'Museum', 'super long very long thing', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (5, 'Københavns Zoo', 'Zoologisk have', 'super long very long thing', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (6, 'Christiania', 'Selvstyrende boligområde', 'super long very long thing', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (7, 'Rundetårn', 'Observatorium midt i København', 'super long very long thing', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (8, 'Nyhavn', 'Havnestrækning midt i KBH', 'super long very long thing', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (9, 'Christiansborg Slot', 'Hjemsted for det danske parlament', 'super long very long thing', 10.0);
INSERT INTO attraction (id, name, description, longerdescription, ticket_price) VALUES (10, 'Amalienborg Slot', 'Den royale families primære residens', 'super long very long thing', 10.0);
INSERT INTO users (id, username, hash) VALUES (1, 'william', '$2a$10$0/urwYBzj5mQeuMg3tVksOriQpZcdxR12tahqpL5NHNhgwE6DnjjW');
INSERT INTO tags (id, tag_name) VALUES (1, 'Indoor');
INSERT INTO tags (id, tag_name) VALUES (2, 'Outdoor');
INSERT INTO tags (id, tag_name) VALUES (3, 'Scenic');
INSERT INTO tags (id, tag_name) VALUES (4, 'Historical');
INSERT INTO tags (id, tag_name) VALUES (5, 'Nature');
INSERT INTO tags (id, tag_name) VALUES (6, 'Family-Friendly');
INSERT INTO tags (id, tag_name) VALUES (7, 'Free');
INSERT INTO tags (id, tag_name) VALUES (8, 'Budget-Friendly');
INSERT INTO tags (id, tag_name) VALUES (9, 'Amusement');
INSERT INTO tags (id, tag_name) VALUES (10, 'Waterfront');
INSERT INTO tags (id, tag_name) VALUES (11, 'Quick Visit');
INSERT INTO tags (id, tag_name) VALUES (12, 'Good for Groups');
INSERT INTO tags (id, tag_name) VALUES (13, 'Museum');
INSERT INTO tags (id, tag_name) VALUES (14, 'Wildlife');
INSERT INTO tags (id, tag_name) VALUES (15, 'Lively');
INSERT INTO tags (id, tag_name) VALUES (16, 'Architecture');
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (1, 2);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (1, 9);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (1, 6);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (2, 2);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (2, 10);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (2, 7);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (2, 11);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (3, 2);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (3, 12);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (4, 1);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (4, 13);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (4, 3);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (5, 2);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (5, 14);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (5, 6);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (6, 2);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (6, 15);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (6, 7);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (7, 1);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (7, 4);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (7, 3);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (8, 2);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (8, 10);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (8, 3);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (8, 7);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (9, 1);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (9, 4);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (9, 16);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (10, 2);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (10, 4);
INSERT INTO attraction_tags (attraction_id, tag_id) VALUES (10, 16);
