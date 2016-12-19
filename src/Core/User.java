package Core;

/**
 * Created by PeriklisMaravelias on 12/19/15.
 */
public class User {
    private String firstname, lastname, username, password, mail;
    private byte[] privateKey;
    private byte[] publicKey;
    private byte[] sessionKey;
    private byte[] encryptedSessionKey;
    private byte[] encryptedSessionKeyPartner;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }

    public byte[] getEncryptedSessionKey() {
        return encryptedSessionKey;
    }

    public void setEncryptedSessionKey(byte[] encryptedSessionKey) {
        this.encryptedSessionKey = encryptedSessionKey;
    }

    public byte[] getEncryptedSessionKeyPartner() {
        return encryptedSessionKeyPartner;
    }

    public void setEncryptedSessionKeyPartner(byte[] encryptedSessionKeyPartner) {
        this.encryptedSessionKeyPartner = encryptedSessionKeyPartner;
    }



}
