package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.Navigator;

public class LoginController {
    @FXML
    private Label titleLabel;

    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        titleLabel.setText("LOGIN");
    }

    @FXML
    private void handleLogin() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Navigator.navigateTo(stage, "Dashboard");
    }
}
