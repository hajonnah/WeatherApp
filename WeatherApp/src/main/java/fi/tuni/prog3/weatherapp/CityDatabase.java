package fi.tuni.prog3.weatherapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.TreeMap;

/**
 * A class which keeps currently known cities in the app in a map.
 * This class also builds new cities and updates known cities' weather info.
 */
public class CityDatabase {
    private static TreeMap<String, CityWeatherStatistics> cities;

    private static API api = new API();

    /**
     * A constructor for CityDatabase.
     */
    public CityDatabase() {
        cities = new TreeMap<>();
    }

    /**
     * A getter which returns a specific city from cities-map.
     * @param name A String value of city name.
     * @return A reference to a CityWeatherStatistics city object.
     */
    public static CityWeatherStatistics getCity(String name) {
        return cities.get(name);
    }

    /**
     * A method that tries to build a city, if the city can be found from the api.
     * If city is already in cities-map, this method updates its data, but doesn't
     * build it from scratch. The data comes from the api through getters.
     * @param name A String value of the city name to be searched.
     * @return A String value of "" if the operation was not successful,
     * else value of city name.
     */
    public String cityBuilder(String name) {

        //Look up the city, if it cannot be found, return "".
        String coords = api.lookUpLocation(name);
        if (coords.isEmpty()) {
            return "";
        }
        
        // Parse name of city from coords
        JsonArray jsonArray = JsonParser.parseString(coords).getAsJsonArray();
        JsonObject cityjson = jsonArray.get(0).getAsJsonObject();
        String cityName = cityjson.get("name").getAsString();       

        //If city is already known, don't make a new one, only
        //replace old data with current data.
        if (cities.containsKey(cityName)) {
            CityWeatherStatistics city = cities.get(cityName);

            double lat = city.getLatitude();
            double lon = city.getLongtitude();

            String currentWeather = api.getCurrentWeather(lat, lon);
            if (currentWeather.isEmpty()) {
                return "";
            }
            city.readCurrentWeather(currentWeather);


            String forecast = api.getForecast(lat, lon);
            if (forecast.isEmpty()) {
                return "";
            }
            city.readDailyWeather(forecast);

            String hourlyWeather = api.getHourlyWeather(lat, lon);
            if (hourlyWeather.isEmpty()) {
                return "";
            }
            city.readHourlyWeather(hourlyWeather);

            return city.getName();

        }

        //City is not known, so make a new one and add it to the list.
        CityWeatherStatistics newCity = new CityWeatherStatistics(cityName);
        cities.put(cityName,newCity);

        newCity.setCoordinates(coords);

        double lat = newCity.getLatitude();
        double lon = newCity.getLongtitude();

        String currentWeather = api.getCurrentWeather(lat, lon);
        if (currentWeather.isEmpty()) {
            return "";
        }
        newCity.readCurrentWeather(currentWeather);
        String forecast = api.getForecast(lat, lon);
        
        if (forecast.isEmpty()) {
            return "";
        }
        newCity.readDailyWeather(forecast);

        String hourlyWeather = api.getHourlyWeather(lat, lon);
        
        if (hourlyWeather.isEmpty()) {
            return "";
        }
        newCity.readHourlyWeather(hourlyWeather);

        return newCity.getName();

    }


}
