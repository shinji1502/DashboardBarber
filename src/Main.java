import java.net.URL;
import java.nio.file.Paths;

import database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 620;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Database.initialize();
        URL fxmlUrl = Paths.get("src/view/Login.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(Paths.get("src/css/style.css").toUri().toString());

        primaryStage.setTitle("BarberPro");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
