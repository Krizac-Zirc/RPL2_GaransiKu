package com.mycompany.mavenproject1;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Pengelola koneksi MySQL GaransiKu.
 * Pengaturan dibaca dari berkas db.properties (classpath) dan dapat ditimpa
 * oleh properti sistem (-Ddb.url=...). Basis data dan tabel dibuat otomatis.
 */
public final class Database {

    private static final Properties KONFIG = muatKonfigurasi();

    private static final String BUAT_TABEL_PENGGUNA =
            "CREATE TABLE IF NOT EXISTS pengguna ("
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "username VARCHAR(20) NOT NULL UNIQUE, "
            + "nama_lengkap VARCHAR(100) NOT NULL, "
            + "salt VARCHAR(64) NOT NULL, "
            + "password_hash VARCHAR(128) NOT NULL, "
            + "dibuat_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    private Database() { }

    private static Properties muatKonfigurasi() {
        Properties p = new Properties();
        try (InputStream in = Database.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            System.err.println("db.properties gagal dibaca, memakai nilai bawaan: " + e.getMessage());
        }
        return p;
    }

    private static String nilai(String kunci, String bawaan) {
        return System.getProperty(kunci, KONFIG.getProperty(kunci, bawaan));
    }

    private static String namaDb() { return nilai("db.nama", "garansiku"); }
    private static String server() { return nilai("db.server", "jdbc:mysql://localhost:3306/"); }
    private static String opsi() { return nilai("db.opsi", "?useUnicode=true&characterEncoding=UTF-8"); }

    private static Connection buka(String url) throws SQLException {
        return DriverManager.getConnection(url, nilai("db.user", "root"), nilai("db.password", ""));
    }

    /** Koneksi baru ke basis data garansiku; wajib ditutup oleh pemanggil. */
    public static Connection getConnection() throws SQLException {
        return buka(server() + namaDb() + opsi());
    }

    /** Membuat basis data dan tabel yang diperlukan bila belum ada. */
    public static void inisialisasi() throws SQLException {
        try (Connection c = buka(server() + opsi()); Statement st = c.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS `" + namaDb()
                    + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute(BUAT_TABEL_PENGGUNA);
        }
    }
}
