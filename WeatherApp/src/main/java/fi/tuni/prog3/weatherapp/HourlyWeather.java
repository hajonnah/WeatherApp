
package fi.tuni.prog3.weatherapp;


/**
 *
 * Represents hourly weather data for a city.
 */
public class HourlyWeather {
    private String hour;
    private int temperature;
    private double windSpeed;
    private int weatherId;
    
    /**
     * Constructs an HourlyWeather object with the specified date, temperature, 
     * wind speed, and weather ID.
     * @param hour Hour for which the weather data is recorded.
     * @param temperature The temperature for the hour.
     * @param windSpeed The wind speed for the hour.
     * @param weatherId The weather ID representing the weather condition 
     * for the hour.
     */
    public HourlyWeather(String hour, int temperature, double windSpeed, 
                                     int weatherId){
        this.hour = hour;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.weatherId = weatherId;
    }

    /**
     * Retrieves hour for the hourly weather data.
     * @return String hour for the hourly weather data.
     */
    public String getHour() {
        return hour;
    }

    /**
     * Retrieves the weather ID representing the weather condition for the hour.
     * @return The weather ID representing the weather condition for the hour.
     */
    public int getWeatherId() {
        return weatherId;
    }
    
    /**
     * Retrieves the temperature for the hour.
     * @return The temperature for the hour.
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * Retrieves the wind speed for the hour.
     * @return The wind speed for the hour.
     */
    public double getWindSpeed() {
        return windSpeed;
    }
}
