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
                boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
                java.io.File workDir = null;

                if (!isWindows) {
                    workDir = new java.io.File(System.getProperty("java.io.tmpdir"), "touristguide_c");
                    java.io.File osmFile = new java.io.File(workDir, "src/main/resources/test_map.osm");
                    if (!osmFile.exists()) {
                        osmFile.getParentFile().mkdirs();
                        try (java.io.InputStream in = getClass().getResourceAsStream("/test_map.osm")) {
                            if (in != null) {
                                java.nio.file.Files.copy(in, osmFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }

                    java.io.File linuxBin = new java.io.File(workDir, "TouristGuide_linux");
                    if (!linuxBin.exists()) {
                        linuxBin.getParentFile().mkdirs();
                        try (java.io.InputStream in = getClass().getResourceAsStream("/TouristGuide_linux")) {
                            if (in != null) {
                                java.nio.file.Files.copy(in, linuxBin.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                    linuxBin.setExecutable(true, false);
                    exePath = linuxBin.getAbsolutePath();
                }


                // vi begynder at lave C programmet men først giver vi den nogle parameter til at bruge inklusiv small som betyder den lille OSM fil
                ProcessBuilder processBuilder = new ProcessBuilder(exePath, startLocation, endLocation, "small");
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
                        // Hvis linjen kun indeholder 'v="' uden noget navn, springer vi den over,
                        // så vi undgår at tilføje en tom/ugyldig attraktion til listen.
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

            if (outputLines.isEmpty()) {
                outputLines = findAttractionsInOsmFallback(startLocation, endLocation);
            }

            return outputLines;
        }

        private List<String> findAttractionsInOsmFallback(String startTerm, String endTerm) {
            List<String> results = new ArrayList<>();
            try (java.io.InputStream in = getClass().getResourceAsStream("/test_map.osm")) {
                if (in == null) return results;
                List<String> lines = new BufferedReader(new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).lines().toList();

                double startLat = 0.0, startLon = 0.0, endLat = 0.0, endLon = 0.0;
                double currentLat = 0.0, currentLon = 0.0;
                String startTarget = "v=\"" + startTerm + "\"";
                String endTarget = "v=\"" + endTerm + "\"";

                for (String line : lines) {
                    if (line.contains("<node")) {
                        currentLat = extractDoubleAttr(line, "lat=\"");
                        currentLon = extractDoubleAttr(line, "lon=\"");
                    }
                    if (startLat == 0.0 && line.contains(startTarget)) {
                        startLat = currentLat;
                        startLon = currentLon;
                    }
                    if (endLat == 0.0 && line.contains(endTarget)) {
                        endLat = currentLat;
                        endLon = currentLon;
                    }
                    if (startLat != 0.0 && endLat != 0.0) break;
                }

                if (startLat == 0.0 || endLat == 0.0) return results;

                double minLat = Math.min(startLat, endLat);
                double maxLat = Math.max(startLat, endLat);
                double minLon = Math.min(startLon, endLon);
                double maxLon = Math.max(startLon, endLon);

                currentLat = 0.0;
                currentLon = 0.0;
                String currentName = "";

                for (String line : lines) {
                    if (line.contains("<node")) {
                        currentLat = extractDoubleAttr(line, "lat=\"");
                        currentLon = extractDoubleAttr(line, "lon=\"");
                        currentName = "";
                    }
                    if (line.contains("k=\"name\"")) {
                        int vIdx = line.indexOf("v=\"");
                        if (vIdx >= 0) {
                            int endIdx = line.indexOf("\"", vIdx + 3);
                            if (endIdx > vIdx + 3) {
                                currentName = line.substring(vIdx + 3, endIdx);
                            }
                        }
                    }
                    if (line.contains("k=\"tourism\"") && currentLat < maxLat && currentLat > minLat && currentLon < maxLon && currentLon > minLon) {
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

