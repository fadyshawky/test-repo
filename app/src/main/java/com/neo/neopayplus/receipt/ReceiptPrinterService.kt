package com.neo.neopayplus.receipt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.neo.neopayplus.MyApplication
import com.neo.neopayplus.R
import com.sunmi.peripheral.printer.SunmiPrinterService
import com.sunmi.peripheral.printer.InnerResultCallback
import android.os.RemoteException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service for printing transaction receipts using Sunmi printer
 */
class ReceiptPrinterService(
    private val sunmiPrinterService: SunmiPrinterService?
) {
    companion object {
        private const val TAG = "ReceiptPrinterService"
        private const val RECEIPT_WIDTH = 384 // 58mm paper width in dots (typical)
    }

    /**
     * Print merchant copy only (for approved transactions - auto-print)
     */
    fun printMerchantCopy(data: ReceiptData, callback: PrintCallback? = null): Boolean {
        if (sunmiPrinterService == null) {
            Log.e(TAG, "Printer service not available")
            callback?.onError("Printer service not available")
            return false
        }

        return try {
            printApprovedReceiptCopy(data, isMerchantCopy = true, callback)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to print merchant copy: ${e.message}", e)
            callback?.onError(e.message ?: "Print failed")
            false
        }
    }

    /**
     * Print customer copy only (for approved transactions - user-initiated)
     */
    fun printCustomerCopy(data: ReceiptData, callback: PrintCallback? = null): Boolean {
        if (sunmiPrinterService == null) {
            Log.e(TAG, "Printer service not available")
            callback?.onError("Printer service not available")
            return false
        }

        return try {
            printApprovedReceiptCopy(data, isMerchantCopy = false, callback)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to print customer copy: ${e.message}", e)
            callback?.onError(e.message ?: "Print failed")
            false
        }
    }

    /**
     * Print approved transaction receipt (dual copy: merchant + customer)
     * @deprecated Use printMerchantCopy() and printCustomerCopy() separately for better UX
     */
    @Deprecated("Use printMerchantCopy() and printCustomerCopy() separately")
    fun printApprovedReceipt(data: ReceiptData, callback: PrintCallback? = null): Boolean {
        if (sunmiPrinterService == null) {
            Log.e(TAG, "Printer service not available")
            callback?.onError("Printer service not available")
            return false
        }

        return try {
            // Print merchant copy first, then customer copy when merchant copy completes
            printApprovedReceiptCopy(
                data, 
                isMerchantCopy = true, 
                object : PrintCallback {
                    override fun onSuccess() {
                        Log.d(TAG, "Merchant copy printed successfully, starting customer copy...")
                        // After merchant copy completes, print customer copy
                        printApprovedReceiptCopy(
                            data, 
                            isMerchantCopy = false,
                            callback // Final callback when customer copy completes
                        )
                    }
                    override fun onError(message: String) {
                        Log.e(TAG, "Merchant copy failed: $message")
                        callback?.onError(message)
                    }
                }
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to print approved receipt: ${e.message}", e)
            callback?.onError(e.message ?: "Print failed")
            false
        }
    }

    /**
     * Print declined transaction receipt (single customer copy)
     */
    fun printDeclinedReceipt(data: ReceiptData, callback: PrintCallback? = null): Boolean {
        if (sunmiPrinterService == null) {
            Log.e(TAG, "Printer service not available")
            callback?.onError("Printer service not available")
            return false
        }

        return try {
            // Print declined receipt (single copy)
            printDeclinedReceiptCopy(data, callback)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to print declined receipt: ${e.message}", e)
            callback?.onError(e.message ?: "Print failed")
            false
        }
    }
    
    interface PrintCallback {
        fun onSuccess()
        fun onError(message: String)
    }

    private fun printApprovedReceiptCopy(data: ReceiptData, isMerchantCopy: Boolean, callback: PrintCallback?) {
        val builder = ApprovedReceiptBuilder(data)
        val receiptLines = builder.build(isMerchantCopy)
        printReceiptLines(receiptLines, callback)
    }

    private fun printDeclinedReceiptCopy(data: ReceiptData, callback: PrintCallback?) {
        val builder = DeclinedReceiptBuilder(data)
        val receiptLines = builder.build()
        printReceiptLines(receiptLines, callback)
    }
    
    private fun printReceiptLines(lines: List<ReceiptLine>, callback: PrintCallback?) {
        if (sunmiPrinterService == null) {
            callback?.onError("Printer service not available")
            return
        }
        
        try {
            val service = sunmiPrinterService!!
            
            // Use printer buffer for batch printing (following Sunmi SDK demo pattern)
            service.enterPrinterBuffer(true)
            Log.d(TAG, "Entered printer buffer")
            
            // Print lines in order (interleaving logos and text as they appear)
            // This ensures logos and text appear in the correct sequence
            for (line in lines) {
                when (line) {
                    is ReceiptLine.Logo -> {
                        // Print logo bitmap
                        val bitmap = loadLogoFromAsset(line.assetPath)
                        if (bitmap != null) {
                            // Convert to monochrome for thermal printer (prevents black boxes)
                            val monochromeBitmap = convertToMonochrome(bitmap)
                            val resizedBitmap = resizeBitmap(monochromeBitmap, RECEIPT_WIDTH)
                            try {
                                // Set alignment using ESC/POS commands before printing bitmap
                                val alignmentCmd = when (line.alignment) {
                                    Alignment.LEFT -> byteArrayOf(0x1B, 0x61, 0x00) // ESC a 0 = Left
                                    Alignment.CENTER -> byteArrayOf(0x1B, 0x61, 0x01) // ESC a 1 = Center
                                    Alignment.RIGHT -> byteArrayOf(0x1B, 0x61, 0x02) // ESC a 2 = Right
                                }
                                service.sendRAWData(alignmentCmd, null)
                                service.printBitmap(resizedBitmap, null)
                                Log.d(TAG, "Printed bitmap from asset: ${line.assetPath} (alignment: ${line.alignment})")
                            } catch (e: RemoteException) {
                                Log.e(TAG, "Print bitmap error: ${e.message}", e)
                                // Continue even if bitmap fails
                            } catch (e: Exception) {
                                Log.e(TAG, "Print bitmap error: ${e.message}", e)
                                // Continue even if bitmap fails
                            }
                        } else {
                            Log.w(TAG, "Failed to load bitmap from asset: ${line.assetPath}")
                        }
                    }
                    is ReceiptLine.Text -> {
                        // Print text line
                        try {
                            // Set alignment before each line
                            val alignment = getAlignment(line.alignment)
                            service.setAlignment(alignment, null)
                            
                            // Set font size before each line
                            val fontSize = getFontSize(line.fontSize)
                            service.setFontSize(fontSize, null)
                            
                            // Set bold if needed
                            if (line.bold) {
                                service.setPrinterStyle(
                                    com.sunmi.peripheral.printer.WoyouConsts.ENABLE_BOLD,
                                    com.sunmi.peripheral.printer.WoyouConsts.ENABLE
                                )
                            } else {
                                service.setPrinterStyle(
                                    com.sunmi.peripheral.printer.WoyouConsts.ENABLE_BOLD,
                                    com.sunmi.peripheral.printer.WoyouConsts.DISABLE
                                )
                            }
                            
                            // Print the text with newline
                            val textToPrint = if (line.text.endsWith("\n")) {
                                line.text
                            } else {
                                "${line.text}\n"
                            }
                            service.printText(textToPrint, null)
                            
                            // Reset bold after printing
                            if (line.bold) {
                                service.setPrinterStyle(
                                    com.sunmi.peripheral.printer.WoyouConsts.ENABLE_BOLD,
                                    com.sunmi.peripheral.printer.WoyouConsts.DISABLE
                                )
                            }
                        } catch (e: RemoteException) {
                            Log.e(TAG, "Print text line error: ${e.message}", e)
                            // Continue even if one line fails
                        } catch (e: Exception) {
                            Log.e(TAG, "Print text line error: ${e.message}", e)
                            // Continue even if one line fails
                        }
                    }
                    is ReceiptLine.Separator -> {
                        // Print separator as text
                        try {
                            service.setAlignment(0, null) // Left align
                            service.printText("--------------------------------\n", null)
                        } catch (e: Exception) {
                            Log.e(TAG, "Print separator error: ${e.message}", e)
                        }
                    }
                    is ReceiptLine.SignatureLine -> {
                        // Print signature line
                        try {
                            service.setAlignment(1, null) // Center align
                            service.printText("_________________________\n", null)
                            service.printText("Customer Signature\n", null)
                        } catch (e: Exception) {
                            Log.e(TAG, "Print signature line error: ${e.message}", e)
                        }
                    }
                    is ReceiptLine.Empty -> {
                        // Print empty line
                        try {
                            service.printText("\n", null)
                        } catch (e: Exception) {
                            Log.e(TAG, "Print empty line error: ${e.message}", e)
                        }
                    }
                }
            }
            
            Log.d(TAG, "Printed ${lines.size} receipt lines")
            
            // Feed paper
            service.lineWrap(3, null)
            Log.d(TAG, "Called lineWrap(3)")
            
            // Exit buffer with callback - this fires when all buffered content is printed
            // Following Sunmi SDK demo pattern: exitPrinterBufferWithCallback(true, callback)
            val exitCallback = object : InnerResultCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(isSuccess: Boolean) {
                    Log.d(TAG, "exitPrinterBufferWithCallback onRunResult: isSuccess=$isSuccess")
                    if (!isSuccess) {
                        Log.e(TAG, "Exit buffer failed")
                        callback?.onError("Print operation failed")
                        return
                    }
                    
                    // After buffer exits, cut paper
                    try {
                        Log.d(TAG, "Cutting paper...")
                        service.cutPaper(object : InnerResultCallback() {
                            @Throws(RemoteException::class)
                            override fun onRunResult(isSuccess: Boolean) {
                                Log.d(TAG, "cutPaper onRunResult: isSuccess=$isSuccess")
                                if (isSuccess) {
                                    Log.d(TAG, "Receipt print completed successfully")
                                    callback?.onSuccess()
                                } else {
                                    Log.e(TAG, "Paper cut failed")
                                    callback?.onError("Paper cut failed")
                                }
                            }
                            
                            @Throws(RemoteException::class)
                            override fun onReturnString(result: String?) {
                                Log.d(TAG, "cutPaper onReturnString: $result")
                            }
                            
                            @Throws(RemoteException::class)
                            override fun onRaiseException(code: Int, msg: String?) {
                                Log.e(TAG, "Cut paper exception: code=$code, msg=$msg")
                                callback?.onError(msg ?: "Cut paper exception: $code")
                            }
                            
                            @Throws(RemoteException::class)
                            override fun onPrintResult(code: Int, msg: String?) {
                                Log.d(TAG, "cutPaper onPrintResult: code=$code, msg=$msg")
                                if (code != 0) {
                                    Log.e(TAG, "Cut paper result error: code=$code, msg=$msg")
                                    callback?.onError(msg ?: "Cut paper error: $code")
                                }
                            }
                        })
                    } catch (e: RemoteException) {
                        Log.e(TAG, "Cut paper error: ${e.message}", e)
                        callback?.onError("Cut paper failed: ${e.message}")
                    }
                }
                
                @Throws(RemoteException::class)
                override fun onReturnString(result: String?) {
                    Log.d(TAG, "exitPrinterBufferWithCallback onReturnString: $result")
                }
                
                @Throws(RemoteException::class)
                override fun onRaiseException(code: Int, msg: String?) {
                    Log.e(TAG, "Exit buffer exception: code=$code, msg=$msg")
                    callback?.onError(msg ?: "Exit buffer exception: $code")
                }
                
                @Throws(RemoteException::class)
                override fun onPrintResult(code: Int, msg: String?) {
                    Log.d(TAG, "exitPrinterBufferWithCallback onPrintResult: code=$code, msg=$msg")
                    if (code != 0) {
                        Log.e(TAG, "Exit buffer result error: code=$code, msg=$msg")
                        callback?.onError(msg ?: "Exit buffer error: $code")
                    }
                }
            }
            
            Log.d(TAG, "Calling exitPrinterBufferWithCallback...")
            service.exitPrinterBufferWithCallback(true, exitCallback)
            
        } catch (e: Exception) {
            Log.e(TAG, "Print error: ${e.message}", e)
            callback?.onError(e.message ?: "Print failed")
            // Try to exit buffer on error
            try {
                sunmiPrinterService?.exitPrinterBuffer(false)
            } catch (ex: Exception) {
                Log.e(TAG, "Error exiting printer buffer: ${ex.message}", ex)
            }
        }
    }
    
    
    
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        if (bitmap.width <= maxWidth) {
            return bitmap
        }
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
        val newHeight = (maxWidth * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true)
    }
    
    /**
     * Convert bitmap to monochrome (black and white) for thermal printer
     * Thermal printers work best with 1-bit monochrome images
     * This prevents black boxes and ensures proper printing
     */
    private fun convertToMonochrome(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val monochrome = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val alpha = (pixel shr 24) and 0xFF
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            
            // Skip transparent pixels
            if (alpha < 128) {
                pixels[i] = 0xFFFFFFFF.toInt() // White for transparent
                continue
            }
            
            // Convert to grayscale using luminance formula
            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            // Threshold: below 128 = black, above = white
            val newPixel = if (gray < 128) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
            pixels[i] = newPixel
        }
        
        monochrome.setPixels(pixels, 0, width, 0, 0, width, height)
        return monochrome
    }

    private fun loadLogoFromAsset(assetPath: String?): Bitmap? {
        if (assetPath == null) return null
        return try {
            val inputStream = MyApplication.app.assets.open(assetPath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load logo from asset '$assetPath': ${e.message}", e)
            null
        }
    }

    private fun getFontSize(size: FontSize): Float {
        return when (size) {
            FontSize.SMALL -> 20f
            FontSize.NORMAL -> 24f
            FontSize.LARGE -> 32f
            FontSize.TITLE -> 40f
        }
    }

    private fun getAlignment(alignment: Alignment): Int {
        return when (alignment) {
            Alignment.LEFT -> 0
            Alignment.CENTER -> 1
            Alignment.RIGHT -> 2
        }
    }
}

/**
 * Receipt line types for building receipt content
 */
sealed class ReceiptLine {
    data class Text(
        val text: String,
        val alignment: Alignment = Alignment.LEFT,
        val fontSize: FontSize = FontSize.NORMAL,
        val bold: Boolean = false
    ) : ReceiptLine()

    object Separator : ReceiptLine()
    
    data class Logo(
        val assetPath: String,
        val alignment: Alignment = Alignment.CENTER
    ) : ReceiptLine()
    
    object SignatureLine : ReceiptLine()
    
    object Empty : ReceiptLine()
}

enum class Alignment {
    LEFT,
    CENTER,
    RIGHT
}

enum class FontSize {
    SMALL,
    NORMAL,
    LARGE,
    TITLE
}
