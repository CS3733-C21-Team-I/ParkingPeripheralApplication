package edu.wpi.cs3733.c21.teamI.serial;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public interface SlipSerialInterface {

  DateTimeFormatter getDateFormat();

  DateTimeFormatter getTimeFormat();

  DecimalFormat getCostFormat();

  void initialize(String[] comPorts);

  void close();

  /**
   * Connects sensors to the Serial Peripheral takes in a list of 4 char codes to be used in
   * communication Protocol SOH, slot char[4], GS, date char[10], GS, startTime char[7], GS, endTime
   * char[7], GS, cost char[6], GS, id char[5], EOT
   */
  public void printTicket(
      String slotCode, String date, String startTime, String endTime, String cost, String id);
}
