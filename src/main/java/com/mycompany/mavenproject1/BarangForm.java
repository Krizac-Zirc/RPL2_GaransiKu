package com.mycompany.mavenproject1;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Form Data Barang GaransiKu (dibuat dengan GUI Builder NetBeans).
 * Menyediakan Tambah, Ubah, dan Hapus barang milik pengguna yang login.
 * Pasangan berkas: BarangForm.java dan BarangForm.form harus berada di folder yang sama.
 *
 * @author Rafly
 */
public class BarangForm extends javax.swing.JFrame {

    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Color HIJAU = new Color(0x15, 0x80, 0x3D);
    private static final Color JINGGA = new Color(0xB4, 0x53, 0x09);
    private static final Color MERAH = new Color(0xB9, 0x1C, 0x1C);

    private final Pengguna pengguna;
    private final BarangDAO dao = new BarangDAO();
    private List<Barang> daftar = new ArrayList<>();
    private DefaultTableModel modelTabel;
    private Barang terpilih;   // barang yang sedang dipilih pada tabel

    /**
     * Creates new form BarangForm
     */
    public BarangForm(Pengguna pengguna) {
        this.pengguna = pengguna;
        initComponents();
        // ---- pengaturan tambahan di luar GUI Builder ----
        getContentPane().setBackground(java.awt.Color.WHITE);
        lblSubjudul.setText("Pemilik: " + pengguna.getNamaLengkap() + " (@" + pengguna.getUsername() + ")");
        siapkanTabel();
        setLocationRelativeTo(null);
        muatData();
    }

