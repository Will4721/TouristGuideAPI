package tourism.tourism.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.service.TouristService;

import java.util.List;

@RestController // Brug @RestController i stedet for @Controller ved string/JSON endpoints
@RequestMapping("attractions")
public class TouristController {

    private final TouristService touristService;

    public TouristController(TouristService touristService) {
        this.touristService = touristService;
    }

    @GetMapping
    public ResponseEntity<String> getAllAttractions() {
        List<TouristAttraction> attractions = touristService.getAllAttractions();

        if (attractions.isEmpty()) {
            return new ResponseEntity<>("Ingen attraktioner fundet i systemet.", HttpStatus.OK);
        }

        StringBuilder result = new StringBuilder();
        for (TouristAttraction item : attractions) {
            result.append(item.toString()).append("\n");
        }

        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<String> searchAttraction(@PathVariable String name) {
        TouristAttraction item = touristService.getAttractionByName(name);
        if (item != null) {
            return new ResponseEntity<>(item.toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<String> executeSearch(@RequestParam("query") String name) {
        return searchAttraction(name);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addAttraction(@RequestParam int id, @RequestParam String name, @RequestParam String description) {
        touristService.addAttraction(id, name, description);
        return new ResponseEntity<>("Attraktion tilføjet: " + name, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateAttraction(
            @RequestParam int pick,
            @RequestParam String name,
            @RequestParam String newname,
            @RequestParam String newdescription) {

        boolean updated = touristService.updateAttraction(pick, name, newname, newdescription);
        if (updated) {
            return new ResponseEntity<>("Attraktion opdateret", HttpStatus.OK);
        }
        return new ResponseEntity<>("Kunne ikke opdatere attraktion", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/delete/{name}")
    public ResponseEntity<String> removeAttraction(@PathVariable String name) {
        boolean deleted = touristService.removeAttraction(name);
        if (deleted) {
            return new ResponseEntity<>("Attraktion slettet: " + name, HttpStatus.OK);
        }
        return new ResponseEntity<>("Kunne ikke finde attraktionen: " + name, HttpStatus.NOT_FOUND);
    }
}


