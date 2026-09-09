package tourism.tourism.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.repository.TouristRepository;

import java.util.List;



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

    public boolean updateAttraction(int pick, String currentName, String newName, String newDescription) {
        TouristAttraction item = touristRepository.findByName(currentName);

        if (item == null) {
            return false;
        }

        if (pick == 1) {
            item.setName(newName);
        } else if (pick == 2) {
            item.setDescription(newDescription);
        } else {
            return false;
        }


        return true;
    }
    public boolean updateTicketPrice(String name, Double ticketPrice) {
        //TODO du returnere metoden du bruger
        //return touristRepository.updateTicketPrice(name, ticketPrice);
        return true;
    }
}