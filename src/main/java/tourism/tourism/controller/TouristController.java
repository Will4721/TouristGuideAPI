package tourism.tourism.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.service.TouristService;
import org.springframework.ui.ModelMap;

import java.util.List;

@Controller
public class TouristController {

    private final TouristService touristService;

    public TouristController(TouristService touristService) {
        this.touristService = touristService;
    }




    @GetMapping("/attractions")
    public String getAllAttractions(ModelMap modelMap) {
        List<TouristAttraction> attractions = touristService.getAllAttractions();


        modelMap.addAttribute("attractions", attractions);

        return "Getall";
    }

    @GetMapping("/attractions/{name}")
    public String searchAttraction( @PathVariable String name, ModelMap modelMap) {
        TouristAttraction item = touristService.getAttractionByName(name);
        modelMap.addAttribute("attraction", item);

        return "main";
    }

    @GetMapping("/attractions/search")
    public String executeSearch(@RequestParam("query") String name,ModelMap modelMap) {
        return searchAttraction(name,modelMap);
    }

    @PostMapping("/attractions/add")
    @ResponseBody
    public ResponseEntity<String> addAttraction(@RequestParam int id, @RequestParam String name, @RequestParam String description) {
        touristService.addAttraction(id, name, description);
        return new ResponseEntity<>("Attraktion tilføjet: " + name, HttpStatus.CREATED);
    }

    @PostMapping("/attractions/update")
    @ResponseBody
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

    @PostMapping("/attractions/delete/{name}")
    @ResponseBody
    public ResponseEntity<String> removeAttraction(@PathVariable String name) {
        boolean deleted = touristService.removeAttraction(name);
        if (deleted) {
            return new ResponseEntity<>("Attraktion slettet: " + name, HttpStatus.OK);
        }
        return new ResponseEntity<>("Kunne ikke finde attraktionen: " + name, HttpStatus.NOT_FOUND);
    }
}


