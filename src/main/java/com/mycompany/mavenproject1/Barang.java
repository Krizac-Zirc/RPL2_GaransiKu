package com.mycompany.mavenproject1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Kelas model Barang.
 * Seluruh atribut bersifat private (Encapsulation) dan hanya dapat
 * diakses melalui method getter dan setter.
 */
public class Barang {

    private int id;
    private String nama;
    private String merek;
    private LocalDate tanggalBeli;
    private int lamaGaransiBulan;
    private String lokasiNota;

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
