package com.dantsu.thermalprinter.async;

import android.util.Log;

import com.dantsu.escposprinter.EscPosPrinterSize;
import com.dantsu.escposprinter.connection.DeviceConnection;

public class AsyncEscPosPrinter extends EscPosPrinterSize {
    private static final String TAG = "AsyncEscPosPrinter-Mullife";

    private DeviceConnection printerConnection;
    private String textToPrint = "";

    public AsyncEscPosPrinter(DeviceConnection printerConnection, int printerDpi, float printerWidthMM, int printerNbrCharactersPerLine) {
        super(printerDpi, printerWidthMM, printerNbrCharactersPerLine);
        Log.i(TAG,"AsyncEscPosPrinter,printerConnection:" + printerConnection + ",printerDpi:"+ printerDpi + ",printerWidthMM:" + printerWidthMM + ",printerNbrCharactersPerLine" + printerNbrCharactersPerLine);
        this.printerConnection = printerConnection;
    }

    public DeviceConnection getPrinterConnection() {
        Log.i(TAG,"getPrinterConnection");
        return this.printerConnection;
    }

    public AsyncEscPosPrinter setTextToPrint(String textToPrint) {
        this.textToPrint = textToPrint;
        return this;
    }

    public String getTextToPrint() {
        return this.textToPrint;
    }
}
