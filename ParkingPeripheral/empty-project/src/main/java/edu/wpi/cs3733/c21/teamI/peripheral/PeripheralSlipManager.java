package edu.wpi.cs3733.c21.teamI.peripheral;

import edu.wpi.cs3733.c21.teamI.Main;
import edu.wpi.cs3733.c21.teamI.database.ParkingPeripheralServerManager;
import edu.wpi.cs3733.c21.teamI.parking.ParkingSlip;
import edu.wpi.cs3733.c21.teamI.serial.ArduinoSlipSerial;
import edu.wpi.cs3733.c21.teamI.serial.SlipSerialInterface;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class PeripheralSlipManager {

  private static PeripheralSlipManager ourInstance;

  public static PeripheralSlipManager getInstance() {
    if (ourInstance == null) ourInstance = new PeripheralSlipManager();
    return ourInstance;
  }

  private SlipSerialInterface serial;

  private PeripheralSlipManager() {
    serial = new ArduinoSlipSerial();
    serial.initialize(new String[] {Main.comPorts});

    try {
      TimeUnit.SECONDS.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void addTicket(int durationMin, double basePrice) {
    ParkingSlip slip =
        ParkingPeripheralServerManager.getInstance()
            .createNewSlip(
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis() + (long) durationMin * 60 * 1000),
                (int) (basePrice * 100));

    String date =
        slip.getEntryTimestamp().toLocalDateTime().toLocalDate().format(serial.getDateFormat());

    System.out.println(date);

    String timeFormat = "hh:mma";
    String startTime =
        slip.getEntryTimestamp().toLocalDateTime().toLocalTime().format(serial.getTimeFormat());
    System.out.println(startTime);

    String endTime =
        slip.getEndTimestamp().toLocalDateTime().toLocalTime().format(serial.getTimeFormat());
    System.out.println(endTime);

    String cost = serial.getCostFormat().format(slip.getBaseCost());
    System.out.println(cost);

    System.out.println(String.valueOf(slip.getId()));

    serial.printTicket(
        slip.getParkingSlot(), date, startTime, endTime, cost, String.valueOf(slip.getId()));
  }
}
