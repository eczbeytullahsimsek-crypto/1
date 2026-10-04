package com.meva.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
internal fun DialogHost(
    type: String,
    selectedFolder: String,
    noteCount: Int,
    onDismiss: () -> Unit,
    onCreateFolder: (String, String, FolderTone) -> Unit,
    onCreateTag: (String, FolderTone) -> Unit,
    onSaveNote: (String, String) -> Unit,
    onTrashCleared: () -> Unit,
    onShareMarkdown: () -> Unit,
    onSharePdf: () -> Unit,
    onCopyLink: () -> Unit
) {
    when (type) {
        "new_folder" -> NewFolderDialog(onDismiss, onCreateFolder)
        "new_tag" -> NewTagDialog(onDismiss, onCreateTag)
        "quick_note" -> QuickNoteDialog(selectedFolder, onDismiss, onSaveNote)
        "export_share" -> ExportDialog(onDismiss, onCopyLink, onShareMarkdown, onSharePdf)
        "workspace_stats" -> StatsDialog(noteCount = noteCount, onDismiss = onDismiss)
        "shortcuts" -> ShortcutsDialog(onDismiss)
        "trash_confirm" -> TrashDialog(onDismiss, onTrashCleared)
    }
}

@Composable
private fun DialogShell(
    title: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    maxWidth: Dp = 520.dp,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = maxWidth),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFFFFFF),
            shadowElevation = 20.dp,
            border = BorderStroke(1.dp, MevaPalette.surfaceHigh)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = MevaPalette.primary, modifier = Modifier.size(25.dp))
                    Text(
                        title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        color = MevaPalette.onSurface
                    )
                    androidx.compose.material3.IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MevaPalette.outline)
                    }
                }
                Spacer(Modifier.height(18.dp))
                content()
            }
        }
    }
}

@Composable
private fun DialogFooter(
    primaryText: String,
    onPrimary: () -> Unit,
    onDismiss: () -> Unit,
    primaryEnabled: Boolean = true,
    cancelText: String = "Vazgeç"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onDismiss) {
            Text(cancelText, color = MevaPalette.onSurfaceVariant)
        }
        Spacer(Modifier.width(6.dp))
        Button(
            onClick = onPrimary,
            enabled = primaryEnabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MevaPalette.primary,
                contentColor = Color.White,
                disabledContainerColor = MevaPalette.surfaceHigh,
                disabledContentColor = MevaPalette.outline
            )
        ) {
            Text(primaryText)
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = MevaPalette.outline,
        style = MaterialTheme.typography.labelSmall,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun ToneSelector(
    tones: List<FolderTone>,
    selected: FolderTone,
    onSelect: (FolderTone) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        tones.forEach { tone ->
            val active = tone == selected
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onSelect(tone) },
                shape = CircleShape,
                color = tone.background(),
                border = if (active) BorderStroke(2.dp, MevaPalette.primary) else null,
                shadowElevation = if (active) 2.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (active) Icon(Icons.Default.Check, contentDescription = "Seçili", tint = tone.foreground(), modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}

@Composable
private fun NewFolderDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, FolderTone) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var tone by rememberSaveable { mutableStateOf(FolderTone.Sage) }
    DialogShell("Yeni Koleksiyon Oluştur", Icons.Default.CreateNewFolder, onDismiss, 520.dp) {
        FieldLabel("Klasör adı")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Örn: Gece Düşünceleri, Yeni Proje…") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.height(16.dp))
        FieldLabel("Açıklama (isteğe bağlı)")
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Koleksiyonun amacını ve içeriğini özetleyin…") },
            minLines = 2,
            maxLines = 3,
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.height(16.dp))
        FieldLabel("Renk & ikon tonu")
        ToneSelector(
            tones = listOf(FolderTone.Sage, FolderTone.Terracotta, FolderTone.Stone, FolderTone.Peach, FolderTone.Forest),
            selected = tone,
            onSelect = { tone = it }
        )
        DialogFooter(
            primaryText = "Oluştur",
            onPrimary = { onCreate(name, description, tone) },
            onDismiss = onDismiss,
            primaryEnabled = name.isNotBlank()
        )
    }
}

