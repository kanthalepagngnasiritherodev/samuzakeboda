package com.example

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.clipboard.ClipboardHistoryManager
import com.example.privacy.SecretMessageCipher
import com.example.shortcuts.TextExpansionManager
import com.example.shortcuts.TextShortcut
import com.example.status.StatusItem
import com.example.status.StatusMediaManager
import com.example.ui.keyboard.KeyboardActionListener
import com.example.ui.keyboard.NeonAmber
import com.example.ui.keyboard.NeonCyan
import com.example.ui.keyboard.NeonGreen
import com.example.ui.keyboard.NeonPurple
import com.example.ui.keyboard.SamuZaKeyboardView
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.util.FancyTextGenerator
import com.example.util.HashtagCategory
import com.example.util.HashtagVault
import com.example.util.SinglishConverter

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ClipboardHistoryManager.init(this)

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepDarkBg),
                    containerColor = DeepDarkBg
                ) { innerPadding ->
                    SamuZaMainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SamuZaMainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Creator Keyboard",
        "Fancy Fonts",
        "Hashtags & Emojis",
        "Status Saver",
        "Secret Privacy",
        "Shortcuts & Clips"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBg)
    ) {
        // App Header with Sleek Dark Glassmorphic styling
        SamuZaAppHeader()

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F1422),
            contentColor = NeonCyan,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) NeonCyan else Color.White.copy(alpha = 0.65f)
                        )
                    }
                )
            }
        }

        // Tab Contents
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> KeyboardPlaygroundTab(context = context)
                1 -> FancyFontsTab(context = context)
                2 -> HashtagVaultTab(context = context)
                3 -> WhatsAppStatusSaverTab(context = context)
                4 -> SecretPrivacyTab(context = context)
                5 -> ShortcutsAndClipboardTab(context = context)
            }
        }
    }
}

