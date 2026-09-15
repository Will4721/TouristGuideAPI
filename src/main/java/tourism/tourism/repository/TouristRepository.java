package tourism.tourism.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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
                double ticket_price = resultSet.getDouble("ticket_price");
                // vi adder dem til vores arraylist
                attractions.add(new TouristAttraction(id, name, description,longerdescription,ticket_price));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        // vi sender alle tingene fra arraylist
        return attractions;
    }

    public int loginCheck(String username, String password,HttpSession session,ModelMap model){
        if(username == null || username.trim().isEmpty()){
            System.out.println("username");
            return -1;
        }
        if(password == null || password.trim().isEmpty()){
            System.out.println("password");
            return -1;
        }
        String sql = "SELECT * FROM users WHERE username = ?";
        List<Map<String, Object>> rows = db.queryForList(sql, username);

        if (rows.size() != 1 || !passwordEncoder.matches(password, (String) rows.get(0).get("hash"))) {
            System.out.println("wrong password");
            return -1;
        }
        int userId = (int) rows.get(0).get("id");
        session.setAttribute("user_id", userId);


        return 1;

    }

    public int registerCheck(String username, String password,HttpSession session,ModelMap model){
        System.out.println("repo called"+" "+ username);

        if(username == null || username.trim().isEmpty()){
            System.out.println("username");

            return -1;
        }
        if(password == null || password.trim().isEmpty()){
            System.out.println("password");

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
        System.out.println("rows found!!");
        return -1;

    }

    // Find en ud fra navn
    public TouristAttraction findByName(String name) {
        String sql = "SELECT a.id, a.name, a.description, a.longerdescription, t.tag_name,  a.ticket_price " +
                "FROM attraction a " +
                "LEFT JOIN attraction_tags at ON a.id = at.attraction_id " +
                "LEFT JOIN tags t ON at.tag_id = t.id " +
                "WHERE a.name = ?";

        // Vi bruger query() med en ResultSetExtractor til at samle dataene
        return db.query(sql, resultSet -> {
            TouristAttraction attraction = null;
            List<String> tags = new ArrayList<>();

            // Loop igennem alle rækkerne fra databasen
            while (resultSet.next()) {
                // Første gang vi looper, bygger vi selve attraktionen
                if (attraction == null) {
                    int id = resultSet.getInt("id");
                    String attractionName = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    String longerDescription = resultSet.getString("longerdescription");
                    double ticket_price = resultSet.getDouble("ticket_price");

                    attraction = new TouristAttraction(id, attractionName, description, longerDescription,ticket_price);
                }

                // Hver række indeholder et nyt tag for denne attraktion
                String tagName = resultSet.getString("tag_name");
                if (tagName != null) {
                    tags.add(tagName);
                }
            }

            // Hvis vi fandt en attraktion, putter vi listen af tags på til sidst
            if (attraction != null) {
                attraction.setTags(tags); // Husk at have en setTags(List<String>) i din model!
                return attraction;
            }

            return null; // Returner null hvis attraktionen ikke findes
        }, name); // Her sender vi navnet ind i SQL'ens spørgsmålstegn (?)
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

    public boolean update(String name,String description,String longerdescription,double ticket_price) {
        //vores query
        String sql = "UPDATE attraction SET name = ?, description = ?, longerdescription=?,ticket_price=? WHERE name = ?";
        //vi connecter til DB
        try (Connection connection = connect();
             //vi skriver vores statement men executer det ikke endnu
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //vi sætter variablen name in i første spørgsmåls tegn i query
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, longerdescription);
            statement.setString(4, String.valueOf(ticket_price));
            statement.setString(5, name);
            //vi tjekker hvor mange linjer bliver slettet
            int rowsUpdated = statement.executeUpdate();
            //vi returner hvor mange blev slettet burde bare være 1 i vores tilfælde
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


}