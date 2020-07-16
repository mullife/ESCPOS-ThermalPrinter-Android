package com.dantsu.thermalprinter.async;

import android.content.Context;
import android.util.Log;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;

public class AsyncBluetoothEscPosPrint extends AsyncEscPosPrint {

    private static final String TAG = "AsyncBluetoothEscPosPrint-Mullife";

    public AsyncBluetoothEscPosPrint(Context context) {
        super(context);
    }

    protected Integer doInBackground(AsyncEscPosPrinter... printersData) {

        Log.i(TAG, "doInBackground");
        if (printersData.length == 0) {
            Log.i(TAG, "doInBackground FINISH_NO_PRINTER");
            return AsyncEscPosPrint.FINISH_NO_PRINTER;
        }

        Log.i(TAG, "doInBackground getPrinterConnection");
        AsyncEscPosPrinter printerData = printersData[0];

        DeviceConnection deviceConnection = printerData.getPrinterConnection();

        if(deviceConnection == null) {
            this.publishProgress(AsyncEscPosPrint.PROGRESS_CONNECTING);
            Log.i(TAG, "doInBackground AsyncEscPosPrinter again");
            printersData[0] = new AsyncEscPosPrinter(
                    BluetoothPrintersConnections.selectFirstPaired(),
                    printerData.getPrinterDpi(),
                    printerData.getPrinterWidthMM(),
                    printerData.getPrinterNbrCharactersPerLine()
            );
            printersData[0].setTextToPrint(printerData.getTextToPrint());
        }

        return super.doInBackground(printersData);
    }
}
