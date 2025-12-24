package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.data.BatchManager
import com.neo.neopayplus.api.SettlementApiFactory
import com.neo.neopayplus.api.SettlementApiService
import com.neo.neopayplus.config.PaymentConfig
import com.neo.neopayplus.receipt.ReceiptPrinterService
import com.neo.neopayplus.receipt.SettlementReceiptData
import com.neo.neopayplus.receipt.BrandTotals
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.White
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettlementScreen(
    receiptPrinterService: ReceiptPrinterService? = null
) {
    // Use mutableStateOf to make it reactive - updates when transactions change
    var allTransactions by remember { mutableStateOf(TransactionJournal.getAllTransactions()) }
    var currentBatchNumber by remember { mutableStateOf(BatchManager.getCurrentBatchNumber()) }
    
    // Get transactions for current batch (unsettled transactions only)
    // All calculations are done by backend - POS only sends raw transaction data
    // Recalculate when allTransactions OR currentBatchNumber changes
    val batchTransactions = remember(allTransactions, currentBatchNumber) { 
        TransactionJournal.getTransactionsByBatch(currentBatchNumber)
            .filter { 
                // Only include unsettled transactions
                !it.isSettled &&
                (
                    // Include approved transactions (sales and refunds)
                    it.status == "APPROVED" ||
                    // Include voided transactions (original transactions marked as VOID)
                    it.status == "VOID"
                )
            }
    }
    
    var isSettling by remember { mutableStateOf(false) }
    var settlementMessage by remember { mutableStateOf<String?>(null) }
    var settlementResponse by remember { mutableStateOf<SettlementApiService.BatchUploadResponse?>(null) }
    
    // Refresh transactions list when settlement completes
    LaunchedEffect(settlementResponse) {
        if (settlementResponse != null) {
            allTransactions = TransactionJournal.getAllTransactions()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Settlement", style = MaterialTheme.typography.titleLarge, color = IndigoBlue)
        
        // Batch info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Batch Summary",
                    style = MaterialTheme.typography.titleMedium,
                    color = IndigoBlue
                )
                
                // Batch number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Batch Number:", color = MutedLavender)
                    Text(
                        currentBatchNumber,
                        style = MaterialTheme.typography.titleSmall,
                        color = IndigoBlue
                    )
                }
                
                Divider()
                
                // Display transaction count (for reference only - totals calculated by backend)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Transactions in Batch:", color = MutedLavender)
                    Text("${batchTransactions.size}", color = IndigoBlue)
                }
                
                Text(
                    "Note: All totals are calculated by backend after settlement",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedLavender
                )
                
                // Terminal info
                Divider()
                Text(
                    "Terminal: ${com.neo.neopayplus.config.PaymentConfig.getTerminalId()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedLavender
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Display settlement summary if available
        settlementResponse?.let { response ->
            if (response.success && response.totals != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Settlement Summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = IndigoBlue
                        )
                        Divider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Batch Number:", color = MutedLavender)
                            Text(response.batchNumber ?: response.batchId ?: "", color = IndigoBlue)
                        }
                        
                        // Sales
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sales:", color = MutedLavender)
                            Text(
                                "${response.totals.countSales} transactions - ${response.totals.totalSales} ${response.totals.currency}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // Refunds
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Refunds:", color = MutedLavender)
                            Text(
                                "${response.totals.countRefund} transactions - ${response.totals.totalRefund} ${response.totals.currency}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        
                        // Voids
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Voids:", color = MutedLavender)
                            Text(
                                "${response.totals.countVoid} transactions - ${response.totals.totalVoid} ${response.totals.currency}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        
                        // Declined
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Declined:", color = MutedLavender)
                            Text(
                                "${response.totals.countDeclined} transactions - ${response.totals.totalDeclined} ${response.totals.currency}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Divider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Grand Total:",
                                style = MaterialTheme.typography.titleMedium,
                                color = IndigoBlue
                            )
                            Text(
                                "${response.totals.grandTotal} ${response.totals.currency}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = IndigoBlue
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Transactions:", color = MutedLavender)
                            Text("${response.acceptedCount}", color = IndigoBlue)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        Button(
            onClick = { 
                // Allow settlement if there are any transactions (sales, refunds, or voids)
                if (batchTransactions.isEmpty()) return@Button
                
                isSettling = true
                settlementMessage = null
                
                // Build settlement request
                val settlementService = SettlementApiFactory.getInstance()
                val request = SettlementApiService.BatchUploadRequest().apply {
                    terminalId = PaymentConfig.getTerminalId()
                    batchNumber = currentBatchNumber
                    
                    // Format batch date/time
                    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
                    val timeFormat = SimpleDateFormat("HHmmss", Locale.US)
                    val now = Date()
                    batchDate = dateFormat.format(now)
                    batchTime = timeFormat.format(now)
                    
                    // Send transactions for validation - backend will compare with transactions found by batch number
                    // Only transactions that match (in both storage and request) will be settled
                    transactions = batchTransactions
                        .filter { !it.isSettled } // Double-check: only unsettled transactions
                        .map { tx ->
                            SettlementApiService.SettlementTransaction().apply {
                                transactionId = tx.transactionId ?: "" // Include transaction ID for matching
                                rrn = tx.rrn ?: ""
                                // Void transactions don't have authCode from bank (void operation doesn't get new authCode)
                                authCode = if (tx.status == "VOID") "" else (tx.authCode ?: "")
                                pan = tx.pan ?: ""
                                amount = tx.amount ?: "0"
                                currencyCode = tx.currencyCode ?: "818"
                                transactionType = tx.transactionType ?: "00"
                                date = tx.date ?: ""
                                time = tx.time ?: ""
                                field55 = "" // TODO: Store field55 if needed
                                responseCode = tx.responseCode ?: ""
                                status = tx.status ?: ""
                            }
                        }
                }
                
                // Upload batch
                settlementService.uploadBatch(request, object : SettlementApiService.BatchUploadCallback {
                    override fun onBatchUploadComplete(response: SettlementApiService.BatchUploadResponse) {
                        isSettling = false
                        if (response.success) {
                            // Mark transactions as settled
                            val settledCount = TransactionJournal.markBatchAsSettled(
                                currentBatchNumber,
                                response.acceptedRrns
                            )
                            
                            // Refresh transactions immediately to reflect settled status
                            allTransactions = TransactionJournal.getAllTransactions()
                            
                            // Increment batch number for next batch
                            val newBatchNumber = BatchManager.incrementBatchNumber()
                            currentBatchNumber = newBatchNumber // Update UI immediately
                            
                            // Refresh transactions again after batch number change to get new batch transactions
                            allTransactions = TransactionJournal.getAllTransactions()
                            
                            // Store response for display
                            settlementResponse = response
                            settlementMessage = "Settlement successful! ${response.acceptedCount} transactions settled."
                            
                            // Print settlement report using backend-calculated data
                            if (receiptPrinterService != null) {
                                try {
                                    val settlementData = createSettlementReceiptData(
                                        response,
                                        request.batchDate ?: "",
                                        request.batchTime ?: ""
                                    )
                                    receiptPrinterService.printSettlementReport(
                                        settlementData,
                                        object : ReceiptPrinterService.PrintCallback {
                                            override fun onSuccess() {
                                                android.util.Log.d("SettlementScreen", "✓ Settlement report printed successfully")
                                            }
                                            override fun onError(message: String) {
                                                android.util.Log.e("SettlementScreen", "✗ Settlement report print error: $message")
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    android.util.Log.e("SettlementScreen", "Failed to create/print settlement report: ${e.message}", e)
                                }
                            }
                        } else {
                            settlementResponse = null
                            settlementMessage = "Settlement failed: ${response.message}"
                        }
                    }
                    
                    override fun onBatchUploadError(error: Throwable) {
                        isSettling = false
                        settlementResponse = null
                        settlementMessage = "Settlement error: ${error.message}"
                    }
                })
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = batchTransactions.isNotEmpty() && !isSettling
        ) {
            Text(if (isSettling) "Settling..." else "Settle Now", color = White)
        }
        
        // Settlement status message
        settlementMessage?.let { message ->
            Text(
                text = message,
                color = if (message.contains("successful", ignoreCase = true)) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        }
        
        // Full-screen loading modal
        if (isSettling) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Loading indicator
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = IndigoBlue,
                            strokeWidth = 4.dp
                        )
                        
                        // Loading message
                        Text(
                            text = "Processing Settlement",
                            style = MaterialTheme.typography.titleLarge,
                            color = IndigoBlue
                        )
                        
                        Text(
                            text = "Please wait while we process your batch settlement...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Transaction count info
                        Text(
                            text = "Settling ${batchTransactions.size} transaction${if (batchTransactions.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedLavender
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper function to create SettlementReceiptData from backend settlement response
 * All calculations are done by backend - POS only uses the response data
 */
private fun createSettlementReceiptData(
    response: SettlementApiService.BatchUploadResponse,
    batchDate: String,
    batchTime: String
): SettlementReceiptData {
    val totals = response.totals ?: return SettlementReceiptData(
        merchantLogoAssetPath = "images/receipt_logo.webp",
        merchantName = PaymentConfig.MERCHANT_NAME,
        internalTerminalId = PaymentConfig.getTerminalId(),
        internalMerchantId = PaymentConfig.getMerchantId(),
        date = batchDate,
        time = batchTime,
        batchNumber = response.batchNumber ?: "",
        batchStatus = "APPROVED",
        visaTotals = BrandTotals("VISA", 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO),
        mastercardTotals = BrandTotals("MASTERCARD", 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO),
        meezaTotals = BrandTotals("MEEZA", 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO),
        generalTotals = BrandTotals("GENERAL TOTAL", 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO)
    )
    
    // Helper to convert backend brand totals to BrandTotals
    // All calculations are done by backend - POS only uses the response data
    fun convertBrandTotals(brandData: SettlementApiService.BrandTotalsData?, brandName: String): BrandTotals {
        if (brandData == null) {
            android.util.Log.d("SettlementScreen", "⚠️ Brand totals null for $brandName - using zeros")
            return BrandTotals(brandName, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO)
        }
        val saleCount = brandData.sales?.count ?: 0
        val saleAmount = BigDecimal(brandData.sales?.total ?: "0.00")
        val voidCount = brandData.voids?.count ?: 0
        val voidAmount = BigDecimal(brandData.voids?.total ?: "0.00")
        val refundCount = brandData.refunds?.count ?: 0
        val refundAmount = BigDecimal(brandData.refunds?.total ?: "0.00")
        // Use backend-calculated total (sales - refunds) - no calculation on POS
        val totalAmount = BigDecimal(brandData.total ?: "0.00")
        
        android.util.Log.d("SettlementScreen", "✓ $brandName totals: sales=$saleCount ($saleAmount), voids=$voidCount ($voidAmount), refunds=$refundCount ($refundAmount), total=$totalAmount (from backend)")
        
        return BrandTotals(
            brandName = brandName,
            saleCount = saleCount,
            saleAmount = saleAmount,
            voidSaleCount = voidCount,
            voidSaleAmount = voidAmount,
            refundCount = refundCount,
            refundAmount = refundAmount,
            totalAmount = totalAmount // From backend, not calculated on POS
        )
    }
    
    // Get brand totals from backend response (all calculated by backend)
    android.util.Log.d("SettlementScreen", "Parsing brand totals from backend response...")
    android.util.Log.d("SettlementScreen", "  VISA: ${if (totals.visa != null) "present" else "null"}")
    android.util.Log.d("SettlementScreen", "  MASTERCARD: ${if (totals.mastercard != null) "present" else "null"}")
    android.util.Log.d("SettlementScreen", "  MEEZA: ${if (totals.meeza != null) "present" else "null"}")
    
    val visaTotals = convertBrandTotals(totals.visa, "VISA")
    val mastercardTotals = convertBrandTotals(totals.mastercard, "MASTERCARD")
    val meezaTotals = convertBrandTotals(totals.meeza, "MEEZA")
    
    // General totals from backend (all calculated by backend)
    // Use backend-provided grand total, not calculated on POS
    val generalTotals = BrandTotals(
        brandName = "GENERAL TOTAL",
        saleCount = totals.countSales,
        saleAmount = BigDecimal(totals.totalSales),
        voidSaleCount = totals.countVoid,
        voidSaleAmount = BigDecimal(totals.totalVoid),
        refundCount = totals.countRefund,
        refundAmount = BigDecimal(totals.totalRefund),
        totalAmount = BigDecimal(totals.grandTotal) // From backend, not calculated on POS
    )
    
    // Parse batch date/time from response or use provided
    val date = response.batchDate ?: batchDate
    val time = response.batchTime ?: batchTime
    
    return SettlementReceiptData(
        merchantLogoAssetPath = "images/receipt_logo.webp",
        merchantName = PaymentConfig.MERCHANT_NAME,
        internalTerminalId = PaymentConfig.getTerminalId(),
        internalMerchantId = PaymentConfig.getMerchantId(),
        date = date,
        time = time,
        batchNumber = response.batchNumber ?: response.batchId ?: "",
        batchStatus = "APPROVED", // Backend returns "success" status
        visaTotals = visaTotals,
        mastercardTotals = mastercardTotals,
        meezaTotals = meezaTotals,
        generalTotals = generalTotals
    )
}

