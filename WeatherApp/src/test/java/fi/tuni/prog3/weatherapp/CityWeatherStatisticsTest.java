package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CityWeatherStatistics class.
 * AI tool ChatGPT 3.5 was used to create a body for this test class.
 * @author thjasy
 */
public class CityWeatherStatisticsTest {

    /**
    * Tests the setCoordinates method of CityWeatherStatistics class.
    */
    @Test
    public void testSetCoordinates() {
        CityWeatherStatistics cityWeather = new CityWeatherStatistics("TestCity");
        cityWeather.setCoordinates("[{\"lat\": 52.5200, \"lon\": 13.4050}]");
        assertEquals(52.5200, cityWeather.getLatitude());
        assertEquals(13.4050, cityWeather.getLongtitude());
    }

    /**
    * Tests the readCurrentWeather method of CityWeatherStatistics class.
    */
    @Test
    public void testReadCurrentWeather() {
        CityWeatherStatistics cityWeather = new CityWeatherStatistics("TestCity");
        assertTrue(cityWeather.readCurrentWeather("{\"main\": {\"temp\": 20.0, \"temp_min\": 15.0, \"temp_max\": 25.0, \"humidity\": 50}, \"wind\": {\"speed\": 5.0}, \"weather\": [{\"id\": 800}]}"));
        assertEquals(20, cityWeather.getCurrentTemperature());
        assertEquals(15, cityWeather.getMinTemperature());
        assertEquals(25, cityWeather.getMaxTemperature());
        assertEquals(50.0, cityWeather.getCurrentHumidity());
        assertEquals(5.0, cityWeather.getCurrentWindspeed());
        assertEquals(800, cityWeather.getCurrentWeatherId());
    }

    /**
    * Tests the readDailyWeather method of CityWeatherStatistics class with successful data.
    */
    @Test
    public void testReadDailyWeather_Succesful() {
        CityWeatherStatistics cityWeather = new CityWeatherStatistics("TestCity");
        assertTrue(cityWeather.readDailyWeather("{\"list\": [{\"temp\": {\"min\": 10.0, \"max\": 20.0}, \"dt\": 1618916400, \"weather\": [{\"id\": 800}]}, {\"temp\": {\"min\": 15.0, \"max\": 25.0}, \"dt\": 1619002800, \"weather\": [{\"id\": 802}]}]}"));
        // The DailyWeather class does not read the first DailyWeather object in the list because it is today's data. 
        // In the check, first values are skipped => the test starts from index 1 (values of the following days).
        assertEquals(1, cityWeather.getDailyWeatherList().size());
        assertEquals(15, cityWeather.getDailyWeatherList().get(0).getMinTemp());
        assertEquals(25, cityWeather.getDailyWeatherList().get(0).getMaxTemp());
        assertEquals(802, cityWeather.getDailyWeatherList().get(0).getWeatherId());
    }

    /**
    * Tests the readDailyWeather method of CityWeatherStatistics class with empty data.
    */
    @Test
    public void testReadDailyWeather_EmptyData() {
        CityWeatherStatistics cityWeather = new CityWeatherStatistics("TestCity");
        String dailyWeatherData = "";
        assertFalse(cityWeather.readDailyWeather(dailyWeatherData));
        assertEquals(0, cityWeather.getDailyWeatherList().size());
    }
  
    /**
    * Tests the readDailyWeather method of CityWeatherStatistics class with invalid data.
    */
    @Test
    public void testReadDailyWeather_InvalidData() {
        CityWeatherStatistics cityWeather = new CityWeatherStatistics("TestCity");
        String dailyWeatherData = "Invalid JSON Data";
        assertFalse(cityWeather.readDailyWeather(dailyWeatherData));
        assertEquals(0, cityWeather.getDailyWeatherList().size());
    }
    
    /**
    * Tests the readHourlyWeather method of CityWeatherStatistics class.
    */
    @Test
    public void testReadHourlyWeather() {
        CityWeatherStatistics cityWeather = new CityWeatherStatistics("TestCity");
        assertTrue(cityWeather.readHourlyWeather("{\"list\": [{\"dt\": 1618916400, \"main\": {\"temp\": 15.0}, \"wind\": {\"speed\": 5.0}, \"weather\": [{\"id\": 801}]}, {\"dt\": 1618917000, \"main\": {\"temp\": 16.0}, \"wind\": {\"speed\": 6.0}, \"weather\": [{\"id\": 802}]}]}"));
        assertEquals(2, cityWeather.getHouryWeatherList().size());
        assertEquals(15, cityWeather.getHouryWeatherList().get(0).getTemperature());
        assertEquals(5.0, cityWeather.getHouryWeatherList().get(0).getWindSpeed());
        assertEquals(801, cityWeather.getHouryWeatherList().get(0).getWeatherId());
    }
}