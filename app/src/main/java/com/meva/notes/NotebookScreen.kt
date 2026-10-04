package com.meva.notes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun NotebookScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    onBack: () -> Unit,
    onNotice: (String) -> Unit,
    onNewNote: () -> Unit,
    onShowStats: () -> Unit,
    onExportPdf: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTool by rememberSaveable { mutableStateOf("pen") }
    var selectedColorHex by rememberSaveable { mutableStateOf("#1A1C1B") }
    var thickness by rememberSaveable { mutableStateOf(2) }
    var zoom by rememberSaveable { mutableStateOf(100) }
    var zenMode by rememberSaveable { mutableStateOf(false) }
    var moreExpanded by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    val strokes = remember { mutableStateListOf<InkStroke>() }
    val redoStrokes = remember { mutableStateListOf<InkStroke>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }

    val selectedColor = colorFromHex(selectedColorHex)
    val eraserRadius = with(LocalDensity.current) { 26.dp.toPx() }
    val colorSamples = listOf(
        "#1A1C1B" to "Kömür siyahı",
        "#EF4444" to "Mercan kırmızı",
        "#F59E0B" to "Amber sarısı",
        "#0EA5E9" to "Camgöbeği",
        "#4A6B5D" to "Adaçayı yeşili"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight().widthIn(max = 320.dp),
                drawerContainerColor = MevaPalette.surfaceLow
            ) {
                NotebookDrawer(
                    onBack = {
                        scope.launch { drawerState.close() }
                        onBack()
                    },
                    onNewNote = {
                        scope.launch { drawerState.close() }
                        onNewNote()
                    },
                    onNotice = onNotice,
                    onStats = onShowStats
                )
            }
        }
    ) {
        Column(Modifier.fillMaxSize().background(MevaPalette.canvas)) {
            if (zenMode) {
                Row(
                    Modifier.fillMaxWidth().background(MevaPalette.background).padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Zen tuval modu", color = MevaPalette.primary, style = MaterialTheme.typography.labelLarge)
                    TextButton(onClick = { zenMode = false }) { Text("Araçları göster", color = MevaPalette.primary) }
                }
            } else {
                NotebookTopToolbar(
                    title = title,
                    onTitleChange = onTitleChange,
                    selectedTool = selectedTool,
                    onToolSelected = { selectedTool = it; onNotice("${it.uppercase()} aracı devrede") },
                    onBack = onBack,
                    onOpenSidebar = { scope.launch { drawerState.open() } },
                    onRecording = {
                        recording = !recording
                        onNotice(if (recording) "Sesli not kaydediliyor… 🎙️" else "Ses kaydı durduruldu")
                    },
                    moreExpanded = moreExpanded,
                    onMoreExpanded = { moreExpanded = it },
                    onNotice = onNotice,
                    onStats = onShowStats,
                    onExport = onExportPdf,
                    onClearCanvas = {
                        strokes.clear()
                        redoStrokes.clear()
                        onNotice("Çizim katmanı temizlendi")
                    },
                    onZen = { zenMode = true }
                )
                NotebookInkToolbar(
                    selectedColor = selectedColorHex,
                    colors = colorSamples,
                    onColor = { selectedColorHex = it; onNotice("Mürekkep rengi seçildi") },
                    thickness = thickness,
                    onThickness = { thickness = it },
                    canUndo = strokes.isNotEmpty(),
                    canRedo = redoStrokes.isNotEmpty(),
                    onUndo = {
                        if (strokes.isNotEmpty()) redoStrokes.add(strokes.removeAt(strokes.lastIndex))
                        else onNotice("Geri alınacak bir çizim yok")
                    },
                    onRedo = {
                        if (redoStrokes.isNotEmpty()) strokes.add(redoStrokes.removeAt(redoStrokes.lastIndex))
                        else onNotice("Yinelenecek bir çizim yok")
                    },
                    onZen = { zenMode = true }
                )
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 860.dp)
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = zoom / 100f
                                scaleY = zoom / 100f
                                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f)
                            }
                            .clip(RoundedCornerShape(19.dp))
                            .background(Color(0xFFFBFBFA))
                            .drawBehind {
                                val gap = 22.dp.toPx()
                                val dot = Color(0x48727974)
                                var y = 10.dp.toPx()
                                while (y < size.height) {
                                    var x = 10.dp.toPx()
                                    while (x < size.width) {
                                        drawCircle(dot, radius = 0.85.dp.toPx(), center = Offset(x, y))
                                        x += gap
                                    }
                                    y += gap
                                }
                            }
                            .padding(horizontal = 21.dp, vertical = 24.dp)
                    ) {
                        NotebookPageContent(
                            title = title,
                            selectedTool = selectedTool,
                            color = selectedColor,
                            thickness = thickness,
                            strokes = strokes,
                            currentPoints = currentPoints,
                            onCommitStroke = { stroke ->
                                strokes.add(stroke)
                                redoStrokes.clear()
                            },
                            onEraseAt = { point ->
                                strokes.removeAll { stroke ->
                                    stroke.points.any { existing -> (existing - point).getDistance() < eraserRadius }
                                }
                            },
                            onNotice = onNotice
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                }

                ZoomControls(
                    zoom = zoom,
                    onZoom = { zoom = (zoom + it).coerceIn(50, 180) },
                    onReset = { zoom = 100 },
                    onFit = { zoom = 95 },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun NotebookTopToolbar(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedTool: String,
    onToolSelected: (String) -> Unit,
    onBack: () -> Unit,
    onOpenSidebar: () -> Unit,
    onRecording: () -> Unit,
    moreExpanded: Boolean,
    onMoreExpanded: (Boolean) -> Unit,
    onNotice: (String) -> Unit,
    onStats: () -> Unit,
    onExport: () -> Unit,
    onClearCanvas: () -> Unit,
    onZen: () -> Unit
) {
    Column(Modifier.fillMaxWidth().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = MevaPalette.onSurfaceVariant)
            }
            IconButton(onClick = onOpenSidebar, modifier = Modifier.size(38.dp)) {
                Icon(Icons.Default.Menu, contentDescription = "Kenar çubuğu", tint = MevaPalette.onSurfaceVariant)
            }
            Column(Modifier.weight(1f).padding(start = 5.dp)) {
                androidx.compose.foundation.text.BasicTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelLarge.copy(color = MevaPalette.onSurface, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.widthIn(max = 170.dp)
                )
                Text("1 / 1 sayfa", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = { onNotice("Sayfa küçük resimleri hazır") }, modifier = Modifier.size(38.dp)) {
                Icon(Icons.Default.ViewAgenda, contentDescription = "Sayfa küçük resimleri", tint = MevaPalette.onSurfaceVariant, modifier = Modifier.size(19.dp))
            }
            IconButton(onClick = { onNotice("Yer imi eklendi") }, modifier = Modifier.size(38.dp)) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = "Yer imi", tint = MevaPalette.onSurfaceVariant, modifier = Modifier.size(19.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            ToolButton("pen", "Kalem", Icons.Default.Edit, selectedTool == "pen", onToolSelected)
            ToolButton("highlighter", "Vurgulayıcı", Icons.Default.Brush, selectedTool == "highlighter", onToolSelected)
            ToolButton("eraser", "Silgi", Icons.Default.DeleteOutline, selectedTool == "eraser", onToolSelected)
            ToolButton("lasso", "Kement / Seçim", Icons.Default.Gesture, selectedTool == "lasso", onToolSelected)
            ToolButton("shape", "Şekil & Cetvel", Icons.Default.Category, selectedTool == "shape", onToolSelected)
            ToolButton("text", "Metin Kutusu", Icons.Default.TextFields, selectedTool == "text", onToolSelected)
            ToolButton("laser", "Lazer İşaretçi", Icons.Default.RadioButtonChecked, selectedTool == "laser", onToolSelected)
            IconButton(onClick = onRecording) {
                Icon(Icons.Default.Mic, contentDescription = "Ses kaydı al", tint = MevaPalette.onSurfaceVariant)
            }
            IconButton(onClick = { onNotice("Meva Akıllı Not Asistanı hazır ✨") }) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Meva Akıllı Not Asistanı", tint = MevaPalette.primary)
            }
            IconButton(onClick = { onNotice("Çıkartma ve ikonlar açıldı") }) {
                Icon(Icons.Default.SentimentSatisfied, contentDescription = "Çıkartma ve ikonlar", tint = MevaPalette.onSurfaceVariant)
            }
            Box {
                IconButton(onClick = { onMoreExpanded(true) }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Daha fazla seçenek", tint = MevaPalette.onSurfaceVariant)
                }
                DropdownMenu(expanded = moreExpanded, onDismissRequest = { onMoreExpanded(false) }) {
                    DropdownMenuItem(
                        text = { Text("Şimdi senkronize et") },
                        leadingIcon = { Icon(Icons.Default.Sync, null) },
                        onClick = { onMoreExpanded(false); onNotice("Bulut ile senkronize edildi ☁️") }
                    )
                    DropdownMenuItem(
                        text = { Text("PDF olarak dışa aktar") },
                        leadingIcon = { Icon(Icons.Default.Description, null, tint = MevaPalette.secondary) },
                        onClick = { onMoreExpanded(false); onExport() }
                    )
                    DropdownMenuItem(
                        text = { Text("Not bilgisi & geçmiş") },
                        leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = MevaPalette.outline) },
                        onClick = { onMoreExpanded(false); onStats() }
                    )
                    DropdownMenuItem(
                        text = { Text("Sayfayı temizle") },
                        leadingIcon = { Icon(Icons.Default.DeleteSweep, null, tint = MevaPalette.error) },
                        onClick = { onMoreExpanded(false); onClearCanvas() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolButton(
    tool: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) MevaPalette.surface else Color.Transparent)
            .clickable { onSelect(tool) }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (active) MevaPalette.primary else MevaPalette.onSurfaceVariant, modifier = Modifier.size(19.dp))
        if (active) {
            Box(Modifier.padding(top = 2.dp).size(4.dp).clip(CircleShape).background(MevaPalette.primary))
        }
    }
}

@Composable
private fun NotebookInkToolbar(
    selectedColor: String,
    colors: List<Pair<String, String>>,
    onColor: (String) -> Unit,
    thickness: Int,
    onThickness: (Int) -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onZen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Color.White.copy(alpha = 0.96f))
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onUndo, enabled = canUndo, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Undo, contentDescription = "Geri al", tint = if (canUndo) MevaPalette.onSurfaceVariant else MevaPalette.outlineVariant)
        }
        IconButton(onClick = onRedo, enabled = canRedo, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Redo, contentDescription = "Yinele", tint = if (canRedo) MevaPalette.onSurfaceVariant else MevaPalette.outlineVariant)
        }
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
            colors.forEach { (hex, label) ->
                Surface(
                    modifier = Modifier
                        .size(19.dp)
                        .clickable { onColor(hex) },
                    color = colorFromHex(hex),
                    shape = CircleShape,
                    border = if (selectedColor == hex) androidx.compose.foundation.BorderStroke(2.dp, MevaPalette.primary) else null
                ) {
                    if (selectedColor == hex) {
                        Box(Modifier.fillMaxSize().padding(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.7f)))
                    }
                }
            }
        }
        Spacer(Modifier.width(11.dp))
        Box(Modifier.width(1.dp).height(18.dp).background(MevaPalette.surfaceHigh))
        Spacer(Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
            (1..4).forEach { size ->
                val diameter = when (size) { 1 -> 4.dp; 2 -> 7.dp; 3 -> 10.dp; else -> 13.dp }
                Box(
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .clickable { onThickness(size) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(diameter)
                            .clip(CircleShape)
                            .background(if (thickness == size) MevaPalette.primary else MevaPalette.outline)
                    )
                }
            }
        }
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = onZen, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Fullscreen, contentDescription = "Tam ekran genişlet", tint = MevaPalette.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun NotebookPageContent(
    title: String,
    selectedTool: String,
    color: Color,
    thickness: Int,
    strokes: List<InkStroke>,
    currentPoints: List<Offset>,
    onCommitStroke: (InkStroke) -> Unit,
    onEraseAt: (Offset) -> Unit,
    onNotice: (String) -> Unit
) {
    var titleNote by remember { mutableStateOf("Layers of Skin") }
    var stickyOffsetOne by remember { mutableStateOf(IntOffset.Zero) }
    var stickyOffsetTwo by remember { mutableStateOf(IntOffset.Zero) }
    var stickyOffsetThree by remember { mutableStateOf(IntOffset.Zero) }

    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(color = MevaPalette.primaryFixed, shape = CircleShape) {
                Text("HISTOLOGY  ·  05 MIN READ", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = MevaPalette.primary, style = MaterialTheme.typography.labelSmall, letterSpacing = 0.45.sp)
            }
            Text("•  Güncellendi bugün", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(15.dp))
        Text(
            title.ifBlank { titleNote },
            color = MevaPalette.onSurface,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 29.sp
        )
        Text(
            "Cildin katmanları ve temel işlevleri",
            modifier = Modifier.padding(top = 4.dp),
            color = MevaPalette.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(17.dp))
        SkinDiagram()
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LayerLegend("01", "Epidermis", Color(0xFFB96D4C))
            LayerLegend("02", "Dermis", Color(0xFFD18578))
            LayerLegend("03", "Hipodermis", Color(0xFFC39B44))
        }
        Spacer(Modifier.height(16.dp))
        Text("Katman notları", color = MevaPalette.onSurface, style = MaterialTheme.typography.titleMedium)
        Text(
            "Yapıyı küçük parçalara ayır; her katmanın kendine özgü bir görevi ve hikâyesi var.",
            modifier = Modifier.padding(top = 4.dp, bottom = 11.dp),
            color = MevaPalette.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val cards: @Composable (Modifier) -> Unit = { modifier ->
                Column(modifier = modifier) {
                    StickyNoteCard(
                        title = "EPİDERMİS",
                        body = "En dış koruyucu katman. Keratinositler yenilenir ve cildi dış etkenlerden korur.",
                        color = Color(0xFFFFF5CF),
                        offset = stickyOffsetOne,
                        onOffset = { stickyOffsetOne = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(9.dp))
                    StickyNoteCard(
                        title = "DERMİS",
                        body = "Kolajen ve elastin; cilde güç, esneklik ve duyusal bağlantılar kazandırır.",
                        color = Color(0xFFECE7FF),
                        offset = stickyOffsetTwo,
                        onOffset = { stickyOffsetTwo = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(9.dp))
                    StickyNoteCard(
                        title = "HİPODERMİS",
                        body = "Yağ dokusu yalıtım sağlar, enerji depolar ve alttaki yapıları destekler.",
                        color = Color(0xFFE2F1F8),
                        offset = stickyOffsetThree,
                        onOffset = { stickyOffsetThree = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (maxWidth >= 600.dp) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StickyNoteCard(
                        title = "EPİDERMİS",
                        body = "En dış koruyucu katman. Keratinositler yenilenir ve cildi dış etkenlerden korur.",
                        color = Color(0xFFFFF5CF),
                        offset = stickyOffsetOne,
                        onOffset = { stickyOffsetOne = it },
                        modifier = Modifier.weight(1f)
                    )
                    StickyNoteCard(
                        title = "DERMİS",
                        body = "Kolajen ve elastin; cilde güç, esneklik ve duyusal bağlantılar kazandırır.",
                        color = Color(0xFFECE7FF),
                        offset = stickyOffsetTwo,
                        onOffset = { stickyOffsetTwo = it },
                        modifier = Modifier.weight(1f)
                    )
                    StickyNoteCard(
                        title = "HİPODERMİS",
                        body = "Yağ dokusu yalıtım sağlar, enerji depolar ve alttaki yapıları destekler.",
                        color = Color(0xFFE2F1F8),
                        offset = stickyOffsetThree,
                        onOffset = { stickyOffsetThree = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                cards(Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Çizim alanı", color = MevaPalette.onSurface, style = MaterialTheme.typography.titleMedium)
            Text("${strokes.size} çizgi", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
        }
        Text(
            if (selectedTool == "text") "Metin eklemek için çizim alanına dokun." else "Notlarına serbestçe çizim ekleyebilirsin.",
            modifier = Modifier.padding(top = 3.dp, bottom = 9.dp),
            color = MevaPalette.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        SketchCanvas(
            selectedTool = selectedTool,
            color = color,
            thickness = thickness,
            strokes = strokes,
            currentPoints = currentPoints,
            onCommitStroke = onCommitStroke,
            onEraseAt = onEraseAt,
            onTap = { point ->
                when (selectedTool) {
                    "text" -> onNotice("Metin kutusu tuvale eklendi")
                    "shape" -> onNotice("Şekil yerleştirmek için sürükle")
                    "laser" -> onNotice("Lazer işaretçi etkin")
                    "lasso" -> onNotice("Seçim alanı başlatıldı")
                }
            }
        )
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(MevaPalette.primary))
            Spacer(Modifier.width(7.dp))
            Text("Yerel olarak kaydedildi", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.weight(1f))
            Text("Meva Notes  ·  ${titleNote.ifBlank { "Not" }}", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SkinDiagram() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFF6F1EB))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("CUTANEOUS STRUCTURE", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall, letterSpacing = 0.8.sp)
            Surface(color = Color.White.copy(alpha = 0.8f), shape = CircleShape) {
                Text("FIG. 01", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(9.dp))
        Canvas(Modifier.fillMaxWidth().height(198.dp).clip(RoundedCornerShape(11.dp))) {
            val w = size.width
            val h = size.height
            drawRect(Color(0xFFFFF9F1), size = size)
            // The three soft tissue layers.
            drawRoundRect(Color(0xFFF3D5BF), Offset(0f, h * 0.12f), Size(w, h * 0.23f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f))
            drawRect(Color(0xFFE6AE9E), Offset(0f, h * 0.35f), Size(w, h * 0.39f))
            drawRoundRect(Color(0xFFF2D996), Offset(0f, h * 0.74f), Size(w, h * 0.26f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f))
            drawLine(Color(0xFFB7745E), Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = 3f)
            drawLine(Color(0xFFCE8D78), Offset(0f, h * 0.74f), Offset(w, h * 0.74f), strokeWidth = 2f)
            // Hair shaft and follicle.
            drawLine(Color(0xFF53433B), Offset(w * 0.34f, 0f), Offset(w * 0.39f, h * 0.56f), strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(Color(0xFF53433B), Offset(w * 0.34f, 1f), Offset(w * 0.31f, -h * 0.16f), strokeWidth = 3f, cap = StrokeCap.Round)
            drawOval(Color(0xFFD98F79), Offset(w * 0.31f, h * 0.42f), Size(w * 0.15f, h * 0.36f))
            drawOval(Color(0xFFB87668), Offset(w * 0.345f, h * 0.61f), Size(w * 0.08f, h * 0.11f))
            // Small gland and vessel curves.
            val gland = Path().apply {
                moveTo(w * 0.49f, h * 0.42f)
                cubicTo(w * 0.55f, h * 0.32f, w * 0.62f, h * 0.48f, w * 0.56f, h * 0.54f)
                cubicTo(w * 0.51f, h * 0.60f, w * 0.61f, h * 0.66f, w * 0.66f, h * 0.57f)
            }
            drawPath(gland, Color(0xFFF7E7C4), style = Stroke(width = 5f, cap = StrokeCap.Round))
            drawLine(Color(0xFFB55F5C), Offset(w * 0.18f, h * 0.82f), Offset(w * 0.77f, h * 0.82f), strokeWidth = 4f, cap = StrokeCap.Round)
            drawLine(Color(0xFFB55F5C), Offset(w * 0.2f, h * 0.89f), Offset(w * 0.73f, h * 0.89f), strokeWidth = 3f, cap = StrokeCap.Round)
            // Fat lobules.
            listOf(0.16f to 0.93f, 0.28f to 0.95f, 0.47f to 0.93f, 0.67f to 0.95f, 0.84f to 0.92f).forEach { (x, y) ->
                drawCircle(Color(0xFFFFE8A7), radius = 12.dp.toPx(), center = Offset(w * x, h * y))
                drawCircle(Color(0xFFD6B866), radius = 12.dp.toPx(), center = Offset(w * x, h * y), style = Stroke(width = 1.2.dp.toPx()))
            }
            // Fine connective fibers.
            for (i in 0..6) {
                val x = w * (0.08f + i * 0.13f)
                drawLine(Color(0xFFFAE5D9).copy(alpha = 0.65f), Offset(x, h * 0.4f), Offset(x + 17f, h * 0.68f), strokeWidth = 1.2f)
            }
        }
    }
}

@Composable
private fun LayerLegend(label: String, name: String, color: Color) {
    Surface(color = Color.White.copy(alpha = 0.84f), shape = CircleShape) {
        Row(
            Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(color))
            Text(label, color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
            Text(name, color = MevaPalette.onSurface, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StickyNoteCard(
    title: String,
    body: String,
    color: Color,
    offset: IntOffset,
    onOffset: (IntOffset) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .offset { offset }
            .pointerInput(Unit) {
                var draggedOffset = offset
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    draggedOffset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
                    onOffset(draggedOffset)
                }
            },
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    val lineGap = 22.dp.toPx()
                    var y = 34.dp.toPx()
                    while (y < size.height) {
                        drawLine(Color(0x25000000), Offset(10.dp.toPx(), y), Offset(size.width - 10.dp.toPx(), y), 1f)
                        y += lineGap
                    }
                }
                .padding(horizontal = 12.dp, vertical = 11.dp)
        ) {
            Text(title, color = MevaPalette.onSurface, style = MaterialTheme.typography.labelSmall, letterSpacing = 0.5.sp, fontWeight = FontWeight.Bold)
            Text(body, modifier = Modifier.padding(top = 5.dp), color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SketchCanvas(
    selectedTool: String,
    color: Color,
    thickness: Int,
    strokes: List<InkStroke>,
    currentPoints: List<Offset>,
    onCommitStroke: (InkStroke) -> Unit,
    onEraseAt: (Offset) -> Unit,
    onTap: (Offset) -> Unit
) {
    val shouldDraw = selectedTool in setOf("pen", "highlighter", "eraser", "shape", "laser")
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(268.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFFFEFEFD))
            .pointerInput(selectedTool, color, thickness) {
                if (shouldDraw) {
                    detectDragGestures(
                        onDragStart = { start ->
                            if (selectedTool != "eraser") {
                                currentPoints.clear()
                                currentPoints.add(start)
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            if (selectedTool == "eraser") {
                                onEraseAt(change.position)
                            } else {
                                currentPoints.add(change.position)
                            }
                        },
                        onDragEnd = {
                            if (currentPoints.size > 1 && selectedTool != "eraser") {
                                val opacity = if (selectedTool == "highlighter") 0.32f else 1f
                                val width = when (selectedTool) {
                                    "highlighter" -> thickness * 5.2f
                                    "laser" -> thickness * 1.3f
                                    else -> thickness * 1.7f
                                } * density
                                onCommitStroke(InkStroke(currentPoints.toList(), color, width, opacity))
                            }
                            currentPoints.clear()
                        },
                        onDragCancel = { currentPoints.clear() }
                    )
                }
            }
            .pointerInput(selectedTool) {
                detectTapGestures { point -> onTap(point) }
            }
    ) {
        val gap = 22.dp.toPx()
        var y = gap / 2f
        while (y < size.height) {
            var x = gap / 2f
            while (x < size.width) {
                drawCircle(Color(0x38727974), radius = 0.8.dp.toPx(), center = Offset(x, y))
                x += gap
            }
            y += gap
        }
        strokes.forEach { stroke ->
            drawStroke(stroke.points, stroke.color.copy(alpha = stroke.opacity), stroke.width)
        }
        if (currentPoints.size > 1 && selectedTool != "eraser") {
            val opacity = if (selectedTool == "highlighter") 0.32f else 1f
            val width = when (selectedTool) {
                "highlighter" -> thickness * 5.2f
                else -> thickness * 1.7f
            } * density
            drawStroke(currentPoints, color.copy(alpha = opacity), width)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStroke(
    points: List<Offset>,
    color: Color,
    width: Float
) {
    if (points.size < 2) return
    for (index in 0 until points.lastIndex) {
        drawLine(
            color = color,
            start = points[index],
            end = points[index + 1],
            strokeWidth = width,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ZoomControls(
    zoom: Int,
    onZoom: (Int) -> Unit,
    onReset: () -> Unit,
    onFit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(13.dp),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 7.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MevaPalette.surfaceHigh)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp)) {
            IconButton(onClick = { onZoom(-10) }, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Uzaklaştır", tint = MevaPalette.onSurfaceVariant, modifier = Modifier.size(17.dp))
            }
            TextButton(onClick = onReset, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 7.dp, vertical = 0.dp)) {
                Text("$zoom%", color = MevaPalette.onSurface, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = { onZoom(10) }, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Yakınlaştır", tint = MevaPalette.onSurfaceVariant, modifier = Modifier.size(17.dp))
            }
            Box(Modifier.width(1.dp).height(17.dp).background(MevaPalette.surfaceHigh))
            IconButton(onClick = onFit, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.FitScreen, contentDescription = "Ekrana sığdır", tint = MevaPalette.primary, modifier = Modifier.size(17.dp))
            }
        }
    }
}

@Composable
private fun NotebookDrawer(
    onBack: () -> Unit,
    onNewNote: () -> Unit,
    onNotice: (String) -> Unit,
    onStats: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 18.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            MevaBrand(Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Text("Meva", color = MevaPalette.onSurface, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kütüphaneye dön") }
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onNewNote,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MevaPalette.primaryContainer)
        ) {
            Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
            Text("Yeni Not")
        }
        Spacer(Modifier.height(18.dp))
        DrawerAction(Icons.Default.Description, "Tüm Notlar", onBack)
        DrawerAction(Icons.Default.Folder, "Klasörler & Koleksiyonlar", onBack, selected = true)
        DrawerAction(Icons.Default.CheckCircle, "Hızlı Görevler & Odak") { onNotice("Odak görevlerin hazır") }
        DrawerAction(Icons.Default.Archive, "Arşiv") { onNotice("Arşiv açılıyor…") }
        DrawerAction(Icons.Default.DeleteOutline, "Çöp Kutusu") { onNotice("Çöp kutusu açılıyor…") }
        Spacer(Modifier.height(18.dp))
        Text("ETİKETLER", modifier = Modifier.padding(horizontal = 10.dp), color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall, letterSpacing = 0.8.sp)
        listOf("anatomi", "dermatoloji", "ders-notu").forEach { tag ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(9.dp)).clickable { onNotice("#$tag etiketi seçildi") }.padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(MevaPalette.primaryContainer))
                Spacer(Modifier.width(9.dp))
                Text("#$tag", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(14.dp))
        DrawerAction(Icons.Default.BarChart, "Not bilgisi & geçmiş", onStats)
        DrawerAction(Icons.Default.Sync, "Şimdi senkronize et") { onNotice("Bulut ile senkronize edildi ☁️") }
        DrawerAction(Icons.Default.Settings, "Ayarlar") { onNotice("Ayarlar yakında burada") }
        Spacer(Modifier.height(20.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = MevaPalette.surface) {
            Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(34.dp).clip(CircleShape).background(MevaPalette.primaryFixed), contentAlignment = Alignment.Center) {
                    Text("EK", color = MevaPalette.primary, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.width(9.dp))
                Column {
                    Text("Elif Kaya", color = MevaPalette.onSurface, style = MaterialTheme.typography.labelLarge)
                    Text("Tıp & Biyoloji", color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun DrawerAction(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit, selected: Boolean = false) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(if (selected) MevaPalette.surfaceHigh else Color.Transparent).clickable(onClick = onClick).padding(horizontal = 11.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (selected) MevaPalette.primary else MevaPalette.onSurfaceVariant, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(12.dp))
        Text(title, color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
    }
}

private fun colorFromHex(value: String): Color = when (value.uppercase()) {
    "#EF4444" -> Color(0xFFEF4444)
    "#F59E0B" -> Color(0xFFF59E0B)
    "#0EA5E9" -> Color(0xFF0EA5E9)
    "#4A6B5D" -> Color(0xFF4A6B5D)
    else -> Color(0xFF1A1C1B)
}
