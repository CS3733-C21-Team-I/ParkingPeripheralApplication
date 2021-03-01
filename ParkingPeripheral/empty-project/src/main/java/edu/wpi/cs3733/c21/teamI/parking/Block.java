package edu.wpi.cs3733.c21.teamI.parking;

import java.util.ArrayList;
import java.util.List;

public class Block {

  private int id;
  private String blockCode;

  private List<Floor> floors;
  private Lot lot;

  public Block(int id, String blockCode) {
    this.id = id;
    this.blockCode = blockCode;
    this.floors = new ArrayList<>();
    this.lot = null;
  }

  public void addFloor(Floor f) {
    floors.add(f);
    f.setBlock(this);
  }

  public void setLot(Lot lot) {
    this.lot = lot;
  }

  public String getBlockCode() {
    return blockCode;
  }

  public Floor getFloorForNum(int num) {
    return floors.stream()
        .filter(n -> n.getFloorNum() == num)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Attemted to acees nonexistant floor: " + num));
  }
}
