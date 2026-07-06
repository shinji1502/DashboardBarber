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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Pelanggan;
import service.DashboardService;
import util.Navigator;

public class LaporanController {
    @FXML
    private Label titleLabel;

    @FXML
    private Label dailyIncomeLabel;

    @FXML
    private Label weeklyIncomeLabel;

    @FXML
    private Label monthlyIncomeLabel;

    @FXML
    private LineChart<String, Number> reportChart;

    @FXML
    private Button laporanNavButton;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id-ID"));

    @FXML
    private void initialize() {
        Pelanggan.initializeFromDatabase();
        laporanNavButton.getStyleClass().add("active");
        titleLabel.setText("Laporan");
        Pelanggan.getAllCustomers().addListener((ListChangeListener<Pelanggan>) change -> Platform.runLater(this::refreshReport));
        refreshReport();
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

    private void refreshReport() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        DashboardService service = new DashboardService();
        long dailyIncome = service.getRevenueForDate(today);
        long weeklyIncome = service.getWeeklySummary();
        long monthlyIncome = service.getMonthlySummary();

        dailyIncomeLabel.setText(String.format("Rp%,d", dailyIncome));
        weeklyIncomeLabel.setText(String.format("Rp%,d", weeklyIncome));
        monthlyIncomeLabel.setText(String.format("Rp%,d", monthlyIncome));
        populateChart(weekStart, service);
    }
    private void populateChart(LocalDate weekStart, DashboardService service) {
        reportChart.getData().clear();
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
        reportChart.getData().add(series);
    }
}