@Composable
fun SamuZaAppHeader() {
    Surface(
        color = Color(0xFF0C101A),
        border = BorderStroke(1.dp, GlassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(NeonCyan, NeonPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "SamuZA Keyboard",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SamuZA Creator Keyboard",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Creator Toolkit • 100% Offline",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NeonGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "0% API Cost",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: KEYBOARD PLAYGROUND & SYSTEM IME ACTIVATION
// -------------------------------------------------------------
@Composable
fun KeyboardPlaygroundTab(context: Context) {
    var isImeEnabled by remember { mutableStateOf(false) }
    var testInputText by remember { mutableStateOf("") }
    var testComposingText by remember { mutableStateOf("") }
    var lastSecretDetected by remember { mutableStateOf<String?>(null) }
    var lastSecretDecoded by remember { mutableStateOf<String?>(null) }

    fun checkImeStatus() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val enabledImes = imm?.enabledInputMethodList ?: emptyList()
        val packageName = context.packageName
        isImeEnabled = enabledImes.any { it.packageName == packageName }
    }

    LaunchedEffect(Unit) {
        checkImeStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // System Keyboard Setup Wizard Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            border = BorderStroke(1.dp, if (isImeEnabled) NeonGreen.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isImeEnabled) Icons.Default.CheckCircle else Icons.Default.Settings,
                            contentDescription = null,
                            tint = if (isImeEnabled) NeonGreen else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isImeEnabled) "Keyboard Active in Android" else "Activate SamuZA Keyboard",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = { checkImeStatus() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enable SamuZA Keyboard in Android Settings to use it across WhatsApp, Telegram, Messenger, and all your apps.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_enable_ime")
                    ) {
                        Text(
                            text = "1. Enable in Settings",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                        border = BorderStroke(1.dp, NeonCyan),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_switch_ime")
                    ) {
                        Text(
                            text = "2. Select Default",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Test Typing Area
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522)),
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interactive Keyboard Test Canvas",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (testInputText.isNotEmpty()) {
                        Text(
                            text = "Clear",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { testInputText = "" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF070B12))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    if (testInputText.isEmpty() && testComposingText.isEmpty()) {
                        Text(
                            text = "Tap keyboard below to test typing, Singlish, Secret Mode, or shortcuts...",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp
                        )
                    } else {
                        Row {
                            Text(
                                text = testInputText,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            if (testComposingText.isNotEmpty()) {
                                Text(
                                    text = testComposingText,
                                    color = NeonCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick presets
                Text(
                    text = "Quick Test Presets:",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = false,
                        onClick = {
                            val converted = SinglishConverter.convert("mama oyaata aadareyi")
                            testInputText += "$converted "
                        },
                        label = { Text("Singlish: mama...", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(labelColor = NeonPurple)
                    )
                    FilterChip(
                        selected = false,
                        onClick = {
                            val secret = SecretMessageCipher.encode("Secret Message Test 123")
                            testInputText += "$secret "
                            lastSecretDetected = secret
                            lastSecretDecoded = "Secret Message Test 123"
                        },
                        label = { Text("Secret: 🔒SZ...", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(labelColor = NeonAmber)
                    )
                    FilterChip(
                        selected = false,
                        onClick = {
                            val exp = TextExpansionManager.findExpansion(context, "addr")
                            testInputText += "${exp ?: ""} "
                        },
                        label = { Text("Shortcut: !addr", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(labelColor = NeonCyan)
                    )
                }

                // If secret detected, show decode result
                if (lastSecretDetected != null && lastSecretDecoded != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonAmber.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Auto-Decoded Plain Text:",
                                    color = NeonAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = lastSecretDecoded ?: "",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // The Sleek iOS 18 Glassmorphic Keyboard View (Live Simulator)
        Text(
            text = "LIVE KEYBOARD VIEW (iOS 18 Glassmorphism):",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        SamuZaKeyboardView(
            listener = object : KeyboardActionListener {
                override fun onCommitText(text: String) {
                    testInputText += testComposingText + text
                    testComposingText = ""
                    if (SecretMessageCipher.isSecretMessage(text)) {
                        lastSecretDetected = text
                        lastSecretDecoded = SecretMessageCipher.decode(text)
                    }
                }

                override fun onSetComposingText(text: String) {
                    testComposingText = text
                }

                override fun onFinishComposingText() {
                    testInputText += testComposingText
                    testComposingText = ""
                }

                override fun onDelete() {
                    if (testComposingText.isNotEmpty()) {
                        testComposingText = ""
                    } else if (testInputText.isNotEmpty()) {
                        testInputText = testInputText.dropLast(1)
                    }
                }

                override fun onAction() {
                    testInputText += testComposingText + "\n"
                    testComposingText = ""
                }

                override fun onSwitchIme() {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.showInputMethodPicker()
                }

                override fun onHide() {
                    Toast.makeText(context, "Keyboard Hide requested", Toast.LENGTH_SHORT).show()
                }
            },
            isEmbeddedPreview = true
        )
    }
}

// -------------------------------------------------------------
// TAB 2: WHATSAPP STATUS SAVER HUB
// -------------------------------------------------------------
@Composable
fun WhatsAppStatusSaverTab(context: Context) {
    var statuses by remember { mutableStateOf(emptyList<StatusItem>()) }
    var isScanning by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    var previewStatus by remember { mutableStateOf<StatusItem?>(null) }
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasPermission = perms.values.any { it }
        statuses = StatusMediaManager.getStatuses(context)
    }

    LaunchedEffect(Unit) {
        statuses = StatusMediaManager.getStatuses(context)
    }

    val filteredStatuses = when (selectedFilter) {
        "Images" -> statuses.filter { !it.isVideo }
        "Videos" -> statuses.filter { it.isVideo }
        else -> statuses
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status Saver Header Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WhatsApp Status Saver",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            isScanning = true
                            statuses = StatusMediaManager.getStatuses(context)
                            isScanning = false
                            Toast.makeText(context, "Scanned WhatsApp storage: ${statuses.size} items", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Scan", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Scans local WhatsApp .Statuses directory. Save photos and videos directly to your Android Gallery without third-party services.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filters
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("All", "Images", "Videos").forEach { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonGreen,
                                    selectedLabelColor = Color.Black,
                                    labelColor = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }

                    // Request Storage permission button
                    Surface(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_MEDIA_IMAGES,
                                        Manifest.permission.READ_MEDIA_VIDEO
                                    )
                                )
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = "Grant Storage Access",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Status Grid
        if (filteredStatuses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No statuses found in WhatsApp folder.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "View statuses inside WhatsApp first, then return here to save them.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredStatuses) { status ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                        border = BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable { previewStatus = status }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (status.uri != null) {
                                AsyncImage(
                                    model = status.uri,
                                    contentDescription = status.displayName,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                if (status.isVideo) listOf(Color(0xFF1E293B), Color(0xFF2563EB))
                                                else listOf(Color(0xFF064E3B), Color(0xFF10B981))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (status.isVideo) Icons.Default.PlayArrow else Icons.Default.VideoLibrary,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            }

                            // Top video badge
                            if (status.isVideo) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopStart)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "VIDEO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Bottom overlay bar with save & preview
                            Surface(
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = status.displayName.take(14) + if (status.displayName.length > 14) "..." else "",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    IconButton(
                                        onClick = {
                                            val ok = StatusMediaManager.saveStatusToGallery(context, status)
                                            if (ok) {
                                                Toast.makeText(context, "Saved to Gallery! 📥", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Failed to save status", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Save to Gallery",
                                            tint = NeonGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Full preview dialog
    previewStatus?.let { status ->
        Dialog(onDismissRequest = { previewStatus = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B101D)),
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (status.isVideo) "Status Video" else "Status Photo",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { previewStatus = null }, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (status.uri != null) {
                            AsyncImage(
                                model = status.uri,
                                contentDescription = status.displayName,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = if (status.isVideo) Icons.Default.PlayArrow else Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val ok = StatusMediaManager.saveStatusToGallery(context, status)
                                if (ok) {
                                    Toast.makeText(context, "Saved to Gallery! 📥", Toast.LENGTH_SHORT).show()
                                }
                                previewStatus = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Save to Gallery", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                if (status.uri != null) {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = if (status.isVideo) "video/*" else "image/*"
                                        putExtra(Intent.EXTRA_STREAM, status.uri)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Status"))
                                } else {
                                    Toast.makeText(context, "Sample status cannot be shared", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, NeonCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Share", color = NeonCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: SECRET PRIVACY ENCODER & DECODER STUDIO
// -------------------------------------------------------------
@Composable
fun SecretPrivacyTab(context: Context) {
    var plainInput by remember { mutableStateOf("") }
    var encryptedOutput by remember { mutableStateOf("") }
    var cipherInput by remember { mutableStateOf("") }
    var decryptedOutput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Privacy Hero Banner
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% Offline Secret Chat Cipher",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Protects messages from snooping, censorship, and shoulder-surfing. Messages convert into encrypted tokens (🔒SZ::...::🔒) that auto-decode for SamuZA Keyboard users when copied.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Section 1: Encoder
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101726)),
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Encode Plain Text into Secret Token", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = plainInput,
                    onValueChange = {
                        plainInput = it
                        encryptedOutput = if (it.isNotEmpty()) SecretMessageCipher.encode(it) else ""
                    },
                    placeholder = { Text("Enter secret message to encode...", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAmber,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (encryptedOutput.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF060912))
                            .border(BorderStroke(1.dp, NeonAmber.copy(alpha = 0.4f)), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = encryptedOutput,
                            color = NeonAmber,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                ClipboardHistoryManager.copyToSystemClipboard(context, encryptedOutput)
                                Toast.makeText(context, "Copied encrypted token to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Copy Token", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, encryptedOutput)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Send Secret Message"))
                            },
                            border = BorderStroke(1.dp, NeonAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Share to App", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 2: Decoder
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101726)),
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Decode Incoming Secret Token", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cipherInput,
                    onValueChange = {
                        cipherInput = it
                        decryptedOutput = SecretMessageCipher.decode(it) ?: ""
                    },
                    placeholder = { Text("Paste 🔒SZ::...::🔒 token here to decode...", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    trailingIcon = {
                        Text(
                            text = "PASTE",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val clip = cb?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                    cipherInput = clip
                                    decryptedOutput = SecretMessageCipher.decode(clip) ?: ""
                                }
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (decryptedOutput.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonCyan.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Decoded Original Message:", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = decryptedOutput, color = Color.White, fontSize = 14.sp)
                        }
                    }
                } else if (cipherInput.isNotEmpty() && SecretMessageCipher.decode(cipherInput) == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Invalid or unencrypted token format.", color = Color.Red.copy(alpha = 0.7f), fontSize = 11.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: CLIPBOARD & TEXT EXPANSION SHORTCUTS MANAGER
// -------------------------------------------------------------
@Composable
fun ShortcutsAndClipboardTab(context: Context) {
    var shortcuts by remember { mutableStateOf(emptyList<TextShortcut>()) }
    var clipboardItems by remember { mutableStateOf(emptyList<String>()) }
    var showAddShortcutDialog by remember { mutableStateOf(false) }

    var newTrigger by remember { mutableStateOf("") }
    var newExpandedText by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Personal") }

    fun refreshData() {
        shortcuts = TextExpansionManager.getShortcuts(context)
        clipboardItems = ClipboardHistoryManager.getItems(context)
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Quick Add Shortcut Button
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Quick Text Expansion", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showAddShortcutDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add Shortcut", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Shortcuts instantly expand into long addresses, bank account numbers, or NICs right from the keyboard top toolbar.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }

        // Shortcuts List
        Text(text = "ACTIVE SHORTCUTS (${shortcuts.size}):", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

        shortcuts.forEach { sc ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1626),
                border = BorderStroke(1.dp, GlassBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = "!${sc.trigger}",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(text = sc.category, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = sc.expandedText, color = Color.White, fontSize = 12.sp)
                    }

                    IconButton(
                        onClick = {
                            TextExpansionManager.removeShortcut(context, sc.id)
                            refreshData()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Clipboard History Section (20 items)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Clipboard History (${clipboardItems.size}/20)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    if (clipboardItems.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            color = Color.Red.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                ClipboardHistoryManager.clearAll(context)
                                refreshData()
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Automatically saves up to 20 copied text snippets so you can paste them anytime directly from the keyboard.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }

        clipboardItems.forEach { item ->
            val isEncrypted = SecretMessageCipher.isSecretMessage(item)
            val decoded = if (isEncrypted) SecretMessageCipher.decode(item) else null

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isEncrypted) NeonAmber.copy(alpha = 0.1f) else Color(0xFF0F1626),
                border = BorderStroke(1.dp, if (isEncrypted) NeonAmber.copy(alpha = 0.4f) else GlassBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (isEncrypted && decoded != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Auto-Decoded: $decoded", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = item, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        } else {
                            Text(text = item, color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Row {
                        IconButton(
                            onClick = {
                                ClipboardHistoryManager.copyToSystemClipboard(context, item)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = {
                                ClipboardHistoryManager.removeItem(context, item)
                                refreshData()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Add Shortcut Dialog
    if (showAddShortcutDialog) {
        AlertDialog(
            onDismissRequest = { showAddShortcutDialog = false },
            containerColor = Color(0xFF101726),
            title = { Text(text = "Add Quick Text Shortcut", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTrigger,
                        onValueChange = { newTrigger = it },
                        label = { Text("Trigger Keyword (e.g. addr, nic, bank)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newExpandedText,
                        onValueChange = { newExpandedText = it },
                        label = { Text("Expanded Full Text", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTrigger.isNotBlank() && newExpandedText.isNotBlank()) {
                            TextExpansionManager.addShortcut(context, newTrigger, newExpandedText, newCategory)
                            newTrigger = ""
                            newExpandedText = ""
                            showAddShortcutDialog = false
                            refreshData()
                            Toast.makeText(context, "Shortcut saved!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddShortcutDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB: FANCY TEXT STYLE GENERATOR FOR CREATORS
// -------------------------------------------------------------
@Composable
fun FancyFontsTab(context: Context) {
    var inputText by remember { mutableStateOf("Mega Sale 50% Off Today!") }

    val presetSnippets = listOf(
        "Mega Sale 50% Off!",
        "New Video Out Now!",
        "Dm For Orders",
        "Giveaway Alert!",
        "Thank You 100K!"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Input Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fancy Font Style Generator",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${FancyTextGenerator.styles.size} Styles",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Type text to convert into stylish Unicode fonts", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Quick Creator Presets:",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(presetSnippets) { snippet ->
                        FilterChip(
                            selected = inputText == snippet,
                            onClick = { inputText = snippet },
                            label = { Text(snippet, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = if (inputText == snippet) Color.Black else NeonCyan,
                                selectedContainerColor = NeonCyan
                            )
                        )
                    }
                }
            }
        }

        // List of Converted Styles
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(FancyTextGenerator.styles) { style ->
                val sampleText = if (inputText.isNotBlank()) inputText else style.sample
                val converted = FancyTextGenerator.convert(sampleText, style.id)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522)),
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = style.name,
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = converted,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Copy button
                            IconButton(
                                onClick = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Fancy Text", converted))
                                    ClipboardHistoryManager.addItem(context, converted)
                                    Toast.makeText(context, "Copied ${style.name} to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Share button
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, converted)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share via"))
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB: HASHTAG & EMOJI VAULT FOR CREATORS
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashtagVaultTab(context: Context) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = HashtagVault.categories
    val selectedCategory = categories.getOrElse(selectedCategoryIndex) { categories.first() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Vault Header
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Creator Hashtag & Emoji Vault",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Viral Clusters",
                            color = NeonAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "High-reach tag clusters customized for TikTok, Instagram Reels, Facebook Pages, and YouTube Shorts.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory.title == cat.title
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryIndex = categories.indexOf(cat) },
                            label = { Text("${cat.iconEmoji} ${cat.title}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = if (isSelected) Color.Black else NeonAmber,
                                selectedContainerColor = NeonAmber
                            )
                        )
                    }
                }
            }
        }

        // Active Category Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522)),
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${selectedCategory.iconEmoji} ${selectedCategory.title}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${selectedCategory.hashtags.size} high-engagement tags",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = {
                                    val cluster = selectedCategory.hashtags.joinToString(" ")
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Hashtags", cluster))
                                    ClipboardHistoryManager.addItem(context, cluster)
                                    Toast.makeText(context, "Copied all ${selectedCategory.hashtags.size} hashtags!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy All", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Individual Tags FlowRow
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (tag in selectedCategory.hashtags) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White.copy(alpha = 0.07f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                    modifier = Modifier.clickable {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("Hashtag", tag))
                                        Toast.makeText(context, "Copied $tag", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text(
                                        text = tag,
                                        color = NeonCyan,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Creator Emojis Section
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1522)),
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Viral Creator Emojis (1-Tap Copy)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (emoji in HashtagVault.creatorEmojis) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White.copy(alpha = 0.08f),
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clickable {
                                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cm.setPrimaryClip(ClipData.newPlainText("Emoji", emoji))
                                            Toast.makeText(context, "Copied $emoji", Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = emoji, fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
