package com.mycompany.mavenproject1.service;

import com.mycompany.mavenproject1.dao.BarangDAO;
import com.mycompany.mavenproject1.model.Barang;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lapisan service untuk data barang: validasi masukan, lalu memanggil DAO.
 * Transaksi diatur Spring secara deklaratif (@Transactional).
 */
public class BarangService {

    private BarangDAO barangDAO;

    // IMPLEMENTASI DI: Setter Injection
    public void setBarangDAO(BarangDAO barangDAO) {
        this.barangDAO = barangDAO;
    }

    // Declarative Transaction: tidak ada beginTransaction/commit/rollback manual
    @Transactional
    public void tambah(Barang barang) {
        validasi(barang);
        barangDAO.tambah(barang);
    }

    // Declarative Transaction: tidak ada beginTransaction/commit/rollback manual
    @Transactional(readOnly = true)
    public List<Barang> daftarMilik(int penggunaId) {
        return barangDAO.daftarMilik(penggunaId);
    }

    // Declarative Transaction: tidak ada beginTransaction/commit/rollback manual
    @Transactional
    public boolean ubah(Barang baru, int penggunaId) {
        validasi(baru);
        return barangDAO.ubah(baru, penggunaId);
    }

    // Declarative Transaction: tidak ada beginTransaction/commit/rollback manual
    @Transactional
    public boolean hapus(int id, int penggunaId) {
        return barangDAO.hapus(id, penggunaId);
    }

    /** Validasi masukan: nama dan merek tidak kosong, lama garansi lebih dari 0. */
    private void validasi(Barang b) {
        if (b.getNama() == null || b.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong.");
        }
        if (b.getMerek() == null || b.getMerek().trim().isEmpty()) {
            throw new IllegalArgumentException("Merek tidak boleh kosong.");
        }
        if (b.getLamaGaransiBulan() <= 0) {
            throw new IllegalArgumentException("Lama garansi harus lebih dari 0 bulan.");
        }
    }
}
