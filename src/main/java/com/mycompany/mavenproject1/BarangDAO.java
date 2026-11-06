package com.mycompany.mavenproject1;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Akses data tabel barang melalui Hibernate (ORM). Tidak ada perintah SQL
 * pada kelas ini; SQL dibentuk Hibernate dari anotasi pada kelas Barang.
 */
public class BarangDAO {

    /** Create: menyimpan barang baru (INSERT). */
    public void tambah(Barang barang) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(barang);
            tx.commit();
        } catch (RuntimeException e) {
            batal(tx);
            throw e;
        }
    }

    /** Read: seluruh barang milik satu pengguna, diurutkan dari yang terbaru dimasukkan. */
    public List<Barang> daftarMilik(int penggunaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Barang b WHERE b.penggunaId = :pid ORDER BY b.id DESC", Barang.class)
                    .setParameter("pid", penggunaId)
                    .list();
        }
    }

    /**
     * Update: mengubah atribut objek hasil get(), lalu commit() sehingga
     * Hibernate otomatis membuat UPDATE. Mengembalikan false bila data tidak ditemukan.
     */
    public boolean ubah(Barang baru, int penggunaId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Barang lama = session.get(Barang.class, baru.getId());
            if (lama == null || lama.getPenggunaId() != penggunaId) {
                tx.rollback();
                return false;
            }
            lama.setNama(baru.getNama());
            lama.setMerek(baru.getMerek());
            lama.setTanggalBeli(baru.getTanggalBeli());
            lama.setLamaGaransiBulan(baru.getLamaGaransiBulan());
            lama.setLokasiNota(baru.getLokasiNota());
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            batal(tx);
            throw e;
        }
    }

    /** Delete: menghapus barang (DELETE). Mengembalikan false bila data tidak ditemukan. */
    public boolean hapus(int id, int penggunaId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Barang barang = session.get(Barang.class, id);
            if (barang == null || barang.getPenggunaId() != penggunaId) {
                tx.rollback();
                return false;
            }
            session.remove(barang);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            batal(tx);
            throw e;
        }
    }

    private static void batal(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