    /** Membuat model tabel, aturan pemilihan baris, lebar kolom, dan warna kolom Status. */
    private void siapkanTabel() {
        modelTabel = new DefaultTableModel(
                new String[]{"Nama", "Merek", "Tanggal Beli", "Berakhir", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;   // isi tabel hanya untuk dibaca, perubahan lewat form
            }
        };
        tblBarang.setModel(modelTabel);
        tblBarang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBarang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanTerpilih();
            }
        });
        int[] lebar = {150, 100, 95, 95, 105};
        for (int i = 0; i < lebar.length; i++) {
            tblBarang.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        tblBarang.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object nilai,
                    boolean dipilih, boolean fokus, int baris, int kolom) {
                Component k = super.getTableCellRendererComponent(t, nilai, dipilih, fokus, baris, kolom);
                if (!dipilih) {
                    String s = String.valueOf(nilai);
                    k.setForeground("Kedaluwarsa".equals(s) ? MERAH
                            : "Hampir Berakhir".equals(s) ? JINGGA : HIJAU);
                }
                return k;
            }
        });
    }

    /** Membaca ulang barang milik pengguna dari database lalu menampilkannya di tabel. */
    private void muatData() {
        try {
            daftar = dao.daftarMilik(pengguna.getId());
            modelTabel.setRowCount(0);
            for (Barang b : daftar) {
                modelTabel.addRow(new Object[]{
                    b.getNama(), b.getMerek(),
                    b.getTanggalBeli().format(FORMAT_TANGGAL),
                    b.getTanggalBerakhir().format(FORMAT_TANGGAL),
                    b.getStatus()});
            }
        } catch (RuntimeException ex) {
            pesan("Gagal memuat data: " + penyebabAkar(ex), true);
        }
    }

    /** Mengisi form dengan data barang yang dipilih pada tabel. */
    private void tampilkanTerpilih() {
        int baris = tblBarang.getSelectedRow();
        if (baris < 0 || baris >= daftar.size()) {
            terpilih = null;
            btnUbah.setEnabled(false);
            btnHapus.setEnabled(false);
            return;
        }
        terpilih = daftar.get(baris);
        txtNama.setText(terpilih.getNama());
        txtMerek.setText(terpilih.getMerek());
        txtTanggal.setText(terpilih.getTanggalBeli().format(FORMAT_TANGGAL));
        txtLama.setText(String.valueOf(terpilih.getLamaGaransiBulan()));
        txtLokasi.setText(terpilih.getLokasiNota());
        btnUbah.setEnabled(true);
        btnHapus.setEnabled(true);
        pesan(" ", false);
    }

    /**
     * Membaca isian form menjadi objek Barang. Kesalahan isian dilaporkan lewat
     * IllegalArgumentException (validasi setter pada kelas Barang ikut berlaku).
     */
    private Barang bacaForm(int id) {
        LocalDate tanggal;
        try {
            tanggal = LocalDate.parse(txtTanggal.getText().trim(), FORMAT_TANGGAL);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Tanggal beli harus berformat dd-MM-yyyy, contoh 05-11-2026.");
        }
        int lama;
        try {
            lama = Integer.parseInt(txtLama.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Lama garansi harus berupa angka bulat.");
        }
        Barang b = new Barang(id, txtNama.getText(), txtMerek.getText(), tanggal, lama, txtLokasi.getText());
        b.setPenggunaId(pengguna.getId());
        return b;
    }

    /** Mengosongkan seluruh isian form dan pilihan baris pada tabel. */
    private void kosongkanForm() {
        txtNama.setText("");
        txtMerek.setText("");
        txtTanggal.setText("");
        txtLama.setText("");
        txtLokasi.setText("");
        tblBarang.clearSelection();
        terpilih = null;
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
        txtNama.requestFocusInWindow();
    }

    /** Menampilkan pesan di bawah form: merah untuk kesalahan, hijau untuk keberhasilan. */
    private void pesan(String teks, boolean galat) {
        lblPesan.setForeground(galat ? MERAH : HIJAU);
        lblPesan.setText("<html><body style='width:250px'>"
                + teks.replace("&", "&amp;").replace("<", "&lt;") + "</body></html>");
    }

    private static String penyebabAkar(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t.getMessage() != null ? t.getMessage() : t.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblSubjudul = new javax.swing.JLabel();
        lblForm = new javax.swing.JLabel();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        lblMerek = new javax.swing.JLabel();
        txtMerek = new javax.swing.JTextField();
        lblTanggal = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JTextField();
        lblLama = new javax.swing.JLabel();
        txtLama = new javax.swing.JTextField();
        lblLokasi = new javax.swing.JLabel();
        txtLokasi = new javax.swing.JTextField();
        lblPesan = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        lblDaftar = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBarang = new javax.swing.JTable();

        panelHeader.setBackground(new java.awt.Color(15, 118, 110));
        panelHeader.setPreferredSize(new java.awt.Dimension(900, 84));

        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblJudul.setForeground(new java.awt.Color(255, 255, 255));
        lblJudul.setText("Data Barang"); // NOI18N

        lblSubjudul.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSubjudul.setForeground(new java.awt.Color(204, 251, 241));
        lblSubjudul.setText(" "); // NOI18N

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblSubjudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(lblJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(lblSubjudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GaransiKu - Data Barang"); // NOI18N

        lblForm.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblForm.setForeground(new java.awt.Color(31, 41, 55));
        lblForm.setText("Form Barang"); // NOI18N

        lblNama.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNama.setForeground(new java.awt.Color(31, 41, 55));
        lblNama.setText("Nama barang"); // NOI18N

        txtNama.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblMerek.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblMerek.setForeground(new java.awt.Color(31, 41, 55));
        lblMerek.setText("Merek"); // NOI18N

        txtMerek.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTanggal.setForeground(new java.awt.Color(31, 41, 55));
        lblTanggal.setText("Tanggal beli (dd-MM-yyyy)"); // NOI18N

        txtTanggal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblLama.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblLama.setForeground(new java.awt.Color(31, 41, 55));
        lblLama.setText("Lama garansi (bulan)"); // NOI18N

        txtLama.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblLokasi.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblLokasi.setForeground(new java.awt.Color(31, 41, 55));
        lblLokasi.setText("Lokasi nota (laci, map, atau berkas)"); // NOI18N

        txtLokasi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblPesan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblPesan.setForeground(new java.awt.Color(185, 28, 28));
        lblPesan.setText(" "); // NOI18N

        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnTambah.setBackground(new java.awt.Color(15, 118, 110));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setBorderPainted(false);
        btnTambah.setFocusPainted(false);
        btnTambah.setText("Tambah"); // NOI18N
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnUbah.setEnabled(false);
        btnUbah.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUbah.setBackground(new java.awt.Color(17, 94, 89));
        btnUbah.setForeground(new java.awt.Color(255, 255, 255));
        btnUbah.setBorderPainted(false);
        btnUbah.setFocusPainted(false);
        btnUbah.setText("Ubah"); // NOI18N
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        btnHapus.setEnabled(false);
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnHapus.setBackground(new java.awt.Color(185, 28, 28));
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setBorderPainted(false);
        btnHapus.setFocusPainted(false);
        btnHapus.setText("Hapus"); // NOI18N
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnBersihkan.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBersihkan.setBackground(new java.awt.Color(243, 244, 246));
        btnBersihkan.setForeground(new java.awt.Color(31, 41, 55));
        btnBersihkan.setBorderPainted(false);
        btnBersihkan.setFocusPainted(false);
        btnBersihkan.setText("Bersihkan"); // NOI18N
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });

        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnKembali.setBackground(new java.awt.Color(243, 244, 246));
        btnKembali.setForeground(new java.awt.Color(31, 41, 55));
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setText("Kembali ke Beranda"); // NOI18N
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        lblDaftar.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblDaftar.setForeground(new java.awt.Color(31, 41, 55));
        lblDaftar.setText("Daftar Barang Anda"); // NOI18N

        jScrollPane1.setPreferredSize(new java.awt.Dimension(560, 440));

        tblBarang.setRowHeight(26);
        tblBarang.setFillsViewportHeight(true);

        jScrollPane1.setViewportView(tblBarang);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblMerek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtMerek, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblLama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtLama, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPesan, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblDaftar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(lblNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(lblMerek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(txtMerek, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(lblTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(lblLama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(txtLama, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(lblLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(txtLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(lblPesan, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(14, 14, 14)
                                        .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblDaftar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        try {
            Barang baru = bacaForm(0);
            dao.tambah(baru);
            muatData();
            kosongkanForm();
            pesan("Barang \"" + baru.getNama() + "\" berhasil ditambahkan.", false);
        } catch (IllegalArgumentException ex) {
            pesan(ex.getMessage(), true);
        } catch (RuntimeException ex) {
            pesan("Kesalahan database: " + penyebabAkar(ex), true);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        if (terpilih == null) {
            pesan("Pilih barang pada tabel terlebih dahulu.", true);
            return;
        }
        try {
            Barang baru = bacaForm(terpilih.getId());
            boolean berhasil = dao.ubah(baru, pengguna.getId());
            muatData();
            if (berhasil) {
                kosongkanForm();
                pesan("Data barang berhasil diubah.", false);
            } else {
                pesan("Data tidak ditemukan, kemungkinan sudah dihapus.", true);
            }
        } catch (IllegalArgumentException ex) {
            pesan(ex.getMessage(), true);
        } catch (RuntimeException ex) {
            pesan("Kesalahan database: " + penyebabAkar(ex), true);
        }
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        if (terpilih == null) {
            pesan("Pilih barang pada tabel terlebih dahulu.", true);
            return;
        }
        int jawab = JOptionPane.showConfirmDialog(this,
                "Hapus barang \"" + terpilih.getNama() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            String nama = terpilih.getNama();
            boolean berhasil = dao.hapus(terpilih.getId(), pengguna.getId());
            muatData();
            kosongkanForm();
            if (berhasil) {
                pesan("Barang \"" + nama + "\" berhasil dihapus.", false);
            } else {
                pesan("Data tidak ditemukan, kemungkinan sudah dihapus.", true);
            }
        } catch (RuntimeException ex) {
            pesan("Kesalahan database: " + penyebabAkar(ex), true);
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        kosongkanForm();
        pesan(" ", false);
    }//GEN-LAST:event_btnBersihkanActionPerformed

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        dispose();
        new BerandaFrame(pengguna).setVisible(true);
    }//GEN-LAST:event_btnKembaliActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDaftar;
    private javax.swing.JLabel lblForm;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblLama;
    private javax.swing.JLabel lblLokasi;
    private javax.swing.JLabel lblMerek;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblPesan;
    private javax.swing.JLabel lblSubjudul;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTable tblBarang;
    private javax.swing.JTextField txtLama;
    private javax.swing.JTextField txtLokasi;
    private javax.swing.JTextField txtMerek;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables
}
