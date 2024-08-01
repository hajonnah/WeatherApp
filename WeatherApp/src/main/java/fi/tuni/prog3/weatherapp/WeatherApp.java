package fi.tuni.prog3.weatherapp;

import com.google.gson.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.ArrayList;


/**
 *  A JavaFX WeatherApp application.
 * The template used for this class was made by Jorma Laurikkala.
 * @author aadaharma
 */
public class WeatherApp extends Application {

    private Stage stage;
    private static String currentPlace;
    private static ArrayList<String> favourites;
    private static final String saveFileName = "lastsave.json";
    private MenuButton favouritesMenu;
    private static ArrayList<MenuItem> menuItems;
    private static final CityDatabase database = new CityDatabase();

    /**
     * Constructor for WeatherApp.
     */
    public WeatherApp() {
        favourites = new ArrayList<>();
        menuItems = new ArrayList<>();
        currentPlace = "";
    }

    /**
     * A getter for the current city name.
     * @return A String value of city name.
     */
    public String getCurrentCity() {
        return currentPlace;
    }

    /**
     * A getter for favourites.
     * @return A Arraylist<String> containing favourite cities' names.
     */
    public static ArrayList<String> getFavourites() {
        return favourites;
    }

    /**
     * Main function which launches the application.
     * @param args An array of command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * A method, which starts the application and is called after
     * constructor. Shows weather data if it can be found from json-file,
     * otherwise shows a welcome screen.
     * @param stage A container for all scenes.
     */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        WeatherApp app = this;

