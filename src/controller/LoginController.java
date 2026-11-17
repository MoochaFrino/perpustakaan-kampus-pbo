package controller;

import view.LoginView;
import view.MainView; 

public class LoginController {
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        
        // Pasang event pada tombol Masuk
        this.view.getBtnLogin().addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        String username = view.getUsernameInput();
        String password = view.getPasswordInput();

        // Validasi login sederhana (sesuai placeholder di LoginView)
        if ("admin".equals(username) && "admin123".equals(password)) {
            view.dispose(); 
            
            // Buka jendela utama
            MainView jendelaUtama = new MainView();
            jendelaUtama.setVisible(true);
            
        } else {
            view.tampilkanPesanError("Username atau Password salah!");
        }
    }
}