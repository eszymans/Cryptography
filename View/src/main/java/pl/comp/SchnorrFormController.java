package pl.comp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import javafx.stage.FileChooser;
import java.nio.file.Files;
import java.io.File;

import javafx.scene.control.Alert;

public class SchnorrFormController {

    @FXML private TextField fieldP, fieldQ, fieldH, fieldPrivateKey, fieldPublicKey;
    @FXML private TextArea fieldMessage, fieldVerifyMessage;
    @FXML private Label labelVerificationResult;
    @FXML private TextField textFieldPlaintext;
    @FXML private TextArea textAreaPlaintext;

    private BigInteger p, q, h, a, v;
    private BigInteger lastSignatureS1, lastSignatureS2;
    private final SecureRandom random = new SecureRandom();
    private GeneratorSchnorr generatorSchnorr = new GeneratorSchnorr();
    private SchnorrSignature schnorrSignature;

    private boolean validateParameters() {
        try {
            // Sprawdzenie czy pola nie są puste
            if (fieldP.getText().isEmpty() || fieldQ.getText().isEmpty() ||
                    fieldH.getText().isEmpty()) {
                showError("Wszystkie parametry (p, q, h) muszą być wprowadzone");
                return false;
            }

            // Konwersja parametrów
            p = new BigInteger(fieldP.getText());
            q = new BigInteger(fieldQ.getText());
            h = new BigInteger(fieldH.getText());

            // Sprawdzenie czy p i q są pierwsze
            if (!p.isProbablePrime(100)) {
                showError("Parametr p nie jest liczbą pierwszą");
                return false;
            }
            if (!q.isProbablePrime(100)) {
                showError("Parametr q nie jest liczbą pierwszą");
                return false;
            }

            // Sprawdzenie czy q jest dzielnikiem p-1
            if (!p.subtract(BigInteger.ONE).mod(q).equals(BigInteger.ZERO)) {
                showError("q nie jest dzielnikiem p-1");
                return false;
            }

            // Sprawdzenie czy h^q ≡ 1 (mod p)
            if (!h.modPow(q, p).equals(BigInteger.ONE)) {
                showError("h nie jest generatorem podgrupy rzędu q");
                return false;
            }

            // Sprawdzenie minimalnych długości
            if (q.bitLength() < 140) {
                showError("q musi być większe niż 2^140");
                return false;
            }
            if (p.bitLength() < 1024) {
                showError("p musi być większe niż 2^1024");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showError("Nieprawidłowy format liczby");
            return false;
        }
    }

    @FXML
    private void generateParams() {
        try {
            q = generatorSchnorr.generateQ();
            if (q == null || q.bitLength() < 140) {
                showError("Błąd generowania parametru q");
                return;
            }

            p = generatorSchnorr.generateP(q);
            if (p == null || p.bitLength() < 1024) {
                showError("Błąd generowania parametru p");
                return;
            }

            h = generatorSchnorr.generateH(p, q);
            if (h == null || h.equals(BigInteger.ONE)) {
                showError("Błąd generowania parametru h");
                return;
            }

            fieldP.setText(p.toString());
            fieldQ.setText(q.toString());
            fieldH.setText(h.toString());
        } catch (Exception e) {
            showError("Błąd podczas generowania parametrów: " + e.getMessage());
        }
    }

    @FXML
    private void generatePrivateKey() {
        if (!validateParameters()) return;

        try {
            a = generatorSchnorr.generateAPrivateKey(p);
            if (a == null || a.compareTo(BigInteger.ZERO) <= 0 || a.compareTo(p) >= 0) {
                showError("Błąd generowania klucza prywatnego");
                return;
            }
            fieldPrivateKey.setText(a.toString());
        } catch (Exception e) {
            showError("Błąd podczas generowania klucza prywatnego");
        }
    }

    @FXML
    private void generatePublicKey() {
        try {
            p = new BigInteger(fieldP.getText());
            h = new BigInteger(fieldH.getText());
            a = new BigInteger(fieldPrivateKey.getText());

            // Poprawne obliczenie klucza publicznego
            v = generatorSchnorr.generateVPublicKey(h, a, p);
            fieldPublicKey.setText(v.toString());
        } catch (Exception e) {
            // Dodaj alert lub komunikat o błędzie
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Błąd podczas generowania klucza publicznego!");
            alert.show();
        }
    }

    @FXML
    private void signMessage() {
        String message = fieldMessage.getText();
        if (message.isEmpty() || a == null) return;

        // Tworzymy obiekt SchnorrSignature, który będzie przechowywał dane do podpisu
        schnorrSignature = new SchnorrSignature(p, q, h, a);


        // Podpisanie wiadomości
        schnorrSignature.SchnorrSignatureCreate(message.getBytes());

        // Zapisanie podpisu
        lastSignatureS1 = schnorrSignature.getS1();
        lastSignatureS2 = schnorrSignature.getS2();
        fieldVerifyMessage.setText("(" + lastSignatureS1.toString() + ", " + lastSignatureS2.toString() + ")");

        // Pokazanie informacji o podpisie
        labelVerificationResult.setText("Podpis został wygenerowany.");
    }



    @FXML
    private void verifySignature() {
        try {
            // Pobierz wartości z pól
            p = new BigInteger(fieldP.getText());
            q = new BigInteger(fieldQ.getText());
            h = new BigInteger(fieldH.getText());
            v = new BigInteger(fieldPublicKey.getText());

            // Utwórz nowy obiekt do weryfikacji
            schnorrSignature = new SchnorrSignature(h, v, p);

            // Pobierz podpis
            String text = fieldVerifyMessage.getText();
            String[] split = text.substring(1, text.length() - 1).split(", ");
            BigInteger s1 = new BigInteger(split[0]);
            BigInteger s2 = new BigInteger(split[1]);

            // Pobierz wiadomość
            byte[] message = fieldMessage.getText().getBytes();

            // Weryfikuj
            boolean isValid = schnorrSignature.SchnorrSignatureCheck(s1, s2, v, h, message);
            labelVerificationResult.setText(isValid ? "Podpis poprawny." : "Podpis niepoprawny.");

        } catch (Exception e) {
            labelVerificationResult.setText("Błąd weryfikacji: " + e.getMessage());
        }
    }

    @FXML
    private void loadAnyFile() {
        FileDao fileDao = new FileDao();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*"));
        fileChooser.setTitle("Wybierz plik");
        File file = fileChooser.showOpenDialog(null);

        String data = fileDao.readFile(file);
        fieldMessage.setText(data);

    }

    private boolean isTextFile(File file) {
        try {
            String fileName = file.getName().toLowerCase();
            return fileName.endsWith(".txt") || fileName.endsWith(".java") || fileName.endsWith(".html") || fileName.endsWith(".xml");
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void saveSignatureToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz podpis");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                String content = "r=" + lastSignatureS1.toString() + "\n" +
                        "s=" + lastSignatureS2.toString();
                Files.writeString(file.toPath(), content);
            } catch (Exception e) {
                showError("Nie udało się zapisać podpisu.");
            }
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
