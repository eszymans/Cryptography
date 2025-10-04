package pl.comp;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GeneratorSchnorr {

    /// Generator liczby Q (liczba pierwsza, większa niż 2^140)
    /// return : BigInteger q
    public BigInteger generateQ(){
        SecureRandom random = new SecureRandom();
        BigInteger q = null;
        int qBitLength = 160;
        do {
            q = new BigInteger(qBitLength, 100, random);
        } while (q.bitLength() < qBitLength);
        return q;
    }

    /// Generator liczby p (liczba pierwsza, większa niż 2^512, taka  ze (p-1)/q należy do liczb naturalnych)
    /// param BigInteger q : liczba q na podstawie której oblicza się p
    /// return BigInteger p
    public BigInteger generateP(BigInteger q) {
        SecureRandom random = new SecureRandom();
        BigInteger p;
        int pBitLength = 1024;

        for (int attempt = 0; attempt < 10000; attempt++) {
            BigInteger m;
            do {
                m = new BigInteger(pBitLength - q.bitLength(), random);
            } while (m.bitLength() < (pBitLength - q.bitLength()));

            p = q.multiply(m).add(BigInteger.ONE);

            //sprawdzamy czy p jest liczbą pierwszą i ma odpowiednią długość
            if (p.bitLength() >= pBitLength && p.isProbablePrime(100)) {
                return p;
            }
        }
        throw new RuntimeException("Nie udało się wygenerować odpowiedniego p");
    }

    /// Generator liczby h (nie może być równa jeden oraz h^q≡1(mod p))
    /// param BigInteger p : liczba p na podstawie ktorej sie oblcza h
    /// param BigInteger q : liczba q na podstawie ktorej sie oblcza h
    /// return BigInteger : liczba h
    public BigInteger generateH(BigInteger p, BigInteger q) {
        SecureRandom random = new SecureRandom();
        BigInteger h;

        BigInteger m = p.subtract(BigInteger.ONE).divide(q);

        do {
            BigInteger randomValue;
            do {
                randomValue = new BigInteger(p.bitLength(), random);
            } while (randomValue.compareTo(BigInteger.ONE) <= 0 ||
                    randomValue.compareTo(p.subtract(BigInteger.ONE)) >= 0);
            h = randomValue.modPow(m, p);
        } while (h.equals(BigInteger.ONE));

        return h;
    }

    /// Gerator liczby v, czyli klucz publiczny (v = ((h^a)^(-1))mod p)
    /// param BigInteger h : liczba h na podstawie ktorej sie oblcza v
    /// param BigInteger a : liczba a ( klucz prywatny)
    /// param BigInteger q : liczba q na podstawie ktorej sie oblcza v
    /// return BigInteger : liczba v, czyli klucz publiczny
    public BigInteger generateVPublicKey(BigInteger h, BigInteger a, BigInteger p) {
        BigInteger v ;
        v = h.modPow(a.negate(), p);
        return v;
    }

    /// Generator liczby a, klucz prywatny (losowa liczba taka, że 1 < a < p-1)
    /// param BigInteger p : liczba p na podstawie której się oblicza a
    public BigInteger generateAPrivateKey(BigInteger p) {
        BigInteger a;
        SecureRandom random = new SecureRandom();
        a = new BigInteger(p.bitLength() - 1, random).mod(p);
        return a;
    }

}
