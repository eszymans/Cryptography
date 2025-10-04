package pl.comp;

import java.util.Arrays;

public class SHA_256 {

    // stałe K - 64 liczby wykorzystane w rundach kompresji SHA-256
    private static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
            0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
            0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
            0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
            0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
            0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
            0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
            0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
            0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    // Wartości początkowe (hash initial values) - 8 rejestrów 32-bitowych
    private static final int[] INITIAL_HASH_VALUES = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
            0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    // Funkcje pomocnicze
    // Funkcja wybierająca : jeśli x = 1, zwróć y, w przeciwnym razie z
    private static int ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    // Funkcja większościowa: zwraca wartość, która pojawia się co najmniej dwa razy
    private static int maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    // Duże sigma0 (SHA-256): rotacje i XORy
    private static int sigma0(int x) {
        return rightRotate(x, 2) ^ rightRotate(x, 13) ^ rightRotate(x, 22);
    }

    // Duże sigma1
    private static int sigma1(int x) {
        return rightRotate(x, 6) ^ rightRotate(x, 11) ^ rightRotate(x, 25);
    }

    // Małe gamma0
    private static int gamma0(int x) {
        return rightRotate(x, 7) ^ rightRotate(x, 18) ^ (x >>> 3);
    }

    // Małe gamma1
    private static int gamma1(int x) {
        return rightRotate(x, 17) ^ rightRotate(x, 19) ^ (x >>> 10);
    }

    // rotacja bitowa w prawo (logiczna)
    private static int rightRotate(int value, int bits) {
        return (value >>> bits) | (value << (32 - bits));
    }

    public byte[] sha256(byte[] message) {

        // Dodajemy padding
        byte[] paddedMessage = padMessage(message);
        //Inicjalizacja has wartości : kopiowanie początkowych wartości H
        int[] H = Arrays.copyOf(INITIAL_HASH_VALUES, 8);

        // Przetwarzanie bloków po 512-bitowych
        for (int i = 0; i < paddedMessage.length; i += 64) {
            // tablicę W - 64 słowa 32-bitowe
            byte[] block = Arrays.copyOfRange(paddedMessage, i, i + 64);

            // tablica W - 64 słowa 32 - bitowe
            int[] W = prepareMessageSchedule(block);

            // inicjalizacja tymczasowych zmiennych
            int a = H[0];
            int b = H[1];
            int c = H[2];
            int d = H[3];
            int e = H[4];
            int f = H[5];
            int g = H[6];
            int h = H[7];

            // Główna pętla kompresji : 64 rund
            for (int t = 0; t < 64; t++) {
                int T1 = h + sigma1(e) + ch(e, f, g) + K[t] + W[t];
                int T2 = sigma0(a) + maj(a, b, c);

                h = g;
                g = f;
                f = e;
                e = d + T1;
                d = c;
                c = b;
                b = a;
                a = T1 + T2;
            }

            // Aktualizacja wartości hash
            H[0] += a;
            H[1] += b;
            H[2] += c;
            H[3] += d;
            H[4] += e;
            H[5] += f;
            H[6] += g;
            H[7] += h;
        }

        // Konwersja końcowego hasha na tablicę bajtów
        // 8 x 32bity = 256 bitów = 32 bajtów
        byte[] hash = new byte[32];
        for (int i = 0; i < 8; i++) {
            hash[i * 4] = (byte) (H[i] >>> 24);
            hash[i * 4 + 1] = (byte) (H[i] >>> 16);
            hash[i * 4 + 2] = (byte) (H[i] >>> 8);
            hash[i * 4 + 3] = (byte) H[i];
        }

        return hash;
    }

    /// Funkcja dodaje padding do wiadomości aby rozmiar się zgadzał
    private static byte[] padMessage(byte[] message) {
        int originalLength = message.length;

        // Ile bajtów zajmuje ogon wiadomości
        int tailLength = originalLength % 64;

        // obliczanie długości dopełnienia
        //dlugosc ≡ 56 mod 64
        int padLength = 64 - ((tailLength + 9) % 64);
        if (padLength == 64) padLength = 0;

        // nowa tablica : oryginał + 1 bajt + padding + 8 bajtów długości
        byte[] padded = new byte[originalLength + padLength + 9];
        System.arraycopy(message, 0, padded, 0, originalLength);

        // Dodaj bit '1' (0x80 = 10000000)
        padded[originalLength] = (byte) 0x80;

        // Dodaj długość oryginalnej wiadomości w bitach
        long lengthInBits = (long) originalLength * 8;
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) (lengthInBits >>> (56 - 8 * i));
        }

        return padded;
    }

    /// Przygotowanie tablicy W
    private static int[] prepareMessageSchedule(byte[] block) {
        int[] W = new int[64];

        // Konwersja bloku na pierwsze 16 słów z bloku ( każde 4 bajty -> 1 int)
        for (int i = 0; i < 16; i++) {
            W[i] = ((block[i * 4] & 0xFF) << 24) |
                    ((block[i * 4 + 1] & 0xFF) << 16) |
                    ((block[i * 4 + 2] & 0xFF) << 8) |
                    (block[i * 4 + 3] & 0xFF);
        }

        // Rozszerzenie do 64 słów
        for (int i = 16; i < 64; i++) {
            W[i] = gamma1(W[i-2]) + W[i-7] + gamma0(W[i-15]) + W[i-16];
        }

        return W;
    }

}