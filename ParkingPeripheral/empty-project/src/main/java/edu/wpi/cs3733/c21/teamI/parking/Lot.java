package edu.wpi.cs3733.c21.teamI.parking;

import java.util.ArrayList;
import java.util.List;

public class Lot {

  private int id;
  private String name;
  private boolean isReentyAllowed;
  private boolean isValetAvailable;

  private List<Block> blocks;

  public Lot(int id, String name, boolean isReentyAllowed, boolean isValetAvailable) {
    this.id = id;
    this.name = name;
    this.isReentyAllowed = isReentyAllowed;
    this.isValetAvailable = isValetAvailable;
    this.blocks = new ArrayList<>();
  }

  public void addBlock(Block newBlock) {
    blocks.add(newBlock);
    newBlock.setLot(this);
  }

  public Block getBlockForCode(String code) {
    return blocks.stream()
        .filter(n -> n.getBlockCode().equals(code))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Attemptled to get non existant block with code: " + code));
  }

  public String getName() {
    return name;
  }
}
