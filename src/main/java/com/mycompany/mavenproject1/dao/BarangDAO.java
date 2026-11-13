package com.mycompany.mavenproject1.dao;

import com.mycompany.mavenproject1.model.Barang;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Akses data tabel barang melalui Hibernate (ORM). Tidak ada perintah SQL
 * pada kelas ini; SQL dibentuk Hibernate dari anotasi pada kelas Barang.
 * Transaksi tidak diatur di sini, melainkan oleh Spring lewat @Transactional di BarangService.
 */
public class BarangDAO {

    private SessionFactory sessionFactory;

    // IMPLEMENTASI DI: Setter Injection (SessionFactory disuntikkan Spring)
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Session dikelola Spring (transparent session management)
    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /** Create: menyimpan barang baru (INSERT). */
    public void tambah(Barang barang) {
        getSession().persist(barang);
    }

    /** Read: seluruh barang milik satu pengguna, diurutkan dari yang terbaru dimasukkan. */
    public List<Barang> daftarMilik(int penggunaId) {
        return getSession().createQuery(
                "FROM Barang b WHERE b.penggunaId = :pid ORDER BY b.id DESC", Barang.class)
                .setParameter("pid", penggunaId)
                .list();
    }

    /**
     * Update: mengubah atribut objek hasil get(); saat transaksi selesai
     * Hibernate otomatis membuat UPDATE. Mengembalikan false bila data tidak ditemukan.
     */
    public boolean ubah(Barang baru, int penggunaId) {
        Barang lama = getSession().get(Barang.class, baru.getId());
        if (lama == null || lama.getPenggunaId() != penggunaId) {
            return false;
        }
        lama.setNama(baru.getNama());
        lama.setMerek(baru.getMerek());
        lama.setTanggalBeli(baru.getTanggalBeli());
        lama.setLamaGaransiBulan(baru.getLamaGaransiBulan());
        lama.setLokasiNota(baru.getLokasiNota());
        return true;
    }

    /** Delete: menghapus barang (DELETE). Mengembalikan false bila data tidak ditemukan. */
    public boolean hapus(int id, int penggunaId) {
        Barang barang = getSession().get(Barang.class, id);
        if (barang == null || barang.getPenggunaId() != penggunaId) {
            return false;
        }
        getSession().remove(barang);
        return true;
    }
}
