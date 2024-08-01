package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.MockedStatic;

/**
 * Test class for ReadAndWriteToFile class.
 * AI tool ChatGPT 3.5 was used to create a body for this test class.
 * @author thjasy
 */
public class ReadAndWriteToFileTest {

    private ReadAndWriteToFile fileHandler;
    private final String testFileName = "testfile.json";
    private MockedStatic<WeatherApp> mockStaticWeatherApp;
    
    /**
    * Sets up the test environment using mock-object of WeatherApp before each test method is executed.
    */
    @BeforeEach
    public void setUp() {
        WeatherApp mockWeatherApp = Mockito.mock(WeatherApp.class);
        Mockito.when(mockWeatherApp.getCurrentCity()).thenReturn("Helsinki");
        mockStaticWeatherApp = Mockito.mockStatic(WeatherApp.class);
        mockStaticWeatherApp.when(WeatherApp::getFavourites).thenReturn(new ArrayList(List.of("Tampere", "Oulu")));
        fileHandler = new ReadAndWriteToFile(mockWeatherApp);
    }

    /**
    * Tears down the test environment after each test method is executed.
    */
    @AfterEach
    public void tearDown() {
        mockStaticWeatherApp.close();
    }
    
    /**
    * Tests the writeToFile method of ReadAndWriteToFile class.
    */
    @Test
    public void testWriteToFile() {
        try {
            assertTrue(fileHandler.writeToFile(testFileName));
            File file = new File(testFileName);
            assertTrue(file.exists(), "Test file should exist");
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
    * Tests the readFromFile method of ReadAndWriteToFile class.
    */
    @Test
    public void testReadFromFile() {
        try {
            FileWriter writer = new FileWriter(testFileName);
            writer.write("{\"currentPlace\":\"Helsinki\",\"favourites\":[\"Tampere\",\"Oulu\"]}");
            writer.close();

            String fileContent = fileHandler.readFromFile(testFileName);
            assertNotNull(fileContent);
            assertTrue(fileContent.contains("Helsinki"));
            assertTrue(fileContent.contains("Tampere"));
            assertTrue(fileContent.contains("Oulu"));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
