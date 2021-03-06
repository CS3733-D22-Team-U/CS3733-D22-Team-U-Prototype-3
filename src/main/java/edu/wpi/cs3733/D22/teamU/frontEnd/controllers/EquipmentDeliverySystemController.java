package edu.wpi.cs3733.D22.teamU.frontEnd.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.D22.teamU.BackEnd.Employee.Employee;
import edu.wpi.cs3733.D22.teamU.BackEnd.Employee.EmployeeDaoImpl;
import edu.wpi.cs3733.D22.teamU.BackEnd.Equipment.Equipment;
import edu.wpi.cs3733.D22.teamU.BackEnd.Location.Location;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.EquipRequest.EquipRequest;
import edu.wpi.cs3733.D22.teamU.BackEnd.Udb;
import edu.wpi.cs3733.D22.teamU.frontEnd.Uapp;
import edu.wpi.cs3733.D22.teamU.frontEnd.javaFXObjects.ComboBoxAutoComplete;
import edu.wpi.cs3733.D22.teamU.frontEnd.services.equipmentDelivery.EquipmentUI;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.SneakyThrows;

public class EquipmentDeliverySystemController extends ServiceController {

  public ComboBox<String> locations;

  public ComboBox<String> employees;

  @FXML TableColumn<EquipmentUI, String> nameCol;

  @FXML TableColumn<EquipmentUI, Integer> inUse;

  @FXML TableColumn<EquipmentUI, Integer> available;

  @FXML TableColumn<EquipmentUI, Integer> total;

  @FXML TableColumn<EquipmentUI, String> location;

  @FXML TableView<EquipmentUI> table;

  @FXML VBox requestHolder;

  @FXML Text requestText;

  @FXML Button clearButton;

  @FXML Button submitButton;

  @FXML TableColumn<EquipmentUI, String> activeReqID;

  @FXML TableColumn<EquipmentUI, String> activeReqName;

  @FXML TableColumn<EquipmentUI, Integer> activeReqAmount;

  @FXML TableColumn<EquipmentUI, String> activeReqType;

  @FXML TableColumn<EquipmentUI, String> activeReqDestination;

  @FXML TableColumn<EquipmentUI, String> activeDate;

  @FXML TableColumn<EquipmentUI, String> activeTime;

  @FXML TableColumn<EquipmentUI, Integer> activePriority;

  @FXML TableView<EquipmentUI> activeRequestTable;

  @FXML VBox inputFields;

  @FXML StackPane requestsStack;

  @FXML Pane newRequestPane;

  @FXML Pane allEquipPane;

  @FXML Pane activeRequestPane;

  @FXML Button newReqButton;

  @FXML Button activeReqButton;

  @FXML Button allEquipButton;

  @FXML Text time;

  ObservableList<EquipmentUI> equipmentUI = FXCollections.observableArrayList();

  ObservableList<JFXCheckBox> checkBoxes = FXCollections.observableArrayList();

  ObservableList<JFXTextArea> checkBoxesInput = FXCollections.observableArrayList();

  ObservableList<EquipmentUI> equipmentUIRequests = FXCollections.observableArrayList();

  // Udb udb;

  ArrayList<String> nodeIDs;

  ArrayList<String> staff;

  private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // super.initialize(location, resources);

    // udb = Udb.getInstance();

    setUpAllEquipment();

    setUpActiveRequests();

    nodeIDs = new ArrayList<>();

    for (Location l : Udb.getInstance().locationImpl.list()) {

      nodeIDs.add(l.getNodeID());
    }

    locations.setTooltip(new Tooltip());

    locations.getItems().addAll(nodeIDs);

    new ComboBoxAutoComplete<String>(locations, 650, 290);

    staff = new ArrayList<>();

    for (Employee l : Udb.getInstance().EmployeeImpl.hList().values()) {

      staff.add(l.getEmployeeID());
    }

    employees.setTooltip(new Tooltip());

    employees.getItems().addAll(staff);

    new ComboBoxAutoComplete<String>(employees, 675, 380);

    for (Node checkBox : requestHolder.getChildren()) {

      checkBoxes.add((JFXCheckBox) checkBox);
    }

    for (Node textArea : inputFields.getChildren()) {

      checkBoxesInput.add((JFXTextArea) textArea);
    }

