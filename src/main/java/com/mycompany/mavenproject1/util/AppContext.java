package com.mycompany.mavenproject1.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Pemegang container Spring (IoC). Context dibuat satu kali saat pertama
 * dipakai (lazy singleton), yaitu setelah Database.inisialisasi() selesai
 * sehingga database sudah ada ketika Hibernate terhubung.
 */
public final class AppContext {

    private static ClassPathXmlApplicationContext context;

    private AppContext() { }

    public static synchronized <T> T getBean(String nama, Class<T> tipe) {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        return context.getBean(nama, tipe);
    }
}
