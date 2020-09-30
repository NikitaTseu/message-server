package bsu.lab2.crypt;

/*
   Read algorithm in http://www.michurin.net/computer-science/rsa.html
   https://habr.com/en/post/112733/
   https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf
   https://ru.wikipedia.org/wiki/Advanced_Encryption_Standard
   https://bit.nmu.org.ua/ua/student/metod/cryptology/%D0%BB%D0%B5%D0%BA%D1%86%D0%B8%D1%8F%209.pdf
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RSA {
    private long p;
    private long q;
    private long e;
    private long phi;
    private long n;

    public RSA(int p, int q) throws CryptException {
        this.p = p;
        this.q = q;
        start();
    }

    private void start() throws CryptException {
        setN();
        setE();
        setPhi();
    }

    private long composition() {
        return getP() * getQ();
    }

    private long eulerPQ() {
        return (getP() - 1) * (getQ() - 1);
    }

    private long findExpNum() throws CryptException {
        int num = 0;
        long euler = eulerPQ();
        for (long i = euler; i > 1; i -= 1) {
            if (isPrime(i) && gcd(i, euler) == 1) {
                return i;
            }
        }
        throw new CryptException("It's bad numbers q and p");
    }

    private static boolean isPrime(long n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i < Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private long getP() {
        return p;
    }

    private void setP(long p) throws CryptException {
        if (isPrime(p)) {
            this.p = p;
        } else {
            throw new CryptException("p is not prime");
        }
    }

    private long getQ() {
        return q;
    }

    private void setQ(long q) throws CryptException {
        if (isPrime(q)) {
            this.q = q;
        } else {
            throw new CryptException("q is not prime");
        }

    }

    public Pair getOpenKey() throws CryptException {
        return new Pair(getE(), getN());
    }

    public Pair getSecretKey() {
        return new Pair(modInverse(getE(), getPhi()), getN());
    }

    public long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private long modInverse(long a, long m) {
        a = a % m;
        for (long x = 1; x < m*2; x++)
            if ((a * x) % m == 1&&x!=getE())
                return x;
        return 1;
    }

    private long getE() {
        return e;
    }

    private void setE() throws CryptException {
        this.e = findExpNum();
    }

    private long getPhi() {
        return phi;
    }

    private void setPhi() {
        this.phi = eulerPQ();
    }

    private long getN() {
        return n;
    }

    private void setN() {
        this.n = composition();
    }
    public List<BigInteger> encrypt(Pair openKey, String session){
        char[] characterStream = session.toCharArray();
        List<BigInteger> encrypted=new ArrayList<>();

        for (char a:
             characterStream) {
            BigInteger b = new BigInteger(String.valueOf((int)a));
            encrypted.add( b.pow((int) openKey.getE()).mod(BigInteger.valueOf(openKey.getN())));
        }
        return encrypted;
    }
    public String decrypt(Pair closeKey, List<BigInteger> cipher){
        StringBuilder decrypted = new StringBuilder();
        for (BigInteger a:cipher
             ) {
            decrypted.append((char)(a.pow((int) closeKey.getE()).mod(BigInteger.valueOf(closeKey.getN()))).intValue());
        }
        return decrypted.toString();
    }

}
