package com.example.lab2;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static java.util.List.*;


public class HelloController {

    @FXML
    public TableView<Person> table;
    public Button addNameButton;
    public TextField nameField;
    public Button addLetterButton;
    public TextField charField;
    public Button startButton;
    public Button deleteNameButton;
    public Button deleteLetterButton;
    public TextField fromField;
    public TextField toField;
    public TextField rightsField;
    public Button grantButton;
    public Button createButton;
    public Button removeButton;

    private Set<String> letters = new HashSet<>(of("A", "B", "C"));

    @FXML
    public void initialize() {
        ObservableList<Person> tableData = FXCollections.observableArrayList(
                new Person("Петр", new HashSet<>(of("A", "B", "C"))),
                new Person("Иван", new HashSet<>(of("A", "C")))
        );

        table.getItems().addAll(tableData);
        table.setEditable(true);
        TableColumn<Person, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(nameColumn);
        letters.forEach(it -> {
            TableColumn<Person, Boolean> letterColumn = new TableColumn<>(it);
            letterColumn.setCellValueFactory(cellData ->
                    new SimpleBooleanProperty(cellData.getValue().getAvailableLetters().contains(it)));
            letterColumn.setCellFactory(p -> {
                CheckBox checkBox = new CheckBox();
                TableCell<Person, Boolean> tableCell = new TableCell<>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {

                        super.updateItem(item, empty);
                        if (empty || item == null)
                            setGraphic(null);
                        else {
                            setGraphic(checkBox);
                            checkBox.setSelected(item);
                        }
                    }
                };
                checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    tableCell.getTableRow().getItem().updateSet(!checkBox.isSelected(), it);
                });
                tableCell.setAlignment(Pos.CENTER);
                tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                return tableCell;
            });
            table.getColumns().add(letterColumn);
        });


        addNameButton.setOnAction(actionEvent -> {
            String name = nameField.getText();
            if (validateField(name, false)) {
                Person person = new Person(name, new HashSet<>());
                table.getItems().add(person);
            }
        });

        deleteNameButton.setOnAction(actionEvent -> {
            String name = nameField.getText();
            try {
                if (validateField(name, false)) {
                    Person data = table.getItems().stream()
                            .filter(it -> it.getName().equals(name))
                            .findFirst().orElseThrow(() -> new RuntimeException("Субъект не найден"));
                    table.getItems().remove(data);
                }
            } catch (RuntimeException e) {
                alert(e.getMessage());
            }
        });

        deleteLetterButton.setOnAction(actionEvent -> {
            String letter = charField.getText();
            try {
                if (validateField(letter, true)) {
                    TableColumn<Person, ?> column = table.getColumns().stream()
                            .filter(it -> it.getText().equals(letter))
                            .findFirst().orElseThrow(() -> new RuntimeException("Субъект не найден"));
                    table.getColumns().remove(column);
                    table.getItems().forEach(it -> {
                        it.getAvailableLetters().remove(letter);
                    });
                }
            } catch (RuntimeException e) {
                alert(e.getMessage());
            }
        });

        addLetterButton.setOnAction(actionEvent -> {
            String letter = charField.getText();
            if (validateField(letter, true)) {
                TableColumn<Person, Boolean> column = new TableColumn<>(letter);
                column.setCellValueFactory(cellData ->
                        new SimpleBooleanProperty(cellData.getValue().getAvailableLetters().contains(letter)));
                column.setCellFactory(p -> {
                    CheckBox checkBox = new CheckBox();
                    TableCell<Person, Boolean> tableCell = new TableCell<>() {
                        @Override
                        protected void updateItem(Boolean item, boolean empty) {

                            super.updateItem(item, empty);
                            if (empty || item == null)
                                setGraphic(null);
                            else {
                                setGraphic(checkBox);
                                checkBox.setSelected(item);
                            }
                        }
                    };
                    checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                        tableCell.getTableRow().getItem().updateSet(!checkBox.isSelected(), letter);
                    });
                    tableCell.setAlignment(Pos.CENTER);
                    tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                    return tableCell;
                });
                letters.add(letter);
                table.getColumns().add(column);
            }
        });

        grantButton.setOnAction(actionEvent -> {
            String from = fromField.getText();
            String to = toField.getText();
            if (validateField(from, false) && validateField(to, false)) {
                if (validateField(rightsField.getText(), false)) {
                    Set<String> rights = new HashSet<>(Arrays.stream(rightsField.getText().split(",")).toList());

                    Map<String, Person> peopleMap = new HashMap<>();
                    table.getItems().forEach(it -> peopleMap.put(it.getName(), it));

                    Person fromPerson = peopleMap.get(from);
                    Person toPerson = peopleMap.get(to);
                    if (fromPerson == null)
                        alert("Пользователь, передающий права, не найден");
                    else if (toPerson == null)
                        alert("Пользователь, принимающий права, не найден");
                    else {
                        fromPerson.getAvailableLetters().forEach(it -> {
                            if (!rights.contains(it)) rights.remove(it);
                        });

                        toPerson.getAvailableLetters().addAll(rights);

                        table.refresh();
                    }
                }
            }
        });

        createButton.setOnAction(actionEvent -> {
            String to = toField.getText();
            String rightsString = rightsField.getText();
            if (validateField(to, false) && validateField(rightsString, false)) {
                if (validateRights(rightsString.split(","))) {
                    Set<String> rights = new HashSet<>(Arrays.stream(rightsField.getText().split(",")).toList());
                    Map<String, Person> peopleMap = new HashMap<>();
                    table.getItems().forEach(it -> peopleMap.put(it.getName(), it));

                    Person toPerson = peopleMap.get(to);
                    Person newPerson = null;
                    if (toPerson == null)
                        newPerson = new Person(to, new HashSet<>());
                    Person finalNewPerson = newPerson;
                    rights.forEach(it -> {
                        if (!letters.contains(it)) {
                            TableColumn<Person, Boolean> column = new TableColumn<>(it);
                            column.setCellValueFactory(cellData ->
                                    new SimpleBooleanProperty(cellData.getValue().getAvailableLetters().contains(it)));
                            column.setCellFactory(p -> {
                                CheckBox checkBox = new CheckBox();
                                TableCell<Person, Boolean> tableCell = new TableCell<>() {
                                    @Override
                                    protected void updateItem(Boolean item, boolean empty) {

                                        super.updateItem(item, empty);
                                        if (empty || item == null)
                                            setGraphic(null);
                                        else {
                                            setGraphic(checkBox);
                                            checkBox.setSelected(item);
                                        }
                                    }
                                };
                                checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                                    tableCell.getTableRow().getItem().updateSet(!checkBox.isSelected(), it);
                                });
                                tableCell.setAlignment(Pos.CENTER);
                                tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                                return tableCell;
                            });
                            letters.add(it);
                            table.getColumns().add(column);
                        }
                        Objects.requireNonNullElse(toPerson, finalNewPerson).updateSet(true, it);
                    });

                    if (toPerson == null) {
                        table.getItems().add(newPerson);
                    } else {
                        table.getItems().stream()
                                .filter(it -> it.getName().equals(toPerson.getName()))
                                .peek(it -> it.setAvailableLetters(toPerson.getAvailableLetters()));

                        table.refresh();
                    }
                }
            }
        });

        removeButton.setOnAction(actionEvent -> {
            String to = toField.getText();
            String rightsString = rightsField.getText();
            if (validateField(to, false) && validateField(rightsString, false)) {
                Set<String> rights = new HashSet<>(Arrays.stream(rightsString.split(",")).toList());

                Map<String, Person> peopleMap = new HashMap<>();
                table.getItems().forEach(it -> peopleMap.put(it.getName(), it));

                Person toPerson = peopleMap.get(to);
                if (toPerson == null)
                    alert("Пользователь не найден");
                else {
                    toPerson.getAvailableLetters().removeAll(rights);

                    table.refresh();
                }
            }
        });

        startButton.setOnAction(actionEvent -> {
            try {
                table.refresh();
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("modal.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 591, 370);
                Stage stage = new Stage();
                ModalController modalController = fxmlLoader.getController();
                modalController.initialize(table.getItems(), letters);
                stage.setTitle("Main");
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                alert("Что-то пошло не так");
            }
        });
    }






    private boolean validateField(String field, Boolean allowWhiteSpace) {
        if (allowWhiteSpace) {
            if (field.length() != 1) {
                alert("Поле не может быть пустым или \n" +
                        "строка не может содержать больше 1 символа");
                return false;
            }
        } else {
            if (field.isBlank()) {
                alert("Поле не может содержать только пробелы или быть пустым");
                return false;
            }
        }
        return true;
    }

    private boolean validateRights(String[] rights) {
        for (String string : rights) {
            if (string.length() != 1) {
                alert("Право не может быть длиной больше 1 символа");
                return false;
            }
        }

        return true;
    }


    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }


}