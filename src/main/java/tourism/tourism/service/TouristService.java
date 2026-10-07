package tourism.tourism.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.repository.TouristRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TouristService {

    private final TouristRepository touristRepository;

    public TouristService(TouristRepository touristRepository) {
        this.touristRepository = touristRepository;
    }

    public List<TouristAttraction> getAllAttractions() {
        return touristRepository.getAll();
    }

    public int checkLogin(String username, String password, HttpSession session , ModelMap model){
        return touristRepository.loginCheck(username,password,session);
    }

    public int registerLogin(String username, String password, HttpSession session , ModelMap model){
        System.out.println("service called");
        return touristRepository.registerCheck(username,password,session,model);
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
        if(!updated){
            return false;
        }
        return true;
    }


    public List<String> getAllTags(){
        List<String> tags = touristRepository.getTags();
        return tags;
    }
    public boolean updatedTags(String currentName, List<String> selectedTags){
        TouristAttraction item = touristRepository.findByName(currentName);
        return touristRepository.updateTags(item.getId(), selectedTags);
    }

        public List<String> getAttractionsFromCProgram(String startLocation, String endLocation) {
            List<String> outputLines = new ArrayList<>();

            try {
                // 1. Path to your compiled C executable
                // 1. Path to your compiled C executable in bin/
                String exePath = "bin/TouristGuide.exe";

                // 2. Build the command: TouristGuide.exe "Tivoli" "Nyhavn" (add "small" as 3rd arg for instant testing)
                ProcessBuilder processBuilder = new ProcessBuilder(exePath, startLocation, endLocation, "small");

                // (Optional) Set working directory if your C program needs to find files relative to the root
                // processBuilder.directory(new File("."));
                // 3. Start the C program
                Process process = processBuilder.start();
                // 4. Read the output (System.out) from the C program
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    // Here you can capture the printed output and filter it
                    String target = "v=\"";
                    if(line.equals(target)){
                        continue;
                    }
                    String replacement = "";
                    String processed = line.replace(target, replacement);

                    // For example, you could ignore lines that start with "Pass 1" or "Searching"
                    // and only add the actual attraction names to a list to return to your controller.
                    outputLines.add(processed);
                }

                // 5. Wait for the C program to finish
                int exitCode = process.waitFor();
                System.out.println("C program finished with exit code: " + exitCode);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return outputLines;
        }
    }

