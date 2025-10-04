package pl.comp;

import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class FileDao {

    private final Converter converter = new Converter();

    public String readFile(File file) {
        try {
            byte[] data = Files.readAllBytes(file.toPath());
                return converter.bytesToBinary(data);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    public void writeFile(File file, String text) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] bytes;
                text = text.replaceAll("\\s+", "");
                bytes = converter.binaryToBytes(text);
                fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isTextFile(byte[] bytes) {
        int nonTextCount = 0;
        for (byte b : bytes) {
            if (b < 0x09 || (b > 0x0D && b < 0x20 && b != 0x7F)) {
                nonTextCount++;
            }
        }
        return (double) nonTextCount / bytes.length < 0.1;
    }
}
