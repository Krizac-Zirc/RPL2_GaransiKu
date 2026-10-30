package com.mycompany.mavenproject1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Akses data tabel pengguna. Seluruh query memakai PreparedStatement
 * sehingga masukan pengguna tidak dapat disisipi perintah SQL.
 */
public class PenggunaDAO {

    public boolean usernameTerpakai(String username) throws SQLException {
        String sql = "SELECT 1 FROM pengguna WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Menyimpan akun baru dan mengembalikan objek Pengguna-nya. */
    public Pengguna registrasi(String username, String namaLengkap, String password)
            throws SQLException {
        String user = username.toLowerCase();
        String nama = namaLengkap.trim();
        String salt = PasswordUtil.buatSalt();
        String sql = "INSERT INTO pengguna (username, nama_lengkap, salt, password_hash) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user);
            ps.setString(2, nama);
            ps.setString(3, salt);
            ps.setString(4, PasswordUtil.hash(password, salt));
            ps.executeUpdate();
            try (ResultSet kunci = ps.getGeneratedKeys()) {
                return new Pengguna(kunci.next() ? kunci.getInt(1) : 0, user, nama);
            }
        }
    }

    /** Mengembalikan Pengguna bila username dan kata sandi cocok, selain itu null. */
    public Pengguna login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, nama_lengkap, salt, password_hash "
                   + "FROM pengguna WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.cocok(password, rs.getString("salt"),
                        rs.getString("password_hash"))) {
                    return new Pengguna(rs.getInt("id"), rs.getString("username"),
                            rs.getString("nama_lengkap"));
                }
                return null;
            }
        }
    }
}
