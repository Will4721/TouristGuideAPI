package tourism.tourism.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tourism.tourism.service.TouristService;

@Controller
@RequestMapping("attractions")
public class TouristController {

    private final TouristService touristService;

    public TouristController(TouristService touristService) {
        this.touristService = touristService;
    }

    // 1. GET /attractions
    @GetMapping

    public ResponseEntity<String> getAllAttractions() {

        return touristService.displayAttractions();

    }

    // 2. GET /attractions/{name}
    @GetMapping("/{name}") // Bruger @PathVariable for at fange navnet direkte i stien
    public ResponseEntity<String> searchAttraction(@PathVariable String name) {
        return touristService.searchAttraction(name);
    }
   // same as the one above but search bar friendly
   @GetMapping("/search")
   public ResponseEntity<String> executeSearch(@RequestParam("query") String name) {
       // Forwards the query directly to your service
       return touristService.searchAttraction(name);
   }

    // 3. POST /attractions/add
    @PostMapping("/add")
    public ResponseEntity<String> addAttraction(@RequestParam int id, @RequestParam String name, @RequestParam String description) {
        //change this so that its auto incrementing and not manuel.
        return touristService.addAttraction(id,name, description);
    }

    // 4. POST /attractions/update
    @PostMapping("/update") // Ændret fra PUT til POST så det matcher billedet
    public ResponseEntity<String> updateAttraction(
            @RequestParam int pick,
            @RequestParam String name,
            @RequestParam String newname,
            @RequestParam String newdescription) {
        return touristService.updateAttraction(pick, name, newname, newdescription);
    }

    // 5. POST /attractions/delete/{name}
    @PostMapping("/delete/{name}") // Ændret fra DELETE til POST og sat @PathVariable på stien
    public ResponseEntity<String> removeAttraction(@PathVariable String name) {
        return touristService.removeAttraction(name);
    }
}


