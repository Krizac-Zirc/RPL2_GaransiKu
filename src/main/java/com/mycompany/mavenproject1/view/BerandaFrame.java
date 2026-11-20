package com.mycompany.mavenproject1.view;

import com.mycompany.mavenproject1.model.Pengguna;
import com.mycompany.mavenproject1.service.BarangService;
import com.mycompany.mavenproject1.util.AppContext;
import com.mycompany.mavenproject1.util.UI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JOptionPane;
import com.mycompany.mavenproject1.model.Barang;
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
        {"Pencarian dan Penyaringan", "Selesai"},
        {"Pengingat Masa Garansi", "Selesai"},
        {"Laporan JasperReports (PDF / XLS)", "Selesai"},
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
        JButton btnCari = UI.tombol("Cari, Filter, dan Laporan", true);
        isi.add(UI.rata(btnCari));
        isi.add(Box.createVerticalStrut(8));
        JButton btnPengingat = UI.tombol("Pengingat Garansi", false);
        isi.add(UI.rata(btnPengingat));
        isi.add(Box.createVerticalStrut(8));
        JButton btnLogout = UI.tombol("Logout", false);
        isi.add(UI.rata(btnLogout));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(UI.header("Halo, " + pengguna.getNamaLengkap(),
                "Login sebagai @" + pengguna.getUsername(), 110, 20), BorderLayout.NORTH);
        getContentPane().add(isi, BorderLayout.CENTER);

        btnBarang.addActionListener(e -> {
            dispose();
            // Service diambil dari container Spring (bukan dibuat dengan new)
            BarangService service = AppContext.getBean("serviceBarang", BarangService.class);
            new BarangForm(pengguna, service).setVisible(true);
        });
        btnCari.addActionListener(e -> {
            dispose();
            new CariFrame(pengguna).setVisible(true);
        });
        btnPengingat.addActionListener(e -> tampilkanPengingat(pengguna, true));
        // Pengingat otomatis muncul saat Beranda dibuka
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                tampilkanPengingat(pengguna, false);
            }
        });
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Menampilkan daftar barang yang garansinya hampir berakhir (sisa 30 hari atau kurang).
     * Bila tidak ada: dialog hanya muncul jika pengguna menekan tombolnya sendiri.
     */
    private void tampilkanPengingat(Pengguna pengguna, boolean tombol) {
        try {
            BarangService service = AppContext.getBean("serviceBarang", BarangService.class);
            List<Barang> hampir = service.pengingat(pengguna.getId());
            int lewat = service.cari(pengguna.getId(), "", "Kedaluwarsa").size();
            if (hampir.isEmpty() && lewat == 0) {
                if (tombol) {
                    JOptionPane.showMessageDialog(this, "Tidak ada garansi yang hampir berakhir.",
                            "Pengingat Garansi", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
            StringBuilder sb = new StringBuilder();
            if (!hampir.isEmpty()) {
                sb.append("Garansi hampir berakhir:\n");
                for (Barang b : hampir) {
                    sb.append(" - ").append(b.getNama()).append(" (").append(b.getMerek())
                      .append("): ").append(b.getSisaHari()).append(" hari lagi\n");
                }
            }
            if (lewat > 0) {
                sb.append("\n").append(lewat).append(" barang sudah kedaluwarsa.");
            }
            JOptionPane.showMessageDialog(this, sb.toString().trim(),
                    "Pengingat Garansi", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            if (tombol) {
                JOptionPane.showMessageDialog(this, "Kesalahan database: " + ex.getMessage(),
                        "Pengingat Garansi", JOptionPane.ERROR_MESSAGE);
            }
        }
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
