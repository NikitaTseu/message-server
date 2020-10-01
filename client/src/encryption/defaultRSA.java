package encryption;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class defaultRSA {
    private Cipher cipher = Cipher.getInstance("RSA");
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    //keyPairGenerator.initialize(4096);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();

    public defaultRSA() throws NoSuchPaddingException, NoSuchAlgorithmException {}
}
