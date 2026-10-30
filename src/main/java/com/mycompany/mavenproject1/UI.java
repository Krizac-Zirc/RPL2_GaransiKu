package com.mycompany.mavenproject1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Kumpulan warna dan komponen bergaya seragam untuk seluruh tampilan GaransiKu.
 */
final class UI {

    static final Color PRIMARY = new Color(0x0F766E);
    static final Color PRIMARY_GELAP = new Color(0x115E59);
    static final Color TEKS = new Color(0x1F2937);
    static final Color ABU = new Color(0x6B7280);
    static final Color GARIS = new Color(0xD1D5DB);
    static final Color MERAH = new Color(0xB91C1C);
    static final Color HIJAU = new Color(0x15803D);

    private UI() { }

    static Font font(int gaya, int ukuran) {
        return new Font("Segoe UI", gaya, ukuran);
    }

    static <T extends JComponent> T rata(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    static JLabel label(String teks, int gaya, int ukuran, Color warna) {
        JLabel l = new JLabel(teks);
        l.setFont(font(gaya, ukuran));
        l.setForeground(warna);
        return l;
    }

    static JLabel labelKesalahan() {
        return label(" ", Font.PLAIN, 12, MERAH);
    }

    static JTextField field() {
        JTextField f = new JTextField();
        gayaField(f);
        return f;
    }

    static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        gayaField(f);
        return f;
    }

    private static void gayaField(JTextField f) {
        f.setFont(font(Font.PLAIN, 14));
        f.setForeground(TEKS);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        f.setPreferredSize(new Dimension(200, 40));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    /** Panel judul berwarna di bagian atas setiap jendela. */
    static JPanel header(String judul, String subjudul, int tinggi) {
        return header(judul, subjudul, tinggi, 26);
    }

    static JPanel header(String judul, String subjudul, int tinggi, int ukuranJudul) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(PRIMARY);
        p.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));
        p.setPreferredSize(new Dimension(440, tinggi));
        p.add(Box.createVerticalGlue());
        p.add(rata(label(judul, Font.BOLD, ukuranJudul, Color.WHITE)));
        p.add(Box.createVerticalStrut(4));
        p.add(rata(label(subjudul, Font.PLAIN, 13, new Color(0xCCFBF1))));
        p.add(Box.createVerticalGlue());
        return p;
    }

    /** Tombol utama (terisi) atau tombol garis tepi. */
    static JButton tombol(String teks, boolean terisi) {
        TombolBulat t = new TombolBulat(teks, terisi);
        t.setPreferredSize(new Dimension(200, 44));
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return t;
    }

    /** Tombol berbentuk tautan teks. */
    static JButton tautan(String teks) {
        JButton b = new JButton(teks);
        b.setFont(font(Font.BOLD, 13));
        b.setForeground(PRIMARY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setMargin(new java.awt.Insets(0, 4, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static class TombolBulat extends JButton {
        private final boolean terisi;
        private boolean sorot;

        TombolBulat(String teks, boolean terisi) {
            super(teks);
            this.terisi = terisi;
            setFont(font(Font.BOLD, 14));
            setForeground(terisi ? Color.WHITE : PRIMARY);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { sorot = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { sorot = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (terisi) {
                g2.setColor(sorot ? PRIMARY_GELAP : PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                g2.setColor(sorot ? new Color(0xF0FDFA) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(PRIMARY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
