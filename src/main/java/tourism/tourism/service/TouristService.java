package tourism.tourism.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.repository.TouristRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class TouristService {
    Logger logger = LoggerFactory.getLogger(TouristService.class);
    private final TouristRepository touristRepository;

    public TouristService(TouristRepository touristRepository) {
        this.touristRepository = touristRepository;
    }

    public List<TouristAttraction> getAllAttractions() {
        return touristRepository.getAll();
    }

    public int checkLogin(String username, String password, HttpSession session){
        return touristRepository.loginCheck(username,password,session);
    }

    public int registerLogin(String username, String password){
        System.out.println("service called");
        return touristRepository.registerCheck(username,password);
    }

    public TouristAttraction getAttractionByName(String name) {
        return touristRepository.findByName(name);
    }

    public void addAttraction(String name, String description,String longerdescription,double ticketPrice, List<String> tags) {
        TouristAttraction attraction = new TouristAttraction(0, name, description,longerdescription,ticketPrice);
        touristRepository.save(attraction);
        if (tags != null && !tags.isEmpty()) {
            updatedTags(name, tags);
        }
    }

    public boolean removeAttraction(String name) {
        return touristRepository.delete(name);
    }

    public boolean updateAttraction( String currentName, String newName, String newDescription,String newlongerdescription,double ticketPrice) {
        TouristAttraction item = touristRepository.findByName(currentName);
        boolean updated = touristRepository.update(newName,newDescription,newlongerdescription,ticketPrice);

        if (item == null) {
            return false;
        }
        return updated;
    }


    public List<String> getAllTags(){
        return touristRepository.getTags();
    }
    public void updatedTags(String currentName, List<String> selectedTags){
        TouristAttraction item = touristRepository.findByName(currentName);
        touristRepository.updateTags(item.getId(), selectedTags);
    }

        public List<String> getAttractionsFromCProgram(String startLocation, String endLocation) {
            List<String> outputLines = new ArrayList<>();

            try {
                //vores precompiled C program i bin
                String exePath = "bin/TouristGuide.exe";
                // vi tjekker om vi kører på windows eller linux som på azure
                boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
                java.io.File workDir = null;

                // hvis det ikke er windows så kører vi på linux på azure
                if (!isWindows) {
                    // vi laver en midlertidig mappe i tmp hvor linux giver lov til at køre filer
                    workDir = new java.io.File(System.getProperty("java.io.tmpdir"), "touristguide_c");
                    java.io.File osmFile = new java.io.File(workDir, "src/main/resources/test_map.osm");
                    // vi tjekker om osm filen allerede ligger der ellers kopierer vi den ud fra jar filen
                    if (!osmFile.exists()) {
                        osmFile.getParentFile().mkdirs();
                        try (java.io.InputStream in = getClass().getResourceAsStream("/test_map.osm")) {
                            if (in != null) {
                                java.nio.file.Files.copy(in, osmFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }

                    // vi henter også vores linux C program ud fra jar filen og lægger det i tmp mappen
                    java.io.File linuxBin = new java.io.File(workDir, "TouristGuide_linux");
                    if (!linuxBin.exists()) {
                        linuxBin.getParentFile().mkdirs();
                        try (java.io.InputStream in = getClass().getResourceAsStream("/TouristGuide_linux")) {
                            if (in != null) {
                                java.nio.file.Files.copy(in, linuxBin.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                    // vi giver filen lov til at blive kørt som et program på linux
                    linuxBin.setExecutable(true, false);
                    exePath = linuxBin.getAbsolutePath();
                }


                // vi begynder at lave C programmet men først giver vi den nogle parameter til at bruge inklusiv small som betyder den lille OSM fil
                ProcessBuilder processBuilder = new ProcessBuilder(exePath, startLocation, endLocation, "small");
                // hvis vi har en workDir sætter vi C programmet til at køre derfra så det kan finde osm filen
                if (workDir != null) {
                    processBuilder.directory(workDir);
                }

                // vi faktisk starter C programmet her
                Process process = processBuilder.start();
                // vi læser alle print statements fra C programmet
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
                String line;
                // vi putter 1 linje af gangen ind i line og processer dem
                while ((line = reader.readLine()) != null) {
                    // Stringen med navnet står sådan her "v="Tivoli"
                    String target = "v=\"";
                    // vi tjekker om vi har fået den linje vi gerne vil have
                    if(line.equals(target)){
                        // hvis linjen kun indeholder v=" uden noget navn springer vi den over så vi undgår at tilføje en tom attraktion til listen
                        continue;
                    }

                    String replacement = "";
                    // vi tager v=" og fjerne den hved at replace den med "" som er igenting
                    String processed = line.replace(target, replacement);

                    // her vælger vi hvad der skal vises på siden
                    outputLines.add(processed);
                }

                // vi venter på programmet er færdig
                int exitCode = process.waitFor();
                System.out.println("C program finished with exit code: " + exitCode);

            } catch (Exception e) {
                logger.error("Exception", e);
            }

            // hvis listen stadig er tom bruger vi vores fallback metode der læser osm filen direkte
            if (outputLines.isEmpty()) {
                outputLines = findAttractionsInOsmFallback(startLocation, endLocation);
            }

            return outputLines;
        }

        // fallback metode der gør præcis det samme som C programmet hvis azure blokerer c filen
        private List<String> findAttractionsInOsmFallback(String startTerm, String endTerm) {
            List<String> results = new ArrayList<>();
            // vi åbner vores lille osm fil direkte fra resources
            try (java.io.InputStream in = getClass().getResourceAsStream("/test_map.osm")) {
                if (in == null) return results;
                // vi læser alle linjerne ind i en liste med utf-8 så æøå virker
                List<String> lines = new BufferedReader(new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).lines().toList();

                double startLat = 0.0, startLon = 0.0, endLat = 0.0, endLon = 0.0;
                double currentLat = 0.0, currentLon = 0.0;
                // vi bygger de to strenge vi leder efter til start og slut
                String startTarget = "v=\"" + startTerm + "\"";
                String endTarget = "v=\"" + endTerm + "\"";

                // første gennemløb hvor vi finder koordinaterne for start og slut attraktionen
                for (String line : lines) {
                    // når vi ser en ny node henter vi lat og lon
                    if (line.contains("<node")) {
                        currentLat = extractDoubleAttr(line, "lat=\"");
                        currentLon = extractDoubleAttr(line, "lon=\"");
                    }
                    // vi tjekker om linjen har navnet på vores start attraktion
                    if (startLat == 0.0 && line.contains(startTarget)) {
                        startLat = currentLat;
                        startLon = currentLon;
                    }
                    // vi tjekker om linjen har navnet på vores slut attraktion
                    if (endLat == 0.0 && line.contains(endTarget)) {
                        endLat = currentLat;
                        endLon = currentLon;
                    }
                    // hvis vi har fundet begge stopper vi første gennemløb
                    if (startLat != 0.0 && endLat != 0.0) break;
                }

                // hvis en af dem ikke blev fundet på kortet stopper vi her
                if (startLat == 0.0 || endLat == 0.0) return results;

                // vi udregner de mindste og største koordinater til vores firkant vi søger i
                double minLat = Math.min(startLat, endLat);
                double maxLat = Math.max(startLat, endLat);
                double minLon = Math.min(startLon, endLon);
                double maxLon = Math.max(startLon, endLon);

                currentLat = 0.0;
                currentLon = 0.0;
                String currentName = "";

                // andet gennemløb hvor vi finder alle attraktioner inde i firkanten
                for (String line : lines) {
                    if (line.contains("<node")) {
                        currentLat = extractDoubleAttr(line, "lat=\"");
                        currentLon = extractDoubleAttr(line, "lon=\"");
                        currentName = "";
                    }
                    // hvis linjen har et navn gemmer vi navnet i currentName
                    if (line.contains("k=\"name\"")) {
                        int vIdx = line.indexOf("v=\"");
                        if (vIdx >= 0) {
                            int endIdx = line.indexOf("\"", vIdx + 3);
                            if (endIdx > vIdx + 3) {
                                currentName = line.substring(vIdx + 3, endIdx);
                            }
                        }
                    }
                    // vi tjekker om det er en turistattraktion og om den ligger inde mellem start og slut
                    if (line.contains("k=\"tourism\"") && currentLat < maxLat && currentLat > minLat && currentLon < maxLon && currentLon > minLon) {
                        // vi tjekker at den har et navn og at det ikke bare er start eller slut attraktionen
                        if (!currentName.isEmpty() && !currentName.equals(startTerm) && !currentName.equals(endTerm)) {
                            results.add(currentName);
                            currentName = "";
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Fallback OSM error", e);
            }
            return results;
        }

        // lille hjælpe metode til at hente tallet ud fra lat og lon i osm filen
        private double extractDoubleAttr(String line, String attrPrefix) {
            int idx = line.indexOf(attrPrefix);
            if (idx < 0) return 0.0;
            int start = idx + attrPrefix.length();
            int end = line.indexOf("\"", start);
            if (end <= start) return 0.0;
            try {
                return Double.parseDouble(line.substring(start, end));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

