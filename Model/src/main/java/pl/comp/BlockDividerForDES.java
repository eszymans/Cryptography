package pl.comp;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class BlockDividerForDES {

    public static List<byte[]> divideIntoBlocks(byte[] text) {
        List<byte[]> blocks = new ArrayList<>();
        int blockSize = 8;

        for (int i = 0; i < text.length; i += blockSize) {
            byte[] block = new byte[blockSize];

            int remaining = text.length - i;
            if (remaining >= blockSize) {
                System.arraycopy(text, i, block, 0, blockSize); //kopiujemy
            } else {
                System.arraycopy(text, i, block, 0, remaining);
                int padValue = blockSize - remaining;
                for (int j = remaining; j < blockSize; j++) {
                    block[j] = (byte) padValue;
                }
            }
            blocks.add(block);
        }
        return blocks;
    }

    public static byte[] removePaddings(byte[] block) {
        int paddingValue = block[7] & 0xFF;

        if (paddingValue < 1 || paddingValue > 8) {
            return block;
        }

        for (int i = 8 - paddingValue; i < 8; i++) {
            if ((block[i] & 0xFF) != paddingValue) {
                return block;
            }
        }
        return Arrays.copyOf(block, 8 - paddingValue);
    }
}
