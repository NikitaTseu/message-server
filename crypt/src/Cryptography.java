import bsu.lab2.crypt.CryptException;
import bsu.lab2.crypt.RSA;

import java.util.stream.IntStream;
import java.util.stream.Stream;

class Cryptography{
    public static void main(String[] args) throws CryptException {
        RSA rsa=new RSA(73,79);
        String session = "var";
        System.out.println(rsa.getOpenKey());
        System.out.println(rsa.getSecretKey());
        System.out.println();
        System.out.println(rsa.decrypt(rsa.getSecretKey(), rsa.encrypt(rsa.getOpenKey(), session)));
    }
}
