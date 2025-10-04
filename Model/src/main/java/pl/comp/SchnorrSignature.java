package pl.comp;

import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.List;

public class SchnorrSignature {
    private BigInteger p;
    private BigInteger q;
    private BigInteger h;
    private BigInteger v;
    private BigInteger a;
    private SHA_256 sha256;
    private static final int BIGINT_LENGTH = 32;

    public BigInteger s1, s2;

    /// Konstruktor klasy dla generacji podpisu
    /// param h
    /// param p
    /// param q
    /// param a
    public SchnorrSignature(BigInteger p, BigInteger q, BigInteger h, BigInteger a) {
        this.p = p;
        this.q = q;
        this.h = h;
        this.a = a;
        this.sha256 = new SHA_256();
    }

    /// Konstruktor klasy dla weryfikacji podpisu
    /// param h
    /// param v
    /// param p
    public SchnorrSignature(BigInteger h, BigInteger v, BigInteger p) {
        this.h = h;
        this.v = v;
        this.p = p;
        this.sha256 = new SHA_256();
    }

    /// Konwertuje : wartość typu BigInteger na tablicę bajtów o ściśle określonej długośći
    /// param value : wartość BigInteger którą trzeba zamienić
    /// param length : określona długość tablicy byte
    /// return byte[] : zwaca przekształconą wartość BigInteger
    private byte[] toFixedLengthBytes(BigInteger value, int length) {
        byte[] result = new byte[length];
        byte[] bytes = value.toByteArray();
        int offset = Math.max(0, bytes.length - length);
        int destOffset = Math.max(0, length - bytes.length);
        System.arraycopy(bytes, offset, result, destOffset,
                Math.min(bytes.length, length));
        return result;
    }

    /// Generator podpisu Schnorra dla danego pliku
    /// param byte[] M : wartość binarna pliku dla którego jest tworzony podpis
    public void SchnorrSignatureCreate(byte[] M) {
        SecureRandom random = new SecureRandom();

        // Tworzenie r (losowy element z zakresu (0, q-1])
        BigInteger r;
        do {
            r = new BigInteger(q.bitLength(), random);
        } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0);

        // Obliczanie X = (h^r) mod p
        BigInteger X = h.modPow(r, p);

        // Przekształcenie X na byte aby móc przeprowadzić konkatenację M i X
        byte[] XBytes = toFixedLengthBytes(X, BIGINT_LENGTH);
        byte[] concatenated = new byte[M.length + BIGINT_LENGTH];
        System.arraycopy(M, 0, concatenated, 0, M.length);
        System.arraycopy(XBytes, 0, concatenated, M.length, BIGINT_LENGTH);

        // Obliczanie funkcji haszującej : s1 = f(MX)
        byte[] hash = sha256.sha256(concatenated);
        BigInteger s1 = new BigInteger(1, hash);

        // Obliczanie s2 = (r + a * s1) mod q
        BigInteger rPlusAHash = r.add(a.multiply(s1));
        BigInteger s2 = rPlusAHash.mod(q);

        // podpis dla M = (s1,s2)
        this.s1 = s1;
        this.s2 = s2;
    }

    /// Sprawdzanie podpisu dla dokumentu M
    /// param BigInteger s1 : przerwsza część podpisu do sprawdzenia
    /// param BigInteger s2 : druga część podpisu do sprawdzenia
    /// param BigInteger v : klucz publiczny do sprawdzenia podpisu
    /// param BigInteger h : parametr do sprawdzenia podpisu
    /// param byte[] M: dokument w formacie binarnym dla którego sprawdzamy opis
    public boolean SchnorrSignatureCheck(BigInteger s1, BigInteger s2, BigInteger v, BigInteger h, byte[] M) {
        try {
            // obliczamy wartość Z =( (h^s2) + (v^s1) )mod p
            BigInteger Z = h.modPow(s2, p).multiply(v.modPow(s1, p)).mod(p);

            // zamieniamy Z na byte aby móc przeprowadzić konkatenację M i Z
            byte[] Zbytes = toFixedLengthBytes(Z, BIGINT_LENGTH);
            byte[] concatenated = new byte[M.length + BIGINT_LENGTH];
            System.arraycopy(M, 0, concatenated, 0, M.length);
            System.arraycopy(Zbytes, 0, concatenated, M.length, BIGINT_LENGTH);

            // hash = f(MZ)
            byte[] hashBytes = sha256.sha256(concatenated);
            BigInteger hashAsInteger = new BigInteger(1, hashBytes);

            //sprawdzamy czy hash = s1, wtedy jest podpis prawidłowy
            return s1.equals(hashAsInteger);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /// Zwraca wartość s2 (druga część klucza)
    public BigInteger getS2() {
        return s2;
    }

    /// Zwraca wartość s1 (pierwsza część klucza)
    public BigInteger getS1() {
        return s1;
    }
}
