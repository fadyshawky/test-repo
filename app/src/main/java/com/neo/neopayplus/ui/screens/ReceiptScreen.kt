package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import com.neo.neopayplus.MyApplication
import com.neo.neopayplus.receipt.ReceiptData
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.White
import java.io.IOException
import java.text.DecimalFormat

@Composable
fun ReceiptScreen(
    receiptData: ReceiptData,
    approved: Boolean,
    isReprint: Boolean = false,
    onPrintCustomerCopy: () -> Unit,
    onCancelCustomerCopy: () -> Unit,
    onShare: () -> Unit,
    onDone: () -> Unit
) {
    val amountFormatter = DecimalFormat("#,##0.00")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Scrollable receipt content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Receipt Preview", style = MaterialTheme.typography.titleLarge, color = IndigoBlue)
            
            // Receipt card - matching printed format
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with merchant logo
                val merchantLogoPath = receiptData.merchantLogoAssetPath ?: "images/receipt_logo.webp"
                val merchantLogoBitmap = remember(merchantLogoPath) { 
                    loadLogoFromAssetNonComposable(merchantLogoPath) 
                }
                merchantLogoBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Merchant Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Terminal ID and Transaction ID (matching receipt builder)
                createLabelValueRow(receiptData.internalTerminalId, receiptData.transactionId ?: "TXN987654321")
                
                // Transaction ID and Order ID (matching receipt builder)
                createLabelValueRow(receiptData.transactionId ?: "TXN987654321", receiptData.orderId ?: "ORD123456789")
                
                // Bank logo
                val bankLogoPath = receiptData.bankLogoAssetPath ?: "images/banque_misr_logo.png"
                val bankLogoBitmap = remember(bankLogoPath) { 
                    loadLogoFromAssetNonComposable(bankLogoPath) 
                }
                bankLogoBitmap?.let { bitmap ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Bank Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Bank Terminal ID and Bank Merchant ID
                createLabelValueRow("TID", receiptData.bankTerminalId ?: "00000001")
                createLabelValueRow("MID", receiptData.bankMerchantId ?: "00000001")
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Card Brand with Card Type (e.g., "MASTERCARD DEBIT", "VISA CREDIT")
                val brandWithType = if (receiptData.cardBrand != null && receiptData.cardType != null) {
                    "${receiptData.cardBrand} ${receiptData.cardType}"
                } else {
                    receiptData.cardBrand ?: ""
                }
                Text(
                    brandWithType.uppercase(),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = IndigoBlue
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // PAN on separate line
                Text(
                    receiptData.maskedPan ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = IndigoBlue
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Entry Mode (centered, bold)
                val entryModeText = when (receiptData.entryMode) {
                    com.neo.neopayplus.receipt.ReceiptEntryMode.CONTACTLESS -> "CONTACTLESS"
                    com.neo.neopayplus.receipt.ReceiptEntryMode.IC -> "IC CARD"
                    com.neo.neopayplus.receipt.ReceiptEntryMode.MAGNETIC -> "MAGNETIC STRIPE"
                }
                Text(
                    entryModeText,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = IndigoBlue
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Batch and Receipt (matching receipt builder format)
                createLabelValueRow("BATCH:${receiptData.batchNumber ?: "001"}", "RECEIPT:${receiptData.receiptNumber ?: "000001"}")
                
                // Date and Time (matching receipt builder format)
                createLabelValueRow("DATE:${receiptData.date}", "TIME:${receiptData.time}")
                
                // RRN and AUTH (matching receipt builder format)
                createLabelValueRow("RRN:${receiptData.rrn ?: "123456789012"}", "AUTH:${receiptData.authCode ?: "AUTH123"}")
                
                // AID (always shown)
                createLabelValueRow("AID", receiptData.aid ?: "")
                
                // Expiry date (masked for display)
                if (receiptData.maskedExpiryDate != null && receiptData.maskedExpiryDate.isNotEmpty()) {
                    createLabelValueRow("EXP", receiptData.maskedExpiryDate)
                }
                
                // TVR (Terminal Verification Results) - tag 95
                if (receiptData.tvr != null && receiptData.tvr.isNotEmpty()) {
                    createLabelValueRow("TVR", receiptData.tvr)
                }
                
                // TSI (Transaction Status Information) - tag 9B
                if (receiptData.tsi != null && receiptData.tsi.isNotEmpty()) {
                    createLabelValueRow("TSI", receiptData.tsi)
                }
                
                // Amount
                val formattedAmount = amountFormatter.format(receiptData.amount) + " ${receiptData.currency}"
                createLabelValueRow("Amount", formattedAmount)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status and Transaction Type combined (e.g., "SALE APPROVED", "REFUND DECLINED")
                val statusTransactionTypeText = when (receiptData.transactionType) {
                    com.neo.neopayplus.receipt.ReceiptTransactionType.SALE -> "SALE"
                    com.neo.neopayplus.receipt.ReceiptTransactionType.REFUND -> "REFUND"
                    com.neo.neopayplus.receipt.ReceiptTransactionType.VOID -> "VOID"
                }
                val statusText = if (approved) {
                    "$statusTransactionTypeText APPROVED"
                } else {
                    "$statusTransactionTypeText DECLINED"
                }
                Text(
                    statusText,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = if (approved) IndigoBlue else MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (approved) {
                    // Approved: CVM text (centered)
                    val cvmText = when (receiptData.cvmMethod) {
                        com.neo.neopayplus.receipt.ReceiptCvmMethod.NO_PIN -> "No PIN Entered"
                        com.neo.neopayplus.receipt.ReceiptCvmMethod.OFFLINE_PIN -> "Offline PIN"
                        com.neo.neopayplus.receipt.ReceiptCvmMethod.ONLINE_PIN -> "Online PIN"
                        com.neo.neopayplus.receipt.ReceiptCvmMethod.SIGNATURE -> "Signature"
                    }
                    Text(
                        cvmText,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MutedLavender
                    )
                    
                    // Authorization text (for customer copy, matching receipt builder)
                    if (!isReprint) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "I authorize to debit the above amount from my account, " +
                                    "i confirm receipt of merchandise inside the shop and in a good condition, " +
                                    "all sales final. I acknowledge and accept the time of transaction",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 10.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MutedLavender
                        )
                    }
                } else {
                    // Declined: Response Code and Message (if available)
                    if (receiptData.responseCode != null) {
                        Text(
                            "Response Code: ${receiptData.responseCode}",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (receiptData.responseMessage != null) {
                        Text(
                            receiptData.responseMessage,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 10.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MutedLavender
                        )
                    }
                }
                
                // Copy type (matching receipt builder)
                val copyType = if (isReprint) {
                    "REPRINT"
                } else {
                    "CUSTOMER COPY"
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    copyType,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = IndigoBlue
                )
                }
            }
        }
        
        // Buttons at bottom (outside scroll)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isReprint) {
                // For reprint: Show "Reprint" button
                Button(
                    onClick = onPrintCustomerCopy,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Reprint", color = White)
                }
            } else if (approved) {
                // For approved: Show "Print Customer Copy" button (merchant copy already printed)
                Button(
                    onClick = onPrintCustomerCopy,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Print Customer Copy", color = White)
                }
            } else {
                // For declined: Show regular print button (single copy)
                Button(
                    onClick = onPrintCustomerCopy,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Print", color = White)
                }
            }
            
            OutlinedButton(onClick = onShare, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Share PDF")
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun createLabelValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = MutedLavender
        )
        Text(
            value,
            fontSize = 12.sp,
            color = IndigoBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun loadLogoFromAssetNonComposable(assetPath: String): android.graphics.Bitmap? {
    return try {
        val inputStream = MyApplication.app.assets.open(assetPath)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        bitmap
    } catch (e: IOException) {
        android.util.Log.e("ReceiptScreen", "Failed to load logo from asset '$assetPath': ${e.message}", e)
        null
    }
}

