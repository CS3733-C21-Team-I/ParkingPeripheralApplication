package edu.wpi.cs3733.c21.teamI.serial;

import edu.wpi.cs3733.c21.teamI.peripheral.ParkingPeripheral;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;

public class ParkingSerialInput implements SerialPortEventListener {

  private ParkingPeripheral controller;

  private SerialPort serialPort;
  private BufferedReader input;
  private OutputStream output;
  private static final int TIME_OUT = 2000;
  private static final int DATA_RATE = 9600;

  private static final String PORT_NAMES[] = {
    "/dev/tty.usbserial-A9007UX1", // Mac OS X
    "/dev/ttyUSB0", // Linux
    "COM4", // Windows
  };

  public void setController(ParkingPeripheral controller) {
    this.controller = controller;
  }

  public void initialize(String[] comPorts) {
    CommPortIdentifier portId = null;
    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

    // First, Find an instance of serial port as set in PORT_NAMES.
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
      for (String portName : comPorts) {
        if (currPortId.getName().equals(portName)) {
          portId = currPortId;
          break;
        }
      }
    }
    if (portId == null) {
      System.out.println("Could not find COM port.");
      return;
    }

    try {
      serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
      serialPort.setSerialPortParams(
          DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

      // open the streams
      input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
      output = serialPort.getOutputStream();

      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  public synchronized void close() {
    if (serialPort != null) {
      serialPort.removeEventListener();
      serialPort.close();
    }
  }

  public synchronized void serialEvent(SerialPortEvent oEvent) {
    System.out.println("Event");
    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      try {
        String inputLine = null;
        if (input.ready()) {
          inputLine = input.readLine();
          inputLine = inputLine.replaceAll("[^\\x00-\\x7F]", "");

          String[] input = inputLine.split(" ");
          if (!input[0].equals("")) controller.handleSlotUpdate(input[0], input[1].equals("1"));
        }

      } catch (Exception e) {
        System.err.println(e.toString());
      }
    }
    // Ignore all the other eventTypes, but you should consider the other ones.
  }

  /**
   * Connects sensors to the Serial Peripheral takes in a list of 4 char codes to be used in
   * communication Protocol SOH, code1 char 1-4, GS, code2 char 1-4, GS, ..., EOT
   *
   * @param parkingCodes
   */
  public void connectSensors(List<String> parkingCodes) {
    try {
      output.write(1);
      for (int i = 0; i < parkingCodes.size(); i++) {
        if (parkingCodes.get(i).length() != 4)
          throw new IllegalArgumentException("Passed Parking Code of not lenght 4");
        output.write(parkingCodes.get(i).getBytes(StandardCharsets.UTF_8));
        if (i == parkingCodes.size() - 1) {
          output.write(4);
        } else {
          output.write(29);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
