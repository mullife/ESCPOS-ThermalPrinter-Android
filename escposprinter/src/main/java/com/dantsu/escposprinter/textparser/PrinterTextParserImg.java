package com.dantsu.escposprinter.textparser;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.EscPosPrinterCommands;
import com.dantsu.escposprinter.EscPosPrinterSize;


public class PrinterTextParserImg implements IPrinterTextParserElement {

    private static final String TAG = "PrinterTextParserImg-Mullife";

    /**
     * Convert Drawable instance to a hexadecimal string of the image data.
     *
     * @param printerSize A EscPosPrinterSize instance that will print the image.
     * @param drawable Drawable instance to be converted.
     * @return A hexadecimal string of the image data. Empty string if Drawable cannot be cast to BitmapDrawable.
     */
    public static String bitmapToHexadecimalString(EscPosPrinterSize printerSize, Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return PrinterTextParserImg.bitmapToHexadecimalString(printerSize, (BitmapDrawable) drawable);
        }
        return "";
    }
    
    /**
     * Convert BitmapDrawable instance to a hexadecimal string of the image data.
     *
     * @param printerSize A EscPosPrinterSize instance that will print the image.
     * @param bitmapDrawable BitmapDrawable instance to be converted.
     * @return A hexadecimal string of the image data.
     */
    public static String bitmapToHexadecimalString(EscPosPrinterSize printerSize, BitmapDrawable bitmapDrawable) {
        return PrinterTextParserImg.bitmapToHexadecimalString(printerSize, bitmapDrawable.getBitmap());
    }
    
    /**
     * Convert Bitmap instance to a hexadecimal string of the image data.
     *
     * @param printerSize A EscPosPrinterSize instance that will print the image.
     * @param bitmap Bitmap instance to be converted.
     * @return A hexadecimal string of the image data.
     */
    public static String bitmapToHexadecimalString(EscPosPrinterSize printerSize, Bitmap bitmap) {
        return PrinterTextParserImg.bytesToHexadecimalString(printerSize.bitmapToBytes(bitmap, EscPosPrinterCommands.PRINT_BITIMAGE_LEVEL1));
    }

    /**
     * Convert Bitmap instance to a hexadecimal string of the image data.
     *
     * @param printerSize A EscPosPrinterSize instance that will print the image.
     * @param bitmap Bitmap instance to be converted.
     * @param format only two level or more than four level with gray
     * @return A hexadecimal string of the image data.
     */
    public static String bitmapToHexadecimalString(EscPosPrinterSize printerSize, Bitmap bitmap, int format) {
        return PrinterTextParserImg.bytesToHexadecimalString(printerSize.bitmapToBytes(bitmap, format));
    }
    
    /**
     * Convert byte array to a hexadecimal string of the image data.
     *
     * @param bytes Bytes contain the image in ESC/POS command.
     * @return A hexadecimal string of the image data.
     */
    public static String bytesToHexadecimalString(byte[] bytes) {
        StringBuilder imageHexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hexString = Integer.toHexString(aByte & 0xFF);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            imageHexString.append(hexString);
        }
        return imageHexString.toString();
    }
    
    /**
     * Convert hexadecimal string of the image data to bytes ESC/POS command.
     *
     * @param hexString Hexadecimal string of the image data.
     * @return Bytes contain the image in ESC/POS command.
     */
    public static byte[] hexadecimalStringToBytes(String hexString) throws NumberFormatException {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int pos = i * 2;
            bytes[i] = (byte) Integer.parseInt(hexString.substring(pos, pos + 2), 16);
        }

        return bytes;
    }
    
    
    private int length;
    private byte[] image;
    
    /**
     * Create new instance of PrinterTextParserImg.
     *
     * @param printerTextParserColumn Parent PrinterTextParserColumn instance.
     * @param textAlign Set the image alignment. Use PrinterTextParser.TAGS_ALIGN_... constants.
     * @param hexadecimalString Hexadecimal string of the image data.
     */
    public PrinterTextParserImg(PrinterTextParserColumn printerTextParserColumn, String textAlign, String hexadecimalString) {
        this(printerTextParserColumn, textAlign, PrinterTextParserImg.hexadecimalStringToBytes(hexadecimalString));
    }

    /**
     * Create new instance of PrinterTextParserImg.
     *
     * @param printerTextParserColumn Parent PrinterTextParserColumn instance.
     * @param textAlign Set the image alignment. Use PrinterTextParser.TAGS_ALIGN_... constants.
     * @param image Bytes contain the image in ESC/POS command.
     */
    public PrinterTextParserImg(PrinterTextParserColumn printerTextParserColumn, String textAlign, byte[] image) {
        EscPosPrinter printer = printerTextParserColumn.getLine().getTextParser().getPrinter();

        int byteWidth = ((int) image[4] & 0xFF),
                width = byteWidth * 8,
                height = ((int) image[6] & 0xFF),
                nbrByteDiff = (int) Math.floor(((float) (printer.getPrinterWidthPx() - width)) / 8f),
                nbrWhiteByteToInsert = 0;

        switch (textAlign) {
            case PrinterTextParser.TAGS_ALIGN_CENTER:
                nbrWhiteByteToInsert = Math.round(((float) nbrByteDiff) / 2f);
                break;
            case PrinterTextParser.TAGS_ALIGN_RIGHT:
                nbrWhiteByteToInsert = nbrByteDiff;
                break;
        }

        if (nbrWhiteByteToInsert > 0) {
            int newByteWidth = byteWidth + nbrWhiteByteToInsert;
            byte[] newImage = new byte[newByteWidth * height + 8];
            System.arraycopy(image, 0, newImage, 0, 8);
            newImage[4] = (byte) newByteWidth;
            for (int i = 0; i < height; i++) {
                System.arraycopy(image, (byteWidth * i + 8), newImage, (newByteWidth * i + nbrWhiteByteToInsert + 8), byteWidth);
            }
            image = newImage;
        }

        this.length = (int) Math.ceil(((float) (((int) image[4] & 0xFF) * 8)) / ((float) printer.getPrinterCharSizeWidthPx()));
        this.image = image;
    }

    /**
     * Get the image width in char length.
     *
     * @return int
     */
    @Override
    public int length() {
        return this.length;
    }

    /**
     * Print image
     *
     * @param printerSocket Instance of EscPosPrinterCommands
     * @return this Fluent method
     */
    @Override
    public PrinterTextParserImg print(EscPosPrinterCommands printerSocket) {
        printerSocket.printImage(this.image);
        return this;
    }
}
