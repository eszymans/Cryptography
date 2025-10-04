package pl.comp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFormController {

    @FXML
    private Button desButton;

    @FXML
    private Button schnorrButton;

    @FXML
    private void handleDESAction(ActionEvent event) {
        openWindow("/pl.comp/DESForm.fxml", "DES - Implementacja algorytmu DES", "DES");
    }

    @FXML
    private void handleSchnorrAction(ActionEvent event) {
        openWindow("/pl.comp/SchnorrForm.fxml", "Schnorr - Implementacja algorytmu Schnorra", "Schnorr");
    }

    @FXML
    private void openWindow(String path, String title, String context) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd podczas ładowania okna" + context);
            alert.setContentText("Wystąpił problem podczas ładowania okna" + context);
            alert.showAndWait();
        }
    }

    public void initialize() {
        desButton.setOnAction(this::handleDESAction);
        schnorrButton.setOnAction(this::handleSchnorrAction);
    }
}
