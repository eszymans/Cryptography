package pl.comp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class DESFormController {

    @FXML
    private TextField textFieldPlainText, textFieldCipherText, textFieldKey;
    @FXML
    private TextArea textAreaPlainText, textAreaCipherText;
    @FXML
    private Button buttonLoadPlainText, buttonLoadCipherText,
            buttonSavePlainText, buttonSaveCipherText,
            buttonEncrypt, buttonDecrypt, randomKey;

    private byte[] key;
    private final FileDao fileDao = new FileDao();
    private DESAlgorythm des;
    private final BlockDividerForDES divider = new BlockDividerForDES();
    private final Converter converter = new Converter();

    @FXML
    private void loadPlainTextFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            textFieldPlainText.setText(file.getAbsolutePath());
            String data = fileDao.readFile(file);
            textAreaPlainText.setText(data);
        }
    }

    @FXML
    private void savePlainTextFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            fileDao.writeFile(file, textAreaPlainText.getText());
        }
    }

    @FXML
    private void loadCipherTextFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            textFieldCipherText.setText(file.getAbsolutePath());
            String data = fileDao.readFile(file);
            textAreaCipherText.setText(data);
        }
    }

    @FXML
    private void saveCipherTextFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            String cipherTextToSave = textAreaCipherText.getText().replace("HEX:", "").replaceAll("\\s+", "");
            fileDao.writeFile(file, cipherTextToSave);
        }
    }

    @FXML
    private void encryptText() {
        StringBuilder entryptedData = new StringBuilder();
        if (!checkAndLoadKey()) {
            return;
        } else {
            String plainText = textAreaPlainText.getText();
            plainText = plainText.replace("HEX:", "").trim();

            List<byte[]> blocks = BlockDividerForDES.divideIntoBlocks(plainText.getBytes());
            for (byte[] block : blocks) {
                des = new DESAlgorythm(key, block);
                byte[] encryptedData = des.encode();
                entryptedData.append(converter.bytesToBinary(encryptedData)+" ");
            }
            textAreaCipherText.setText(entryptedData.toString());
        }
    }

    @FXML
    private void decryptText() {
        if (!checkAndLoadKey()) {
            textAreaPlainText.setText("Decryption failed: Key is missing or invalid.");
            return;
        }

        String cipherText = textAreaCipherText.getText();
        if (cipherText.isEmpty()) {
            textAreaPlainText.setText("Decryption failed: No ciphertext provided.");
            return;
        }

        String cleanedCipherText = cipherText.replace("BIN:", "").replaceAll("\\s+", "");

        if (cleanedCipherText.length() % 64 != 0) {
            textAreaPlainText.setText("Decryption failed: Invalid binary ciphertext length.");
            return;
        }

        byte[] ciphertextBytes = converter.binaryToBytes(cleanedCipherText);

        StringBuilder decryptedText = new StringBuilder();
        try {
            for (int i = 0; i < ciphertextBytes.length; i += 8) {
                byte[] block = Arrays.copyOfRange(ciphertextBytes, i, i + 8);
                des = new DESAlgorythm(key, block);
                byte[] decryptedBlock = des.decode(block);
                decryptedText.append(new String(decryptedBlock, StandardCharsets.UTF_8));
            }
            textAreaPlainText.setText(decryptedText.toString());
        } catch (Exception e) {
            textAreaPlainText.setText("Decryption failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void randomKeyLos(ActionEvent actionEvent) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);

        SecretKey keySec = keyGen.generateKey();
        byte[] keyBytes = keySec.getEncoded();
        if (keyBytes.length != 8) {
            throw new IllegalArgumentException("Generated key has an incorrect length");
        }
        key = keyBytes;

        String hexKey = converter.bytesToBinary(keyBytes);  // Przekształcamy bajty na hex
        textFieldKey.setText(hexKey);
    }


    private boolean checkAndLoadKey() {
        String inputKey = textFieldKey.getText();

        if (inputKey == null || inputKey.trim().isEmpty()) {
            textAreaCipherText.setText("No key – please provide a key.");
            return false;
        }

        try {
            byte[] decodedKey = converter.binaryToBytes(inputKey.trim());  // Używamy metody hexToBytes z Converter

            if (decodedKey.length != 8) {
                textAreaCipherText.setText("Invalid key length. DES key must be 8 bytes.");
                return false;
            }

            this.key = decodedKey;
            return true;

        } catch (IllegalArgumentException e) {
            textAreaCipherText.setText("Incorrect key format (hex).");
            return false;
        }
    }
}
