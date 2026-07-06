package service;

import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardService {

    public long getTodayRevenue() {
        long value = getRevenueForDate(LocalDate.now());
        System.out.println("[DEBUG][DashboardService] getTodayRevenue -> " + value);
        return value;
    }

    public long getTodayCustomerCount() {
        String sql = "SELECT COUNT(*) AS cnt FROM pelanggan WHERE tanggal_kunjungan = ?";
        String target = formatDate(LocalDate.now());
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, target);
            try (ResultSet rs = ps.executeQuery()) {
                long cnt = rs.getLong("cnt");
                System.out.println("[DEBUG][DashboardService] getTodayCustomerCount date=" + target + " count=" + cnt);
                return cnt;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Gagal mengambil jumlah pelanggan hari ini", ex);
        }
    }

    public long getRevenueForDate(LocalDate date) {
        String sql = "SELECT SUM(harga) AS total FROM pelanggan WHERE tanggal_kunjungan = ?";
        String target = formatDate(date);
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, target);
            try (ResultSet rs = ps.executeQuery()) {
                long total = rs.getLong("total");
                System.out.println("[DEBUG][DashboardService] getRevenueForDate date=" + target + " total=" + total);
                return total;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Gagal mengambil pendapatan harian", ex);
        }
    }

    public Map<String, Long> getWeeklyRevenue() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        String[] dates = new String[7];
        for (int i = 0; i < 7; i++) dates[i] = formatDate(weekStart.plusDays(i));

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < dates.length; i++) {
            if (i > 0) placeholders.append(',');
            placeholders.append('?');
        }
        String sql = "SELECT tanggal_kunjungan, SUM(harga) AS total FROM pelanggan WHERE tanggal_kunjungan IN (" + placeholders + ") GROUP BY tanggal_kunjungan";

        Map<String, Long> totals = new LinkedHashMap<>();
        for (String d : dates) totals.put(d, 0L);

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < dates.length; i++) ps.setString(i + 1, dates[i]);
            int rows = 0;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totals.put(rs.getString("tanggal_kunjungan"), rs.getLong("total"));
                    rows++;
                }
            }
            System.out.println("[DEBUG][DashboardService] getWeeklyRevenue weekStart=" + formatDate(weekStart));
            System.out.println("[DEBUG][DashboardService] Dates used for query:");
            for (String d : dates) System.out.println("  " + d);
            System.out.println("[DEBUG][DashboardService] Rows returned by grouped query: " + rows);
            System.out.println("[DEBUG][DashboardService] Weekly totals:");
            for (Map.Entry<String, Long> e : totals.entrySet()) System.out.println("  " + e.getKey() + " -> " + e.getValue());
        } catch (SQLException ex) {
            throw new RuntimeException("Gagal mengambil pendapatan mingguan", ex);
        }
        return totals;
    }

    public long getWeeklySummary() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        return getRevenueBetween(weekStart, weekStart.plusDays(6));
    }

    public long getMonthlySummary() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        return getRevenueBetween(monthStart, monthEnd);
    }

    public long getRevenueBetween(LocalDate startDate, LocalDate endDate) {
        java.util.List<String> dates = new java.util.ArrayList<>();
        LocalDate cur = startDate;
        while (!cur.isAfter(endDate)) {
            dates.add(formatDate(cur));
            cur = cur.plusDays(1);
        }
        if (dates.isEmpty()) return 0L;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < dates.size(); i++) {
            if (i > 0) placeholders.append(',');
            placeholders.append('?');
        }
        String sql = "SELECT SUM(harga) AS total FROM pelanggan WHERE tanggal_kunjungan IN (" + placeholders + ")";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < dates.size(); i++) ps.setString(i + 1, dates.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                long total = rs.getLong("total");
                System.out.println("[DEBUG][DashboardService] getRevenueBetween start=" + formatDate(startDate) + " end=" + formatDate(endDate) + " daysUsed=" + dates.size() + " total=" + total);
                return total;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Gagal mengambil pendapatan rentang tanggal", ex);
        }
    }

    public Map<String, Long> getDailyRevenueForRange(LocalDate startDate, int days) {
        Map<String, Long> map = new LinkedHashMap<>();
        LocalDate cur = startDate;
        for (int i = 0; i < days; i++) {
            map.put(formatDate(cur.plusDays(i)), getRevenueForDate(cur.plusDays(i)));
        }
        return map;
    }

    public void refreshDashboard() {
        // reload customer list
        model.Pelanggan.initializeFromDatabase();
    }

    private String formatDate(LocalDate date) {
        return model.Pelanggan.formatDate(date);
    }
}
