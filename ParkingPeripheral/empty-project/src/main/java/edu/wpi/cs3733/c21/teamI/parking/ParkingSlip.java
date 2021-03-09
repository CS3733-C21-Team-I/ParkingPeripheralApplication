package edu.wpi.cs3733.c21.teamI.parking;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ParkingSlip {

  public static Map<Integer, Double> pricingMap;

  private static final double OVERCHARGE_PER_5_MIN = .50;

  private static final double PENALTY_UNCALCULATED = -1;

  static {
    pricingMap = new HashMap<>();
    pricingMap.put(60, 5.00);
    pricingMap.put(120, 5.50);
    pricingMap.put(180, 6.00);
    pricingMap.put(300, 7.00);
    pricingMap.put(540, 8.00);
  }

  private int id;
  private int parkingCode;
  private String parkingSlot;
  private Timestamp entryTimestamp;
  private Timestamp endTimestamp;
  private double baseCost;
  private double penalty;

  public ParkingSlip(
      int id,
      int parkingCode,
      String parkingSlot,
      Timestamp entryTimestamp,
      Timestamp endTimestamp,
      double baseCost) {
    this.id = id;
    this.parkingCode = parkingCode;
    this.parkingSlot = parkingSlot;
    this.entryTimestamp = entryTimestamp;
    this.endTimestamp = endTimestamp;
    this.baseCost = baseCost;
    this.penalty = PENALTY_UNCALCULATED;
  }

  public void calculatePenalty(Timestamp currentTime) {
    int minOver = (int) (currentTime.getTime() - endTimestamp.getTime() / 1000L / 60L);
    if (minOver <= 0) penalty = 0;
    else {
      penalty = ((minOver / 5) + 1) * OVERCHARGE_PER_5_MIN;
    }
  }

  public int getId() {
    return id;
  }

  public int getParkingCode() {
    return parkingCode;
  }

  public String getParkingSlot() {
    return parkingSlot;
  }

  public Timestamp getEntryTimestamp() {
    return entryTimestamp;
  }

  public Timestamp getEndTimestamp() {
    return endTimestamp;
  }

  public double getBaseCost() {
    return baseCost;
  }

  public double getPenalty() {
    return penalty;
  }
}