        //Make a new task for reading the JSON-file concurrently.
        Task<Void> read = new Task<>() {
            @Override
            protected Void call() {
                final String[] data = new String[1];
                ReadAndWriteToFile reader = new ReadAndWriteToFile(app);
                //If data is found, parse and save, else throw exception.
                try {
                    data[0] = reader.readFromFile(saveFileName);
                    parseAndSave(data[0]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };

        //Make new thread for the task 'read' and start it.
        Thread th = new Thread(read);
        th.setDaemon(true);
        th.start();

        //Make a welcome scene.
        BorderPane startRoot = makeRoot();
        startRoot.setCenter(getWelcomeMessage());

        Scene startScene = new Scene(startRoot);
        startScene.getStylesheets().add("stylesheet.css");

        //If there is a current place, and it can be built in database, show weather.
        if (!currentPlace.isEmpty()) {
            if (!database.cityBuilder(currentPlace).isEmpty()) {
                showCity(currentPlace);
                stage.setTitle("WeatherApp");
                // If there is a current place, but it cannot be built in database, show warning.
            } else {
                noCityWarning(currentPlace);
            }
            // If there isn't a current place, show welcome screen.
        } else {
            switchScene(startScene);
            stage.setTitle("WeatherApp");
        }
        stage.show();
    }


    /**
     * Parser to parse data from string first to JSON with Gson
     * and then add it to datastructures.
     * @param lastSave A string of places.
     */
    private void parseAndSave(String lastSave) {
        Gson gson = new Gson();

        try {
            JsonObject jsonObject = gson.fromJson(lastSave, JsonObject.class);

            currentPlace = jsonObject.get("currentPlace").getAsString();

            JsonArray favouritesArray = jsonObject.getAsJsonArray("favourites");
            for (JsonElement element : favouritesArray) {
                String favourite = element.getAsString();
                favourites.add(favourite);
                menuItems.add(new MenuItem(favourite));
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * Shows a pop-up warning for the user, if the city cannot
     * be shown.
     * @param city A String value of city name.
     */
    private void noCityWarning(String city) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("City not found");
        alert.setContentText("Sorry, the city \"" + city + "\" was not found!");

        alert.showAndWait();
    }


    /**
     * A method for a welcome message when this app is first started.
     * @return A HBox containing the welcome message.
     */
    private HBox getWelcomeMessage() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        //Make a VBox to put inside HBox.
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        Label l1 = new Label("Welcome to WeatherApp!");
        l1.getStyleClass().add("h1");

        Label l2 = new Label("Use the search bar above to see current weather around the world.");
        l2.getStyleClass().add("h2");

        vbox.getChildren().add(l1);
        vbox.getChildren().add(l2);

        hbox.getChildren().add(getLeftCrescent());
        hbox.getChildren().add(vbox);
        hbox.getChildren().add(getRightCrescent());

        return hbox;
    }

    /**
     * A method which returns a crescent icon.
     * @return A Label containing the crescent icon.
     */
    private Label getLeftCrescent() {
        Label label = new Label();
        FontIcon icon = new FontIcon();
        icon.setIconLiteral("wi-moon-waning-crescent-2");
        icon.setIconSize(150);
        icon.setIconColor(Color.NAVY);
        label.setGraphic(icon);

        return label;
    }

    /**
     * A method which return a crescent icon.
     * @return A Label containing the crescent icon.
     */
    private Label getRightCrescent() {
        Label label = new Label();
        FontIcon icon = new FontIcon();
        icon.setIconLiteral("wi-moon-waxing-crescent-5");
        icon.setIconSize(150);
        icon.setIconColor(Color.NAVY);
        label.setGraphic(icon);

        return label;
    }

    /**
     * A method, which switches the scene on stage.
     * @param scene A scene to be rendered on screen.
     */
    private void switchScene(Scene scene) {
        stage.setScene(scene);
    }

    /**
     * A method which makes a BorderPane root for a scene
     * and adds all necessary components.
     * @return A BorderPane root for scenes.
     */
    private BorderPane makeRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setPrefSize(1000, 700);
        root.getStyleClass().add("root");

        GridPane top = getTopGrid();
        BorderPane.setMargin(top, new Insets(5, 10, 0, 10));
        root.setTop(top);

        Button quitButton = getQuitButton();
        BorderPane.setMargin(quitButton, new Insets(0, 10, 20, 10));
        BorderPane.setAlignment(quitButton, Pos.TOP_RIGHT);
        root.setBottom(quitButton);

        return root;
    }

    /**
     * A method for getting the top grid of BorderPane.
     * Top grid contains the search bar and favourites menu.
     * @return A GridPane containing the search bar and menu.
     */
    private GridPane getTopGrid() {
        GridPane top = new GridPane();
        top.setPadding(new Insets(10));

        //Add column constraints to ensure grid elements are in correct places.
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.CENTER);
        top.getColumnConstraints().addAll(col1, col2);

        HBox search = getSearchBar();
        HBox fav = getFavouriteMenu();

        top.add(fav, 0,0);
        top.add(search,1,0);

        return top;
    }

    /**
     * A method which returns the search bar.
     * @return A HBox containing the search bar.
     */
    private HBox getSearchBar() {
        HBox searchBar = new HBox();
        searchBar.setSpacing(5);
        searchBar.setAlignment(Pos.CENTER_RIGHT);

        TextField input = new TextField();
        input.setPrefSize(200,25);
        input.setPromptText("Enter location.");
        searchBar.getChildren().add(input);

        Button search = new Button("Search");
        search.setPrefSize(60,25);
        searchBar.getChildren().add(search);

        setKeyAction(input);
        setButtonAction(search, input);

        return searchBar;
    }

    /**
     * Set an action for a button, which searches for a city
     * based on user's input, and then shows the city or
     * shows an error pop-up window.
     * @param button A button to set the action to.
     * @param input A TextField where the input is taken from.
     */
    private void setButtonAction(Button button, TextField input) {

        button.setOnAction(e -> {
            String city = String.valueOf(input.getText());
            if (!city.isEmpty()) {
                String cityName = database.cityBuilder(city);
                if (!cityName.isEmpty()) {
                    currentPlace = cityName;
                    showCity(cityName);
                } else {
                    noCityWarning(city);
                }
            }

        });
    }

    /**
     * Set an action for pressing a key, which searches for a city
     * based on user's input, and then shows the city or
     * shows an error pop-up window.
     * @param input A TextField where the input is taken from.
     */
    private void setKeyAction(TextField input) {

        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String city = String.valueOf(input.getText());
                if (!city.isEmpty()) {
                    String cityName = database.cityBuilder(city);
                    if (!cityName.isEmpty()) {
                        currentPlace = cityName;
                        showCity(cityName);
                    } else {
                        noCityWarning(city);
                    }
                }
            }
        });
    }

    /**
     * A method which makes and returns a drop-down menu for
     * displaying user's favourite cities. Uses the attribute
     * menuItems to get menu items.
     * @return A HBox containing the menu.
     */
    private HBox getFavouriteMenu() {
        HBox favourites = new HBox();
        favourites.setSpacing(5);
        favourites.setAlignment(Pos.CENTER_LEFT);

        favouritesMenu = new MenuButton("Favourites");
        favouritesMenu.setPrefSize(80,20);

        //For every item in menuItems, add it to menu
        for (MenuItem fav : menuItems) {
            if (!favouritesMenu.getItems().contains(fav)) {
                //Set action for menu item
                fav.setOnAction((e) -> {
                    showCity(fav.getText());
                });
                favouritesMenu.getItems().add(fav);
            }
        }
        favourites.getChildren().add(favouritesMenu);

        return favourites;
    }

    /**
     * A method that makes a quit button and assigns
     * an action for the button.
     * @return A Button.
     */
    private Button getQuitButton() {

        Button button = new Button("Quit");

        //Adding an event to the button to terminate the application
        // and save currentPlace and favourites to a file.
        button.setOnAction((ActionEvent event) -> {
            WeatherApp app = this;
            //If currentPlace is not empty, attempt to save data in JSON-format.
            if (!currentPlace.isEmpty()) {
                Task<Void> write = new Task<>() {
                    @Override
                    protected Void call() {
                        ReadAndWriteToFile writer = new ReadAndWriteToFile(app);
                        try {
                            writer.writeToFile(saveFileName);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                };

                //Run the task 'write' concurrently
                Thread th = new Thread(write);
                th.setDaemon(true);
                th.start();
            }

            //When thread 'th' is ready, exit app.
            Platform.runLater(() -> {
                Platform.exit();
            });

        });

        return button;
    }


    /**
     * Shows city's weather data if city can be found or built. Makes a
     * cityScene and switches to it.
     *
     * @param name A string value of city's name.
     */
    private void showCity(String name) {

        //Try to find city.
        CityWeatherStatistics city = CityDatabase.getCity(name);

        //If city not found, try to build it and find after that.
        if (city == null) {
            String cityName = database.cityBuilder(name);
            if (!cityName.isEmpty()) {
                city = CityDatabase.getCity(cityName);
            } else {
                return;
            }
        }

        //Make a scene to show city's weather.
        BorderPane cityRoot = makeRoot();
        cityRoot.setCenter(getCenterHBox(city));

        Scene cityScene = new Scene(cityRoot);
        cityScene.getStylesheets().add("stylesheet.css");

        switchScene(cityScene);
    }

    /**
     * A method which returns the HBox which is to be
     * in the middle of the city scene.
     * @param city The current city.
     * @return HBox which contains smaller components.
     */
    private HBox getCenterHBox(CityWeatherStatistics city) {
        HBox centerHBox = new HBox();
        centerHBox.setSpacing(60);
        centerHBox.setAlignment(Pos.CENTER);

        centerHBox.getChildren().addAll(getLeftVBox(city), getRightVBox(city));

        return centerHBox;
    }


    /**
     * A method which returns a VBox containing city's
     * current weather, highlights and next days' forecast.
     * This VBox is to be on the left side of the center HBox.
     * @param city The current city.
     * @return VBox which contains smaller components.
     */
    private VBox getLeftVBox(CityWeatherStatistics city) {
        VBox leftVBox = new VBox();
        leftVBox.setAlignment(Pos.TOP_CENTER);

        leftVBox.getChildren().add(getCurrentWeather(city));
        leftVBox.getChildren().add(getHighlights(city));
        leftVBox.getChildren().add(getNextDays(city));

        return leftVBox;
    }

    /**
     * A method which returns a VBox containing the current
     * weather statistics for the current city.
     * @param city The current city.
     * @return VBox containing smaller components.
     */
    private VBox getCurrentWeather(CityWeatherStatistics city) {
        VBox currentWeather = new VBox();

        currentWeather.setPrefSize(600, 500);
        currentWeather.setAlignment(Pos.TOP_CENTER);

        //Make container for the favourite-button.
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.getChildren().add(getSetFavouriteButton());

        currentWeather.getChildren().add(buttonBox);

        Label cityName = new Label(city.getName());
        cityName.getStyleClass().add("h1");
        cityName.setStyle("-fx-font-size: 50");

        currentWeather.getChildren().add(cityName);

        currentWeather.getChildren().add(getTempAndIcon(city));
        currentWeather.getChildren().add(getHumAndWind(city));

        return currentWeather;
    }

    /**
     * A method returning the correct icon and temperature
     * for the current city at this hour.
     * @param city The current city.
     * @return HBox containing icon and temp components.
     */
    private HBox getTempAndIcon(CityWeatherStatistics city) {
        HBox tempAndIcon = new HBox();
        tempAndIcon.setAlignment(Pos.CENTER);
        tempAndIcon.setSpacing(80);

        //Get correct weather icon by getting the next hour, and
        //substracting 1 to get this hour. Works also in case if the next hour is 01.
        tempAndIcon.getChildren().add(getBigIcon(city.getCurrentWeatherId(),
                Integer.parseInt(city.getHouryWeatherList().get(0).getHour())-1));

        //Make container for temp and Celsius icon
        HBox celsius = new HBox();
        celsius.setSpacing(20);
        celsius.setAlignment(Pos.TOP_CENTER);
        Label temp = new Label(String.valueOf(city.getCurrentTemperature()));
        temp.setStyle("-fx-font-size: 150");
        celsius.getChildren().add(temp);
        celsius.getChildren().add(getBigCelsiusIcon());

        tempAndIcon.getChildren().add(celsius);

        return tempAndIcon;
    }

    /**
     * A method for getting a big weather icon.
     * @param id The weather id for current weather.
     * @param hour The hour for which the icon is shown.
     * @return A Label containing the icon.
     */
    private Label getBigIcon(int id, int hour) {

        Label label = new Label();
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(getIcon(id, hour));
        icon.setIconSize(150);
        icon.setIconColor(Color.NAVY);
        label.setGraphic(icon);

        return label;
    }

    /**
     * A method returning a big Celsius icon.
     * @return Label, which contains the icon.
     */
    private Label getBigCelsiusIcon() {
        Label label = new Label();
        FontIcon icon = new FontIcon();
        icon.setIconLiteral("wi-celsius");
        icon.setIconSize(150);
        icon.setIconColor(Color.NAVY);
        label.setGraphic(icon);

        return label;
    }

    /**
     * A method returning humidity and wind speed for the
     * current city.
     * @param city The current city.
     * @return HBox containing hum and wind components.
     */
    private HBox getHumAndWind(CityWeatherStatistics city) {
        HBox humAndWind = new HBox();
        humAndWind.setSpacing(80);
        humAndWind.setAlignment(Pos.TOP_CENTER);
        humAndWind.getChildren().add(getWindSpeed(city));
        humAndWind.getChildren().add(getHumidity(city));

        return humAndWind;
    }

    /**
     * A method for getting the current wind speed.
     * @param city The current city.
     * @return A Label containing the data.
     */
    private Label getWindSpeed(CityWeatherStatistics city) {
        Label windSpeed = new Label("Wind speed: "+ city.getCurrentWindspeed() + " m/s");
        windSpeed.getStyleClass().add("h2");
        return windSpeed;
    }

    /**
     * A method for getting the current humidity.
     * @param city The current city.
     * @return A Label containing the data.
     */
    private Label getHumidity(CityWeatherStatistics city) {
        Label humidity = new Label("Humidity: "+ city.getCurrentHumidity() + " %");
        humidity.getStyleClass().add("h2");
        return humidity;
    }


    /**
     * A method which returns the highlights of the day:
     * min and max temp.
     * @param city The current city.
     * @return A HBox containing the data.
     */
    private HBox getHighlights(CityWeatherStatistics city) {
        HBox highlights = new HBox();
        highlights.setPrefSize(500, 200);
        highlights.setSpacing(80);
        highlights.setAlignment(Pos.TOP_CENTER);

        highlights.getChildren().add(getCurrentMaxTemp(city));
        highlights.getChildren().add(getCurrentMinTemp(city));

        return highlights;
    }

    /**
     * A method which returns the current max temp and Celsius icon.
     * @param city The current city.
     * @return A HBox containing the max temp and Celsius icon.
     */
    private HBox getCurrentMaxTemp(CityWeatherStatistics city) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.BASELINE_CENTER);
        hbox.setSpacing(5);
        Label max = new Label("Max: " + city.getMaxTemperature());
        max.getStyleClass().add("h2");
        Label icon = getSmallCelsiusIcon();
        hbox.getChildren().addAll(max, icon);
        return hbox;
    }

    /**
     * A method which returns the current min temp and Celsius icon.
     * @param city The current city.
     * @return A HBox containing the min temp and Celsius icon.
     */
    private HBox getCurrentMinTemp(CityWeatherStatistics city) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.BASELINE_CENTER);
        hbox.setSpacing(5);
        Label min = new Label("Min: " + city.getMinTemperature());
        min.getStyleClass().add("h2");
        Label icon = getSmallCelsiusIcon();
        hbox.getChildren().addAll(min, icon);
        return hbox;
    }

    /**
     * A method which returns a small Celsius icon.
     * @return A Label containing the icon.
     */
    private Label getSmallCelsiusIcon() {
        Label label = new Label();
        FontIcon icon = new FontIcon();
        icon.setIconLiteral("wi-celsius");
        icon.setIconSize(25);
        icon.setIconColor(Color.NAVY);
        label.setGraphic(icon);

        return label;
    }

    /**
     * A method which returns the next four days' weather statistics.
     * @param city The current city.
     * @return A HBox containing the data.
     */
    private HBox getNextDays(CityWeatherStatistics city) {
        HBox nextDays = new HBox();
        nextDays.setAlignment(Pos.TOP_CENTER);
        nextDays.setPrefSize(500, 200);
        nextDays.setSpacing(60);

        ArrayList<DailyWeather> daily = city.getDailyWeatherList();

        //This for-loop makes a VBox for the weather data of one day
        //for all the days in arraylist.
        for (DailyWeather dailyWeather : daily) {
            VBox vbox = new VBox();
            vbox.setSpacing(10);

            Label date = new Label(String.valueOf(dailyWeather.getDate()));
            date.getStyleClass().add("h3");
            vbox.getChildren().add(date);

            vbox.getChildren().add(setMediumIcon((dailyWeather.getWeatherId()), 6));

            HBox hbox1 = new HBox();
            hbox1.setAlignment(Pos.BASELINE_CENTER);
            hbox1.setSpacing(5);
            Label maxTemp = new Label("Max: " + dailyWeather.getMaxTemp());
            hbox1.getChildren().addAll(maxTemp, getSmallCelsiusIcon());

            HBox hbox2 = new HBox();
            hbox2.setAlignment(Pos.BASELINE_CENTER);
            hbox2.setSpacing(5);
            Label minTemp = new Label("Min: " + dailyWeather.getMinTemp());
            hbox2.getChildren().addAll(minTemp, getSmallCelsiusIcon());

            vbox.getChildren().addAll(hbox1, hbox2);
            nextDays.getChildren().add(vbox);
        }

        return nextDays;
    }

    /**
     * A method for getting a medium weather icon.
     * @param id The weather id for current weather.
     * @param hour The hour for which the icon is shown.
     * @return A Label containing the icon.
     */
    private Label setMediumIcon(int id, int hour) {

        Label label = new Label();
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(getIcon(id, hour));
        icon.setIconSize(80);
        icon.setIconColor(Color.NAVY);
        label.setGraphic(icon);

        return label;
    }

    /**
     * A method which returns a VBox containing the next hours'
     * weather info. The VBox will be placed on the right side
     * of a city scene.
     * @param city The current city.
     * @return A VBox containing the next hours' weather info.
     */
    private VBox getRightVBox(CityWeatherStatistics city) {

        VBox rightVBox = new VBox();
        rightVBox.setPrefSize(250,600);
        rightVBox.setSpacing(5);
        rightVBox.setPadding(new Insets(50, 0, 0, 0));

        ArrayList<HourlyWeather> hourly = city.getHouryWeatherList();

        //This for-loop makes a HBox for the data of one hour for
        //all the hours in arraylist.
        for (HourlyWeather hourlyWeather : hourly) {
            HBox child = new HBox();
            child.setAlignment(Pos.BASELINE_LEFT);
            child.setSpacing(8);

            Label hour = new Label(String.valueOf(hourlyWeather.getHour()));
            hour.getStyleClass().add("h4");
            child.getChildren().add(hour);

            child.getChildren().add(setSmallIcon(hourlyWeather.getWeatherId(), Integer.parseInt(hourlyWeather.getHour())));

            HBox hbox = new HBox();
            hbox.setAlignment(Pos.BASELINE_CENTER);
            hbox.setSpacing(2);
            hbox.getChildren().add(new Label(String.valueOf(hourlyWeather.getTemperature())));
            hbox.getChildren().add(getSmallCelsiusIcon());

            child.getChildren().add(hbox);
            child.getChildren().add(new Label("Wind: " + hourlyWeather.getWindSpeed() + " m/s"));

            rightVBox.getChildren().add(child);
            VBox.setVgrow(child, Priority.ALWAYS);

        }
        return rightVBox;
    }

    /**
     * A method for getting a small weather icon.
     * @param id The weather id for current weather.
     * @param hour 1 The hour for which the icon is shown.
     * @return A Label containing the icon.
     */
    private Label setSmallIcon(int id, int hour) {

        Label label = new Label();
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(getIcon(id, hour));
        icon.setIconSize(40);
        icon.setIconColor(Color.NAVY);
        label.setGraphic(icon);

        return label;
    }


    /**
     * A method which returns a button for adding a city to favourites.
     * @return A Button which sets a city to favourite.
     */
    private Button getSetFavouriteButton() {
        Button setFav = new Button("Add to favourites");

        setFav.setOnAction(e -> {
            addToFavourites();
        });

        return setFav;
    }

    /**
     * A method which adds the current city to favourites.
     */
    private void addToFavourites() {
        //Check if the city is already in favourites.
        if (!favourites.contains(currentPlace)) {
            favourites.add(currentPlace);
            menuItems.add(new MenuItem(currentPlace));
            MenuItem favourite = new MenuItem(currentPlace);
            favourite.setOnAction((e) -> {
                showCity(favourite.getText());
                    });
            favouritesMenu.getItems().add(favourite);
        }
    }

    /**
     * A method for picking the right icon code based on weather id and day-/nighttime.
     * The most usual weather icon cases are on top, so the program will be more efficient.
     * @param id A weather id which determines the icon.
     * @param hour The hour which determines the icon.
     * @return Icon code.
     */
    private String getIcon(int id, int hour) {
        String icon = "";

        switch (id) {
            //Check the hour and depending on the value, show icon for daytime or nighttime.
            case 800:
                icon = (hour >= 6 && hour <= 21)  ? "wi-day-sunny" : "wi-night-clear";
                break;

            case 801:
                icon = (hour >= 6 && hour <= 21)  ? "wi-day-sunny-overcast" : "wi-night-partly-cloudy";
                break;

            case 802:
                icon = (hour >= 6 && hour <= 21)  ? "wi-day-cloudy" : "wi-night-cloudy";
                break;

            case 803:
            case 804:
                icon =  "wi-cloudy";
                break;

            case 501:
            case 502:
            case 503:
            case 504:
                icon =  "wi-rain";
                break;

            case 302:
            case 312:
            case 313:
            case 314:
            case 321:
                icon =  "wi-rain-mix";
                break;

            case 520:
            case 521:
            case 522:
            case 531:
                icon =  "wi-showers";
                break;

            case 201:
            case 202:
            case 212:
            case 221:
            case 232:
                icon =  "wi-thunderstorm";
                break;

            case 200:
            case 210:
            case 230:
            case 231:
            case 211:
                icon =  "wi-storm-showers";
                break;

            case 300:
            case 301:
            case 310:
            case 311:
                icon =  "wi-sleet";
                break;

            case 500:
                icon = "wi-sprinkle";
                break;

            case 511:
            case 600:
            case 601:
            case 602:
            case 611:
            case 612:
            case 613:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:
                icon =  "wi-snowflake-cold";
                break;

            case 741:
                icon =  "wi-fog";
                break;

            case 781:
                icon =  "wi-tornado";
                break;

            case 731:
            case 761:
            case 711:
            case 721:
            case 751:
            case 762:
            case 771:
            case 701:
                icon =  "wi-dust";
                break;
        }

        return icon;
    }

}