@Composable
private fun NewTagDialog(
    onDismiss: () -> Unit,
    onCreate: (String, FolderTone) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var tone by rememberSaveable { mutableStateOf(FolderTone.Sage) }
    DialogShell("Yeni Etiket Ekle", Icons.Default.Edit, onDismiss, 430.dp) {
        FieldLabel("Etiket adı")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.removePrefix("#") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Text("#", color = MevaPalette.outline, fontWeight = FontWeight.Medium) },
            placeholder = { Text("ornek-etiket") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.height(18.dp))
        FieldLabel("Etiket rengi")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                FolderTone.Sage to "Adaçayı",
                FolderTone.Terracotta to "Toprak",
                FolderTone.Stone to "Nötr"
            ).forEach { (choice, label) ->
                val selected = tone == choice
                Surface(
                    modifier = Modifier.clickable { tone = choice },
                    shape = CircleShape,
                    color = choice.background(),
                    border = if (selected) BorderStroke(1.5.dp, MevaPalette.primary) else null
                ) {
                    Text(
                        label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = choice.foreground(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        DialogFooter(
            primaryText = "Etiketi Kaydet",
            onPrimary = { onCreate(name, tone) },
            onDismiss = onDismiss,
            primaryEnabled = name.isNotBlank()
        )
    }
}

@Composable
private fun QuickNoteDialog(
    folder: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    val wordCount = remember(body) { body.trim().split(Regex("\\s+")).count { it.isNotBlank() } }
    DialogShell("Hızlı Not", Icons.Default.Edit, onDismiss, 620.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Taslak bu cihazda tutulur", color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
            Text("$wordCount kelime", color = MevaPalette.primary, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Başlıksız Düşünce…") },
            textStyle = MaterialTheme.typography.titleLarge,
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Row(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 9.dp)
                .background(MevaPalette.surfaceLow, RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("B", "I", "H1", "☑").forEach { format ->
                Text(
                    format,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                        .clickable {
                            body += when (format) {
                                "B" -> " **kalın metin**"
                                "I" -> " _italik metin_"
                                "H1" -> "\n# Başlık"
                                else -> "\n☐ Görev"
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    color = MevaPalette.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
            placeholder = { Text("Dingin zihninizden geçenleri buraya serbestçe aktarın…") },
            shape = RoundedCornerShape(14.dp),
            maxLines = 9
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Koleksiyon: $folder", color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
            Row {
                TextButton(onClick = onDismiss) { Text("Kapat", color = MevaPalette.onSurfaceVariant) }
                Button(
                    onClick = { onSave(title, body) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MevaPalette.primary)
                ) { Text("Kaydet") }
            }
        }
    }
}

@Composable
private fun ExportDialog(
    onDismiss: () -> Unit,
    onCopyLink: () -> Unit,
    onMarkdown: () -> Unit,
    onPdf: () -> Unit
) {
    DialogShell("Paylaş & Dışa Aktar", Icons.Default.Share, onDismiss, 470.dp) {
        Text(
            "Koleksiyonlarınızı bağlantı yoluyla paylaşın veya istediğiniz formatta dışa aktarın.",
            color = MevaPalette.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(14.dp))
        ExportRow(
            icon = Icons.Default.Link,
            title = "Koleksiyon bağlantısı",
            subtitle = "meva.app/c/dusunceler-82f",
            action = "Kopyala",
            onClick = onCopyLink
        )
        Spacer(Modifier.height(9.dp))
        ExportRow(
            icon = Icons.Default.Edit,
            title = "Markdown olarak paylaş (.md)",
            subtitle = "Notlarınız hiyerarşik olarak arşivlenir",
            action = "Paylaş",
            onClick = onMarkdown
        )
        Spacer(Modifier.height(9.dp))
        ExportRow(
            icon = Icons.Default.PictureAsPdf,
            title = "Zen PDF kitapçığı (.pdf)",
            subtitle = "Temiz, baskıya uygun bir çıktı oluşturun",
            action = "Paylaş",
            onClick = onPdf
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Kapat", color = MevaPalette.onSurfaceVariant) }
        }
    }
}

@Composable
private fun ExportRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    action: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MevaPalette.surfaceLow)
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MevaPalette.primary)
            Column(Modifier.weight(1f)) {
                Text(title, color = MevaPalette.onSurface, style = MaterialTheme.typography.labelLarge)
                Text(subtitle, color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
            }
            Text(action, color = MevaPalette.primary, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun StatsDialog(noteCount: Int, onDismiss: () -> Unit) {
    DialogShell("Çalışma İstatistikleri", Icons.Default.BarChart, onDismiss, 430.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            StatLine("Haftalık odak süresi", "14s 20dk", MevaPalette.primary)
            StatLine("Yazılan toplam kelime", "18.420", MevaPalette.secondary)
            StatLine("Bu cihazdaki notlar", noteCount.toString(), MevaPalette.tertiary)
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MevaPalette.surfaceLow, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Haftalık yazma hedefi", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    Text("82%", color = MevaPalette.primary, style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { 0.82f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MevaPalette.primary,
                    trackColor = MevaPalette.surfaceHigh
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Kapat") }
        }
    }
}

@Composable
private fun StatLine(label: String, value: String, valueColor: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MevaPalette.surfaceLow, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = valueColor, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ShortcutsDialog(onDismiss: () -> Unit) {
    DialogShell("Klavye Kısayolları", Icons.Default.Keyboard, onDismiss, 410.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShortcutLine("Yeni hızlı not", "⌘ + N")
            ShortcutLine("Aramayı odakla", "⌘ + K")
            ShortcutLine("Zen odak modu", "⌘ + F")
            ShortcutLine("Paylaş & dışa aktar", "⌘ + E")
            ShortcutLine("Pencereleri kapat", "ESC")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) { Text("Anladım") }
        }
    }
}

@Composable
private fun ShortcutLine(action: String, keys: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(action, color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Surface(color = MevaPalette.surfaceHigh, shape = RoundedCornerShape(6.dp)) {
            Text(keys, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = MevaPalette.onSurface, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun TrashDialog(onDismiss: () -> Unit, onClear: () -> Unit) {
    DialogShell("Çöp Kutusunu Temizle", Icons.Default.DeleteForever, onDismiss, 390.dp) {
        Text(
            "Çöp kutusunda bekleyen tüm notlar kalıcı olarak silinecektir. Bu işlem geri alınamaz.",
            color = MevaPalette.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            Modifier.fillMaxWidth().padding(top = 18.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) { Text("Vazgeç", color = MevaPalette.onSurfaceVariant) }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onClear,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MevaPalette.error)
            ) { Text("Evet, Temizle") }
        }
    }
}
