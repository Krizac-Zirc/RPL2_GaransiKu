package com.mycompany.mavenproject1.view;

import com.mycompany.mavenproject1.model.Barang;
import com.mycompany.mavenproject1.model.Pengguna;
import com.mycompany.mavenproject1.service.BarangService;
import com.mycompany.mavenproject1.service.LaporanService;
import com.mycompany.mavenproject1.util.AppContext;
import com.mycompany.mavenproject1.util.UI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Pencarian dan penyaringan barang, serta pencetakan laporan JasperReports
 * (PDF atau XLS) dari daftar yang sedang tampil.
 */
public class CariFrame extends JFrame {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Color JINGGA = new Color(0xB4, 0x53, 0x09);

    private final Pengguna pengguna;
    private final BarangService service;
    private final LaporanService laporan;
    private final JTextField txtKata = UI.field();
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"Semua", "Aktif", "Hampir Berakhir", "Kedaluwarsa"});
    private final JLabel lblJumlah = UI.label(" ", Font.PLAIN, 12, UI.ABU);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Nama", "Merek", "Tanggal Beli", "Berakhir", "Sisa Hari", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int baris, int kolom) {
            return false;
        }
    };
    private List<Barang> tampil = new ArrayList<>();

    public CariFrame(Pengguna pengguna) {
        super("GaransiKu - Pencarian dan Laporan");
        this.pengguna = pengguna;
        // Service diambil dari container Spring (bukan dibuat dengan new)
        this.service = AppContext.getBean("serviceBarang", BarangService.class);
        this.laporan = AppContext.getBean("serviceLaporan", LaporanService.class);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ---- baris pencarian dan penyaringan ----
        JButton btnCari = UI.tombol("Cari", true);
        btnCari.setPreferredSize(new Dimension(90, 40));
        JButton btnReset = UI.tombol("Reset", false);
        btnReset.setPreferredSize(new Dimension(90, 40));
        txtKata.setPreferredSize(new Dimension(260, 40));
        cmbStatus.setPreferredSize(new Dimension(150, 40));
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filter.setBackground(Color.WHITE);
        filter.add(UI.label("Nama / merek:", Font.PLAIN, 13, UI.TEKS));
        filter.add(txtKata);
        filter.add(UI.label("Status:", Font.PLAIN, 13, UI.TEKS));
        filter.add(cmbStatus);
        filter.add(btnCari);
        filter.add(btnReset);

        // ---- tabel hasil ----
        JTable tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setFont(UI.font(Font.PLAIN, 13));
        tabel.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object nilai,
                    boolean dipilih, boolean fokus, int baris, int kolom) {
                Component k = super.getTableCellRendererComponent(t, nilai, dipilih, fokus, baris, kolom);
                if (!dipilih) {
                    String s = String.valueOf(nilai);
                    k.setForeground("Kedaluwarsa".equals(s) ? UI.MERAH
                            : "Hampir Berakhir".equals(s) ? JINGGA : UI.HIJAU);
                }
                return k;
            }
        });

        JPanel tengah = new JPanel(new BorderLayout(0, 6));
        tengah.setBackground(Color.WHITE);
        tengah.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 20));
        tengah.add(filter, BorderLayout.NORTH);
        tengah.add(new JScrollPane(tabel), BorderLayout.CENTER);
        tengah.add(lblJumlah, BorderLayout.SOUTH);

        // ---- tombol laporan ----
        JButton btnCetak = UI.tombol("Cetak", true);
        JButton btnPdf = UI.tombol("Ekspor PDF", true);
        JButton btnXls = UI.tombol("Ekspor XLS", true);
        JButton btnKembali = UI.tombol("Kembali", false);
        for (JButton b : new JButton[]{btnCetak, btnPdf, btnXls, btnKembali}) {
            b.setPreferredSize(new Dimension(150, 42));
        }
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bawah.setBackground(Color.WHITE);
        bawah.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        bawah.add(btnCetak);
        bawah.add(btnPdf);
        bawah.add(btnXls);
        bawah.add(btnKembali);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().add(UI.header("Pencarian dan Laporan",
                "Pemilik: " + pengguna.getNamaLengkap() + " (@" + pengguna.getUsername() + ")", 84, 22),
                BorderLayout.NORTH);
        getContentPane().add(tengah, BorderLayout.CENTER);
        getContentPane().add(bawah, BorderLayout.SOUTH);

        btnCari.addActionListener(e -> muat());
        txtKata.addActionListener(e -> muat());
        cmbStatus.addActionListener(e -> muat());
        btnReset.addActionListener(e -> {
            txtKata.setText("");
            cmbStatus.setSelectedIndex(0);
            muat();
        });
        btnCetak.addActionListener(e -> cetak());
        btnPdf.addActionListener(e -> ekspor(true));
        btnXls.addActionListener(e -> ekspor(false));
        btnKembali.addActionListener(e -> {
            dispose();
            new BerandaFrame(pengguna).setVisible(true);
        });

        setPreferredSize(new Dimension(860, 560));
        pack();
        setLocationRelativeTo(null);
        muat();
    }

    /** Mengambil data sesuai kata kunci dan status, lalu menampilkannya di tabel. */
    private void muat() {
        try {
            tampil = service.cari(pengguna.getId(), txtKata.getText(), (String) cmbStatus.getSelectedItem());
            model.setRowCount(0);
            for (Barang b : tampil) {
                model.addRow(new Object[]{b.getNama(), b.getMerek(),
                    b.getTanggalBeli().format(FORMAT), b.getTanggalBerakhir().format(FORMAT),
                    b.getSisaHari(), b.getStatus()});
            }
            lblJumlah.setText(tampil.size() + " barang ditemukan");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan database: " + ex.getMessage(),
                    "GaransiKu", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tombol Cetak: mengisi laporan (kompilasi .jrxml, isi data, parameter) lalu
     * menampilkannya di jendela pratinjau JasperViewer, seperti pada StokView.
     */
    private void cetak() {
        if (tampil.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak.",
                    "GaransiKu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            String ket = "Kata kunci: " + (txtKata.getText().trim().isEmpty() ? "-" : txtKata.getText().trim())
                    + " | Status: " + cmbStatus.getSelectedItem();
            JasperPrint cetak = laporan.isi(tampil, pengguna.getNamaLengkap(), ket);
            JasperViewer.viewReport(cetak, false);   // false: menutup pratinjau tidak menutup aplikasi
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** Membuat laporan dari daftar yang sedang tampil, lalu menyimpannya sebagai PDF atau XLS. */
    private void ekspor(boolean pdf) {
        if (tampil.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak.",
                    "GaransiKu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String ext = pdf ? "pdf" : "xls";
        JFileChooser pilih = new JFileChooser();
        pilih.setDialogTitle("Simpan laporan " + ext.toUpperCase());
        pilih.setFileFilter(new FileNameExtensionFilter(pdf ? "Berkas PDF (*.pdf)" : "Berkas Excel (*.xls)", ext));
        pilih.setSelectedFile(new File("Laporan_Garansi." + ext));
        if (pilih.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File tujuan = pilih.getSelectedFile();
        if (!tujuan.getName().toLowerCase().endsWith("." + ext)) {
            tujuan = new File(tujuan.getParentFile(), tujuan.getName() + "." + ext);
        }
        if (tujuan.exists() && JOptionPane.showConfirmDialog(this,
                "Berkas sudah ada. Timpa?", "GaransiKu", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String ket = "Kata kunci: " + (txtKata.getText().trim().isEmpty() ? "-" : txtKata.getText().trim())
                    + " | Status: " + cmbStatus.getSelectedItem();
            JasperPrint cetak = laporan.isi(tampil, pengguna.getNamaLengkap(), ket);
            if (pdf) {
                laporan.eksporPdf(cetak, tujuan);
            } else {
                laporan.eksporXls(cetak, tujuan);
            }
            JOptionPane.showMessageDialog(this, "Laporan tersimpan di:\n" + tujuan.getAbsolutePath(),
                    "GaransiKu", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            Throwable t = ex;
            while (t.getCause() != null && t.getCause() != t) {
                t = t.getCause();
            }
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan: " + t,
                    "GaransiKu", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
