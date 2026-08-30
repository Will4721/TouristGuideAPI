package tourism.tourism.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.repository.TouristRepository;

import java.util.List;

@Service
public class TouristService {

    // 1. Forbind servicen med dit repository (Dependency Injection)
    private final TouristRepository touristRepository;

    // vi sørger får at alle Services har deres eget repo
    public TouristService(TouristRepository touristRepository) {
        this.touristRepository = touristRepository;
    }

    // CREATE: Tilføj attraktion
    public ResponseEntity<String> addAttraction(int id,String name, String description) {
        TouristAttraction attraction = new TouristAttraction(id,name, description);
        touristRepository.save(attraction); // Delegerer arbejdet til repo

        return new ResponseEntity<>("Attraktion tilføjet: " + attraction.toString(), HttpStatus.CREATED);
    }

    // DELETE: Fjern attraktion baseret på navn
    public ResponseEntity<String> removeAttraction(String name) {
        // Delegerer sletningen til repo, som nu bruger den rigtige 'name' variabel i stedet for "TargetName"
        boolean deleted = touristRepository.delete(name);

        if (deleted) {
            return new ResponseEntity<>("Attraktion slettet: " + name, HttpStatus.OK);
        }
        return new ResponseEntity<>("Kunne ikke finde attraktionen: " + name, HttpStatus.NOT_FOUND);
    }

    // UPDATE: Opdater eksisterende attraktion
    public ResponseEntity<String> updateAttraction(int pick, String currentName, String newName, String newDescription) {
        // vi henter alle steder fra vores REP som virker som vores database
        TouristAttraction item = touristRepository.findByName(currentName);

        if (item == null) {
            return new ResponseEntity<>("Attraktion ikke fundet", HttpStatus.NOT_FOUND);
        }

        // Hvis pick er 1, opdaterer vi navnet
        if (pick == 1) {
            item.setName(newName);
            return new ResponseEntity<>("Navn opdateret: " + item.toString(), HttpStatus.OK);
        }
        // Hvis pick er 2, opdaterer vi beskrivelsen
        if (pick == 2) {
            item.setDescription(newDescription);
            return new ResponseEntity<>("Beskrivelse opdateret: " + item.toString(), HttpStatus.OK);
        }

        return new ResponseEntity<>("Ugyldigt valg (pick skal være 1 eller 2)", HttpStatus.BAD_REQUEST);
    }

    //  Søg efter én bestemt attraktion ud fra navn
    public ResponseEntity<String> searchAttraction(String name) {
        TouristAttraction item = touristRepository.findByName(name);

        if (item != null) {
            return new ResponseEntity<>(item.toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    //  Vis alle attraktioner
    public ResponseEntity<String> displayAttractions() {
        // vi henter alle steder fra vores REP som virker som vores database
        List<TouristAttraction> alleSteder = touristRepository.getAll();

        if (alleSteder.isEmpty()) {
            return new ResponseEntity<>("Ingen attraktioner fundet i systemet.", HttpStatus.OK);
        }

        // Vi bygger en samlet tekststreng med alle attraktioner så loopet ikke afbrydes efter den første
        // som jeg har lært gennem C så er garbage collection et meget vigtig punkt når det kommer til efficency
        //der nok ikke brug for det men jeg vil gerne øve mig
        //vi bruger StringBuilder som kan ændre en String unden at lave en ny og fjerne den gamle hvilket gør den hurtigere især
        //hvis vi har en stor mængde tekst
        StringBuilder result = new StringBuilder();
        for (TouristAttraction item : alleSteder) {
            result.append(item.toString()).append("\n");
        }

        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }
}
