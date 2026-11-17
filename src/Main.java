import view.LoginView;
import controller.LoginController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView layarLogin = new LoginView();
            
            new LoginController(layarLogin);
            
            layarLogin.setVisible(true);
        });
    }
}