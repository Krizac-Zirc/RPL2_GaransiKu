package com.mycompany.mavenproject1;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Logika registrasi dan login yang dipakai oleh tampilan (GUI).
 */
public class AuthService {

    private final PenggunaDAO dao = new PenggunaDAO();

    /**
     * Mendaftarkan akun baru.
     * @return null bila berhasil, atau pesan kesalahan bila gagal
     */
    public String registrasi(String username, String nama, String sandi, String ulang) {
        username = username.trim();
        if (!username.matches("[A-Za-z0-9_]{4,20}")) {
            return "Username harus 4-20 karakter: huruf, angka, atau garis bawah.";
        }
        if (nama.trim().isEmpty()) {
            return "Nama lengkap tidak boleh kosong.";
        }
        if (sandi.length() < 6) {
            return "Kata sandi minimal 6 karakter.";
        }
        if (!sandi.equals(ulang)) {
            return "Kata sandi dan pengulangannya tidak sama.";
        }
        try {
            dao.registrasi(username, nama, sandi);
            return null;
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Username sudah dipakai.";
        } catch (SQLException e) {
            return "Kesalahan database: " + e.getMessage();
        }
    }

    /** @return objek Pengguna bila cocok, atau null bila username/kata sandi salah */
    public Pengguna login(String username, String sandi) throws SQLException {
        return dao.login(username.trim(), sandi);
    }
}
