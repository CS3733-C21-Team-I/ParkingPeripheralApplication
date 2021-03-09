package edu.wpi.cs3733.c21.teamI.database;

import edu.wpi.cs3733.c21.teamI.parking.Block;
import edu.wpi.cs3733.c21.teamI.parking.Floor;
import edu.wpi.cs3733.c21.teamI.parking.Lot;
import edu.wpi.cs3733.c21.teamI.parking.ParkingSlip;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ParkingPeripheralServerManager extends DatabaseManager {

  private static final String DB_URL = "jdbc:derby://localhost:1527/peripheralDB";

  private static ParkingPeripheralServerManager ourInstance;

  public static void init() {
    ourInstance = new ParkingPeripheralServerManager(false);
  }

  public static ParkingPeripheralServerManager getInstance() {
    return ourInstance;
  }

  private ParkingPeripheralServerManager(boolean regen) {
    super(DB_URL, regen);
  }

  public Map<String, Lot> loadLots() {

    try {
      Map<String, Lot> lots = new HashMap<>();
      Statement lotStatement = databaseRef.getConnection().createStatement();
      ResultSet lotResults = lotStatement.executeQuery("SELECT * FROM PARKING_LOTS");

      while (lotResults.next()) {
        Lot lot =
            new Lot(
                lotResults.getInt("ID"),
                lotResults.getString("NAME"),
                lotResults.getBoolean("IS_REENTRY_ALLOWED"),
                lotResults.getBoolean("IS_VALLET_AVAILABLE"));

        Statement blockStatement = databaseRef.getConnection().createStatement();
        ResultSet blockResults =
            blockStatement.executeQuery(
                "SELECT * FROM PARKING_BLOCKS WHERE PARKING_LOT_ID=" + lotResults.getInt("ID"));

        while (blockResults.next()) {
          Block block = new Block(blockResults.getInt("ID"), blockResults.getString("BLOCK_CODE"));

          Statement floorStatement = databaseRef.getConnection().createStatement();
          ResultSet floorResults =
              floorStatement.executeQuery(
                  "SELECT * FROM PARKING_FLOORS WHERE BLOCK_ID=" + blockResults.getInt("ID"));

          while (floorResults.next()) {
            Map<String, Integer> slotCodes = new HashMap<>();
            Statement slotStatement = databaseRef.getConnection().createStatement();
            ResultSet slotResults =
                slotStatement.executeQuery(
                    "SELECT * FROM PARKING_SLOTS WHERE FLOOR_ID=" + floorResults.getInt("ID"));

            while (slotResults.next()) {
              slotCodes.put(
                  slotResults.getString("CODE") + slotResults.getInt("SLOT_NUMBER"),
                  slotResults.getInt("ID"));
            }

            Floor floor =
                new Floor(
                    floorResults.getInt("ID"),
                    slotCodes,
                    floorResults.getInt("FLOOR_NUMBER"),
                    floorResults.getBoolean("IS_COVERED"),
                    floorResults.getBoolean("IS_ACCESSIBLE"),
                    floorResults.getBoolean("IS_RESERVED_STAFF"));
            block.addFloor(floor);
          }
          lot.addBlock(block);
        }
        lots.put(lot.getName(), lot);
      }

      return lots;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void updateSlotOccupancy(int slotId, boolean occupied) {
    System.out.println(slotId + " : " + occupied);

    try {
      Statement statement = databaseRef.getConnection().createStatement();
      int i =
          statement.executeUpdate(
              "UPDATE PARKING_SLOTS SET is_occupied = " + occupied + " WHERE id=" + slotId);
      if (i == 0) throw new IllegalStateException("Attempted to update non existant parking slot");
    } catch (SQLException e) {
      e.printStackTrace();
    }

    int updatedFloor = -1;
    try {
      Statement floorStatement = databaseRef.getConnection().createStatement();
      ResultSet floorRs =
          floorStatement.executeQuery(
              "SELECT * FROM PARKING_FLOORS WHERE id=(SELECT floor_id FROM PARKING_SLOTS WHERE id="
                  + slotId
                  + ")");
      if (!floorRs.next()) throw new IllegalStateException("No Parking_Floor for updated slot");
      boolean isFloorOccupied = floorRs.getBoolean("is_floor_full");

      Statement slotStatement = databaseRef.getConnection().createStatement();
      ResultSet slotResults =
          slotStatement.executeQuery(
              "SELECT * FROM PARKING_SLOTS WHERE is_occupied="
                  + false
                  + " AND floor_id="
                  + floorRs.getInt("id"));
      if (!slotResults.next()) {
        System.out.println("No empty slots on floor: " + floorRs.getInt("id"));
        if (isFloorOccupied != true) updatedFloor = floorRs.getInt("id");
        floorStatement.executeUpdate(
            "UPDATE PARKING_FLOORS SET IS_FLOOR_FULL = true WHERE ID=" + floorRs.getInt("id"));
      } else {
        System.out.println("Emply slots on floor: " + floorRs.getInt("id") + " " + isFloorOccupied);
        if (isFloorOccupied == true) updatedFloor = floorRs.getInt("id");
        floorStatement.executeUpdate(
            "UPDATE PARKING_FLOORS SET IS_FLOOR_FULL=false WHERE ID=" + floorRs.getInt("id"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (updatedFloor == -1) return;

    int updatedBlock = -1;
    try {
      Statement blockStatement = databaseRef.getConnection().createStatement();
      ResultSet blockRs =
          blockStatement.executeQuery(
              "SELECT * FROM PARKING_BLOCKS WHERE id=(SELECT block_id FROM PARKING_FLOORS WHERE id="
                  + updatedFloor
                  + ")");
      if (!blockRs.next())
        throw new IllegalStateException("No Parking_Block for updated floor: " + updatedFloor);
      boolean isBlockOccupied = blockRs.getBoolean("is_block_full");
      Statement floorStatement = databaseRef.getConnection().createStatement();
      ResultSet floorResulst =
          floorStatement.executeQuery(
              "SELECT * FROM PARKING_FLOORS WHERE IS_FLOOR_FULL="
                  + false
                  + " AND block_id="
                  + blockRs.getInt("id"));
      if (!floorResulst.next()) {
        System.out.println("No emply floors in block: " + blockRs.getInt("id"));
        if (isBlockOccupied != true) updatedBlock = blockRs.getInt("id");
        blockStatement.executeUpdate(
            "UPDATE PARKING_BLOCKS SET IS_BLOCK_FULL=true WHERE ID=" + blockRs.getInt("id"));
      } else {
        System.out.println(
            "Emply floors in block: "
                + blockRs.getInt("id")
                + " Floor: "
                + floorResulst.getInt("id"));
        if (isBlockOccupied == true) updatedBlock = blockRs.getInt("id");
        blockStatement.executeUpdate(
            "UPDATE PARKING_BLOCKS SET IS_BLOCK_FULL=false WHERE ID=" + blockRs.getInt("id"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (updatedBlock == -1) return;

    try {
      Statement lotStatement = databaseRef.getConnection().createStatement();
      ResultSet lotResult =
          lotStatement.executeQuery(
              "SELECT * FROM PARKING_LOTS WHERE ID=(SELECT PARKING_LOT_ID FROM PARKING_BLOCKS WHERE ID="
                  + updatedBlock
                  + ")");
      if (!lotResult.next())
        throw new IllegalStateException("No Parking_Lot for updated block: " + updatedBlock);

      Statement blockStatement = databaseRef.getConnection().createStatement();
      ResultSet blockRS =
          blockStatement.executeQuery(
              "SELECT * FROM PARKING_BLOCKS WHERE IS_BLOCK_FULL=false AND PARKING_LOT_ID="
                  + lotResult.getInt("ID"));

      if (!blockRS.next()) {
        lotStatement.executeUpdate(
            "UPDATE PARKING_LOTS SET IS_LOT_FULL=true WHERE ID=" + lotResult.getInt("ID"));
      } else {
        lotStatement.executeUpdate(
            "UPDATE PARKING_LOTS SET IS_LOT_FULL=false WHERE ID=" + lotResult.getInt("ID"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public ParkingSlip createNewSlip(Timestamp startTime, Timestamp endTime, int cost) {
    int slotId = -1;
    String slotCode = null;

    try {
      String slotQuery =
          "SELECT * FROM PARKING_SLOTS ps LEFT JOIN (SELECT P.ID, P.SLOT_ID FROM PARKING_SLIPS P WHERE NOT (((P.ENTRY_TIMESTAMP>=? AND P.ENTRY_TIMESTAMP>=?) OR (P.EXIT_TIMESTAMP<=? AND P.EXIT_TIMESTAMP<=?)))) as Pslip on ps.ID = Pslip.SLOT_ID WHERE Pslip.ID IS NULL AND ps.IS_OCCUPIED=false";
      PreparedStatement statement = databaseRef.getConnection().prepareStatement(slotQuery);

      statement.setTimestamp(1, startTime);
      statement.setTimestamp(2, endTime);
      statement.setTimestamp(3, startTime);
      statement.setTimestamp(4, endTime);

      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        slotId = rs.getInt("ID");
        slotCode = rs.getString("CODE") + rs.getString("SLOT_NUMBER");
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (slotId == -1 || slotCode == null)
      throw new IllegalStateException("Failed to find empty slot when printing ticket");

    System.out.println(slotId + ": " + slotCode);

    int createdSlip = -1;

    try {
      String query =
          "INSERT INTO PARKING_SLIPS (SLOT_ID, ENTRY_TIMESTAMP, EXIT_TIMESTAMP, BASE_COST, IS_PAID) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement preparedStatement =
          databaseRef.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      preparedStatement.setInt(1, slotId);
      preparedStatement.setTimestamp(2, startTime);
      preparedStatement.setTimestamp(3, endTime);
      preparedStatement.setInt(4, cost);
      preparedStatement.setBoolean(5, false);

      preparedStatement.execute();

      ResultSet rs = preparedStatement.getGeneratedKeys();
      rs.next();
      createdSlip = rs.getInt(1);

    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (createdSlip == -1) throw new IllegalStateException("Failed to add parking slip to DB");

    return new ParkingSlip(createdSlip, slotId, slotCode, startTime, endTime, cost / 100.0);
  }
}
