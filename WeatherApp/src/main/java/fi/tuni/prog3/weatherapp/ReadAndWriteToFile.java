package fi.tuni.prog3.weatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Class for reading and writing favorite cities' weather data from and to a JSON-file.
 * Implements the iReadAndWriteToFile interface.
 * AI tool ChatGPT 3.5 was used to create a body for this class.
 * @author thjasy
 */
public class ReadAndWriteToFile implements iReadAndWriteToFile {

    private WeatherApp weatherApp;

    /**
    * Constructor for ReadAndWriteToFile class.
    * 
    * @param weatherApp an instance of WeatherApp
    */
    public ReadAndWriteToFile(WeatherApp weatherApp) {
        this.weatherApp = weatherApp;
    }
    
    /**
    * Reads a JSON file and returns its content as a string.
    * 
    * @param fileName the name of the JSON file to read
    * @return a string containing the content of the JSON file
    * @throws Exception if an error occurs while reading the file
    */
    @Override
    public String readFromFile(String fileName) throws Exception {
        // Read a JSON file and return it as a string
        try (FileReader reader = new FileReader(fileName)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.toString();
        }
    }

    /**
    * Writes WeatherApp data (locations) to a JSON file.
    * 
    * @param fileName the name of the JSON file to write
    * @return true if the writing operation is successful, false otherwise
    * @throws Exception if an error occurs while writing the file
    */
    @Override
    public boolean writeToFile(String fileName) throws Exception {
        // Writing WeatherApp data to a JSON file
        try (FileWriter file = new FileWriter(fileName)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("currentPlace", weatherApp.getCurrentCity());

            // Convert list of favorite cities to JsonArray
            JsonArray favouritesArray = new JsonArray();
            for (String favourite : WeatherApp.getFavourites()) {
                favouritesArray.add(favourite);
            }
            jsonObject.add("favourites", favouritesArray);

            Gson gson = new Gson();
            gson.toJson(jsonObject, file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
