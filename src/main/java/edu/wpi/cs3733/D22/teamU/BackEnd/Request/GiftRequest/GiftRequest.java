package edu.wpi.cs3733.D22.teamU.BackEnd.Request.GiftRequest;

import edu.wpi.cs3733.D22.teamU.BackEnd.Employee.Employee;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.Request;

public class GiftRequest extends Request {
  public String gifts;
  public String message;

  public GiftRequest(
      String ID,
      String name, // sender name
      String patientName,
      String gifts,
      String message,
      String status,
      Employee employee,
      String destination,
      String date,
      String time) {
    this.ID = ID;
    this.name = name; // sender name
    this.patientName = patientName;
    this.gifts = gifts;
    this.message = message;
    this.status = status;
    this.employee = employee;
    this.destination = destination;
    this.date = date;
    this.time = time;
  }

  public String getGifts() {
    return gifts;
  }

  public void setGifts(String gifts) {
    this.gifts = gifts;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
