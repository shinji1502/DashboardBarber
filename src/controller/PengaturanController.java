package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.Navigator;

public class PengaturanController {
    @FXML
    private Label titleLabel;

    @FXML
    private Button pengaturanNavButton;

    @FXML
    private void initialize() {
        titleLabel.setText("Pengaturan");
        pengaturanNavButton.getStyleClass().add("active");
    }

    @FXML
    private void showDashboard() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        Navigator.navigateTo(stage, "Dashboard");
    }

    @FXML
    private void showPelanggan() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        Navigator.navigateTo(stage, "Pelanggan");
    }

    @FXML
    private void showLaporan() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        Navigator.navigateTo(stage, "Laporan");
    }

    @FXML
    private void showPengaturan() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        Navigator.navigateTo(stage, "Pengaturan");
    }

    @FXML
    private void goToLogin() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        Navigator.navigateTo(stage, "Login");
    }
}
