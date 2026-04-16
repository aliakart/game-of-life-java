package pl.lodz.p;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.adapter.JavaBeanBooleanProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class GameOfLifeApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(GameOfLifeApp.class);
    private Stage stage; //  referencja do okna
    private ResourceBundle bundle;
    private Locale locale = new Locale("pl","PL");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        loadBundle();
        logger.info(bundle.getString("app.start"));
        primaryStage.setTitle(bundle.getString("title"));
        showConfig();
    }

    private void loadBundle() {
        bundle = ResourceBundle.getBundle("pl.lodz.p.messages", locale);
        stage.setTitle(bundle.getString("title"));
        try {
            ResourceBundle authors = ResourceBundle.getBundle("pl.lodz.p.Authors", locale);
            if (authors.containsKey("Authors")) {
                String[] list = (String[]) authors.getObject("Authors");
                logger.info("{} {}", bundle.getString("log.authors"), String.join(", ", list));
            }
        } catch (MissingResourceException e) {
            logger.warn(bundle.getString("error.authors"), e);
        }
    }

    // scena 1
    private void showConfig() {


        ComboBox<Enum> densityBox = new ComboBox<>();
        densityBox.getItems().setAll(Enum.values()); // wartości z Enuma
        densityBox.setValue(Enum.LOW);

        ComboBox<String> langBox = new ComboBox<>();
        langBox.getItems().addAll("PL", "EN");
        langBox.setValue(locale.getLanguage().toUpperCase());
        langBox.setOnAction(e -> {
            locale = new Locale(langBox.getValue().toLowerCase());
            loadBundle();
            showConfig();
        });

        Spinner<Integer> sizeSpinner = new Spinner<>(4, 20, 10);

        // przycisk startu
        Button btnStart = new Button(bundle.getString("start"));
        btnStart.setOnAction(e -> {
            int size = sizeSpinner.getValue();
            if (size < 4) {
                size = 4;
            }
            GameOfLifeBoard board = new GameOfLifeBoard(size, size, densityBox.getValue().getProbability());
            showGame(board);
        });

        Button btnLoad = new Button(bundle.getString("load"));
        btnLoad.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                logger.info("Attempting to load file: {}", file.getAbsolutePath());
                try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getFileDao(file.getAbsolutePath())) {
                    showGame(dao.read());
                } catch (GameOfLifeException ex) {
                    handleException(ex);
                } catch (Exception ex) {
                    logger.error("Unexpected error", ex);
                }
            }
        });

        Button btnLoadDb = new Button("Load DB");
        btnLoadDb.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle(bundle.getString("title"));

            dialog.setHeaderText(bundle.getString("db.select"));
            dialog.setContentText(bundle.getString("db.name"));

            dialog.showAndWait().ifPresent(name -> {
                if (name.trim().isEmpty()) {
                    return;
                }
                try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getJdbcDao(name)) {
                    showGame(dao.read());
                } catch (GameOfLifeException ex) {
                    handleException(ex);
                } catch (Exception ex) {
                    logger.error("Critical DB Error", ex);
                }
            });
        });

        Label sizeLabel = new Label(bundle.getString("size"));

        VBox root = new VBox(10,sizeLabel,sizeSpinner, new Label(bundle.getString("lang")),
                             langBox, btnStart, btnLoad,btnLoadDb);
        root.setAlignment(Pos.CENTER); // wyśrodkowanie
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }

    // scena 2
    private void showGame(GameOfLifeBoard board) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(1);
        grid.setVgap(1);

        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                Rectangle rect = new Rectangle(25, 25); // rozmiar komórki
                GameOfLifeCell cell = board.getCell(i, j);
                try {
                    JavaBeanBooleanProperty cellAdapter = JavaBeanBooleanPropertyBuilder.create()
                            .bean(cell)
                            .name("cellValue")
                            .build();

                    rect.fillProperty().bind(Bindings.when(cellAdapter)
                                    .then(Color.GREEN)
                                    .otherwise(Color.BLACK));

                    rect.setOnMouseClicked(e -> cellAdapter.set(!cellAdapter.get()));

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                grid.add(rect, j, i);
            }
        }

        Button btnBack = new Button(bundle.getString("back"));
        btnBack.setOnAction(e -> showConfig());


        VBox root = new VBox(15, btnBack, grid);
        root.setAlignment(Pos.CENTER);

        Button btnSave = new Button(bundle.getString("save"));
        btnSave.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getFileDao(file.getAbsolutePath())) {
                    dao.write(board);
                } catch (GameOfLifeException ex) {
                    handleException(ex);
                } catch (Exception ex) {
                    logger.error("Unexpected error during save", ex);
                }
            }
        });

        Button btnSaveDb = new Button("Save DB");
        btnSaveDb.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("plansza_1");
            dialog.setTitle(bundle.getString("title"));
            dialog.setHeaderText("Zapisz do bazy danych");

            dialog.setContentText(bundle.getString("db.name"));

            dialog.showAndWait().ifPresent(name -> {
                if (name.trim().isEmpty()) {
                    return;
                }
                try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getJdbcDao(name)) {
                    dao.write(board);
                    new Alert(Alert.AlertType.INFORMATION, "Zapisano!").show();
                } catch (GameOfLifeException ex) {
                    handleException(ex);
                } catch (Exception ex) {
                    logger.error("Critical DB Save Error", ex);
                }
            });
        });



        HBox controls = new HBox(10, btnBack, btnSave,btnSaveDb);
        controls.setAlignment(Pos.CENTER);
        VBox gameRoot = new VBox(15, controls, grid);
        gameRoot.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(gameRoot, 600, 600));
    }

    private void handleException(GameOfLifeException ex) {

        logger.error("Application exception occurred: key={}", ex.getMessageKey(), ex);

        String translatedMessage;
        try {
            translatedMessage = bundle.getString(ex.getMessageKey());
        } catch (MissingResourceException e) {
            translatedMessage = ex.getMessageKey();
        }

        new Alert(Alert.AlertType.ERROR, translatedMessage).show();
    }

    @Override
    public void stop() {
        logger.info(bundle.getString("app.finish"));
    }
}