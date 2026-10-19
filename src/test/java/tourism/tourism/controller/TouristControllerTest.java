package tourism.tourism.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.ui.ModelMap;
import tourism.tourism.model.TouristAttraction;
import tourism.tourism.service.TouristService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TouristController.class)
class TouristControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private TouristService touristService;


    @Test
    void testGetAllAttractions() throws Exception {


        TouristAttraction attraction1 = new TouristAttraction(1, "Tivoli", "Amusement park", "A historic amusement park in Copenhagen", 250.00);
        TouristAttraction attraction2 = new TouristAttraction(2, "The Little Mermaid", "Statue", "Famous bronze statue by Edvard Eriksen", 0.00);
        List<TouristAttraction> mockList = Arrays.asList(attraction1, attraction2);


        when(touristService.getAllAttractions()).thenReturn(mockList);


        mockMvc.perform(get("/attractions"))
                .andExpect(status().isOk())
                .andExpect(view().name("Getall"))
                .andExpect(model().attributeExists("attractions"))
                .andExpect(model().attribute("attractions", mockList));

}

    @Test
    void getLogin() throws Exception{
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

    }

    @Test
    void getRegister() throws Exception{
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void login() throws Exception{
        when(touristService.checkLogin(eq("admin"), eq("password123"), any(HttpSession.class)))
                .thenReturn(1);


        when(touristService.getAllAttractions()).thenReturn(Collections.emptyList());


        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "admin"))
                .andExpect(view().name("Getall"));
    }

    @Test
    void register() throws Exception{
        when(touristService.registerLogin(eq("admin"), eq("password123")))
                .thenReturn(1);

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void searchAttraction() throws Exception{
        TouristAttraction target = new TouristAttraction(1, "Tivoli", "Amusement park", "A historic amusement park in Copenhagen", 250.00);
        TouristAttraction otherTarget = new TouristAttraction(2, "The Little Mermaid", "Statue", "Famous bronze statue by Edvard Eriksen", 0.00);
        List<TouristAttraction> mockList = Arrays.asList(target, otherTarget);
        when(touristService.getAllAttractions()).thenReturn(mockList);
        when(touristService.getAttractionByName(eq("Tivoli"))).thenReturn(target);

        mockMvc.perform(get("/attractions/Tivoli"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("attraction", target))
                .andExpect(model().attribute("attractions", mockList));

    }

    @Test
    void executeSearch() throws Exception{
        TouristAttraction mockItem = new TouristAttraction(1, "Tivoli", "Amusement park", "A historic amusement park in Copenhagen", 250.00);
        List<TouristAttraction> mockList = Arrays.asList(mockItem);
        when(touristService.getAllAttractions()).thenReturn(mockList);
        when(touristService.getAttractionByName(eq("Tivoli"))).thenReturn(mockItem);

        mockMvc.perform(get("/attractions/search")
                        .param("query", "Tivoli"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("attraction", mockItem))
                .andExpect(model().attribute("attractions", mockList));

    }

    @Test
    void showAddPage() throws Exception{
        mockMvc.perform(get("/attractions/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add"));


    }
/*
    @Test
    void addAttraction() throws Exception{
       when(touristService.addAttraction(eq(1),eq("test"),eq("please"),eq("pleasseee"),eq(20.0)).thenReturn(null));
        mockMvc.perform(post("/attractions/add")
                        .param("id", "1")
                        .param("username", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "admin"))
                .andExpect(view().name("Getall"));
    }
*/
    /*
    @Test
    void updateAttraction() throws Exception{
    }

     */
/*
    @Test
    void removeAttraction() throws Exception{
    }

 */
}