package com.daon.onorder.assist;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ESCPOSSample {

    private final char ESC = ESCPOS.ESC;

    public ESCPOSSample()
    {
    }

    public void Print_Sample1(ESCPOSPrinter escposPrinter) throws UnsupportedEncodingException
    {
        escposPrinter.printNormal(ESC+"|cA"+ESC+"|2CReceipt\r\n\r\n\r\n");
        escposPrinter.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
        escposPrinter.printNormal(ESC+"|cAThank you for coming to our shop!\n");
        escposPrinter.printNormal(ESC+"|cADate\n\n");
        escposPrinter.printNormal("Chicken                             $10.00\n");
        escposPrinter.printNormal("Hamburger                           $20.00\n");
        escposPrinter.printNormal("Pizza                               $30.00\n");
        escposPrinter.printNormal("Lemons                              $40.00\n");
        escposPrinter.printNormal("Drink                               $50.00\n");
        escposPrinter.printNormal("Excluded tax                       $150.00\n");
        escposPrinter.printNormal(ESC+"|uCTax(5%)                              $7.50\n");
        escposPrinter.printNormal(ESC+"|bC"+ESC+"|2CTotal         $157.50\n\n");
        escposPrinter.printNormal("Payment                            $200.00\n");
        escposPrinter.printNormal("Change                              $42.50\n\n");
        escposPrinter.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
        escposPrinter.lineFeed(4);
        escposPrinter.cutPaper();
    }

    public void Print_Sample2(ESCPOSPrinter escposPrinter) throws UnsupportedEncodingException
    {
        escposPrinter.printText("Receipt\r\n\r\n\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH);
        escposPrinter.printText("TEL (123)-456-7890\r\n", LKPrint.LK_ALIGNMENT_RIGHT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Thank you for coming to our shop!\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Chicken                             $10.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Hamburger                           $20.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Pizza                               $30.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Lemons                              $40.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Drink                               $50.00\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Excluded tax                       $150.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Tax(5%)                              $7.50\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_UNDERLINE, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Total         $157.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH);
        escposPrinter.printText("Payment                            $200.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("Change                              $42.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        // Reverse print
        //posPtr.printText("Change                              $42.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT | LKPrint.LK_FNT_REVERSE, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
        escposPrinter.lineFeed(4);
        escposPrinter.cutPaper();
    }

    public void Print_Image(ESCPOSPrinter escposPrinter) throws IOException
    {
        escposPrinter.printBitmap("//sdcard//temp//test//car_s.jpg", LKPrint.LK_ALIGNMENT_CENTER);
        escposPrinter.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", LKPrint.LK_ALIGNMENT_LEFT);
        escposPrinter.printBitmap("//sdcard//temp//test//denmark_flag.jpg", LKPrint.LK_ALIGNMENT_RIGHT);
    }

    public void Print_Bar_1D(ESCPOSPrinter escposPrinter) throws UnsupportedEncodingException
    {
        String barCodeData = "123456789012";

        escposPrinter.printString("UPCA\r\n");
        escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCA, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("UPCE\r\n");
        escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCE, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("EAN8\r\n");
        escposPrinter.printBarCode("1234567", ESCPOSConst.LK_BCS_EAN8, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("EAN13\r\n");
        escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_EAN13, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("CODE39\r\n");
        escposPrinter.printBarCode("ABCDEFGHI", ESCPOSConst.LK_BCS_Code39, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("ITF\r\n");
        escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_ITF, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("CODABAR\r\n");
        escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Codabar, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("CODE93\r\n");
        escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Code93, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.printString("CODE128\r\n");
        escposPrinter.printBarCode("{BNo.{C4567890120", ESCPOSConst.LK_BCS_Code128, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        escposPrinter.lineFeed(4);
        escposPrinter.cutPaper();
    }

    public void Print_Bar_2D(ESCPOSPrinter escposPrinter) throws UnsupportedEncodingException
    {
        String data = "ABCDEFGHIJKLMN";
        escposPrinter.printString("PDF417\r\n");
        escposPrinter.printPDF417(data, data.length(), 0, 10, ESCPOSConst.LK_ALIGNMENT_LEFT);
        escposPrinter.printString("QRCode\r\n");
        escposPrinter.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
        escposPrinter.lineFeed(4);
        escposPrinter.cutPaper();
    }
}
