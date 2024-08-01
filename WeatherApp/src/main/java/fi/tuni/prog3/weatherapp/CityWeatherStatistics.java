
package fi.tuni.prog3.weatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * Class for representing city weather statistics.
 */
public class CityWeatherStatistics {
    private final String name;
    private double latitude;
    private double longtitude;
    private int currentTemperature;
    private int minTemperature;
    private int maxTemperature;
    private int currentWeatherId;
    private double currentWindspeed;
    private double currentHumidity;
    private final ArrayList<DailyWeather> dailyWeatherList;
    private final ArrayList<HourlyWeather> houryWeatherList;
    
    /**
     * Constructs a CityWeatherStatistics object with the given city name.
     * @param name The name of the city.
     */
    public CityWeatherStatistics(String name){
        this.name = name;
        this.dailyWeatherList = new ArrayList<>();
        this.houryWeatherList = new ArrayList<>();
    }

    /**
     * Sets the latitude and longitude coordinates of the city.
     * @param coords The JSON string containing the city coordinates.
     */
    public void setCoordinates(String coords) {            
        JsonArray jsonArray = JsonParser.parseString(coords).getAsJsonArray();
        // Check if the JsonArray is not empty
        if (jsonArray.size() > 0) {
            // Get the first JsonObject from the array
            JsonObject city = jsonArray.get(0).getAsJsonObject();

            // Extract latitude and longitude values
            double lat = city.get("lat").getAsDouble();
            double lon = city.get("lon").getAsDouble();
            this.latitude = lat;
            this.longtitude = lon;         
        }
    }

    
    /**
     * Reads and parses the current weather data for the city.
     * @param currentWeather The JSON string containing the current weather data.
     * @return True if reading the data was successful, false otherwise.
     */
    public boolean readCurrentWeather(String currentWeather) {
        // Parse the JSON string
        // If currentWeather is "", reading currentWeather failed
        if (currentWeather.equals("")){
            return false;
        }
        else{
            JsonObject jsonObject = JsonParser.parseString(currentWeather).
                                                        getAsJsonObject();
            double temperature = jsonObject.getAsJsonObject("main").
                                                get("temp").getAsDouble();
            double tempMinDouble = jsonObject.getAsJsonObject("main").
                                                get("temp_min").getAsDouble();         
            double tempMaxDouble = jsonObject.getAsJsonObject("main").
                                                get("temp_max").getAsDouble();
            int tempMin = (int) Math.round(tempMinDouble);
            int tempMax = (int) Math.round(tempMaxDouble);
            double humidity = jsonObject.getAsJsonObject("main").
                                            get("humidity").getAsDouble();
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed")
                                                            .getAsDouble();
            int weatherId = jsonObject.getAsJsonArray("weather").get(0).
                                    getAsJsonObject().get("id").getAsInt();
            int temperatureRounded = (int) Math.round(temperature);

            this.currentHumidity = humidity;
            this.currentTemperature = temperatureRounded;
            this.currentWindspeed = windSpeed;
            this.currentWeatherId = weatherId; 
            this.maxTemperature = tempMax;
            this.minTemperature = tempMin;

            return true;
        }
    }
    
