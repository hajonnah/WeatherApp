
package fi.tuni.prog3.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import io.github.cdimascio.dotenv.Dotenv;

/**
 *
 * Class for extracting data from the OpenWeatherMap API.
 */
public class API implements iAPI {   
    // Load environment variables from the .env file
    Dotenv dotenv = Dotenv.load();
    // Get the API key from the environment variables
    String API_KEY = dotenv.get("API_KEY");  
    
    /**
     * Returns coordinates for a location.
     * @param loc Name of the location for which coordinates should be fetched.
     * @return String.
     */
    @Override
    public String lookUpLocation(String loc) {
        // Check if loc contains only letters, including Scandinavian letters
        String onlyLetters = "[\\p{L} -]+";
        // Check also that loc is not empty or containing only spaces
        if (!loc.matches(onlyLetters) || loc.trim().isEmpty()) {
        // Return empty string if loc is empty or contains non-letter characters
            return "";
        }
        
        HttpURLConnection connection = null;
        try {
            // Construct the URL for the Geocoding API request
            String apiUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" 
                            + loc + "&limit=5&appid=" + API_KEY;

            // Make an HTTP request
            connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            // Read the response
            StringBuilder response;
            try ( BufferedReader reader = new BufferedReader
                    (new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }    
            String coordinates = response.toString(); 
            // If coordinates equals [] city was not found
            if (coordinates.equals("[]")){
                return "";
            }
            return coordinates;
            
        } catch (IOException e) {           
            //Return an empty string to indicate failure
            return "";
        
        } finally {
        // Close the connection in the finally block
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Returns the current weather for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     */
    @Override
    public String getCurrentWeather(double lat, double lon){ 
        HttpURLConnection connection = null;
        try {    
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?lat=" 
                       + lat + "&lon=" + lon + "&units=metric&appid=" + API_KEY;
            URL url = new URL(urlStr);
            connection =(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = 
                    new BufferedReader
                        (new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }          
            String currentWeather = response.toString();                      
            return currentWeather;
             
        }catch (IOException e) {
            // Return an empty string to indicate failure
            return "";
        } finally {
        // Close the connection in the finally block
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Returns a daily forecast for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     */
    @Override
    public String getForecast(double lat, double lon) {
        HttpURLConnection connection = null;
        try {                  
            String urlStr = 
                 "https://api.openweathermap.org/data/2.5/forecast/daily?lat=" 
                 + lat + "&lon=" + lon + "&cnt=5&units=metric&appid=" + API_KEY;
            URL url = new URL(urlStr);
            connection =(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = 
                    new BufferedReader
                        (new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }          
            String dailyWeather = response.toString();                       
            return dailyWeather;
             
        }catch (IOException e) {
            // Return an empty string to indicate failure
            return "";
        }finally {
        // Close the connection in the finally block
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Returns a hourly forecast for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     */
    public String getHourlyWeather(double lat, double lon){
        HttpURLConnection connection = null;
        try {                  
            String urlStr = 
                    "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=" 
                    + lat + "&lon=" + lon + "&units=metric&appid=" + API_KEY 
                    + "&cnt=10";
            URL url = new URL(urlStr);
            connection =(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = 
                    new BufferedReader
                        (new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }          
            String dailyWeather = response.toString();            
            
            return dailyWeather;
             
        }catch (IOException e) {
            // Return an empty string to indicate failure
            return "";
        }finally {
        // Close the connection in the finally block
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
    

