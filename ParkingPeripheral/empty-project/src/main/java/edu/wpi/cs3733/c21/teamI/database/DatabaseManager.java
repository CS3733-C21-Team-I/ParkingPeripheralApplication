package edu.wpi.cs3733.c21.teamI.database;

public abstract class DatabaseManager {

  DatabaseRef databaseRef;

  DatabaseManager(String url, boolean regen) {
    databaseRef = DatabaseRef.getConnection(url, regen);
  }

  public static void initPeripheralDatabaseManagers() {
    ParkingPeripheralServerManager.init();
  }
}
