package com.mycompany.mavenproject1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** Halaman Beranda setelah login berhasil. */
public class BerandaFrame extends JFrame {

    private static final String[][] FITUR = {
        {"Registrasi dan Login", "Selesai"},
        {"Tambah / Ubah / Hapus Data Barang", "Selesai"},
        {"Lihat Daftar Barang", "Selesai"},
        {"Pencarian dan Penyaringan", "Belum tersedia"},
        {"Pengingat Masa Garansi", "Belum tersedia"},
    };

    public BerandaFrame(Pengguna pengguna) {
        super("GaransiKu - Beranda");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setBackground(Color.WHITE);
        isi.setBorder(BorderFactory.createEmptyBorder(22, 32, 24, 32));

        isi.add(UI.rata(UI.label("Fitur GaransiKu", Font.BOLD, 18, UI.TEKS)));
        isi.add(Box.createVerticalStrut(12));
        for (String[] f : FITUR) {
            isi.add(UI.rata(barisFitur(f[0], f[1])));
            isi.add(Box.createVerticalStrut(8));
        }
        isi.add(Box.createVerticalStrut(10));
        JButton btnBarang = UI.tombol("Kelola Data Barang", true);
        isi.add(UI.rata(btnBarang));
        isi.add(Box.createVerticalStrut(8));
        JButton btnLogout = UI.tombol("Logout", false);
        isi.add(UI.rata(btnLogout));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(UI.header("Halo, " + pengguna.getNamaLengkap(),
                "Login sebagai @" + pengguna.getUsername(), 110, 20), BorderLayout.NORTH);
        getContentPane().add(isi, BorderLayout.CENTER);

        btnBarang.addActionListener(e -> {
            dispose();
            new BarangForm(pengguna).setVisible(true);
        });
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        pack();
        setLocationRelativeTo(null);
    }

    private static JPanel barisFitur(String nama, String status) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xF9FAFB));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UI.GARIS),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        p.add(UI.label(nama, Font.PLAIN, 13, UI.TEKS), BorderLayout.WEST);
        boolean selesai = status.equals("Selesai");
        p.add(UI.label(status, Font.BOLD, 12, selesai ? UI.HIJAU : UI.ABU), BorderLayout.EAST);
        return p;
    }
}