    /**
     * Reads and parses the daily weather data for the city.
     * @param dailyWeatherData The JSON string containing the daily weather data.
     * @return True if reading the data was successful, false otherwise.
     */
    public boolean readDailyWeather(String dailyWeatherData){
        if (dailyWeatherData.equals("")){
            return false;
        }
        else{
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(dailyWeatherData, 
                                                        JsonObject.class);
                JsonArray listArray = jsonObject.getAsJsonArray("list");
                
                // Ignore todays Weather and get only forecast information
                boolean todaysWeather = true;
                        
                for (JsonElement element : listArray) {
                    if (todaysWeather){
                        todaysWeather = false;
                    }
                    else{
                        JsonObject listItem = element.getAsJsonObject();
                        double minDouble = listItem.getAsJsonObject("temp").
                                                    get("min").getAsDouble();
                        double maxDouble = listItem.getAsJsonObject("temp").
                                                    get("max").getAsDouble();
                        long dt = listItem.get("dt").getAsLong();

                        int minTemp = (int) Math.round(minDouble);
                        int maxTemp = (int) Math.round(maxDouble);


                        Date date = new Date(dt * 1000L);

                        // Extract weatherId information
                        JsonArray weatherArray = listItem.getAsJsonArray("weather");
                        // Default value if weather ID is not found
                        int weatherId = -1; 
                        for (JsonElement weatherElement : weatherArray) {
                            weatherId = weatherElement.getAsJsonObject().
                                                        get("id").getAsInt();
                            break;
                        }
                        // create new DailyWeather object
                        DailyWeather dailyWeather = new DailyWeather(date, minTemp, 
                                                                maxTemp, weatherId);
                        dailyWeatherList.add(dailyWeather);
                    }
                       
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }            
    }
    
    /**
     * Reads and parses the hourly weather forecast data for the city.
     * @param hourlyWeatherData The JSON string containing the hourly weather
     * forecast data.
     * @return True if reading the data was successful, false otherwise.
     */
    public boolean readHourlyWeather(String hourlyWeatherData){
        if (hourlyWeatherData.equals("")){
            return false;
        }
        else{
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(hourlyWeatherData, 
                                                        JsonObject.class);
                JsonArray list = jsonObject.getAsJsonArray("list");

                for (JsonElement element : list) {
                    JsonObject item = element.getAsJsonObject();
                    long dt = item.get("dt").getAsLong();
                    Date date = new Date(dt * 1000L);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("HH");
                    // Format the date to extract only the hour
                    String hour = sdf.format(date);
                    
                    double tempDouble = item.getAsJsonObject("main").
                                            get("temp").getAsDouble();
                    int temperature = (int) Math.round(tempDouble);
                    double windSpeed = item.getAsJsonObject("wind").
                                            get("speed").getAsDouble();

                    // Extract weatherId information
                    JsonArray weatherArray = item.getAsJsonArray("weather");
                    // Default value if weather ID is not found
                    int weatherId = -1; 
                    for (JsonElement weatherElement : weatherArray) {
                        weatherId = weatherElement.getAsJsonObject().
                                                get("id").getAsInt();
                        break;
                    }
                    HourlyWeather hourlyWeather = new HourlyWeather(hour, 
                                        temperature, windSpeed, weatherId);
                    houryWeatherList.add(hourlyWeather);
                }                
                return true;
            
            } catch (Exception e) {
                return false;
            }
        }            
    }

    /**
     * Retrieves the name of the city.
     * @return The name of the city.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the latitude of the city.
     * @return The latitude of the city.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Retrieves the longitude of the city.
     * @return The longitude of the city.
     */
    public double getLongtitude() {
        return longtitude;
    }

    /**
     * Retrieves the current temperature of the city.
     * @return The current temperature of the city.
     */
    public int getCurrentTemperature() {
        return currentTemperature;
    }

    /**
     * Retrieves todays minimal temperature of the city.
     * @return Todays minimal temperature of the city.
     */
    public int getMinTemperature() {
        return minTemperature;
    }

    /**
     * Retrieves todays maximal temperature of the city.
     * @return Todays maximal temperature of the city.
     */
    public int getMaxTemperature() {
        return maxTemperature;
    }   

    /**
     * Retrieves the current weather ID of the city for the weather icon.
     * @return The current weather ID of the city.
     */
    public int getCurrentWeatherId() {
        return currentWeatherId;
    }

    /**
     * Retrieves the current wind speed of the city.
     * @return The current wind speed of the city.
     */
    public double getCurrentWindspeed() {
        return currentWindspeed;
    }  

    /**
     * Retrieves the current humidity of the city.
     * @return The current humidity of the city.
     */
    public double getCurrentHumidity() {
        return currentHumidity;
    }
    
    /**
     * Retrieves the list of daily weather forecasts for the city.
     * @return The list of daily weather forecasts for the city.
     */
    public ArrayList<DailyWeather> getDailyWeatherList() {
        return dailyWeatherList;
    }

    /**
     * Retrieves the list of hourly weather forecasts for the city.
     * @return The list of hourly weather forecasts for the city.
     */
    public ArrayList<HourlyWeather> getHouryWeatherList() {
        return houryWeatherList;
    }   
}
