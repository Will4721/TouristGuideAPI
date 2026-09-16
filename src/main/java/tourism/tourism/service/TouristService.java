package tourism.tourism.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.repository.TouristRepository;

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
        return touristRepository.loginCheck(username,password,session,model);
    }

    public int registerLogin(String username, String password, HttpSession session , ModelMap model){
        System.out.println("service called");
        return touristRepository.registerCheck(username,password,session,model);
    }

    public TouristAttraction getAttractionByName(String name) {
        return touristRepository.findByName(name);
    }

    public void addAttraction(int id, String name, String description,String longerdescription,double ticketPrice) {
        TouristAttraction attraction = new TouristAttraction(id, name, description,longerdescription,ticketPrice);
        touristRepository.save(attraction);
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
}