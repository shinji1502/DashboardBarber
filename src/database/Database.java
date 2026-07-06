package database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class Database {
    private static final String DB_NAME = "barberpro.db";

    private Database() {
    }

    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "CREATE TABLE IF NOT EXISTS pelanggan ("
                                 + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                 + "nama TEXT NOT NULL,"
                                 + "telepon TEXT NOT NULL,"
                                 + "jenis_kelamin TEXT NOT NULL,"
                                 + "tanggal_kunjungan TEXT NOT NULL,"
                                 + "layanan TEXT NOT NULL,"
                                 + "harga INTEGER NOT NULL)")) {
                statement.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException exception) {
            throw new RuntimeException("Gagal menginisialisasi database SQLite", exception);
        }
    }

    public static Connection getConnection() throws SQLException {
        Path databasePath = Paths.get(DB_NAME).toAbsolutePath();
        String url = "jdbc:sqlite:" + databasePath;
        return DriverManager.getConnection(url);
    }
}
