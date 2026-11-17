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
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;

public class PanelPeminjaman extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField txtIdTransaksi;
    private JComboBox<String> cmbAnggota;
    private JComboBox<String> cmbKoleksi;
    private JSpinner spnTanggalPinjam;
    private JSpinner spnTanggalKembali;
    private JCheckBox chkDiskonDenda;
    private JSpinner spnPersenDiskon;

    private JTextField txtCariTransaksi;
    private JComboBox<String> cmbFilterStatus;
    private JTable tabelTransaksi;
    private DefaultTableModel modelTabel;

    private JButton btnPinjam;
    private JButton btnKembalikan;
    private JButton btnHapusTrx;
    private JButton btnReset;
    private JButton btnCari;

    public PanelPeminjaman() {
        inisialisasiUI();
        pasangEventViewLokal();
    }

    private void inisialisasiUI() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setBackground(new Color(245, 247, 250));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 215)),
                " Form Transaksi Peminjaman & Pengembalian "));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIdTransaksi = new JTextField(12);
        cmbAnggota = new JComboBox<>();
        cmbKoleksi = new JComboBox<>();

        spnTanggalPinjam = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spnTanggalPinjam.setEditor(new JSpinner.DateEditor(spnTanggalPinjam, "yyyy-MM-dd"));

        spnTanggalKembali = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spnTanggalKembali.setEditor(new JSpinner.DateEditor(spnTanggalKembali, "yyyy-MM-dd"));

        chkDiskonDenda = new JCheckBox("Terapkan Diskon Denda (%)");
        chkDiskonDenda.setOpaque(false);
        spnPersenDiskon = new JSpinner(new SpinnerNumberModel(10, 0, 100, 5));
        spnPersenDiskon.setEnabled(false);

        // Baris 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        panelForm.add(new JLabel("ID Transaksi:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        panelForm.add(txtIdTransaksi, gbc);

        gbc.gridx = 2; gbc.weightx = 0.15;
        panelForm.add(new JLabel("Tanggal Pinjam:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        panelForm.add(spnTanggalPinjam, gbc);

        // Baris 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        panelForm.add(new JLabel("Pilih Anggota:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        panelForm.add(cmbAnggota, gbc);

        gbc.gridx = 2; gbc.weightx = 0.15;
        panelForm.add(new JLabel("Pilih Koleksi:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        panelForm.add(cmbKoleksi, gbc);

        // Baris 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15;
        panelForm.add(new JLabel("Tanggal Kembali:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        panelForm.add(spnTanggalKembali, gbc);

        JPanel panelDiskon = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelDiskon.setOpaque(false);
        panelDiskon.add(chkDiskonDenda);
        panelDiskon.add(spnPersenDiskon);
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2;
        panelForm.add(panelDiskon, gbc);
        gbc.gridwidth = 1;

        // Baris 3: Tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelTombol.setBackground(Color.WHITE);

        btnPinjam = buatTombol("Proses Peminjaman", new Color(46, 125, 50), Color.WHITE);
        btnKembalikan = buatTombol("Proses Pengembalian", new Color(230, 81, 0), Color.WHITE);
        btnHapusTrx = buatTombol("Hapus Transaksi", new Color(198, 40, 40), Color.WHITE);
        btnReset = buatTombol("Bersihkan Form", new Color(117, 117, 117), Color.WHITE);

        panelTombol.add(btnPinjam);
        panelTombol.add(btnKembalikan);
        panelTombol.add(btnHapusTrx);
        panelTombol.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panelForm.add(panelTombol, gbc);

        add(panelForm, BorderLayout.NORTH);

        // Panel JTable Transaksi
        JPanel panelTabelWrapper = new JPanel(new BorderLayout(8, 8));
        panelTabelWrapper.setBackground(Color.WHITE);
        panelTabelWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 215)),
                " Tabel Riwayat Sirkulasi Peminjaman & Denda "));

        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelCari.setBackground(Color.WHITE);
        panelCari.add(new JLabel("Cari Transaksi:"));
        txtCariTransaksi = new JTextField(20);
        panelCari.add(txtCariTransaksi);
        panelCari.add(new JLabel("Filter Status:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "DIPINJAM", "DIKEMBALIKAN"});
        panelCari.add(cmbFilterStatus);
        btnCari = buatTombol("Cari / Filter", new Color(25, 55, 109), Color.WHITE);
        panelCari.add(btnCari);

        String[] kolom = {
                "ID Trx", "Anggota", "Koleksi", "Jenis",
                "Tgl Pinjam", "Jatuh Tempo", "Tgl Kembali", "Denda", "Status"
        };
        modelTabel = new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelTransaksi = new JTable(modelTabel);
        tabelTransaksi.setRowHeight(24);
        tabelTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelTransaksi.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        panelTabelWrapper.add(panelCari, BorderLayout.NORTH);
        panelTabelWrapper.add(new JScrollPane(tabelTransaksi), BorderLayout.CENTER);
        add(panelTabelWrapper, BorderLayout.CENTER);
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
        chkDiskonDenda.addActionListener(e -> spnPersenDiskon.setEnabled(chkDiskonDenda.isSelected()));
        btnReset.addActionListener(e -> bersihkanForm());
    }

    public void bersihkanForm() {
        txtIdTransaksi.setText("");
        spnTanggalPinjam.setValue(new Date());
        spnTanggalKembali.setValue(new Date());
        chkDiskonDenda.setSelected(false);
        spnPersenDiskon.setEnabled(false);
        tabelTransaksi.clearSelection();
    }

    public boolean konfirmasiHapus(String idTrx) {
        int hasil = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus transaksi [" + idTrx + "]?",
                "Konfirmasi Hapus Transaksi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return hasil == JOptionPane.YES_OPTION;
    }

    public void tampilkanPesanInfo(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public void tampilkanPesanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Kesalahan Transaksi", JOptionPane.ERROR_MESSAGE);
    }

    // Getter untuk Controller
    public JTextField getTxtIdTransaksi() { return txtIdTransaksi; }
    public JComboBox<String> getCmbAnggota() { return cmbAnggota; }
    public JComboBox<String> getCmbKoleksi() { return cmbKoleksi; }
    public JSpinner getSpnTanggalPinjam() { return spnTanggalPinjam; }
    public JSpinner getSpnTanggalKembali() { return spnTanggalKembali; }
    public JCheckBox getChkDiskonDenda() { return chkDiskonDenda; }
    public JSpinner getSpnPersenDiskon() { return spnPersenDiskon; }
    public JTextField getTxtCariTransaksi() { return txtCariTransaksi; }
    public JComboBox<String> getCmbFilterStatus() { return cmbFilterStatus; }
    public JTable getTabelTransaksi() { return tabelTransaksi; }
    public DefaultTableModel getModelTabel() { return modelTabel; }
    public JButton getBtnPinjam() { return btnPinjam; }
    public JButton getBtnKembalikan() { return btnKembalikan; }
    public JButton getBtnHapusTrx() { return btnHapusTrx; }
    public JButton getBtnReset() { return btnReset; }
    public JButton getBtnCari() { return btnCari; }
}
