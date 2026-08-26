package tourism.tourism.repository;

import org.springframework.stereotype.Repository;
import tourism.tourism.model.TouristAttraction;

import java.util.ArrayList;



@Repository
public class TouristRepository {
  private final ArrayList<TouristAttraction> steder = new ArrayList<>();


    public TouristRepository() {
        TouristAttraction tivoli = new TouristAttraction("Tivoli","its a tivoli");
        TouristAttraction bakken = new TouristAttraction("Bakken", "its the oldest Tivoli in the world");

        steder.add(tivoli);
        steder.add(bakken);
    }
    //crud create new resource
    public void addAttraction(String name, String description){
        TouristAttraction Attraction = new TouristAttraction(name,description);
    }
    //crud remove resource
    public void removeAttraction(String name, String description){

            steder.removeIf(item -> item.getName().equals("TargetName"));

    }
    //crud remove resource
    public void updateAttraction(int pick, String name,String newname, String description,String newdescription){
        //change to switch case if more attributes arrive
        if(pick == 1){
            for(TouristAttraction item : steder){
                if(item.getName().equals(name)){
                    item.setName(newname);
                    break;
                }
            }
        }
        if(pick == 2){
            for(TouristAttraction item : steder){
                if(item.getName().equals(name)){
                    item.setName(newname);
                    break;
                }
            }
        }

    }

    public String searchAttraction(String name){
        for(TouristAttraction item : steder){
            if(item.getName().equals(name)){
                return "Name: " + item.getName() + " " + "Description: " + item.getDescription();
            }
        }
        return "Not found";
    }
    public void displayAttractions(){
        for(TouristAttraction item : steder){
            System.out.println("Name: " + item.getName() + " " + "Description: " + item.getDescription());
        }
    }



}