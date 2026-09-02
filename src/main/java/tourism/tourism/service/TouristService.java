package tourism.tourism.service;

import org.springframework.stereotype.Service;
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

    public TouristAttraction getAttractionByName(String name) {
        return touristRepository.findByName(name);
    }

    public void addAttraction(int id, String name, String description) {
        TouristAttraction attraction = new TouristAttraction(id, name, description);
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
}