package tourism.tourism.repository;

import org.springframework.stereotype.Repository;
import tourism.tourism.model.TouristAttraction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TouristRepository {
    private final List<TouristAttraction> steder = new ArrayList<>();
    private final String url = "jdbc:sqlite:src/main/resources/Attractions.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }


    public TouristRepository() {
        steder.add(new TouristAttraction(1,"Tivoli", "its a tivoli"));
        steder.add(new TouristAttraction(2,"Bakken", "its the oldest Tivoli in the world"));
    }

    // Hent alle
    public List<TouristAttraction> getAll() {

        List<TouristAttraction> attractions = new ArrayList<>();

        String sql = "SELECT * FROM Attraction";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");

                TouristAttraction attraction =
                        new TouristAttraction(id, name, description);

                attractions.add(attraction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attractions;
    }

    // Find en ud fra navn
    public TouristAttraction findByName(String name) {
        for (TouristAttraction item : steder) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null; // Returnerer null hvis den ikke findes
    }

    // Tilføj ny
    public void save(TouristAttraction attraction) {
        steder.add(attraction);
    }

    // Slet ud fra navn
    public boolean delete(String name) {
        return steder.removeIf(item -> item.getName().equalsIgnoreCase(name));
    }
}