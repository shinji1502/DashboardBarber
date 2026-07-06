package controller;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Pelanggan;
import util.Navigator;

public class PelangganController {
    @FXML
    private Label titleLabel;

    @FXML
    private Button addButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Pelanggan> customerTable;

    @FXML
    private TableColumn<Pelanggan, String> idColumn;

    @FXML
    private TableColumn<Pelanggan, String> namaColumn;

    @FXML
    private TableColumn<Pelanggan, String> teleponColumn;

    @FXML
    private TableColumn<Pelanggan, String> tanggalColumn;

    @FXML
    private TableColumn<Pelanggan, String> layananColumn;

    @FXML
    private TableColumn<Pelanggan, Number> hargaColumn;

    @FXML
    private TableColumn<Pelanggan, Void> aksiColumn;

    @FXML
    private Button pelangganNavButton;

    private final ObservableList<Pelanggan> masterData = Pelanggan.getAllCustomers();
    private final FilteredList<Pelanggan> filteredData = new FilteredList<>(masterData, pelanggan -> true);
    private final SortedList<Pelanggan> sortedData = new SortedList<>(filteredData);
    private Pelanggan editingCustomer;

    @FXML
    private void initialize() {
        Pelanggan.initializeFromDatabase();
        pelangganNavButton.getStyleClass().add("active");
        titleLabel.setText("Data Pelanggan");
        searchField.setPromptText("Cari pelanggan");

        setupTable();
        configureTable();   // Tambahkan ini
        setupSearch();

        customerTable.setItems(sortedData);
        sortedData.comparatorProperty().bind(customerTable.comparatorProperty());
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

    @FXML
    private void showAddDialog() {
        showCustomerDialog(null);
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        teleponColumn.setCellValueFactory(new PropertyValueFactory<>("nomorTelepon"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalKunjungan"));
        layananColumn.setCellValueFactory(new PropertyValueFactory<>("layanan"));
        hargaColumn.setCellValueFactory(new PropertyValueFactory<>("harga"));

        aksiColumn.setCellFactory(column -> new TableCell<Pelanggan, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final HBox box = new HBox(6, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Pelanggan pelanggan = getCurrentCustomer();
                    if (pelanggan != null) {
                        showCustomerDialog(pelanggan);
                    }
                });
                deleteButton.setOnAction(event -> {
                    Pelanggan pelanggan = getCurrentCustomer();
                    if (pelanggan != null) {
                        deleteCustomer(pelanggan);
                    }
                });
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }

            private Pelanggan getCurrentCustomer() {
                TableRow<Pelanggan> row = getTableRow();
                if (row == null) {
                    return null;
                }
                return row.getItem();
            }
        });
    }
    private void configureTable() {
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customerTable.setPlaceholder(new Label("Belum ada data pelanggan"));
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(pelanggan -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String filter = newValue.toLowerCase();
                return pelanggan.getNama().toLowerCase().contains(filter)
                        || pelanggan.getNomorTelepon().contains(filter);
            });
        });
    }

    private void showCustomerDialog(Pelanggan customer) {
        editingCustomer = customer;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(customer == null ? "Tambah Pelanggan" : "Edit Pelanggan");
        dialog.initOwner(addButton.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Laki-laki", "Perempuan"));
        DatePicker datePicker = new DatePicker();
        ComboBox<String> serviceBox = new ComboBox<>(FXCollections.observableArrayList(
                "Potong Rambut", "Potong + Cuci", "Hair Spa", "Pewarnaan", "Cukur Jenggot"));
        TextField priceField = new TextField();
        priceField.setEditable(false);

        if (customer != null) {
            nameField.setText(customer.getNama());
            phoneField.setText(customer.getNomorTelepon());
            genderBox.setValue(customer.getJenisKelamin());
            if (Pelanggan.parseDate(customer.getTanggalKunjungan()) != null) {
                datePicker.setValue(Pelanggan.parseDate(customer.getTanggalKunjungan()));
            }
            serviceBox.setValue(customer.getLayanan());
            priceField.setText(String.valueOf(customer.getHarga()));
        } else {
            genderBox.setValue("Laki-laki");
            serviceBox.setValue("Potong Rambut");
            priceField.setText(String.valueOf(getServicePrice("Potong Rambut")));
        }

        serviceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            priceField.setText(String.valueOf(getServicePrice(newValue)));
        });

        grid.add(new Label("Nama"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Nomor Telepon"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Jenis Kelamin"), 0, 2);
        grid.add(genderBox, 1, 2);
        grid.add(new Label("Tanggal"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Layanan"), 0, 4);
        grid.add(serviceBox, 1, 4);
        grid.add(new Label("Total Harga"), 0, 5);
        grid.add(priceField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String service = serviceBox.getValue();
            if (name.isEmpty()) {
                showAlert("Nama tidak boleh kosong.");
                event.consume();
                return;
            }
            if (!phone.matches("\\d+")) {
                showAlert("Nomor telepon harus angka.");
                event.consume();
                return;
            }
            if (datePicker.getValue() == null) {
                showAlert("Tanggal wajib diisi.");
                event.consume();
                return;
            }
            if (service == null || service.trim().isEmpty()) {
                showAlert("Layanan wajib dipilih.");
                event.consume();
                return;
            }

            int price = getServicePrice(service);
            String dateText = Pelanggan.formatDate(datePicker.getValue());
            if (editingCustomer == null) {
                Pelanggan newCustomer = new Pelanggan("", name, phone,
                        genderBox.getValue(), dateText, service, price);
                Pelanggan.addCustomer(newCustomer);
            } else {
                editingCustomer.setNama(name);
                editingCustomer.setNomorTelepon(phone);
                editingCustomer.setJenisKelamin(genderBox.getValue());
                editingCustomer.setTanggalKunjungan(dateText);
                editingCustomer.setLayanan(service);
                editingCustomer.setHarga(price);
                Pelanggan.updateCustomer(editingCustomer);
            }
            // ensure model re-initializes and controllers refresh on FX thread
            javafx.application.Platform.runLater(() -> {
                applySearchFilter();
                customerTable.refresh();
            });
        });

        dialog.showAndWait();
    }

    private void deleteCustomer(Pelanggan pelanggan) {
        if (pelanggan == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Apakah Anda yakin ingin menghapus pelanggan ini?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Pelanggan.removeCustomer(pelanggan);
                // ensure services/controllers refresh via list listener; also refresh table view
                applySearchFilter();
                customerTable.refresh();
                customerTable.getSelectionModel().clearSelection();
            }
        });
    }

    private void applySearchFilter() {
        if (searchField == null) {
            return;
        }
        filteredData.setPredicate(pelangganItem -> {
            String currentFilter = searchField.getText();
            if (currentFilter == null || currentFilter.trim().isEmpty()) {
                return true;
            }
            String filter = currentFilter.toLowerCase();
            return pelangganItem.getNama().toLowerCase().contains(filter)
                    || pelangganItem.getNomorTelepon().contains(filter);
        });
    }

    private int getServicePrice(String service) {
        Map<String, Integer> prices = new HashMap<>();
        prices.put("Potong Rambut", 50000);
        prices.put("Potong + Cuci", 75000);
        prices.put("Hair Spa", 120000);
        prices.put("Pewarnaan", 150000);
        prices.put("Cukur Jenggot", 35000);
        return prices.getOrDefault(service, 0);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validasi");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
