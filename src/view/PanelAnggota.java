package view;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PanelAnggota extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField txtIdAnggota;
    private JTextField txtNimNip;
    private JTextField txtNama;
    private JComboBox<String> cmbKategori;
    private JTextField txtJurusan;
    private JRadioButton rbLakiLaki;
    private JRadioButton rbPerempuan;
    private JCheckBox chkStatusAktif;

    private JTextField txtCariAnggota;
    private JTable tabelAnggota;
    private DefaultTableModel modelTabel;

    private JButton btnTambah;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnReset;
    private JButton btnCari;

    public PanelAnggota() {
        inisialisasiUI();
        pasangEventViewLokal();
    }

    private void inisialisasiUI() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setBackground(new Color(245, 247, 250));

        JPanel panelFormWrapper = new JPanel(new BorderLayout());
        panelFormWrapper.setBackground(Color.WHITE);
        panelFormWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 215)),
                " Form Data Anggota Perpustakaan (Mahasiswa / Dosen / Staf) "));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIdAnggota = new JTextField(12);
        txtNimNip = new JTextField(16);
        txtNama = new JTextField(20);
        cmbKategori = new JComboBox<>(new String[]{"Mahasiswa", "Dosen", "Staf"});
        txtJurusan = new JTextField(20);

        rbLakiLaki = new JRadioButton("Laki-laki", true);
        rbPerempuan = new JRadioButton("Perempuan");
        rbLakiLaki.setOpaque(false);
        rbPerempuan.setOpaque(false);
        ButtonGroup bgGender = new ButtonGroup();
        bgGender.add(rbLakiLaki);
        bgGender.add(rbPerempuan);

        JPanel panelGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelGender.setOpaque(false);
        panelGender.add(rbLakiLaki);
        panelGender.add(rbPerempuan);

        chkStatusAktif = new JCheckBox("Status Keanggotaan Aktif", true);
        chkStatusAktif.setOpaque(false);

        tambahBarisForm(panelForm, gbc, 0, 0, "ID Anggota:", txtIdAnggota);
        tambahBarisForm(panelForm, gbc, 0, 2, "NIM / NIP:", txtNimNip);
        tambahBarisForm(panelForm, gbc, 1, 0, "Nama Lengkap:", txtNama);
        tambahBarisForm(panelForm, gbc, 1, 2, "Kategori Civitas:", cmbKategori);
        tambahBarisForm(panelForm, gbc, 2, 0, "Program Studi / Unit:", txtJurusan);
        tambahBarisForm(panelForm, gbc, 2, 2, "Jenis Kelamin:", panelGender);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panelForm.add(chkStatusAktif, gbc);
        gbc.gridwidth = 1;

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelTombol.setBackground(Color.WHITE);

        btnTambah = buatTombol("Tambah Anggota", new Color(46, 125, 50), Color.WHITE);
        btnUbah = buatTombol("Simpan Perubahan", new Color(25, 118, 210), Color.WHITE);
        btnHapus = buatTombol("Hapus Anggota", new Color(198, 40, 40), Color.WHITE);
        btnReset = buatTombol("Bersihkan Form", new Color(117, 117, 117), Color.WHITE);

        panelTombol.add(btnTambah);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnReset);

        panelFormWrapper.add(panelForm, BorderLayout.CENTER);
        panelFormWrapper.add(panelTombol, BorderLayout.SOUTH);
        add(panelFormWrapper, BorderLayout.NORTH);

        // Panel JTable Anggota
        JPanel panelTabelWrapper = new JPanel(new BorderLayout(8, 8));
        panelTabelWrapper.setBackground(Color.WHITE);
        panelTabelWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 215)),
                " Tabel Data Anggota Perpustakaan "));

        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelCari.setBackground(Color.WHITE);
        panelCari.add(new JLabel("Cari Anggota:"));
        txtCariAnggota = new JTextField(25);
        panelCari.add(txtCariAnggota);
        btnCari = buatTombol("Cari Anggota", new Color(25, 55, 109), Color.WHITE);
        panelCari.add(btnCari);

        String[] kolom = {
                "ID Anggota", "NIM / NIP", "Nama Lengkap", "Kategori",
                "Program Studi / Unit", "Jenis Kelamin", "Status"
        };
        modelTabel = new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelAnggota = new JTable(modelTabel);
        tabelAnggota.setRowHeight(24);
        tabelAnggota.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelAnggota.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        panelTabelWrapper.add(panelCari, BorderLayout.NORTH);
        panelTabelWrapper.add(new JScrollPane(tabelAnggota), BorderLayout.CENTER);
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
        btnReset.addActionListener(e -> bersihkanForm());
    }

    public void bersihkanForm() {
        txtIdAnggota.setText("");
        txtIdAnggota.setEditable(true);
        txtNimNip.setText("");
        txtNama.setText("");
        cmbKategori.setSelectedIndex(0);
        txtJurusan.setText("");
        rbLakiLaki.setSelected(true);
        chkStatusAktif.setSelected(true);
        tabelAnggota.clearSelection();
    }

    public boolean konfirmasiHapus(String idAnggota, String nama) {
        int hasil = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus anggota [" + idAnggota + "] " + nama + "?",
                "Konfirmasi Hapus Anggota",
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
    public JTextField getTxtIdAnggota() { return txtIdAnggota; }
    public JTextField getTxtNimNip() { return txtNimNip; }
    public JTextField getTxtNama() { return txtNama; }
    public JComboBox<String> getCmbKategori() { return cmbKategori; }
    public JTextField getTxtJurusan() { return txtJurusan; }
    public JRadioButton getRbLakiLaki() { return rbLakiLaki; }
    public JRadioButton getRbPerempuan() { return rbPerempuan; }
    public JCheckBox getChkStatusAktif() { return chkStatusAktif; }
    public JTextField getTxtCariAnggota() { return txtCariAnggota; }
    public JTable getTabelAnggota() { return tabelAnggota; }
    public DefaultTableModel getModelTabel() { return modelTabel; }
    public JButton getBtnTambah() { return btnTambah; }
    public JButton getBtnUbah() { return btnUbah; }
    public JButton getBtnHapus() { return btnHapus; }
    public JButton getBtnReset() { return btnReset; }
    public JButton getBtnCari() { return btnCari; }
}
