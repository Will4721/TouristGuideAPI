package tourism.tourism.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.ModelMap;
import tourism.tourism.model.TouristAttraction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.servlet.http.HttpSession;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TouristRepository {
    @Autowired
    private JdbcTemplate db;
    private final String url = "jdbc:sqlite:Attractions.db";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
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
                String longerdescription = resultSet.getString("longerdescription");
                // vi adder dem til vores arraylist
                attractions.add(new TouristAttraction(id, name, description,longerdescription));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        // vi sender alle tingene fra arraylist
        return attractions;
    }

    public int loginCheck(String username, String password,HttpSession session,ModelMap model){
        if(username == null || username.trim().isEmpty()){
            apology(model, "must provide username", 403);
            return -1;
        }
        if(password == null || password.trim().isEmpty()){
            apology(model, "must provide password", 403);
            return -1;
        }
        String sql = "SELECT * FROM users WHERE username = ?";
        List<Map<String, Object>> rows = db.queryForList(sql, username);

        if (rows.size() != 1 || !passwordEncoder.matches(password, (String) rows.get(0).get("hash"))) {
            apology(model, "invalid username and/or password", 403);
            return -1;
        }
        int userId = (int) rows.get(0).get("id");
        session.setAttribute("user_id", userId);

        // 6. Redirect til forsiden
        return 1;

    }

    public int registerCheck(String username, String password,HttpSession session,ModelMap model){
        if(username == null || username.trim().isEmpty()){
            apology(model, "must provide username", 403);
            return -1;
        }
        if(password == null || password.trim().isEmpty()){
            apology(model, "must provide password", 403);
            return -1;
        }
        String sql = "SELECT * FROM users WHERE username = ?";
        List<Map<String, Object>> rows = db.queryForList(sql, username);
        String secureHash = passwordEncoder.encode(password);
        if(rows.isEmpty()){
            String insert = "INSERT INTO users (username, hash) VALUES (?, ?)";
            db.update(insert, username, secureHash);
            return 1;

        }

        return -1;

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
                String longerdescription = resultSet.getString("longerdescription");
                //vi returnere alle dataerne
                return new TouristAttraction(
                        id,
                        attractionName,
                        description,
                        longerdescription
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
    private String apology(ModelMap model, String message, int statusCode) {
        model.addAttribute("message", message);
        model.addAttribute("status", statusCode);
        return "apology"; // Returnerer din apology.html skabelon
    }

}