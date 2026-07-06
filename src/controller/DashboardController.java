package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import service.DashboardService;

import javafx.collections.ListChangeListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Pelanggan;
import service.DashboardService;
import util.Navigator;

public class DashboardController {
    @FXML
    private Label titleLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label incomeValueLabel;

    @FXML
    private Label customerCountLabel;

    @FXML
    private BarChart<String, Number> weeklyChart;

    @FXML
    private Button pelangganButton;

    @FXML
    private Button laporanButton;

    @FXML
    private Button pengaturanButton;

    @FXML
private Button dashboardButton;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id-ID"));

    @FXML
    private void initialize() {
        Pelanggan.initializeFromDatabase();
        dashboardButton.getStyleClass().add("active");
        titleLabel.setText("Dashboard");
        dateLabel.setText(LocalDate.now().format(formatter));
        Pelanggan.getAllCustomers().addListener((ListChangeListener<Pelanggan>) change -> Platform.runLater(this::refreshDashboard));
        refreshDashboard();
    }

    @FXML
    private void showDashboard() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        Navigator.navigateTo(stage, "Dashboard");
    }

    @FXML
    private void showPelanggan() {
        Stage stage = (Stage) pelangganButton.getScene().getWindow();
        Navigator.navigateTo(stage, "Pelanggan");
    }

    @FXML
    private void showLaporan() {
        Stage stage = (Stage) laporanButton.getScene().getWindow();
        Navigator.navigateTo(stage, "Laporan");
    }

    @FXML
    private void showPengaturan() {
        Stage stage = (Stage) pengaturanButton.getScene().getWindow();
        Navigator.navigateTo(stage, "Pengaturan");
    }

    @FXML
    private void goToLogin() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        Navigator.navigateTo(stage, "Login");
    }

    private void refreshDashboard() {
        if (incomeValueLabel == null || customerCountLabel == null || weeklyChart == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        DashboardService service = new DashboardService();
        long incomeToday = service.getTodayRevenue();
        long customerToday = service.getTodayCustomerCount();

        incomeValueLabel.setText(String.format("Rp%,d", incomeToday));
        customerCountLabel.setText(customerToday + " Pelanggan");
        populateChart(today.with(java.time.DayOfWeek.MONDAY), service);
    }
    private void populateChart(LocalDate weekStart, DashboardService service) {
        weeklyChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pendapatan");

        String[] days = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
        Map<String, Long> totals = service.getWeeklyRevenue();

        for (int index = 0; index < days.length; index++) {
            String key = Pelanggan.formatDate(weekStart.plusDays(index));
            long value = totals.getOrDefault(key, 0L);
            System.out.println(days[index] + " = " + value);
            series.getData().add(new XYChart.Data<>(days[index], value));
        }
        weeklyChart.getData().add(series);
    }
}
