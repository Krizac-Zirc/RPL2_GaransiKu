package com.mycompany.mavenproject1;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Pembuat SessionFactory Hibernate. SessionFactory dibuat satu kali
 * (saat pertama dipakai) karena pembuatannya mahal, lalu dipakai bersama.
 * Pengaturan koneksi dibaca dari hibernate.cfg.xml.
 */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() { }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        return sessionFactory;
    }

    /** Menutup SessionFactory saat aplikasi berhenti. */
    public static synchronized void tutup() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
