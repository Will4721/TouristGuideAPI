package tourism.tourism.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.service.TouristService;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
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
            return getAllAttractions(model);
        }else{
            return apology(model, "I dont know what you did but stop", 400, "Error_400", "I dont know what you did but stop");
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
            return apology(model, "I dont know what you did but stop", 400, "Error_400", "I dont know what you did but stop");
        }
    }

    @GetMapping("/attractions/{name}")
    public String searchAttraction( @PathVariable String name, ModelMap modelMap) {
        List<TouristAttraction> attractions = touristService.getAllAttractions();
        TouristAttraction item = touristService.getAttractionByName(name);
        if (item == null) {

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
    public String addAttraction(@RequestParam int id, @RequestParam String name, @RequestParam
    String description, @RequestParam String longerdescription,@RequestParam double ticketPrice) {
        touristService.addAttraction(id, name, description,longerdescription,ticketPrice);
        return "redirect:/attractions";
    }

    @PostMapping("/attractions/update/{name}")
    public String updateAttraction(
            @PathVariable String name,
            @RequestParam String newname,
            @RequestParam String newdescription,
            @RequestParam String newlongerdescription,
            @RequestParam double ticketPrice,
            @RequestParam(required = false) List<String> tags,
            ModelMap model) {

        boolean updated = touristService.updateAttraction(
                name,
                newname,
                newdescription,
                newlongerdescription,
                ticketPrice
        );

        if (updated) {

            if (tags == null) {
                tags = new ArrayList<>();
            }

            touristService.updatedTags(newname, tags);

            return "redirect:/attractions";
        }

        return apology(
                model,
                "I dont know what you did but stop",
                400,
                "Error_400",
                "I dont know what you did but stop"
        );
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

    @GetMapping("/attractions/update/{name}")
    public String showUpdatePage(@PathVariable String name, ModelMap model) {

        TouristAttraction item = touristService.getAttractionByName(name);
        List<String> tags = touristService.getAllTags();

        if (item == null) {
            return apology(
                    model,
                    "I dont know what you did but stop",
                    400,
                    "Error_400",
                    "I dont know what you did but stop"
            );
        }

        model.addAttribute("item", item);
        model.addAttribute("tags",tags);

        return "opdater";
    }

    private String apology(ModelMap model, String message, int statusCode,String topText,String bottomText) {
        model.addAttribute("message", message);
        model.addAttribute("status", statusCode);
        model.addAttribute("top", topText);
        model.addAttribute("bottom", bottomText);

        return "apology";
    }
}


