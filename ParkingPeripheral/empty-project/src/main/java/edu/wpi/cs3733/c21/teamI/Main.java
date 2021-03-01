package edu.wpi.cs3733.c21.teamI;

import edu.wpi.cs3733.c21.teamI.database.DatabaseManager;
import edu.wpi.cs3733.c21.teamI.database.ParkingPeripheralServerManager;
import edu.wpi.cs3733.c21.teamI.parking.Lot;
import edu.wpi.cs3733.c21.teamI.peripheral.ParkingPeripheral;
import java.util.Map;

public class Main {

  public static String[] comPorts;

  public static void main(String[] args) {
    comPorts = args;

    DatabaseManager.initPeripheralDatabaseManagers();

    ParkingPeripheral peripheral1 = new ParkingPeripheral();

    Map<String, Lot> lots = ParkingPeripheralServerManager.getInstance().loadLots();

    Lot westLot = lots.get("Western Parking");

    peripheral1.assignSensors(6, westLot.getBlockForCode("WC").getFloorForNum(1));
  }
}
