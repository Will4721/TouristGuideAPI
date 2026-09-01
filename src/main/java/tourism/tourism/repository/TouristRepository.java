package tourism.tourism.repository;

import org.springframework.stereotype.Repository;
import tourism.tourism.model.TouristAttraction;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TouristRepository {

    private final String url = System.getenv("ghcr.io/railwayapp-templates/postgres-ssl:18");

    public Connection connect() throws SQLException {
        // 1. Henter det automatiske link fra Railway live
        String railwayUrl = System.getenv("ghcr.io/railwayapp-templates/postgres-ssl:18");

        String finalUrl;
        if (railwayUrl != null) {
            // Railway leverer linket som "postgresql://...", men Java JDBC kræver "jdbc:postgresql://..."
            finalUrl = railwayUrl.replace("postgresql://", "jdbc:postgresql://");
        } else {
            // Fallback til din lokale test-database på din egen computer, hvis du arbejder derhjemme
            finalUrl = "jdbc:sqlite:Attractions.db";
        }

        return DriverManager.getConnection(finalUrl);
    }


    // Hent alle
    public List<TouristAttraction> getAll() {
        //vi laver en array til at gemme de ting vi tager fra DB
        List<TouristAttraction> attractions = new ArrayList<>();
        //vores query
        String sql = "SELECT * FROM attraction";
        // vi connecter til DB
        try (Connection connection = connect();
             //vi skriver vores query til DB
             PreparedStatement statement = connection.prepareStatement(sql);
             //vi executer queary
             ResultSet resultSet = statement.executeQuery()) {
            //next() rykker til den næste linje i table
            while (resultSet.next()) {
                //vi henter de forskellige ting i row
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                // vi adder dem til vores arraylist
                attractions.add(new TouristAttraction(id, name, description));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        // vi sender alle tingene fra arraylist
        return attractions;
    }

    // Find en ud fra navn
    public TouristAttraction findByName(String name) {
        //vores query
        String sql = "SELECT * FROM attraction WHERE name = ?";
        //vi connecter til DB
        try (Connection connection = connect();
             //vi skriver vores statement men executer det ikke endnu
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //vi sætter variablen name in i første spørgsmåls tegn i query
            statement.setString(1, name);
            //vi executer query
            ResultSet resultSet = statement.executeQuery();
            //vi tjekker at linjen existere
            if (resultSet.next()) {
                //vi henter data fra linje
                int id = resultSet.getInt("id");
                String attractionName = resultSet.getString("name");
                String description = resultSet.getString("description");
                //vi returnere alle dataerne
                return new TouristAttraction(
                        id,
                        attractionName,
                        description
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Tilføj ny
    public void save(TouristAttraction attraction) {
        //vores query
        String sql = """
                INSERT INTO attraction (name, description)
                VALUES (?, ?)
                """;
        //vi connecter til DB
        try (Connection connection = connect();
             //vi skriver vores statement men executer det ikke endnu
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //vi sætter variablenerne  name og description ind i første og anden spørgsmåls tegn i query
            statement.setString(1, attraction.getName());
            statement.setString(2, attraction.getDescription());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Slet ud fra navn
    public boolean delete(String name) {
        //vores query
        String sql = "DELETE FROM attraction WHERE name = ?";
        //vi connecter til DB
        try (Connection connection = connect();
             //vi skriver vores statement men executer det ikke endnu
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //vi sætter variablen name in i første spørgsmåls tegn i query
            statement.setString(1, name);
            //vi tjekker hvor mange linjer bliver slettet
            int rowsDeleted = statement.executeUpdate();
            //vi returner hvor mange blev slettet burde bare være 1 i vores tilfælde
            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}