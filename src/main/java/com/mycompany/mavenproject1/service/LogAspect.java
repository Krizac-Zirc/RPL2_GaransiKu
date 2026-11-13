package com.mycompany.mavenproject1.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Aspek AOP untuk logging: mencatat setiap pemanggilan method BarangService
 * ke konsol tanpa mengubah kode BarangService.
 */
@Aspect
public class LogAspect {

    // Sebelum method service dijalankan
    @Before("execution(* com.mycompany.mavenproject1.service.BarangService.*(..))")
    public void sebelum(JoinPoint jp) {
        System.out.println("[LOG] " + jp.getSignature().getName() + "() dipanggil");
    }

    // Setelah method selesai tanpa error
    @AfterReturning("execution(* com.mycompany.mavenproject1.service.BarangService.*(..))")
    public void sesudah(JoinPoint jp) {
        System.out.println("[LOG] " + jp.getSignature().getName() + "() selesai");
    }

    // Bila method melempar exception
    @AfterThrowing(pointcut = "execution(* com.mycompany.mavenproject1.service.BarangService.*(..))",
                   throwing = "ex")
    public void gagal(JoinPoint jp, Throwable ex) {
        System.out.println("[LOG] " + jp.getSignature().getName() + "() gagal: " + ex.getMessage());
    }
}
