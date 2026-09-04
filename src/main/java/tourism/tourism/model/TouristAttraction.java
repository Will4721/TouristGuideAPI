package tourism.tourism.model;

public class TouristAttraction {
    private int id;
    private String name;
    private String description;
    private String longerdescription;

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

    public TouristAttraction(int id, String name, String description, String longerdescription){
        this.id = id;
        this.name = name;
        this.description = description;
        this.longerdescription = longerdescription;

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
}

