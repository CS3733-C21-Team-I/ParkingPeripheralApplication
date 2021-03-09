package edu.wpi.cs3733.c21.teamI.peripheral;

import edu.wpi.cs3733.c21.teamI.Main;
import edu.wpi.cs3733.c21.teamI.database.ParkingPeripheralServerManager;
import edu.wpi.cs3733.c21.teamI.parking.Floor;
import edu.wpi.cs3733.c21.teamI.serial.ParkingSerialInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingPeripheral {

  private ParkingSerialInput input;

  private Floor assignedFloor;

  private static final Map<Floor, List<String>> alreadyAlocatedSensors = new HashMap<>();

  public ParkingPeripheral() {
    this.input = new ParkingSerialInput();
    this.assignedFloor = null;

    input.setController(this);
    input.initialize(new String[] {Main.comPorts});
  }

  public void assignSensors(int numSensors, Floor floor) {
    if (numSensors > 8)
      throw new IllegalArgumentException("Peripherals currently cannot handle more than 8 sensors");

    this.assignedFloor = floor;

    if (!alreadyAlocatedSensors.containsKey(floor))
      alreadyAlocatedSensors.put(floor, new ArrayList<>());

    List<String> trackedCodes = new ArrayList<>();
    for (Map.Entry<String, Integer> slot : floor.getSlotCodes().entrySet()) {
      if (!alreadyAlocatedSensors.get(floor).contains(slot.getKey())) {
        alreadyAlocatedSensors.get(floor).add(slot.getKey());
        System.out.println(slot.getKey());
        trackedCodes.add(slot.getKey());
        System.out.println(slot.getValue());
        if (trackedCodes.size() >= numSensors) break;
      }
    }

    if (trackedCodes.size() != numSensors)
      throw new IllegalArgumentException(
          "Attempted to allocate more sensors than untracked slots in floor");

    input.connectSensors(trackedCodes);
  }

  public void handleSlotUpdate(String code, boolean occupied) {
    if (assignedFloor == null) return;

    int effectedSlot = assignedFloor.getSlotCodes().get(code);

    ParkingPeripheralServerManager.getInstance().updateSlotOccupancy(effectedSlot, occupied);
  }
}
