
package fi.tuni.prog3.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * Represents daily weather data for a city.
 */
public class DailyWeather {
    private String date;
    private int minTemp;
    private int maxTemp;
    private int weatherId;
    
    /**
     * Constructs a DailyWeather object with the specified date, 
     * minimum temperature, maximum temperature and weather ID.
     * @param date The date for which the weather data is recorded.
     * @param minTemp The minimum temperature for the day.
     * @param maxTemp The maximum temperature for the day.
     * @param weatherId The weather ID representing the weather condition 
     * for the day.
     */
    public DailyWeather(Date date, int minTemp, int maxTemp, int weatherId){
        // Format the date to extract weekday, month, and date
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE", Locale.ENGLISH); 
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH); 
        SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("d");
        String weekday = weekdayFormat.format(date);
        String month = monthFormat.format(date);
        String dayOfMonth = dayOfMonthFormat.format(date);
        
        this.date = weekday + " " + month + " " + dayOfMonth;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.weatherId = weatherId;
    }

    /**
     * Retrieves the date for the daily weather data.
     * @return The date for the daily weather data.
     */
    public String getDate() {
        return date;
    }

    /**
     * Retrieves the weather ID representing the weather condition for the day.
     * @return The weather ID representing the weather condition for the day.
     */
    public int getWeatherId() {
        return weatherId;
    }

    /**
     * Retrieves the minimum temperature for the day.
     * @return The minimum temperature for the day.
     */
    public int getMinTemp() {
        return minTemp;
    }

    /**
     * Retrieves the maximum temperature for the day.
     * @return The maximum temperature for the day.
     */
    public int getMaxTemp() {
        return maxTemp;
    }  
}
