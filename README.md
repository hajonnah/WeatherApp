# WeatherApp

__WeatherApp__ is a Java application designed to provide weather information for cities around the world. 
Key features include:
*	__Search Functionality__: Users can search for weather information for different cities globally. On first launch, the application presents a welcome screen. On subsequent launches, the app automatically displays the weather data for the last searched location.
*	__Weather Data__: Retrieves information from the OpenWeatherMap API, including:
    *	Current temperature, wind speed, humidity, minimum and maximum temperatures and a weather icon.
    *	A ten-hour forecast with temperature, wind speed, and weather icons.
    *	Weather icons and temperatures for the next four days.
*	__Favorites__: Allows users to save favorite locations for quick access via a drop-down menu.
*	__Data Persistence__: Saves the last visited location and favorite locations to disk upon exiting.

__Project Details__

This project was developed as part of the Programming 3 (Interfaces & Techniques) course at Tampere University during Spring 2024.
*	__API.java, CityWeatherStatistics.java, HourlyWeather.java__ and __DailyWeather.java__: Developed by Jonna Hartikka (@hajonnah).
*	__WeatherApp.java__ (User Interface): Developed by Aada Härmä (@aadaharma).
*	__CityDatabase.java__: Collaboratively developed by Jonna Hartikka and Aada Härmä.
*	__ReadAndWriteToFile.java, CityWeatherStatisticsTest.java__ and __ReadAndWriteToFileTest.java__: Developed by Jaakko Sysimetsä.
*	__iAPI.java__ and __iReadAndWriteToFile.java__: Provided as part of the course materials.

__Documentation__: Available in Finnish only.

__Requirements__

*	__API Key__: This program requires an API key from OpenWeatherMap.
*	__Environment Variables__: The program uses the java-dotenv library to load environment variables (API key) from a .env file.

__Setup__

To use this program, follow these steps:
1.	Create a .env file in the root directory of your project.
2.	Add your API key to the .env file:
API_KEY=your_api_key_here