    for (int i = 0; i < checkBoxesInput.size(); i++) {

      int finalI = i;

      checkBoxesInput
          .get(i)
          .disableProperty()
          .bind(
              Bindings.createBooleanBinding(
                  () -> !checkBoxes.get(finalI).isSelected(),
                  checkBoxes.stream().map(CheckBox::selectedProperty).toArray(Observable[]::new)));
    }

    clearButton
        .disableProperty()
        .bind(
            Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(JFXCheckBox::isSelected),
                checkBoxes.stream().map(JFXCheckBox::selectedProperty).toArray(Observable[]::new)));

    // BooleanBinding submit =locations.idProperty().isEmpty().and(

    // Bindings.createBooleanBinding(checkBoxes.stream().noneMatch(JFXCheckBox::isSelected)));

    submitButton
        .disableProperty()
        .bind(
            Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(JFXCheckBox::isSelected),
                checkBoxes.stream().map(JFXCheckBox::selectedProperty).toArray(Observable[]::new)));

    handleTime();
  }

  private void handleTime() {

    Thread timeThread =
        new Thread(
            () -> {
              while (Uapp.running) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                String timeStampTime = sdf3.format(timestamp).substring(11);

                time.setText(timeStampTime);
              }
            });

    timeThread.start();
  }

  private void setUpAllEquipment() throws SQLException, IOException {

    nameCol.setCellValueFactory(new PropertyValueFactory<EquipmentUI, String>("equipmentName"));

    inUse.setCellValueFactory(new PropertyValueFactory<EquipmentUI, Integer>("amountInUse"));

    available.setCellValueFactory(
        new PropertyValueFactory<EquipmentUI, Integer>("amountAvailable"));

    total.setCellValueFactory(new PropertyValueFactory<EquipmentUI, Integer>("totalAmount"));

    location.setCellValueFactory(new PropertyValueFactory<EquipmentUI, String>("location"));

    table.setItems(getEquipmentList());
  }

  private void setUpActiveRequests() throws SQLException, IOException {

    activeReqID.setCellValueFactory(new PropertyValueFactory<>("id"));

    activeReqName.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));

    activeReqAmount.setCellValueFactory(new PropertyValueFactory<>("requestAmount"));

    activeReqType.setCellValueFactory(new PropertyValueFactory<>("type"));

    activeReqDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));

    activeDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

    activeTime.setCellValueFactory(new PropertyValueFactory<>("requestTime"));

    activePriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

    activeRequestTable.setItems(getActiveRequestList());
  }

  private ObservableList<EquipmentUI> newRequest(
      String id,
      String name,
      int amount,
      String destination,
      String date,
      String time,
      int priority) {

    equipmentUIRequests.add(new EquipmentUI(id, name, amount, destination, date, time, priority));

    return equipmentUIRequests;
  }

  private ObservableList<EquipmentUI> getEquipmentList() throws SQLException, IOException {

    equipmentUI.clear();

    for (Equipment equipment : Udb.getInstance().EquipmentImpl.EquipmentList) {

      equipmentUI.add(
          new EquipmentUI(
              equipment.getName(),
              equipment.getInUse(),
              equipment.getAvailable(),
              equipment.getAmount(),
              equipment.getLocationID()));
    }

    return equipmentUI;
  }

  private ObservableList<EquipmentUI> getActiveRequestList() throws SQLException, IOException {

    for (EquipRequest equipRequest : Udb.getInstance().equipRequestImpl.hList().values()) {

      equipmentUIRequests.add(
          new EquipmentUI(
              equipRequest.getID(),
              equipRequest.getName(),
              equipRequest.getAmount(),
              equipRequest.getDestination(),
              equipRequest.getDate(),
              equipRequest.getTime(),
              equipRequest.getPriority()));
    }

    return equipmentUIRequests;
  }

  @Override
  public void addRequest() {

    StringBuilder startRequestString = new StringBuilder("Your request for : ");

    String endRequest = " has been placed successfully";

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    int requestAmount = 0;

    for (int i = 0; i < checkBoxes.size(); i++) {

      if (checkBoxes.get(i).isSelected()) {

        String inputString = "";

        if (checkBoxesInput.get(i).getText().trim().equals("")) {

          inputString = "0";

        } else {

          inputString = checkBoxesInput.get(i).getText().trim();
        }

        String room = locations.getValue().toString();

        requestAmount = Integer.parseInt(inputString);

        startRequestString
            .append(requestAmount)
            .append(" ")
            .append(checkBoxes.get(i).getText())
            .append("(s) to room ")
            .append(room)
            .append(", ");

        boolean alreadyHere = true;
        String serviceID = "notWork";

        while (alreadyHere) {
          double rand = Math.random() * 10000;

          try {
            alreadyHere =
                Udb.getInstance().compServRequestImpl.hList().containsKey("EQU" + (int) rand);
          } catch (Exception e) {
            System.out.println(
                "alreadyHere variable messed up in equip service request controller");
          }

          serviceID = "EQU" + (int) rand;
        }

        EquipmentUI request =
            new EquipmentUI(
                serviceID,
                checkBoxes.get(i).getText(),
                requestAmount,
                room,
                sdf3.format(timestamp).substring(0, 10),
                sdf3.format(timestamp).substring(11),
                1);

        activeRequestTable.setItems(
            newRequest(
                request.getId(),
                request.getEquipmentName(),
                request.getRequestAmount(),
                request.getDestination(),
                request.getRequestDate(),
                request.getRequestTime(),
                1));

        try {

          Udb.getInstance()
              .add( // TODO Have random ID and enter Room Destination
                  new EquipRequest(
                      request.getId(),
                      request.getEquipmentName(),
                      request.getRequestAmount(),
                      request.getType(),
                      "sent",
                      checkEmployee(employees.getValue()),
                      request.getDestination(),
                      request.getRequestDate(),
                      request.getRequestTime(),
                      1));

        } catch (IOException e) {

          e.printStackTrace();

        } catch (SQLException e) {

          e.printStackTrace();
        }
      }
    }

    requestText.setText(startRequestString + endRequest);

    requestText.setVisible(true);

    new Thread(
            () -> {
              try {

                Thread.sleep(3500); // milliseconds

                Platform.runLater(
                    () -> {
                      requestText.setVisible(false);
                    });

              } catch (InterruptedException ie) {

              }
            })
        .start();
  }

  @Override
  public void removeRequest() {}

  @Override
  public void updateRequest() {}

  public void clearRequest() {

    for (int i = 0; i < checkBoxes.size(); i++) {

      checkBoxes.get(i).setSelected(false);

      checkBoxesInput.get(i).clear();
    }

    requestText.setText("Cleared Requests!");

    requestText.setVisible(true);

    new Thread(
            () -> {
              try {

                Thread.sleep(1500); // milliseconds

                Platform.runLater(
                    () -> {
                      requestText.setVisible(false);
                    });

              } catch (InterruptedException ie) {

              }
            })
        .start();
  }

  public Employee checkEmployee(String employee) throws NullPointerException {

    if (EmployeeDaoImpl.List.get(employee) != null) {

      return EmployeeDaoImpl.List.get(employee);

    } else {

      Employee empty = new Employee("N/A");

      return empty;
    }
  }

  public void switchToNewRequest(ActionEvent actionEvent) {

    ObservableList<Node> stackNodes = requestsStack.getChildren();

    Node newReq = stackNodes.get(stackNodes.indexOf(newRequestPane));

    for (Node node : stackNodes) {

      node.setVisible(false);
    }

    newReq.setVisible(true);

    newReq.toBack();

    activeReqButton.setUnderline(false);

    newReqButton.setUnderline(true);

    allEquipButton.setUnderline(false);
  }

  public void switchToActive(ActionEvent actionEvent) {

    ObservableList<Node> stackNodes = requestsStack.getChildren();

    Node active = stackNodes.get(stackNodes.indexOf(activeRequestPane));

    for (Node node : stackNodes) {

      node.setVisible(false);
    }

    active.setVisible(true);

    active.toBack();

    activeReqButton.setUnderline(true);

    newReqButton.setUnderline(false);

    allEquipButton.setUnderline(false);
  }

  public void switchToEquipment(ActionEvent actionEvent) {

    ObservableList<Node> stackNodes = requestsStack.getChildren();

    Node active = stackNodes.get(stackNodes.indexOf(allEquipPane));

    for (Node node : stackNodes) {

      node.setVisible(false);
    }

    active.setVisible(true);

    active.toBack();

    activeReqButton.setUnderline(false);

    newReqButton.setUnderline(false);

    allEquipButton.setUnderline(true);
  }

  public void mouseHovered(MouseEvent mouseEvent) {

    Button button = (Button) mouseEvent.getSource();

    button.setStyle("-fx-border-color: #E6F6F7");
  }

  public void mouseExit(MouseEvent mouseEvent) {

    Button button = (Button) mouseEvent.getSource();

    button.setStyle("-fx-border-color: transparent");
  }
}
