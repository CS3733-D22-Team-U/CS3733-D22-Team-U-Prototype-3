package edu.wpi.cs3733.D22.teamU.frontEnd.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.D22.teamU.BackEnd.Employee.Employee;
import edu.wpi.cs3733.D22.teamU.BackEnd.Equipment.Equipment;
import edu.wpi.cs3733.D22.teamU.BackEnd.Location.Location;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.EquipRequest.EquipRequest;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequestDaoImpl;
import edu.wpi.cs3733.D22.teamU.BackEnd.Udb;
import edu.wpi.cs3733.D22.teamU.frontEnd.Uapp;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import edu.wpi.cs3733.D22.teamU.frontEnd.javaFXObjects.ComboBoxAutoComplete;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class MedicineDeliveryController extends ServiceController {

  @FXML JFXCheckBox Advil;
  @FXML JFXCheckBox Alprozalam;
  @FXML JFXCheckBox AmphetamineSalt;
  @FXML JFXCheckBox Atorvastatin;
  @FXML JFXCheckBox Lisinopril;
  @FXML JFXCheckBox Metformin;
  @FXML JFXCheckBox specialCheck;
  @FXML Button clearButton;
  @FXML Button submitButton;
  @FXML TextArea specialReq;
  @FXML TextField patientName;
  @FXML TextField staffName;
  @FXML TextField advilTxt;
  // @FXML TextField IDtxt;
  // @FXML TextField amount;
  @FXML TextField alproTxt;
  @FXML TextField saltTxt;
  @FXML TextField atorvTxt;
  @FXML TextField lisinTxt;
  @FXML TextField metTxt;
  @FXML TextArea specialReqTxt;
  @FXML Text reset;
  @FXML Text processText;
  @FXML JFXHamburger hamburger;
  @FXML VBox medVbox;
  @FXML VBox nameVbox;
  @FXML VBox vBoxPane;
  @FXML Pane pane;
  @FXML Pane assistPane;
  @FXML AnchorPane bigPane;
  @FXML TabPane tab;
  @FXML TextField destination;

  @FXML Text time;

  @FXML TableColumn<MedicineRequest, String> activeReqID;
  @FXML TableColumn<MedicineRequest, String> activeReqName;
  @FXML TableColumn<MedicineRequest, Integer> activeReqAmount;
  @FXML TableColumn<MedicineRequest, String> activeReqType;
  @FXML TableColumn<MedicineRequest, String> activeReqDestination;
  @FXML TableColumn<MedicineRequest, String> activeDate;
  @FXML TableColumn<MedicineRequest, String> activeTime;
  @FXML TableColumn<MedicineRequest, Integer> activePriority;

  @FXML TableColumn<MedicineRequest, String> nameCol;
  @FXML TableColumn<MedicineRequest, Integer> inUse;
  @FXML TableColumn<MedicineRequest, Integer> available;
  @FXML TableColumn<MedicineRequest, Integer> total;
  @FXML TableColumn<MedicineRequest, String> location;
  @FXML TableView<MedicineRequest> table;

  @FXML Text requestText;

  @FXML TableView<MedicineRequest> activeRequestTable;
  @FXML VBox requestHolder;

  @FXML
  StackPane requestsStack;
  @FXML Pane newRequestPane;
  @FXML Pane allEquipPane;
  @FXML Pane activeRequestPane;

  @FXML Button newReqButton;
  @FXML Button activeReqButton;
  @FXML Button allEquipButton;
  public ComboBox<String> locations;
  public ComboBox<String> employees;
  ArrayList<String> nodeIDs;
  @FXML VBox inputFields;

  ArrayList<String> staff;

  ObservableList<MedicineRequest> medicineRequests = FXCollections.observableArrayList();


  ObservableList<MedicineRequest> medUIRequests = FXCollections.observableArrayList();
  ObservableList<JFXCheckBox> checkBoxes = FXCollections.observableArrayList();
  ObservableList<JFXTextArea> checkBoxesInput = FXCollections.observableArrayList();

  // Udb udb = DBController.udb;

  private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // super.initialize(location, resources);
    // udb = Udb.getInstance();
    setUpAllMed();
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

  private void setUpActiveRequests() throws SQLException, IOException {
    activeReqID.setCellValueFactory(new PropertyValueFactory<>("id"));
    activeReqName.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
    activeReqAmount.setCellValueFactory(new PropertyValueFactory<>("requestAmount"));
    activeReqType.setCellValueFactory(new PropertyValueFactory<>("type"));
    activeReqDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
    activeDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
    activeTime.setCellValueFactory(new PropertyValueFactory<>("requestTime"));
    activePriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
    activeRequestTable.setItems(getActiveRequestList());

  }

  private ObservableList<MedicineRequest> newRequest(
          String id,
          String name,
          int amount,
          String patientName,
          String status,
          Employee employee,
          String destination,
          String date,
          String time,
          int priority) {
    medicineRequests.add(new MedicineRequest(id, name, amount, patientName, status, employee, destination, date, time));
    return medicineRequests;
  }








  private void setUpAllMed() throws SQLException, IOException {
    nameCol.setCellValueFactory(new PropertyValueFactory<edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest, String>("medicineName"));
    inUse.setCellValueFactory(new PropertyValueFactory<edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest, Integer>("amountInUse"));
    available.setCellValueFactory(
            new PropertyValueFactory<edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest, Integer>("amountAvailable"));
    total.setCellValueFactory(new PropertyValueFactory<edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest, Integer>("totalAmount"));
    location.setCellValueFactory(new PropertyValueFactory<edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest, String>("location"));
    //table.setItems(getEquipmentList());
  }

  private ObservableList<MedicineRequest> getActiveRequestList() throws SQLException, IOException {
    for (edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest request : MedicineRequestDaoImpl.List.values()) {
      medUIRequests.add(
          new edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest(
              request.getID(),
              request.getName(),
              request.getAmount(),
              request.getPatientName(),
              request.getStatus(),
              request.getEmployee(),
              request.getDestination(),
              request.getDate(),
              request.getTime()));
    }
    return medUIRequests;
  }

  public Employee checkEmployee(String employee) throws SQLException, IOException {
    if (Udb.getInstance().EmployeeImpl.List.get(employee) != null) {
      return Udb.getInstance().EmployeeImpl.List.get(employee);
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

  public void switchToMedicine(ActionEvent actionEvent) {
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

  @SneakyThrows
  @Override
  public void addRequest() {
    String patientInput = patientName.getText().trim();
    String staffInput = staffName.getText().trim();
    String destinationInput = destination.getText().trim();
    // String amountInput = amount.getText().trim();

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    for (int i = 0; i < checkBoxes.size(); i++) {
      if (checkBoxes.get(i).isSelected()) {
        double rand = Math.random() * 10000;
        // int amount = Integer.parseInt(checkBoxInput.get(i).toString().trim());
        int amount = 24;
        edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest request =
            new edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest(
                (int) rand + "",
                checkBoxes.get(i).getText(),
                amount,
                patientInput,
                "Ordered",
                checkEmployee(staffInput),
                destinationInput,
                sdf3.format(timestamp).substring(0, 10),
                sdf3.format(timestamp).substring(11));
        activeRequestTable.setItems(
            newRequest(
                request.getID(),
                request.getName(),
                request.getAmount(),
                request.getPatientName(),
                request.getStatus(),
                request.getEmployee(),
                request.getDestination(),
                request.getDate(),
                request.getTime()));
        try {
          Udb.getInstance()
              .medicineRequestImpl
              .add(
                  new edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest(
                      request.getID(),
                      request.getName(),
                      request.getAmount(),
                      request.getPatientName(),
                      request.getStatus(),
                      request.getEmployee(),
                      request.getDestination(),
                      request.getDate(),
                      request.getTime()));
          processText.setText("Request for " + checkBoxes.get(i).getText() + " successfully sent.");
        } catch (IOException e) {
          e.printStackTrace();
          processText.setText("Request for " + checkBoxes.get(i).getText() + " failed.");
        }
      }
    }
    clear();
  }

  public void enableTxt() {
    if (Advil.isSelected()) {
      advilTxt.setDisable(false);
    }
    if (Alprozalam.isSelected()) {
      alproTxt.setDisable(false);
    }
    if (AmphetamineSalt.isSelected()) {
      saltTxt.setDisable(false);
    }
    if (Atorvastatin.isSelected()) {
      atorvTxt.setDisable(false);
    }
    if (Lisinopril.isSelected()) {
      lisinTxt.setDisable(false);
    }
    if (Metformin.isSelected()) {
      metTxt.setDisable(false);
    }
    if (specialCheck.isSelected()) {
      specialReqTxt.setDisable(false);
    }
  }

  public void clear() {
    Advil.setSelected(false);
    Alprozalam.setSelected(false);
    AmphetamineSalt.setSelected(false);
    Atorvastatin.setSelected(false);
    Lisinopril.setSelected(false);
    Metformin.setSelected(false);
    specialCheck.setSelected(false);
    patientName.setText("");
    staffName.setText("");
    // IDtxt.setText("");
    advilTxt.setText("");
    alproTxt.setText("");
    saltTxt.setText("");
    atorvTxt.setText("");
    lisinTxt.setText("");
    metTxt.setText("");
    specialReqTxt.setText("");
    reset.setText("Cleared requests!");
    reset.setVisible(true);
    new Thread(
            () -> {
              try {
                Thread.sleep(1500); // milliseconds
                Platform.runLater(
                    () -> {
                      reset.setVisible(false);
                    });
              } catch (InterruptedException ie) {
              }
            })
        .start();
  }
  //    lisinTxt.equals("") && metTxt.equals("") && specialReqTxt.equals(""))
  public void reqFields() {
    if (staffName.getText().equals("")
        || patientName.getText().equals("")
        // IDtxt.getText().equals("")
        || (advilTxt.getText().equals("")
                && alproTxt.getText().equals("")
                && saltTxt.getText().equals(""))
            && atorvTxt.getText().equals("")
            && lisinTxt.getText().equals("")
            && metTxt.getText().equals("")
            && specialReqTxt.getText().equals("")) {
      processText.setText("Please fill out all required fields!");
      processText.setVisible(true);
    } else {
      process();
    }
  }

  public void process() {
    processText.setText("Processing...");
    processText.setVisible(true);
    new Thread(
            () -> {
              try {
                Thread.sleep(2000); // milliseconds
                Platform.runLater(
                    () -> {
                      processText.setText(
                          "Staff Name: "
                              + staffName.getText()
                              + "\n"
                              + "Patient Name: "
                              + patientName.getText()
                              + "\n"
                              + "Order ID: "
                              // IDtxt.getText()
                              + "\n"
                              + ""
                              + "\n"
                              + "Medicine Order: "
                              + "\n"
                              + ""
                              + "\n"
                              + "Advil: "
                              + advilTxt.getText()
                              + "\n"
                              + "Alprozalam: "
                              + alproTxt.getText()
                              + "\n"
                              + "Amphetamine Salt: "
                              + saltTxt.getText()
                              + "\n"
                              + "Atorvastatin: "
                              + atorvTxt.getText()
                              + "\n"
                              + "Lisinopril: "
                              + lisinTxt.getText()
                              + "\n"
                              + "Metformin: "
                              + metTxt.getText()
                              + "\n"
                              + "Special Request: "
                              + request());
                    });
              } catch (InterruptedException ie) {
              }
            })
        .start();
  }

  private String request() {
    String request = "";
    if (specialReqTxt.equals("")) {
      request = "No response";
    } else {
      request = specialReqTxt.getText();
    }
    return request;
  }

  public void toMedHelp(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/medHelp.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }

  private ObservableList<edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest> newRequest(
      String id,
      String name,
      int amount,
      String patientName,
      String status,
      Employee employee,
      String location,
      String date,
      String time) {
    medUIRequests.add(
        new edu.wpi.cs3733.D22.teamU.BackEnd.Request.MedicineRequest.MedicineRequest(id, name, amount, patientName, status, employee, location, date, time));
    return medUIRequests;
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
}
