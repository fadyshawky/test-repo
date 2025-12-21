package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neo.neopayplus.receipt.ReceiptData
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.White
import java.text.DecimalFormat

@Composable
fun ReceiptScreen(
    receiptData: ReceiptData,
    approved: Boolean,
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
                // Merchant Logo
                Text("Merchant Logo", fontSize = 10.sp, color = MutedLavender, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Order ID
                createLabelValueRow("Order ID", receiptData.orderId ?: "ORD123456789")
                
                // Transaction ID
                createLabelValueRow("Transaction ID", receiptData.transactionId ?: "TXN987654321")
                
                if (approved) {
                    // Approved: Internal Terminal ID and Merchant ID (TID/MID labels)
                    createLabelValueRow("TID", receiptData.internalTerminalId)
                    createLabelValueRow("MID", receiptData.internalMerchantId)
                    
                    // Bank Logo (only for approved)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bank Logo", fontSize = 10.sp, color = MutedLavender, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Bank Terminal ID and Bank Merchant ID
                    createLabelValueRow("TID", receiptData.bankTerminalId ?: "00000001")
                    createLabelValueRow("MID", receiptData.bankMerchantId ?: "00000001")
                } else {
                    // Declined: Internal Terminal ID and Merchant ID (full labels)
                    createLabelValueRow("Terminal ID", receiptData.internalTerminalId)
                    createLabelValueRow("Merchant ID", receiptData.internalMerchantId)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Card Brand and Transaction Type + PAN (centered, bold, large)
                val transactionTypeText = when (receiptData.transactionType) {
                    com.neo.neopayplus.receipt.ReceiptTransactionType.SALE -> "SALE"
                    com.neo.neopayplus.receipt.ReceiptTransactionType.REFUND -> "REFUND"
                    com.neo.neopayplus.receipt.ReceiptTransactionType.VOID -> "VOID"
                }
                val brandTypeText = "${receiptData.cardBrand ?: ""} $transactionTypeText"
                Text(
                    brandTypeText,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = IndigoBlue
                )
                
                // PAN (on same line as brand/type in printed receipt, but separate line in UI for clarity)
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
                
                // Batch and Receipt (combined on one line)
                val batchReceiptText = "BATCH:${receiptData.batchNumber ?: "001"} RECEIPT:${receiptData.receiptNumber ?: "000001"}"
                createLabelValueRow("", batchReceiptText)
                
                // Date and Time (combined on one line)
                val dateTimeText = "DATE:${receiptData.date} TIME:${receiptData.time}"
                createLabelValueRow("", dateTimeText)
                
                if (approved) {
                    // Approved: RRN and AUTH (combined on one line)
                    val rrnAuthText = "RRN:${receiptData.rrn ?: "123456789012"} AUTH:${receiptData.authCode ?: "AUTH123"}"
                    createLabelValueRow("", rrnAuthText)
                } else {
                    // Declined: RRN only (no AUTH)
                    createLabelValueRow("RRN", receiptData.rrn ?: "")
                }
                
                // AID (always shown)
                createLabelValueRow("AID", receiptData.aid ?: "")
                
                // Amount
                val formattedAmount = amountFormatter.format(receiptData.amount) + " ${receiptData.currency}"
                createLabelValueRow("Amount", formattedAmount)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status (centered, bold, large)
                Text(
                    if (approved) "APPROVED" else "DECLINED",
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
            if (approved) {
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
        horizontalArrangement = if (label.isEmpty()) Arrangement.Start else Arrangement.SpaceBetween
    ) {
        if (label.isNotEmpty()) {
            Text(
                label, // No colon, matching receipt builder
                fontSize = 12.sp,
                color = MutedLavender
            )
        }
        Text(
            value,
            fontSize = 12.sp,
            color = IndigoBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

