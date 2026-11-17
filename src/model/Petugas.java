package model;

public class Petugas extends Orang {
    private String username;
    private String password;

    public Petugas(int id, String nama, String username, String password) {
        super(id, nama);
        setUsername(username);
        setPassword(password);
    }

    @Override public String getPeran() { return "Petugas"; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("Password minimal 4 karakter.");
        this.password = password;
    }

    public boolean cekLogin(String user, String pass) {
        return username.equals(user) && password.equals(pass);
    }
}