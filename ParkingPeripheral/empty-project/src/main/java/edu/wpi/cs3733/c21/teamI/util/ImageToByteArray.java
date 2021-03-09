package edu.wpi.cs3733.c21.teamI.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageToByteArray {
  public static void main(String args[]) throws Exception {
    System.out.println(System.getProperty("user.dir") + "\\ParkingLogo.jpg");
    BufferedImage bImage =
        ImageIO.read(new File(System.getProperty("user.dir") + "\\ParkingLogo.jpg"));
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ImageIO.write(bImage, "jpg", bos);
    System.out.println("W: " + bImage.getWidth());
    System.out.println("H: " + bImage.getHeight());
    byte[] data = bos.toByteArray();

    for (int i = 0; i < data.length; i++) {
      if (i % 8 == 0) {
        System.out.println();
      }
      System.out.print("0x");
      System.out.print(String.format("%02X", data[i]));
      System.out.print(", ");
    }
  }
}
