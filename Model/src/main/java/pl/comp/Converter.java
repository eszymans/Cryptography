package pl.comp;

public class Converter {

    // Helper method to convert byte array to hexadecimal string
    public String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));  // Konwertujemy bajty na format hex
        }
        return hexString.toString();
    }

    // Helper method to convert hexadecimal string back to byte array
    public byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);  // Parsowanie hex na bajty
        }
        return bytes;
    }


    public String bytesToBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String bin = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            sb.append(bin);
        }
        return sb.toString();
    }

    public byte[] binaryToBytes(String binary) {
        int length = binary.length();
        byte[] bytes = new byte[length / 8];
        for (int i = 0; i < length; i += 8) {
            String byteString = binary.substring(i, i + 8);
            bytes[i / 8] = (byte) Integer.parseInt(byteString, 2);
        }
        return bytes;
    }
}
