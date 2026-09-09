package tourism.tourism.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/login")
    public String getLogin(ModelMap modelMap){
        return "login";
    }

    @GetMapping("/register")
    public String getRegister(ModelMap modelMap){

        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestParam(required = false) String username, @RequestParam(required = false) String password, HttpSession session, ModelMap model){
        if(touristService.checkLogin(username,password,session,model) == 1) {
            model.addAttribute("username", username);
            return "main";
        }else{
            return "login";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam(required = false) String username, @RequestParam(required = false) String password, HttpSession session, ModelMap model){
        if(touristService.registerLogin(username,password,session,model) == 1) {
            System.out.println("user made");
            model.addAttribute("username", username);
            return "login";
        }else{
            System.out.println("error");
            model.addAttribute("error", "Username already taken");
            return "register";
        }
    }

    @GetMapping("/attractions/{name}")
    public String searchAttraction( @PathVariable String name, ModelMap modelMap) {
        List<TouristAttraction> attractions = touristService.getAllAttractions();
        TouristAttraction item = touristService.getAttractionByName(name);
        if (item == null) {
            // Redirect right back to the working /attractions main list page
            return "redirect:/attractions";
        }
        modelMap.addAttribute("attraction", item);
        modelMap.addAttribute("attractions", attractions);

        return "main";
    }

    @GetMapping("/attractions/search")
    public String executeSearch(@RequestParam("query") String name,ModelMap modelMap) {
        return searchAttraction(name,modelMap);
    }

    @GetMapping("/attractions/add")
    public String showAddPage(){
        return "add";
    }

    @PostMapping("/attractions/add")
    public String addAttraction(@RequestParam int id, @RequestParam String name, @RequestParam String description, @RequestParam String longerdescription) {
        touristService.addAttraction(id, name, description,longerdescription);
        return "redirect:/attractions";
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


