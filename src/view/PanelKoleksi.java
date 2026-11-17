package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Year;

public class PanelKoleksi extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField txtIdKoleksi;
    private JComboBox<String> cmbJenisKoleksi;
    private JTextField txtJudul;
    private JTextField txtPenulis;
    private JSpinner spnTahunTerbit;
    private JSpinner spnStok;
    private JCheckBox chkReferensiSaja;

    private JLabel lblAtributKhusus1;
    private JTextField txtAtributKhusus1;
    private JLabel lblAtributKhusus2;
    private JSpinner spnAtributKhusus2;

    private JTextField txtCari;
    private JComboBox<String> cmbFilterJenis;
    private JTable tabelKoleksi;
    private DefaultTableModel modelTabel;

    private JButton btnTambah;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnReset;
    private JButton btnCari;

    public PanelKoleksi() {
        inisialisasiUI();
        pasangEventViewLokal();
    }

    private void inisialisasiUI() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setBackground(new Color(245, 247, 250));

        // Panel Form Input
        JPanel panelFormWrapper = new JPanel(new BorderLayout());
        panelFormWrapper.setBackground(Color.WHITE);
        panelFormWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 215)),
                " Form Data Koleksi (Buku / Jurnal / Skripsi) "));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIdKoleksi = new JTextField(12);
        cmbJenisKoleksi = new JComboBox<>(new String[]{"Buku", "Jurnal", "Skripsi"});
        txtJudul = new JTextField(20);
        txtPenulis = new JTextField(20);

        int tahunIni = Year.now().getValue();
        spnTahunTerbit = new JSpinner(new SpinnerNumberModel(tahunIni, 1900, tahunIni + 1, 1));
        spnTahunTerbit.setEditor(new JSpinner.NumberEditor(spnTahunTerbit, "#"));

        spnStok = new JSpinner(new SpinnerNumberModel(1, 0, 500, 1));
        chkReferensiSaja = new JCheckBox("Koleksi Referensi (Hanya Baca di Tempat)");
        chkReferensiSaja.setOpaque(false);

        lblAtributKhusus1 = new JLabel("Nomor ISBN:");
        txtAtributKhusus1 = new JTextField(16);
        lblAtributKhusus2 = new JLabel("Jumlah Halaman:");
        spnAtributKhusus2 = new JSpinner(new SpinnerNumberModel(200, 1, 5000, 1));
        spnAtributKhusus2.setEditor(new JSpinner.NumberEditor(spnAtributKhusus2, "#"));

        tambahBarisForm(panelForm, gbc, 0, 0, "ID Koleksi:", txtIdKoleksi);
        tambahBarisForm(panelForm, gbc, 0, 2, "Jenis Koleksi:", cmbJenisKoleksi);
        tambahBarisForm(panelForm, gbc, 1, 0, "Judul Koleksi:", txtJudul);
        tambahBarisForm(panelForm, gbc, 1, 2, "Penulis / Pengarang:", txtPenulis);
        tambahBarisForm(panelForm, gbc, 2, 0, "Tahun Terbit:", spnTahunTerbit);
        tambahBarisForm(panelForm, gbc, 2, 2, "Jumlah Stok:", spnStok);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.15;
        panelForm.add(lblAtributKhusus1, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        panelForm.add(txtAtributKhusus1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.15;
        panelForm.add(lblAtributKhusus2, gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.35;
        panelForm.add(spnAtributKhusus2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        panelForm.add(chkReferensiSaja, gbc);
        gbc.gridwidth = 1;

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelTombol.setBackground(Color.WHITE);

        btnTambah = buatTombol("Tambah Baru", new Color(46, 125, 50), Color.WHITE);
        btnUbah = buatTombol("Simpan Perubahan", new Color(25, 118, 210), Color.WHITE);
        btnHapus = buatTombol("Hapus Koleksi", new Color(198, 40, 40), Color.WHITE);
        btnReset = buatTombol("Bersihkan Form", new Color(117, 117, 117), Color.WHITE);

        panelTombol.add(btnTambah);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnReset);

        panelFormWrapper.add(panelForm, BorderLayout.CENTER);
        panelFormWrapper.add(panelTombol, BorderLayout.SOUTH);
        add(panelFormWrapper, BorderLayout.NORTH);

        // Panel JTable Koleksi (kosong, siap diisi Controller)
        JPanel panelTabelWrapper = new JPanel(new BorderLayout(8, 8));
        panelTabelWrapper.setBackground(Color.WHITE);
        panelTabelWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 215)),
                " Daftar Koleksi Perpustakaan "));

        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelCari.setBackground(Color.WHITE);
        panelCari.add(new JLabel("Cari Judul / ID:"));
        txtCari = new JTextField(22);
        panelCari.add(txtCari);
        panelCari.add(new JLabel("Filter Jenis:"));
        cmbFilterJenis = new JComboBox<>(new String[]{"Semua", "Buku", "Jurnal", "Skripsi"});
        panelCari.add(cmbFilterJenis);
        btnCari = buatTombol("Cari / Filter", new Color(25, 55, 109), Color.WHITE);
        panelCari.add(btnCari);

        String[] kolom = {
                "ID Koleksi", "Jenis", "Judul Koleksi", "Penulis",
                "Tahun", "Stok", "Status", "Detail Spesifik"
        };
        modelTabel = new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelKoleksi = new JTable(modelTabel);
        tabelKoleksi.setRowHeight(24);
        tabelKoleksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelKoleksi.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        panelTabelWrapper.add(panelCari, BorderLayout.NORTH);
        panelTabelWrapper.add(new JScrollPane(tabelKoleksi), BorderLayout.CENTER);
        add(panelTabelWrapper, BorderLayout.CENTER);
    }

    private void tambahBarisForm(JPanel panel, GridBagConstraints gbc, int row, int colStart,
            String labelText, Component comp) {
        gbc.gridx = colStart;
        gbc.gridy = row;
        gbc.weightx = 0.15;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = colStart + 1;
        gbc.weightx = 0.35;
        panel.add(comp, gbc);
    }

    private JButton buatTombol(String teks, Color bg, Color fg) {
        JButton btn = new JButton(teks);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    private void pasangEventViewLokal() {
        cmbJenisKoleksi.addActionListener(e -> sesuaikanLabelAtributKhusus());
        btnReset.addActionListener(e -> bersihkanForm());
    }

    private void sesuaikanLabelAtributKhusus() {
        String jenis = (String) cmbJenisKoleksi.getSelectedItem();
        if ("Buku".equalsIgnoreCase(jenis)) {
            lblAtributKhusus1.setText("Nomor ISBN:");
            lblAtributKhusus2.setText("Jumlah Halaman:");
            spnAtributKhusus2.setValue(200);
        } else if ("Jurnal".equalsIgnoreCase(jenis)) {
            lblAtributKhusus1.setText("Nomor ISSN:");
            lblAtributKhusus2.setText("Volume / Edisi:");
            spnAtributKhusus2.setValue(1);
        } else {
            lblAtributKhusus1.setText("Program Studi:");
            lblAtributKhusus2.setText("Tahun Wisuda:");
            spnAtributKhusus2.setValue(Year.now().getValue());
        }
    }

    public void bersihkanForm() {
        txtIdKoleksi.setText("");
        txtIdKoleksi.setEditable(true);
        cmbJenisKoleksi.setSelectedIndex(0);
        txtJudul.setText("");
        txtPenulis.setText("");
        spnTahunTerbit.setValue(Year.now().getValue());
        spnStok.setValue(1);
        chkReferensiSaja.setSelected(false);
        txtAtributKhusus1.setText("");
        sesuaikanLabelAtributKhusus();
        tabelKoleksi.clearSelection();
    }

    public boolean konfirmasiHapus(String idKoleksi, String judul) {
        int hasil = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus koleksi [" + idKoleksi + "] " + judul + "?",
                "Konfirmasi Hapus Koleksi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return hasil == JOptionPane.YES_OPTION;
    }

    public void tampilkanPesanInfo(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public void tampilkanPesanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
    }

    // Getter untuk Controller
    public JTextField getTxtIdKoleksi() { return txtIdKoleksi; }
    public JComboBox<String> getCmbJenisKoleksi() { return cmbJenisKoleksi; }
    public JTextField getTxtJudul() { return txtJudul; }
    public JTextField getTxtPenulis() { return txtPenulis; }
    public JSpinner getSpnTahunTerbit() { return spnTahunTerbit; }
    public JSpinner getSpnStok() { return spnStok; }
    public JCheckBox getChkReferensiSaja() { return chkReferensiSaja; }
    public JTextField getTxtAtributKhusus1() { return txtAtributKhusus1; }
    public JSpinner getSpnAtributKhusus2() { return spnAtributKhusus2; }
    public JTextField getTxtCari() { return txtCari; }
    public JComboBox<String> getCmbFilterJenis() { return cmbFilterJenis; }
    public JTable getTabelKoleksi() { return tabelKoleksi; }
    public DefaultTableModel getModelTabel() { return modelTabel; }
    public JButton getBtnTambah() { return btnTambah; }
    public JButton getBtnUbah() { return btnUbah; }
    public JButton getBtnHapus() { return btnHapus; }
    public JButton getBtnReset() { return btnReset; }
    public JButton getBtnCari() { return btnCari; }
}
