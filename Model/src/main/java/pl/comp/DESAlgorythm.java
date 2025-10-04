package pl.comp;

import java.util.Arrays;

class DESAlgorythm {
    private final byte[] key;
    private final byte[] blockOfData;
    private byte[] leftKey;
    private byte[] rightKey;

    private static final byte[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final byte[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    private static final byte[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    private static final byte[] SHIFTS = {
            1, 1, 2, 2, 2, 2, 2, 2,
            1, 2, 2, 2, 2, 2, 2, 1
    };

    private static final byte[] EXPANSION = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    private static final byte[][] S1 = {
            {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
            {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
            {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
            {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
    };

    private static final byte[][] S2 = {
            {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
            {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
            {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 9, 3, 15, 6, 2},
            {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 5, 0, 14, 9, 12}
    };

    private static final byte[][] S3 = {
            {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
            {13, 7, 0, 9, 3, 14, 15, 1, 10, 6, 12, 11, 8, 5, 4, 2},
            {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
            {1, 10, 13, 0, 14, 7, 11, 4, 9, 5, 15, 12, 8, 2, 3, 6}
    };

    private static final byte[][] S4 = {
            {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
            {13, 8, 10, 1, 3, 15, 12, 9, 7, 4, 14, 2, 0, 5, 11, 6},
            {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
            {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8}
    };

    private static final byte[][] S5 = {
            {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 14, 0, 9},
            {14, 11, 2, 12, 4, 7, 13, 1, 10, 15, 9, 3, 5, 0, 6, 8},
            {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 14, 0},
            {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
    };

    private static final byte[][] S6 = {
            {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
            {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
            {9, 14, 15, 5, 1, 3, 7, 12, 2, 8, 13, 10, 11, 6, 0, 4},
            {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
    };

    private static final byte[][] S7 = {
            {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
            {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
            {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
            {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
    };

    private static final byte[][] S8 = {
            {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
            {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
            {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
            {2, 7, 12, 9, 5, 10, 15, 4, 14, 1, 8, 13, 0, 3, 11, 6}
    };

    private static final byte[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
    };

    private static final byte[] FP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    // Ogólna funkcja służąca do permutacji - ze względu na rodzaj tablicy podanej do argumentu
    private byte[] permute(byte[] input, byte[] permutationTable, int outputSize) {
        byte[] output = new byte[(outputSize + 7) / 8];
        // pobiera bit z wejścia i ustawia go na pozycji i
        for (int i = 0; i < outputSize; i++) {
            int bit = getBit(input, permutationTable[i] - 1);
            setBit(output, i, bit);
        }
        return output;
    }

    private int getBit(byte[] data, int position) {
        // wyliczenie, w którym bajcie znajduje się dany bit i na jakiej jest pozycji
        int byteIndex = position / 8;
        int bitIndex = 7 - (position % 8);

        // przesuwa, żeby bit był na pozycje zero, a całą reszte maskuje
        return (data[byteIndex] >> bitIndex) & 1;
    }

    private void setBit(byte[] data, int position, int value) {
        int byteIndex = position / 8;
        int bitIndex = 7 - (position % 8);
        // ustawienie bitu na 1, operacja OR z przesuniętym jednynkowym bitem
        if (value == 1) {
            data[byteIndex] |= (1 << bitIndex);
        // ustawienie bitu na o, operacja AND  z negacją przesuniętego bitu
        } else {
            data[byteIndex] &= ~(1 << bitIndex);
        }
    }


    public void keyInitiaion() {
        // Permutacja PC1 i zwrócenie 56 bitowego klucza
        byte[] permutedKey = permute(key, PC1, 56);
        leftKey = Arrays.copyOfRange(permutedKey, 0, permutedKey.length / 2);
        rightKey = Arrays.copyOfRange(permutedKey, permutedKey.length / 2, permutedKey.length);
    }


    private byte[] applyPC2(byte[] left, byte[] right) {
        byte[] combined = new byte[7];

        // połączenie lewej części klucza i prawej części klucza
        System.arraycopy(left, 0, combined, 0, left.length);
        System.arraycopy(right, 0, combined, left.length, right.length);

        // Permutacja PC2, zwrócenie 48 bitów klucza
        return permute(combined, PC2, 48);
    }


    private byte[] shiftKey(byte[] keyPart, int shifts) {
        int size = keyPart.length * 8;
        byte[] shiftedKeyPart = new byte[keyPart.length];
        for (int i = 0; i < size; i++) {
            // cykliczne przesunięcie, wybranie bitu, który ma zaostać przesunięty o określoną liczbę pozycji w lewo
            int sourceIndex = (i + shifts) % size;
            int bit = getBit(keyPart, sourceIndex);

            setBit(shiftedKeyPart, i, bit);

        }
        return shiftedKeyPart;
    }




    private byte[] expandAndXor(byte[] rightData, byte[] subKey) {
        // rozszerzenie prawej części bloku danych do 48 bitów za pomocą permutacji z tablicą EXPANSION
        byte[] expanded = permute(rightData, EXPANSION, 48);

        return xorBytes(expanded, subKey);

    }

    private byte[] applySBoxes(byte[] input48bit) {
        byte[] output32bit = new byte[4];
        int outputBitIndex = 0;

        for (int i = 0; i < 8; i++) {
            int blockStartIndex = i*6;

            int row = (getBit(input48bit, blockStartIndex) << 1) | getBit(input48bit, blockStartIndex + 5);
            int col = (getBit(input48bit, blockStartIndex + 1) << 3) | (getBit(input48bit, blockStartIndex + 2) << 2) |
                    (getBit(input48bit, blockStartIndex + 3) << 1) | getBit(input48bit, blockStartIndex + 4);
            byte[][] sBox = S_BOXES[i];
            byte sBoxValue = sBox[row][col];

            // Wynikiem każdego S-bloku są 4 bity wejściowe, tworzenie 32-bitowej tablicy
            for (int j = 3; j >= 0; j--) {
                setBit(output32bit, outputBitIndex++, (sBoxValue >> j) & 1);
            }
        }
        return output32bit;
    }



    private byte[] xorBytes(byte[] data1, byte[] data2) {
        byte[] result = new byte[data1.length];
        // operacja XOR bit po bicie między bajatami z
        for (int i = 0; i < data1.length; i++) {
            result[i] = (byte) (data1[i] ^ data2[i]);
        }
        return result;
    }


    private byte[] createSubKey(int round) {
        byte[] tempLeft = Arrays.copyOf(leftKey, leftKey.length);
        byte[] tempRight = Arrays.copyOf(rightKey, rightKey.length);

        // przesuwa klucz określoną liczbę razy zależnie od powtórzenia funkcji Feistela
        for (int i = 0; i < round; i++) {
            tempLeft = shiftKey(tempLeft, SHIFTS[i]);
            tempRight = shiftKey(tempRight, SHIFTS[i]);
        }

        // zastosowanie permutacji PC2
        return applyPC2(tempLeft, tempRight);

    }

    public byte[] encode() {
        // Permutacja początkowa bloku
        byte[] permutedBlock = permute(blockOfData, IP, 64);

        // Podzielenie bloku wejściowego na dwie 32-bitowe części: prawa i lewa
        byte[] left = Arrays.copyOfRange(permutedBlock, 0, 4);
        byte[] right = Arrays.copyOfRange(permutedBlock, 4, 8);

        // Powtórzenie 16 razy funkcji Feistela
        for (int round = 0; round < 16; round++) {

            // stworzenie podklucza zależnie od obecnej rundy
            byte[] subkey = createSubKey(round + 1);

            // rozszerzenie prawej części do 48 bitów i przeprowadzenie operacji XOR z podklcuzem
            byte[] expandedAndXored = expandAndXor(right, subkey);

            // Permutacja z użyciem S-bloków
            byte[] sBoxOutput = applySBoxes(expandedAndXored);

            // Wynik permutacji poddawany permutacj z P-blokami
            byte[] permutedSBoxOutput = permute(sBoxOutput, P, 32);

            //Operacja XOR na lewej połowie danych i permutowanych s-blokach
            byte[] newRight = xorBytes(left, permutedSBoxOutput);

            // Lewa połowa danych staje się nową prawą połową, a porzednia staje się lewą
            left = right;
            right = newRight;
        }

        byte[] combined = new byte[8];

        // połączdnie prawej i lewej połowy danych
        System.arraycopy(right, 0, combined, 0, right.length);
        System.arraycopy(left, 0, combined, right.length, left.length);

        // końcowa permutacja - odwrotność permutacji początkowej
        return permute(combined, FP, 64);
    }

    public byte[] decode(byte[] ciphertext) {
        // początkowa permutacja
        byte[] permutedCiphertext = permute(ciphertext, IP, 64);

        // podzielenie na prawą i lewą połowę danych
        byte[] left = Arrays.copyOfRange(permutedCiphertext, 0, 4);
        byte[] right = Arrays.copyOfRange(permutedCiphertext, 4, 8);

        // 16 powtórzeń funkcji Feistela - dobieranie podkluczy w odwrotnej kolejności
        for (int round = 16; round > 0; round--) {
            byte[] subkey = createSubKey(round);

            byte[] expandedAndXored = expandAndXor(right, subkey);
            byte[] sBoxOutput = applySBoxes(expandedAndXored);
            byte[] permuted = permute(sBoxOutput, P, 32);
            byte[] newRight = xorBytes(left, permuted);

            left = right;
            right = newRight;
        }

        // połączenie lewej i prawej połowy danych
        byte[] combined = new byte[8];
        System.arraycopy(right, 0, combined, 0, 4);
        System.arraycopy(left, 0, combined, 4, 4);


        // permutacja końcowa
        return permute(combined, FP, 64);
    }


    // konstruktor DES algorytmu
    DESAlgorythm(byte[] key, byte[] blockOfData) {
        this.key = key;
        this.blockOfData = blockOfData;
        keyInitiaion();
    }


    // tablica przechowująca S-bloki
    private static final byte[][][] S_BOXES = {S1, S2, S3, S4, S5, S6, S7, S8};



}