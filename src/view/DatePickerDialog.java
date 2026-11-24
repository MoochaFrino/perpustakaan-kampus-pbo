package view;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

/**
 * Dialog pemilih tanggal (Calendar Date Picker) berbasis Java Swing murni
 * tanpa library eksternal, terintegrasi dengan JSpinner tanggal.
 */
public class DatePickerDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final String[] NAMA_BULAN = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };
    private static final String[] NAMA_HARI = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};

    private static final Color WARNA_UTAMA = new Color(25, 55, 109);
    private static final Color WARNA_AKSEN = new Color(25, 118, 210);
    private static final Color WARNA_HARI_INI = new Color(227, 242, 253);

    private YearMonth bulanTampil;
    private LocalDate tanggalTerpilih;
    private boolean dikonfirmasi = false;

    private JComboBox<String> cmbBulan;
    private JLabel lblTahun;
    private JPanel panelTanggal;
    private boolean sedangUpdateCombo = false;

    public DatePickerDialog(Window owner, LocalDate tanggalAwal) {
        super(owner, "Pilih Tanggal", ModalityType.APPLICATION_MODAL);
        this.tanggalTerpilih = (tanggalAwal != null) ? tanggalAwal : LocalDate.now();
        this.bulanTampil = YearMonth.from(this.tanggalTerpilih);

        inisialisasiUI();
        renderKalender();
        pack();
        setResizable(false);
    }

    private void inisialisasiUI() {
        setLayout(new BorderLayout(0, 6));
        getContentPane().setBackground(Color.WHITE);

        // Header navigasi bulan & tahun
        JPanel panelHeader = new JPanel(new BorderLayout(4, 4));
        panelHeader.setBackground(WARNA_UTAMA);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panelKiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panelKiri.setOpaque(false);
        JButton btnTahunLalu = buatTombolNav("<<", "Tahun Sebelumnya");
        JButton btnBulanLalu = buatTombolNav("<", "Bulan Sebelumnya");
        panelKiri.add(btnTahunLalu);
        panelKiri.add(btnBulanLalu);

        JPanel panelTengah = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        panelTengah.setOpaque(false);
        cmbBulan = new JComboBox<>(NAMA_BULAN);
        cmbBulan.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTahun = new JLabel("", SwingConstants.CENTER);
        lblTahun.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTahun.setForeground(Color.WHITE);
        panelTengah.add(cmbBulan);
        panelTengah.add(lblTahun);

        JPanel panelKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        panelKanan.setOpaque(false);
        JButton btnBulanDepan = buatTombolNav(">", "Bulan Berikutnya");
        JButton btnTahunDepan = buatTombolNav(">>", "Tahun Berikutnya");
        panelKanan.add(btnBulanDepan);
        panelKanan.add(btnTahunDepan);

        panelHeader.add(panelKiri, BorderLayout.WEST);
        panelHeader.add(panelTengah, BorderLayout.CENTER);
        panelHeader.add(panelKanan, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Grid hari + tanggal
        JPanel panelBody = new JPanel(new BorderLayout(0, 4));
        panelBody.setBackground(Color.WHITE);
        panelBody.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JPanel panelNamaHari = new JPanel(new GridLayout(1, 7, 4, 4));
        panelNamaHari.setBackground(Color.WHITE);
        for (int i = 0; i < NAMA_HARI.length; i++) {
            JLabel lbl = new JLabel(NAMA_HARI[i], SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(i == 0 ? new Color(198, 40, 40) : new Color(80, 90, 105));
            panelNamaHari.add(lbl);
        }

        panelTanggal = new JPanel(new GridLayout(6, 7, 4, 4));
        panelTanggal.setBackground(Color.WHITE);

        panelBody.add(panelNamaHari, BorderLayout.NORTH);
        panelBody.add(panelTanggal, BorderLayout.CENTER);
        add(panelBody, BorderLayout.CENTER);

        // Footer tombol Hari Ini & Batal
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 226, 235)));

        JButton btnHariIni = new JButton("Hari Ini");
        btnHariIni.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnHariIni.setBackground(WARNA_AKSEN);
        btnHariIni.setForeground(Color.WHITE);
        btnHariIni.setFocusPainted(false);
        btnHariIni.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnBatal.setFocusPainted(false);

        panelFooter.add(btnHariIni);
        panelFooter.add(btnBatal);
        add(panelFooter, BorderLayout.SOUTH);

        // Event listener navigasi
        btnTahunLalu.addActionListener(e -> { bulanTampil = bulanTampil.minusYears(1); renderKalender(); });
        btnBulanLalu.addActionListener(e -> { bulanTampil = bulanTampil.minusMonths(1); renderKalender(); });
        btnBulanDepan.addActionListener(e -> { bulanTampil = bulanTampil.plusMonths(1); renderKalender(); });
        btnTahunDepan.addActionListener(e -> { bulanTampil = bulanTampil.plusYears(1); renderKalender(); });

        cmbBulan.addActionListener(e -> {
            if (!sedangUpdateCombo) {
                int bulanBaru = cmbBulan.getSelectedIndex() + 1;
                bulanTampil = YearMonth.of(bulanTampil.getYear(), bulanBaru);
                renderKalender();
            }
        });

        btnHariIni.addActionListener(e -> {
            tanggalTerpilih = LocalDate.now();
            dikonfirmasi = true;
            dispose();
        });

        btnBatal.addActionListener(e -> dispose());
    }

    private JButton buatTombolNav(String teks, String tooltip) {
        JButton btn = new JButton(teks);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 75, 138));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 7, 4, 7));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void renderKalender() {
        sedangUpdateCombo = true;
        cmbBulan.setSelectedIndex(bulanTampil.getMonthValue() - 1);
        lblTahun.setText(String.valueOf(bulanTampil.getYear()));
        sedangUpdateCombo = false;

        panelTanggal.removeAll();

        LocalDate hariPertama = bulanTampil.atDay(1);
        int offsetHari = hariPertama.getDayOfWeek().getValue() % 7; // Minggu = 0, Senin = 1, ..., Sabtu = 6
        int jumlahHari = bulanTampil.lengthOfMonth();
        LocalDate hariIni = LocalDate.now();

        for (int i = 0; i < offsetHari; i++) {
            panelTanggal.add(new JLabel(""));
        }

        for (int tgl = 1; tgl <= jumlahHari; tgl++) {
            LocalDate tanggalLoop = bulanTampil.atDay(tgl);
            JButton btn = new JButton(String.valueOf(tgl));
            btn.setPreferredSize(new Dimension(38, 30));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMargin(new java.awt.Insets(2, 2, 2, 2));

            boolean adalahTerpilih = tanggalLoop.equals(tanggalTerpilih);
            boolean adalahHariIni = tanggalLoop.equals(hariIni);
            boolean adalahMinggu = (tanggalLoop.getDayOfWeek().getValue() % 7) == 0;

            if (adalahTerpilih) {
                btn.setBackground(WARNA_UTAMA);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            } else if (adalahHariIni) {
                btn.setBackground(WARNA_HARI_INI);
                btn.setForeground(WARNA_UTAMA);
                btn.setFont(new Font("SansSerif", Font.BOLD, 12));
                btn.setBorder(BorderFactory.createLineBorder(WARNA_AKSEN, 1));
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(adalahMinggu ? new Color(198, 40, 40) : new Color(40, 45, 55));
            }

            btn.addActionListener(e -> {
                tanggalTerpilih = tanggalLoop;
                dikonfirmasi = true;
                dispose();
            });
            panelTanggal.add(btn);
        }

        int sisaSel = 42 - (offsetHari + jumlahHari);
        for (int i = 0; i < sisaSel; i++) {
            panelTanggal.add(new JLabel(""));
        }

        panelTanggal.revalidate();
        panelTanggal.repaint();
    }

    /**
     * Membuka dialog kalender tepat di dekat komponen pemicu, lalu memperbarui
     * nilai JSpinner jika pengguna memilih tanggal.
     */
    public static void bukaUntukSpinner(Component parent, JSpinner spinner, Component anchor) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        Date nilaiSekarang = (Date) spinner.getValue();
        LocalDate awal = (nilaiSekarang != null)
                ? nilaiSekarang.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now();

        DatePickerDialog dialog = new DatePickerDialog(owner, awal);
        if (anchor != null && anchor.isShowing()) {
            Point lokasi = anchor.getLocationOnScreen();
            dialog.setLocation(lokasi.x - dialog.getWidth() + anchor.getWidth(), lokasi.y + anchor.getHeight() + 2);
        } else {
            dialog.setLocationRelativeTo(owner);
        }

        dialog.setVisible(true);
        if (dialog.dikonfirmasi && dialog.tanggalTerpilih != null) {
            Date hasil = Date.from(dialog.tanggalTerpilih.atStartOfDay(ZoneId.systemDefault()).toInstant());
            spinner.setValue(hasil);
        }
    }

    /**
     * Membuat ikon kalender vektor kecil (14x14) agar tampil tajam di semua OS
     * tanpa bergantung pada font emoji.
     */
    public static Icon buatIkonKalender() {
        return new Icon() {
            @Override public int getIconWidth() { return 14; }
            @Override public int getIconHeight() { return 14; }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.drawRect(x + 1, y + 3, 11, 9);
                g2.fillRect(x + 1, y + 3, 12, 3);
                g2.drawLine(x + 4, y + 1, x + 4, y + 4);
                g2.drawLine(x + 9, y + 1, x + 9, y + 4);
                g2.fillRect(x + 3, y + 8, 2, 2);
                g2.fillRect(x + 6, y + 8, 2, 2);
                g2.fillRect(x + 9, y + 8, 2, 2);
                g2.dispose();
            }
        };
    }
}
