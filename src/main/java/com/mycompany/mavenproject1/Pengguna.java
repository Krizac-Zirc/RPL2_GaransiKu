package com.mycompany.mavenproject1;

/**
 * Model Pengguna (akun yang terdaftar di GaransiKu).
 * Atribut bersifat private dan hanya dapat dibaca melalui getter.
 * Hash kata sandi sengaja tidak disimpan di objek ini.
 */
public class Pengguna {

    private final int id;
    private final String username;
    private final String namaLengkap;

    public Pengguna(int id, String username, String namaLengkap) {
        this.id = id;
        this.username = username;
        this.namaLengkap = namaLengkap;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getNamaLengkap() { return namaLengkap; }
}
