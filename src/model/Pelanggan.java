package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import database.Database;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Pelanggan {
    private final SimpleStringProperty id;
    private final SimpleStringProperty nama;
    private final SimpleStringProperty nomorTelepon;
    private final SimpleStringProperty jenisKelamin;
    private final SimpleStringProperty tanggalKunjungan;
    private final SimpleStringProperty layanan;
    private final SimpleIntegerProperty harga;

    private static final ObservableList<Pelanggan> CUSTOMERS = FXCollections.observableArrayList();
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id-ID"));

    public Pelanggan() {
        this("", "", "", "Laki-laki", "", "", 0);
    }

    public Pelanggan(String id, String nama, String nomorTelepon, String jenisKelamin,
                     String tanggalKunjungan, String layanan, int harga) {
        this.id = new SimpleStringProperty(id);
        this.nama = new SimpleStringProperty(nama);
        this.nomorTelepon = new SimpleStringProperty(nomorTelepon);
        this.jenisKelamin = new SimpleStringProperty(jenisKelamin);
        this.tanggalKunjungan = new SimpleStringProperty(tanggalKunjungan);
        this.layanan = new SimpleStringProperty(layanan);
        this.harga = new SimpleIntegerProperty(harga);
    }

    public static ObservableList<Pelanggan> getAllCustomers() {
        return CUSTOMERS;
    }

    public static void initializeFromDatabase() {
        Database.initialize();
        CUSTOMERS.clear();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, nama, telepon, jenis_kelamin, tanggal_kunjungan, layanan, harga FROM pelanggan ORDER BY id ASC");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                CUSTOMERS.add(new Pelanggan(
                        String.valueOf(resultSet.getInt("id")),
                        resultSet.getString("nama"),
                        resultSet.getString("telepon"),
                        resultSet.getString("jenis_kelamin"),
                        resultSet.getString("tanggal_kunjungan"),
                        resultSet.getString("layanan"),
                        resultSet.getInt("harga")));
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memuat pelanggan dari database", exception);
        }
    }

    public static void addCustomer(Pelanggan pelanggan) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO pelanggan (nama, telepon, jenis_kelamin, tanggal_kunjungan, layanan, harga) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, pelanggan.getNama());
            statement.setString(2, pelanggan.getNomorTelepon());
            statement.setString(3, pelanggan.getJenisKelamin());
            statement.setString(4, pelanggan.getTanggalKunjungan());
            statement.setString(5, pelanggan.getLayanan());
            statement.setInt(6, pelanggan.getHarga());
            statement.executeUpdate();
            initializeFromDatabase();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan pelanggan", exception);
        }
    }

    public static void updateCustomer(Pelanggan pelanggan) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE pelanggan SET nama = ?, telepon = ?, jenis_kelamin = ?, tanggal_kunjungan = ?, layanan = ?, harga = ? WHERE id = ?")) {
            statement.setString(1, pelanggan.getNama());
            statement.setString(2, pelanggan.getNomorTelepon());
            statement.setString(3, pelanggan.getJenisKelamin());
            statement.setString(4, pelanggan.getTanggalKunjungan());
            statement.setString(5, pelanggan.getLayanan());
            statement.setInt(6, pelanggan.getHarga());
            statement.setInt(7, Integer.parseInt(pelanggan.getId()));
            statement.executeUpdate();
            initializeFromDatabase();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui pelanggan", exception);
        }
    }

    public static void removeCustomer(Pelanggan pelanggan) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM pelanggan WHERE id = ?")) {
            statement.setInt(1, Integer.parseInt(pelanggan.getId()));
            statement.executeUpdate();
            initializeFromDatabase();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus pelanggan", exception);
        }
    }

    public static String generateNextId() {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) AS max_id FROM pelanggan");
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int maxId = resultSet.getInt("max_id");
                return String.valueOf(maxId + 1);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghasilkan ID pelanggan", exception);
        }
        return "1";
    }

    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DISPLAY_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getNama() {
        return nama.get();
    }

    public void setNama(String nama) {
        this.nama.set(nama);
    }

    public SimpleStringProperty namaProperty() {
        return nama;
    }

    public String getNomorTelepon() {
        return nomorTelepon.get();
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon.set(nomorTelepon);
    }

    public SimpleStringProperty nomorTeleponProperty() {
        return nomorTelepon;
    }

    public String getJenisKelamin() {
        return jenisKelamin.get();
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin.set(jenisKelamin);
    }

    public SimpleStringProperty jenisKelaminProperty() {
        return jenisKelamin;
    }

    public String getTanggalKunjungan() {
        return tanggalKunjungan.get();
    }

    public void setTanggalKunjungan(String tanggalKunjungan) {
        this.tanggalKunjungan.set(tanggalKunjungan);
    }

    public SimpleStringProperty tanggalKunjunganProperty() {
        return tanggalKunjungan;
    }

    public String getLayanan() {
        return layanan.get();
    }

    public void setLayanan(String layanan) {
        this.layanan.set(layanan);
    }

    public SimpleStringProperty layananProperty() {
        return layanan;
    }

    public int getHarga() {
        return harga.get();
    }

    public void setHarga(int harga) {
        this.harga.set(harga);
    }

    public SimpleIntegerProperty hargaProperty() {
        return harga;
    }
}
