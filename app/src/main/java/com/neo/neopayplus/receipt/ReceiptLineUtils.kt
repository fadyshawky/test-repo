package com.neo.neopayplus.receipt

/**
 * Common utility functions for receipt line formatting
 * Follows DRY principle to avoid code duplication
 */
object ReceiptLineUtils {
    
    private const val DEFAULT_RECEIPT_WIDTH = 32 // Typical thermal receipt width in characters
    
    /**
     * Create a label-value line with proper spacing
     * Format: Label Value (left-aligned, with spacing)
     * 
     * @param label The label text
     * @param value The value text
     * @param receiptWidth The width of the receipt (default: 32)
     * @return ReceiptLine with formatted text
     */
    fun createLabelValueLine(label: String, value: String, receiptWidth: Int = DEFAULT_RECEIPT_WIDTH): ReceiptLine {
        val labelLength = label.length
        val valueLength = value.length
        val availableSpace = receiptWidth - labelLength - valueLength
        
        return if (availableSpace > 0) {
            val spacing = " ".repeat(availableSpace)
            val line = "$label$spacing$value"
            ReceiptLine.Text(line, Alignment.LEFT, FontSize.NORMAL)
        } else {
            // If too long, just concatenate with single space
            ReceiptLine.Text("$label $value", Alignment.LEFT, FontSize.NORMAL)
        }
    }
    
    /**
     * Create a left-right value line with proper spacing
     * Format: LeftValue.          RightValue (with spacing)
     * Used in settlement receipts
     * 
     * @param leftValue The left-aligned value
     * @param rightValue The right-aligned value
     * @param receiptWidth The width of the receipt (default: 32)
     * @return ReceiptLine with formatted text
     */
    fun createLeftRightLine(leftValue: String, rightValue: String, receiptWidth: Int = DEFAULT_RECEIPT_WIDTH): ReceiptLine {
        val leftPart = "$leftValue."
        val spaces = receiptWidth - leftPart.length - rightValue.length
        val line = if (spaces > 0) {
            leftPart + " ".repeat(spaces) + rightValue
        } else {
            "$leftPart $rightValue"
        }
        return ReceiptLine.Text(line, Alignment.LEFT, FontSize.NORMAL)
    }
}

