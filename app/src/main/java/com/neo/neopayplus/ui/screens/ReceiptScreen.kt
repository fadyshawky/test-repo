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
                // Logos section (placeholder - actual logos would be shown here)
                Text("Merchant Logo", fontSize = 10.sp, color = MutedLavender, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                
                // Order ID
                createLabelValueRow("Order ID", receiptData.orderId ?: "ORD123456789")
                
                // Transaction ID
                createLabelValueRow("Transaction ID", receiptData.transactionId ?: "TXN987654321")
                
                // Internal Terminal ID and Merchant ID
                createLabelValueRow("Terminal ID", receiptData.internalTerminalId)
                createLabelValueRow("Merchant ID", receiptData.internalMerchantId)
                
                Text("Bank Logo", fontSize = 10.sp, color = MutedLavender, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                
                // Bank Terminal ID and Bank Merchant ID
                createLabelValueRow("Terminal ID", receiptData.bankTerminalId ?: "00000001")
                createLabelValueRow("Merchant ID", receiptData.bankMerchantId ?: "00000001")
                
                Divider()
                
                // Card Brand and Transaction Type (centered, bold, large) - matching receipt builder
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
                
                // PAN
                Text(
                    receiptData.maskedPan ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = IndigoBlue
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Entry Mode
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
                
                // Batch Number
                createLabelValueRow("Batch Number", receiptData.batchNumber ?: "001")
                
                // Receipt Number
                createLabelValueRow("Receipt Number", receiptData.receiptNumber ?: "000001")
                
                // Date
                createLabelValueRow("Date", receiptData.date)
                
                // Time
                createLabelValueRow("Time", receiptData.time)
                
                // RRN
                createLabelValueRow("RRN", receiptData.rrn ?: "123456789012")
                
                // AUTH
                createLabelValueRow("AUTH", receiptData.authCode ?: "AUTH123")
                
                // APP ID (matching receipt builder - no condition, always shown)
                createLabelValueRow("APP ID", receiptData.aid ?: "")
                
                // Amount
                val formattedAmount = amountFormatter.format(receiptData.amount) + " ${receiptData.currency}"
                createLabelValueRow("Amount", formattedAmount)
                
                Divider()
                
                // Status
                Text(
                    if (approved) "APPROVED" else "DECLINED",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = if (approved) IndigoBlue else MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // CVM
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label, // No colon, matching receipt builder
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

