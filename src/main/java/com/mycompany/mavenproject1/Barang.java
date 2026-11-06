package com.mycompany.mavenproject1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Kelas model Barang sekaligus entity Hibernate (tabel "barang").
 * Seluruh atribut bersifat private (Encapsulation) dan hanya dapat
 * diakses melalui method getter dan setter. Anotasi dipasang pada atribut
 * sehingga Hibernate membaca atribut langsung; method turunan seperti
 * getStatus() tidak ikut dipetakan menjadi kolom.
 */
@Entity
@Table(name = "barang")
public class Barang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nama", nullable = false)
    private String nama;

    @Column(name = "merek")
    private String merek;

    @Column(name = "tanggal_beli", nullable = false)
    private LocalDate tanggalBeli;

    @Column(name = "lama_garansi_bulan", nullable = false)
    private int lamaGaransiBulan;

    @Column(name = "lokasi_nota")
    private String lokasiNota;

    /** Id pengguna pemilik barang (kolom biasa, tanpa relasi ke entity lain). */
    @Column(name = "pengguna_id", nullable = false)
    private int penggunaId;

    /** Konstruktor kosong wajib ada agar Hibernate dapat membuat objek dari baris tabel. */
    public Barang() { }

    public Barang(int id, String nama, String merek, LocalDate tanggalBeli,
                  int lamaGaransiBulan, String lokasiNota) {
        this.id = id;
        setNama(nama);
        setMerek(merek);
        setTanggalBeli(tanggalBeli);
        setLamaGaransiBulan(lamaGaransiBulan);
        setLokasiNota(lokasiNota);
    }

    // ---------- Getter ----------
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getMerek() { return merek; }
    public LocalDate getTanggalBeli() { return tanggalBeli; }
    public int getLamaGaransiBulan() { return lamaGaransiBulan; }
    public String getLokasiNota() { return lokasiNota; }
    public int getPenggunaId() { return penggunaId; }

    // ---------- Setter (dengan validasi) ----------
    public void setNama(String nama) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public void setMerek(String merek) {
        this.merek = (merek == null || merek.isBlank()) ? "-" : merek.trim();
    }

    public void setTanggalBeli(LocalDate tanggalBeli) {
        if (tanggalBeli == null) {
            throw new IllegalArgumentException("Tanggal beli tidak boleh kosong.");
        }
        this.tanggalBeli = tanggalBeli;
    }

    public void setLamaGaransiBulan(int lamaGaransiBulan) {
        if (lamaGaransiBulan <= 0) {
            throw new IllegalArgumentException("Lama garansi harus lebih dari 0 bulan.");
        }
        this.lamaGaransiBulan = lamaGaransiBulan;
    }

    public void setLokasiNota(String lokasiNota) {
        this.lokasiNota = (lokasiNota == null || lokasiNota.isBlank()) ? "-" : lokasiNota.trim();
    }

    public void setPenggunaId(int penggunaId) { this.penggunaId = penggunaId; }

    // ---------- Method turunan ----------
    public LocalDate getTanggalBerakhir() {
        return tanggalBeli.plusMonths(lamaGaransiBulan);
    }

    public long getSisaHari() {
        return ChronoUnit.DAYS.between(LocalDate.now(), getTanggalBerakhir());
    }

    public String getStatus() {
        long sisa = getSisaHari();
        if (sisa < 0) {
            return "Kedaluwarsa";
        } else if (sisa <= 30) {
            return "Hampir Berakhir";
        }
        return "Aktif";
    }
}
