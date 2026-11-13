# GaransiKu

Aplikasi desktop pelacak garansi barang rumah tangga. Pengguna dapat mendaftar, masuk, dan (pada tahap pengembangan berikutnya) mencatat barang beserta masa garansinya agar tidak ada garansi yang terlewat.

Tugas Rekayasa Perangkat Lunak 2 (RPL 2), Universitas Gunadarma.

| | |
|---|---|
| Nama | Rafly Ahmad Mulyantoro |
| NPM | 51423194 |
| Kelas | 4IA22 |

## Status Pengembangan

| Fitur | Status |
|---|---|
| Registrasi akun | Selesai |
| Login | Selesai |
| Beranda | Selesai (tampilan awal) |
| Tambah, ubah, hapus barang (Hibernate) | Selesai |
| Integrasi Spring (DI, @Transactional, AOP) | Selesai |
| Daftar barang | Selesai |
| Pencarian dan penyaringan barang | Belum dikerjakan |
| Pengingat garansi | Belum dikerjakan |
| Laporan (JasperReports) | Belum dikerjakan |

## Teknologi

- Java 17, Swing (GUI Builder NetBeans)
- Maven
- MySQL (XAMPP) dengan JDBC (`mysql-connector-j`) untuk akun pengguna
- Hibernate 5.6.15.Final (ORM) untuk data barang
- Spring Framework 5.3.39: Dependency Injection (IoC), transaksi deklaratif `@Transactional`, dan AOP untuk logging
- Penyimpanan kata sandi: PBKDF2WithHmacSHA256 dengan salt acak
- Pola DAO, Service, dan PreparedStatement

## Kebutuhan Perangkat

- JDK 17
- Apache NetBeans
- XAMPP (modul MySQL aktif)

## Cara Menjalankan

1. Jalankan XAMPP, lalu klik **Start** pada MySQL.
2. Klon repositori ini dan buka sebagai proyek Maven di NetBeans.
3. Sesuaikan `src/main/resources/db.properties` bila pengguna atau kata sandi MySQL berbeda. Bawaan XAMPP: pengguna `root`, tanpa kata sandi.
4. Klik kanan proyek, pilih **Clean and Build** agar dependensi terunduh.
5. Jalankan `Mavenproject1.java` atau `LoginForm.java`.

Basis data `garansiku` dan tabel `pengguna` dibuat otomatis saat aplikasi pertama kali dijalankan, sedangkan tabel `barang` dibuat otomatis oleh Hibernate saat form Data Barang pertama kali dibuka. Pengaturan koneksi pada `db.properties` (JDBC) dan `applicationContext.xml` (Hibernate lewat Spring) harus sama.

## Struktur Proyek

```
src/main/java/com/mycompany/mavenproject1/
    Mavenproject1.java         titik masuk aplikasi
    dao/      BarangDAO (Hibernate, setter injection), PenggunaDAO (JDBC)
    model/    Barang (entity Hibernate), Pengguna
    service/  BarangService (@Transactional), AuthService, LogAspect (AOP)
    view/     LoginForm, RegistrasiFrame, BerandaFrame, BarangForm
    util/     Database, PasswordUtil, UI, AppContext (container Spring)
src/main/resources/
    applicationContext.xml     konfigurasi bean Spring
    hibernate.cfg.xml          pengaturan Hibernate (koneksi dari DataSource Spring)
    db.properties              pengaturan koneksi MySQL (JDBC)
```
