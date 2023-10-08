package com.example.lab2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModalController {
    public TextField inputArea;
    public ChoiceBox<String> nameBox;
    public TextField outputArea;
    public Button startButton;


    @FXML
    public void initialize(ObservableList<Person> people, Set<String> letters) {
        Map<String, Set<String>> peopleMap = new HashMap<>();
        people.forEach(it -> peopleMap.put(it.getName(), it.getAvailableLetters()));
        nameBox.setItems(FXCollections.observableList(peopleMap.keySet().stream().toList()));
        nameBox.setValue(nameBox.getItems().get(0));

        startButton.setOnAction(actionEvent -> {
            String name = nameBox.getValue();
            String text = inputArea.getText();
            if (validateField(text)) {
                StringBuilder output = new StringBuilder();
                char[] textArray = text.toCharArray();
                for (char symbol : textArray) {
                    String stringSymbol = String.valueOf(symbol);
                    if (letters.contains(stringSymbol)) {
                        Set<String> availableLetters = peopleMap.get(name);
                        if (availableLetters.contains(stringSymbol)) {
                            output.append(stringSymbol);
                        }
                    }
                }
                outputArea.clear();
                outputArea.appendText(output.toString());
            }
        });

    }


    private boolean validateField(String field) {
        if (field.isBlank()) {
            alert(Alert.AlertType.ERROR, "Поле не может содержать пробелы или быть пустым");
            return false;
        }
        return true;
    }


    private void alert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
