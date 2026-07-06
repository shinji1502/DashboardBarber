package util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navigator {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 620;
    private static final String VIEW_FOLDER = "src/view/";
    private static final String CSS_FILE = "src/css/style.css";

    private Navigator() {
    }

    public static void navigateTo(Stage stage, String pageName) {
        try {
            boolean isMaximized = stage.isMaximized();
            boolean isFullScreen = stage.isFullScreen();
            URL fxmlUrl = Paths.get(VIEW_FOLDER + pageName + ".fxml").toUri().toURL();
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Scene scene;
            if (stage.getScene() == null) {
                scene = new Scene(root, WIDTH, HEIGHT);
            } else {
                scene = new Scene(
                        root,
                        stage.getScene().getWidth(),
                        stage.getScene().getHeight()
                );
            }
            scene.getStylesheets().add(Paths.get(CSS_FILE).toUri().toString());
            stage.setScene(scene);
            stage.setTitle("BarberPro - " + pageName);
            stage.setMaximized(isMaximized);
            stage.setFullScreen(isFullScreen);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load page: " + pageName, e);
        }
    }
}
