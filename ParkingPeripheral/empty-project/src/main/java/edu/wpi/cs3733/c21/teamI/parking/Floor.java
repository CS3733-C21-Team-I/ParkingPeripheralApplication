package edu.wpi.cs3733.c21.teamI.parking;

import java.util.Map;

public class Floor {

  private int id;
  private Map<String, Integer> slotCodes;
  private int floorNum;
  private boolean isCovered;
  private boolean isDisabledAcessable;
  private boolean isStaffOnly;

  private Block block;

  public Floor(
      int id,
      Map<String, Integer> slotCodes,
      int floorNum,
      boolean isCovered,
      boolean isDisabledAcessable,
      boolean isStaffOnly) {
    this.id = id;
    this.slotCodes = slotCodes;
    this.floorNum = floorNum;
    this.isCovered = isCovered;
    this.isDisabledAcessable = isDisabledAcessable;
    this.isStaffOnly = isStaffOnly;
    this.block = null;
  }

  public void setBlock(Block block) {
    this.block = block;
  }

  public Map<String, Integer> getSlotCodes() {
    return slotCodes;
  }

  public int getFloorNum() {
    return floorNum;
  }
}
