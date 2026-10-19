# Dokumentation af ændringer (Azure Deployment & Database Integration)

Dette dokument beskriver de ændringer der er lavet for at få **TouristGuideAPI** til at køre både lokalt og på Azure med en MySQL database og C ruteplanlægger

---

## 1. Spring Profiles og Database Konfiguration (`application.properties` & `application-prod.properties`)
- Satte `spring.profiles.active=prod` i `src/main/resources/application.properties` og udkommenterede `spring.sql.init.mode=always` samt `DROP TABLE IF EXISTS` i `schema.sql` så databasen på Azure ikke bliver nulstillet ved opstart
- Opdaterede `src/main/resources/application-prod.properties` til at læse `PROD_DATABASE_URL` `PROD_USERNAME` og `PROD_PASSWORD` fra miljøvariabler på Azure og GitHub med automatisk fallback til den lokale `localhost:3306/touristguide` database når man kører projektet lokalt i IntelliJ:
  ```properties
  spring.datasource.url=${PROD_DATABASE_URL:jdbc:mysql://localhost:3306/touristguide?createDatabaseIfNotExist=true}
  spring.datasource.username=${PROD_USERNAME:root}
  spring.datasource.password=${PROD_PASSWORD:${PASSWORD:}}
  ```

---

## 2. Skift fra SQLite til Spring MySQL DataSource (`TouristRepository.java`)
- I `TouristRepository.connect()` var forbindelsen tidligere hardcoded til `jdbc:sqlite:Attractions.db` hvilket gav `HTTP ERROR 500` på Azure
- Ændrede `connect()` til at hente forbindelsen direkte fra Spring Boots `JdbcTemplate` DataSource (`db.getDataSource().getConnection()`) så `getAll()` `save()` `delete()` `update()` og `updateTags()` alle bruger MySQL databasen
- Initialiserede `Logger logger = LoggerFactory.getLogger(TouristRepository.class)` så `catch` blokkene ikke kaster en `NullPointerException`

---

## 3. C Ruteplanlægger på både Windows og Linux Azure (`TouristService.java` & `master_touristguide.yml`)
- Da Azure Web App kører på Linux kan den ikke køre `bin/TouristGuide.exe` direkte
- Tilføjede et step i `.github/workflows/master_touristguide.yml` der kompilerer `src/main/c/main.c` statisk til Linux under GitHub Actions buildet:
  ```yaml
  - name: Compile C program for Linux
    run: gcc -static src/main/c/main.c -o src/main/resources/TouristGuide_linux
  ```
- Opdaterede `getAttractionsFromCProgram()` i `TouristService.java` så den:
  - Bruger `bin/TouristGuide.exe` når programmet kører på Windows
  - Kopierer `TouristGuide_linux` og `test_map.osm` ud i `/tmp/touristguide_c` og gør filen eksekverbar når programmet kører på Linux på Azure
  - Har en fallback metode `findAttractionsInOsmFallback()` der læser `test_map.osm` med præcis samme 2-gennemløbs bounding-box logik som C programmet hvis Azure containeren blokerer eksterne processer

---

## 4. Test og Navigation (`TouristControllerTest.java` & `navbar.html`)
- Rettede argumenterne i `TouristControllerTest.java` for `checkLogin` og `registerLogin` så de matcher metoderne i `TouristService` og Maven kan bygge uden kompileringsfejl
- Tilføjede `Plan-Route` link (`/Route`) i `src/main/resources/templates/fragments/navbar.html`
