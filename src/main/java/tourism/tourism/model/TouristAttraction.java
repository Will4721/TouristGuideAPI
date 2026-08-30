package tourism.tourism.model;

public class TouristAttraction {
    private int id;
    private String name;
    private String description;

    @Override
    public String toString() {
        return "TouristAttraction{"+" ID= '" +id+"' "+
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public TouristAttraction(int id,String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;

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
