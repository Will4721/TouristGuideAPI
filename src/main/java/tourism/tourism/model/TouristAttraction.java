package tourism.tourism.model;

import java.util.ArrayList;
import java.util.List;

public class TouristAttraction {
    private final int id;
    private String name;
    private String description;
    private String longerdescription;
    private Double ticketPrice;

    private List<String> tags = new ArrayList<>();
    @Override
    public String toString() {
        return "TouristAttraction{"+" ID= '" +id+"' "+
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getLongerdescription() {
        return longerdescription;
    }

    public void setLongerdescription(String longerdescription) {
        this.longerdescription = longerdescription;
    }

    public int getId() {
        return id;
    }

    public TouristAttraction(int id, String name, String description, String longerdescription, Double ticketPrice){
        this.id = id;
        this.name = name;
        this.description = description;
        this.longerdescription = longerdescription;
        this.ticketPrice = ticketPrice;


    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags){
        this.tags = tags;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}

