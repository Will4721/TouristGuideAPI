package tourism.touristguideapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
// Vi importerer din rigtige hovedklasse herunder
import tourism.tourism.TouristGuideApiApplication;

// Vi fortæller Spring præcis, hvilken klasse der starter din applikation
@SpringBootTest(classes = TouristGuideApiApplication.class)
class TouristGuideApiApplicationTests {

    @Test
    void contextLoads() {
        // Denne test tjekker blot om dit projekt kan starte op uden fejl
    }

}
