package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class MainView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;
    private PanelKoleksi panelKoleksi;
    private PanelAnggota panelAnggota;
    private PanelPeminjaman panelPeminjaman;

    private controller.PerpustakaanController ctrlKoleksi;
    private controller.AnggotaController ctrlAnggota;
    private controller.PeminjamanController ctrlPeminjaman;

    private JLabel lblStatKoleksi;
    private JLabel lblStatAnggota;
    private JLabel lblStatDipinjam;
    private JLabel lblStatDenda;
    private JMenuItem itemLogout;

    public MainView() {
        inisialisasiMenuBar();
        inisialisasiFrame();
    }

    private void inisialisasiMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuNavigasi = new JMenu("Menu Utama");
        JMenuItem itemBeranda = new JMenuItem("Beranda");
        JMenuItem itemKoleksi = new JMenuItem("Kelola Koleksi");
        JMenuItem itemAnggota = new JMenuItem("Kelola Anggota");
        JMenuItem itemPeminjaman = new JMenuItem("Sirkulasi Peminjaman");

        itemBeranda.addActionListener(e -> pindahTab(0));
        itemKoleksi.addActionListener(e -> pindahTab(1));
        itemAnggota.addActionListener(e -> pindahTab(2));
        itemPeminjaman.addActionListener(e -> pindahTab(3));

        menuNavigasi.add(itemBeranda);
        menuNavigasi.add(itemKoleksi);
        menuNavigasi.add(itemAnggota);
        menuNavigasi.add(itemPeminjaman);

        JMenu menuSesi = new JMenu("Akun");
        itemLogout = new JMenuItem("Logout");
        JMenuItem itemKeluar = new JMenuItem("Keluar Aplikasi");
        itemKeluar.addActionListener(e -> System.exit(0));

        menuSesi.add(itemLogout);
        menuSesi.addSeparator();
        menuSesi.add(itemKeluar);

        menuBar.add(menuNavigasi);
        menuBar.add(menuSesi);
        setJMenuBar(menuBar);
    }

    private void inisialisasiFrame() {
        setTitle("Sistem Informasi Perpustakaan Kampus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 650));
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Banner Atas
        JPanel panelBanner = new JPanel(new BorderLayout());
        panelBanner.setBackground(new Color(25, 55, 109));
        panelBanner.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel lblJudul = new JLabel("SISTEM INFORMASI PERPUSTAKAAN KAMPUS");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblJudul.setForeground(Color.WHITE);
        panelBanner.add(lblJudul, BorderLayout.WEST);

        add(panelBanner, BorderLayout.NORTH);

        // TabbedPane Utama
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        panelKoleksi = new PanelKoleksi();
        ctrlKoleksi = new controller.PerpustakaanController(panelKoleksi);
        panelAnggota = new PanelAnggota();
        ctrlAnggota = new controller.AnggotaController(panelAnggota);
        panelPeminjaman = new PanelPeminjaman();
        ctrlPeminjaman = new controller.PeminjamanController(panelPeminjaman);

        tabbedPane.addTab("Beranda", buatPanelBeranda());
        tabbedPane.addTab("Data Koleksi", panelKoleksi);
        tabbedPane.addTab("Data Anggota", panelAnggota);
        tabbedPane.addTab("Peminjaman & Pengembalian", panelPeminjaman);

        add(tabbedPane, BorderLayout.CENTER);

        // Setiap kali tab dibuka, data tab itu dimuat ulang dari database
        tabbedPane.addChangeListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0: muatStatistikBeranda(); break;
                case 1: ctrlKoleksi.muatDataTabel(); break;
                case 2: ctrlAnggota.muatDataTabel(); break;
                case 3: ctrlPeminjaman.muatDataTabel(); break;
                default: break;
            }
        });
        muatStatistikBeranda();
    }

    private void muatStatistikBeranda() {
        try {
            int totalKoleksi = new dao.KoleksiDAO().ambilSemuaKoleksi().size();
            int totalAnggota = new dao.AnggotaDAO().ambilSemuaAnggota().size();
            java.util.List<Object[]> pinjaman = new dao.PeminjamanDAO().ambilSemuaPeminjaman();

            // Denda berjalan: peminjaman yang sudah lewat jatuh tempo dan belum dikembalikan
            double denda = 0;
            java.time.LocalDate hariIni = java.time.LocalDate.now();
            for (Object[] baris : pinjaman) {
                String jenis = baris[3].toString();
                java.time.LocalDate jatuhTempo = java.time.LocalDate.parse(baris[5].toString());
                long telat = java.time.temporal.ChronoUnit.DAYS.between(jatuhTempo, hariIni);
                if (telat > 0) {
                    denda += "Skripsi".equalsIgnoreCase(jenis) ? 10000 + telat * 5000 : telat * 2000;
                }
            }

            perbaruiStatistik(totalKoleksi, totalAnggota, pinjaman.size(),
                    String.format("Rp %,.0f", denda));
        } catch (java.sql.SQLException ex) {
            tampilkanPesan("Gagal memuat statistik beranda: " + ex.getMessage());
        }
    }

    private JPanel buatPanelBeranda() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(new Color(245, 247, 250));

        JPanel panelKartu = new JPanel(new GridLayout(1, 4, 14, 14));
        panelKartu.setOpaque(false);

        lblStatKoleksi = new JLabel("0", SwingConstants.CENTER);
        lblStatAnggota = new JLabel("0", SwingConstants.CENTER);
        lblStatDipinjam = new JLabel("0", SwingConstants.CENTER);
        lblStatDenda = new JLabel("Rp 0", SwingConstants.CENTER);

        panelKartu.add(buatKartuStat("Total Koleksi", lblStatKoleksi, new Color(25, 118, 210)));
        panelKartu.add(buatKartuStat("Total Anggota", lblStatAnggota, new Color(46, 125, 50)));
        panelKartu.add(buatKartuStat("Sedang Dipinjam", lblStatDipinjam, new Color(230, 81, 0)));
        panelKartu.add(buatKartuStat("Total Denda", lblStatDenda, new Color(106, 27, 154)));

        panel.add(panelKartu, BorderLayout.NORTH);

        JPanel panelNavigasi = new JPanel(new GridLayout(3, 1, 12, 12));
        panelNavigasi.setBackground(Color.WHITE);
        panelNavigasi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Navigasi Cepat Modul "),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)));

        JButton btnKeKoleksi = new JButton("Buka Kelola Koleksi (Buku / Jurnal / Skripsi)");
        JButton btnKeAnggota = new JButton("Buka Kelola Anggota Kampus");
        JButton btnKePeminjaman = new JButton("Buka Sirkulasi Peminjaman & Pengembalian");

        btnKeKoleksi.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnKeAnggota.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnKePeminjaman.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnKeKoleksi.addActionListener(e -> pindahTab(1));
        btnKeAnggota.addActionListener(e -> pindahTab(2));
        btnKePeminjaman.addActionListener(e -> pindahTab(3));

        panelNavigasi.add(btnKeKoleksi);
        panelNavigasi.add(btnKeAnggota);
        panelNavigasi.add(btnKePeminjaman);

        panel.add(panelNavigasi, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatKartuStat(String judul, JLabel lblNilai, Color warna) {
        JPanel kartu = new JPanel(new BorderLayout(6, 6));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, warna),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)));

        JLabel lblJudul = new JLabel(judul, SwingConstants.CENTER);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblJudul.setForeground(new Color(90, 90, 90));

        lblNilai.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblNilai.setForeground(warna);

        kartu.add(lblJudul, BorderLayout.NORTH);
        kartu.add(lblNilai, BorderLayout.CENTER);
        return kartu;
    }

    public void pindahTab(int index) {
        tabbedPane.setSelectedIndex(index);
    }

    public void perbaruiStatistik(int totalKoleksi, int totalAnggota, int totalDipinjam, String totalDenda) {
        lblStatKoleksi.setText(String.valueOf(totalKoleksi));
        lblStatAnggota.setText(String.valueOf(totalAnggota));
        lblStatDipinjam.setText(String.valueOf(totalDipinjam));
        lblStatDenda.setText(totalDenda);
    }

    public void tampilkanPesan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan);
    }

    // Getter untuk Controller
    public PanelKoleksi getPanelKoleksi() { return panelKoleksi; }
    public PanelAnggota getPanelAnggota() { return panelAnggota; }
    public PanelPeminjaman getPanelPeminjaman() { return panelPeminjaman; }
    public JMenuItem getItemLogout() { return itemLogout; }
}
