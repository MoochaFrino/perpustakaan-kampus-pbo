package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkTampilkanPassword;
    private JButton btnLogin;
    private JButton btnReset;

    public LoginView() {
        inisialisasiUI();
        pasangEventViewLokal();
    }

    private void inisialisasiUI() {
        setTitle("Login - Sistem Informasi Perpustakaan Kampus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(460, 390));
        setSize(480, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel panelHeader = new JPanel(new BorderLayout(0, 6));
        panelHeader.setBackground(new Color(25, 55, 109));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblJudul = new JLabel("SISTEM PERPUSTAKAAN KAMPUS", SwingConstants.CENTER);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblJudul.setForeground(Color.WHITE);

        JLabel lblSubJudul = new JLabel("Silakan masuk untuk mengelola koleksi, anggota, dan peminjaman", SwingConstants.CENTER);
        lblSubJudul.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubJudul.setForeground(new Color(200, 220, 255));

        panelHeader.add(lblJudul, BorderLayout.CENTER);
        panelHeader.add(lblSubJudul, BorderLayout.SOUTH);
        add(panelHeader, BorderLayout.NORTH);

        // Form Login dengan GridBagLayout
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(245, 247, 250));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panelForm.add(lblUser, gbc);

        txtUsername = new JTextField("admin", 18);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panelForm.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panelForm.add(lblPass, gbc);

        txtPassword = new JPasswordField("admin123", 18);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        panelForm.add(txtPassword, gbc);

        chkTampilkanPassword = new JCheckBox("Tampilkan Password");
        chkTampilkanPassword.setOpaque(false);
        chkTampilkanPassword.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelForm.add(chkTampilkanPassword, gbc);

        JPanel panelTombol = new JPanel(new GridBagLayout());
        panelTombol.setOpaque(false);
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(6, 4, 6, 4);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.weightx = 1.0;

        btnLogin = new JButton("Masuk");
        btnLogin.setBackground(new Color(25, 118, 210));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReset = new JButton("Reset Form");
        btnReset.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnReset.setFocusPainted(false);

        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        panelTombol.add(btnLogin, gbcBtn);
        gbcBtn.gridx = 1;
        gbcBtn.gridy = 0;
        panelTombol.add(btnReset, gbcBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelForm.add(panelTombol, gbc);

        add(panelForm, BorderLayout.CENTER);
    }

    private void pasangEventViewLokal() {
        chkTampilkanPassword.addActionListener(e -> toggleVisibilitasPassword());
        btnReset.addActionListener(e -> bersihkanForm());
    }

    private void toggleVisibilitasPassword() {
        if (chkTampilkanPassword.isSelected()) {
            txtPassword.setEchoChar((char) 0);
        } else {
            txtPassword.setEchoChar('\u2022');
        }
    }

    public void bersihkanForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocusInWindow();
    }

    public void tampilkanPesanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Login Gagal", JOptionPane.ERROR_MESSAGE);
    }

    // Getter untuk dihubungkan dengan Controller
    public String getUsernameInput() {
        return txtUsername.getText().trim();
    }

    public String getPasswordInput() {
        return new String(txtPassword.getPassword());
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }
}
