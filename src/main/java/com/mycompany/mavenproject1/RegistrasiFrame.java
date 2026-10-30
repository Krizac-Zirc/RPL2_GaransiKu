package com.mycompany.mavenproject1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Jendela Registrasi akun GaransiKu. */
public class RegistrasiFrame extends JFrame {

    private final AuthService auth = new AuthService();
    private final JTextField txtUsername = UI.field();
    private final JTextField txtNama = UI.field();
    private final JPasswordField txtPassword = UI.passwordField();
    private final JPasswordField txtUlang = UI.passwordField();
    private final JCheckBox chkTampil = new JCheckBox("Tampilkan kata sandi");
    private final JLabel lblError = UI.labelKesalahan();
    private final JButton btnDaftar = UI.tombol("Daftar", true);
    private final JButton btnKembali = UI.tombol("Kembali ke Login", false);
    private final char echoBawaan = txtPassword.getEchoChar();

    public RegistrasiFrame() {
        super("GaransiKu - Registrasi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setBackground(Color.WHITE);
        isi.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        isi.add(UI.rata(UI.label("Buat akun baru", Font.BOLD, 18, UI.TEKS)));
        isi.add(Box.createVerticalStrut(14));
        tambahKolom(isi, "Username", txtUsername);
        isi.add(Box.createVerticalStrut(3));
        isi.add(UI.rata(UI.label("4-20 karakter: huruf, angka, atau _", Font.PLAIN, 11, UI.ABU)));
        isi.add(Box.createVerticalStrut(10));
        tambahKolom(isi, "Nama lengkap", txtNama);
        isi.add(Box.createVerticalStrut(10));
        tambahKolom(isi, "Kata sandi (minimal 6 karakter)", txtPassword);
        isi.add(Box.createVerticalStrut(10));
        tambahKolom(isi, "Ulangi kata sandi", txtUlang);

        chkTampil.setFont(UI.font(Font.PLAIN, 12));
        chkTampil.setForeground(UI.ABU);
        chkTampil.setBackground(Color.WHITE);
        chkTampil.setFocusPainted(false);
        isi.add(Box.createVerticalStrut(4));
        isi.add(UI.rata(chkTampil));
        isi.add(Box.createVerticalStrut(4));
        isi.add(UI.rata(lblError));
        isi.add(Box.createVerticalStrut(8));
        isi.add(UI.rata(btnDaftar));
        isi.add(Box.createVerticalStrut(10));
        isi.add(UI.rata(btnKembali));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(UI.header("GaransiKu", "Registrasi Akun", 90), BorderLayout.NORTH);
        getContentPane().add(isi, BorderLayout.CENTER);

        // ---- aksi ----
        chkTampil.addActionListener(e -> {
            char c = chkTampil.isSelected() ? (char) 0 : echoBawaan;
            txtPassword.setEchoChar(c);
            txtUlang.setEchoChar(c);
        });
        btnDaftar.addActionListener(e -> prosesDaftar());
        btnKembali.addActionListener(e -> kembaliKeLogin(null));
        getRootPane().setDefaultButton(btnDaftar);

        pack();
        setLocationRelativeTo(null);
    }

    private static void tambahKolom(JPanel isi, String judul, JTextField field) {
        isi.add(UI.rata(UI.label(judul, Font.PLAIN, 13, UI.TEKS)));
        isi.add(Box.createVerticalStrut(5));
        isi.add(UI.rata(field));
    }

    private void prosesDaftar() {
        String pesan = auth.registrasi(txtUsername.getText(), txtNama.getText(),
                new String(txtPassword.getPassword()), new String(txtUlang.getPassword()));
        if (pesan != null) {
            lblError.setText(pesan);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Registrasi berhasil. Silakan login dengan akun Anda.",
                "Registrasi", JOptionPane.INFORMATION_MESSAGE);
        kembaliKeLogin(txtUsername.getText().trim().toLowerCase());
    }

    private void kembaliKeLogin(String username) {
        dispose();
        LoginForm login = new LoginForm();
        if (username != null) {
            login.isiUsername(username);
        }
        login.setVisible(true);
    }
}
