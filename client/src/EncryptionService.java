import encryption.AES;
import encryption.CryptException;
import encryption.RSA;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class EncryptionService {
    private byte[] sessionKey;
    private RSA rsa;

    {
        try {
            rsa = new RSA(73, 79);
        } catch (CryptException e) {
            e.printStackTrace();
        }
    }

    public void setSessionKey(byte[] key){
        this.sessionKey = new byte[16];
        System.arraycopy(key, 0, this.sessionKey, 0, this.sessionKey.length);
    }

    public byte[] encrypt(String strToEncrypt)
    {
        return AES.encrypt(strToEncrypt, sessionKey);
    }

    public String decrypt(byte[] encryptedIvTextBytes)
    {
        return AES.decrypt(encryptedIvTextBytes, sessionKey);
    }

    public byte[] decryptRSA(byte[] key){
        return this.rsa.decrypt(this.rsa.getSecretKey(), key);
    }

    public byte[] getPublicKeyRSA(){
        String s = "";
        try {
            s = String.valueOf(this.rsa.getOpenKey().getE()) + "," + String.valueOf(this.rsa.getOpenKey().getN());
        } catch (CryptException e) {
            e.printStackTrace();
        }
        return s.getBytes(StandardCharsets.UTF_8);
    }
}