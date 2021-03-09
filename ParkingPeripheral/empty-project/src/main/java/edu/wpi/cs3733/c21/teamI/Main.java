package edu.wpi.cs3733.c21.teamI;

import edu.wpi.cs3733.c21.teamI.database.DatabaseManager;
import edu.wpi.cs3733.c21.teamI.database.ParkingPeripheralServerManager;
import edu.wpi.cs3733.c21.teamI.parking.Lot;
import edu.wpi.cs3733.c21.teamI.peripheral.ParkingPeripheral;
import edu.wpi.cs3733.c21.teamI.peripheral.PeripheralSlipManager;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {

  public static String comPorts;

  public static void main(String[] args) {
    comPorts = args[0];

    if (Arrays.asList(args).contains("sensor")) {
      DatabaseManager.initPeripheralDatabaseManagers();

      ParkingPeripheral peripheral1 = new ParkingPeripheral();

      Map<String, Lot> lots = ParkingPeripheralServerManager.getInstance().loadLots();

      Lot westLot = lots.get("Western Parking");

      try {
        TimeUnit.SECONDS.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      peripheral1.assignSensors(6, westLot.getBlockForCode("WC").getFloorForNum(1));
    } else if (Arrays.asList(args).contains("printTicket")) {
      DatabaseManager.initPeripheralDatabaseManagers();

      // PeripheralSlipManager.getInstance().addTicket(60, 5.00);
      PeripheralSlipManager.getInstance().addTicket(120, 5.50);

      //      input.printTicket("03/09/2021", "04:45AM", "05:45AM", "$05.50", "10001");
      //      System.out.println("Done");

    }
  }
}
