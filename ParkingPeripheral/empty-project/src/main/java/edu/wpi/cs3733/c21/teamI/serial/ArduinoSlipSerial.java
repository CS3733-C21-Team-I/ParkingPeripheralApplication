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
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

public class ArduinoSlipSerial implements SerialPortEventListener, SlipSerialInterface {

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

  @Override
  public DateTimeFormatter getDateFormat() {
    return DateTimeFormatter.ofPattern("dd/MM/yyyy");
  }

  @Override
  public DateTimeFormatter getTimeFormat() {
    return DateTimeFormatter.ofPattern("hh:mma");
  }

  @Override
  public DecimalFormat getCostFormat() {
    return new DecimalFormat("$00.00");
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

  public synchronized void serialEvent(SerialPortEvent oEvent) {}

  /**
   * Connects sensors to the Serial Peripheral takes in a list of 4 char codes to be used in
   * communication Protocol SOH, date char[10], GS, startTime char[7], GS, endTime char[7], GS, cost
   * char[6], GS, id char[5], EOT
   */
  public void printTicket(
      String slotCode, String date, String startTime, String endTime, String cost, String id) {
    if (date.length() != 10
        || slotCode.length() != 4
        || startTime.length() != 7
        || endTime.length() != 7
        || cost.length() != 6
        || id.length() != 5) {
      throw new IllegalArgumentException("Argument of wrong length to printTicket");
    }

    try {
      output.write(1);
      output.write(slotCode.getBytes(StandardCharsets.UTF_8));
      output.write(29);
      output.write(date.getBytes(StandardCharsets.UTF_8));
      output.write(29);
      output.write(startTime.getBytes(StandardCharsets.UTF_8));
      output.write(29);
      output.write(endTime.getBytes(StandardCharsets.UTF_8));
      output.write(29);
      output.write(cost.getBytes(StandardCharsets.UTF_8));
      output.write(29);
      output.write(id.getBytes(StandardCharsets.UTF_8));
      output.write(4);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
