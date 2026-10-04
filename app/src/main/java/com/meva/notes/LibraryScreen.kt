package com.meva.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
internal fun LibraryScreen(
    folders: List<CollectionFolder>,
    tags: List<MevaTag>,
    totalNotes: Int,
    archivedCount: Int,
    trashCount: Int,
    onNotice: (String) -> Unit,
    onNewFolder: () -> Unit,
    onNewTag: () -> Unit,
    onNewNote: () -> Unit,
    onOpenNotebook: () -> Unit,
    onOpenFolder: (CollectionFolder) -> Unit,
    onShare: () -> Unit,
    onShowStats: () -> Unit,
    onShowShortcuts: () -> Unit,
    onShowTrash: () -> Unit,
    onSync: () -> Unit,
    onArchiveChanged: (Int) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }
    var listView by remember { mutableStateOf(false) }
    var zenMode by remember { mutableStateOf(false) }
    var splitMode by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }
    var moreExpanded by remember { mutableStateOf(false) }
    var sortMode by remember { mutableStateOf("recent") }
    var sidebarSearch by remember { mutableStateOf("") }

    val query = search.trim().removePrefix("#").lowercase()
    val filtered = folders.filter { folder ->
        val matchesSearch = query.isBlank() ||
            folder.title.lowercase().contains(query) ||
            folder.description.lowercase().contains(query) ||
            folder.tags.any { it.lowercase().contains(query) }
        val matchesCategory = selectedFilter == "all" || folder.categories.contains(selectedFilter)
        matchesSearch && matchesCategory
    }
    val filteredFolders = when (sortMode) {
        "alpha" -> filtered.sortedBy { it.title.lowercase() }
        "notes" -> filtered.sortedByDescending { it.count }
        else -> filtered
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight().widthIn(max = 320.dp),
                drawerContainerColor = MevaPalette.surfaceLow
            ) {
                LibraryDrawerContent(
                    folders = folders,
                    tags = tags,
                    sidebarSearch = sidebarSearch,
                    onSidebarSearch = { sidebarSearch = it; search = it },
                    onNewNote = {
                        scope.launch { drawerState.close() }
                        onNewNote()
                    },
                    onNewTag = {
                        scope.launch { drawerState.close() }
                        onNewTag()
                    },
                    onTagClick = { tag ->
                        search = tag
                        selectedFilter = "all"
                        scope.launch { drawerState.close() }
                    },
                    onNavigate = { destination ->
                        when (destination) {
                            "Tüm Notlar" -> {
                                search = ""
                                selectedFilter = "all"
                            }
                            "Arşiv" -> onNotice("Arşivde $archivedCount not güvenle saklanıyor")
                            "Çöp Kutusu" -> onShowTrash()
                            "Hızlı Görevler & Odak" -> onNotice("Odak görevlerin hazır")
                            else -> {
                                search = ""
                                selectedFilter = "all"
                            }
                        }
                        scope.launch { drawerState.close() }
                    },
                    onSettings = { onNotice("Ayarlar yakında burada") }
                )
            }
        },
        gesturesEnabled = !zenMode
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MevaPalette.background)
        ) {
            if (!zenMode) {
                LibraryTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onOpenNotebook = onOpenNotebook,
                    onShare = onShare,
                    onShowStats = onShowStats,
                    onShowShortcuts = onShowShortcuts,
                    onShowTrash = onShowTrash,
                    onSync = onSync,
                    sortExpanded = sortExpanded,
                    onSortExpanded = { sortExpanded = it },
                    moreExpanded = moreExpanded,
                    onMoreExpanded = { moreExpanded = it },
                    sortMode = sortMode,
                    onSortMode = { sortMode = it },
                    splitMode = splitMode,
                    onSplit = {
                        splitMode = !splitMode
                        onNotice(if (splitMode) "Bölünmüş görünüm paneli aktif 🪟" else "Bölünmüş görünüm kapatıldı")
                    },
                    onZen = {
                        zenMode = true
                        onNotice("Zen odak modu devrede 🧘")
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MevaPalette.surface)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Zen odak modu", color = MevaPalette.primary, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { zenMode = false; onNotice("Odak modundan çıkıldı") }) {
                        Text("Çık", color = MevaPalette.primary)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 22.dp, bottom = 36.dp)
            ) {
                LibraryIntro(
                    onNewFolder = onNewFolder,
                    listView = listView,
                    onGrid = { listView = false },
                    onList = { listView = true }
                )
                Spacer(Modifier.height(22.dp))
                StatsGrid(
                    totalNotes = totalNotes,
                    folderCount = folders.size,
                    tagCount = tags.size,
                    archivedCount = archivedCount,
                    onAllNotes = { selectedFilter = "all"; search = "" },
                    onFolders = { selectedFilter = "all"; search = "" },
                    onTags = { onNotice("Etiket bulutundan etiket seçebilirsin") },
                    onArchived = { onNotice("Arşivde $archivedCount not güvenle saklanıyor") }
                )
                Spacer(Modifier.height(18.dp))
                SearchAndFilters(
                    search = search,
                    onSearch = { search = it },
                    selected = selectedFilter,
                    onSelectFilter = { selectedFilter = it }
                )
                Spacer(Modifier.height(20.dp))
                if (filteredFolders.isEmpty()) {
                    EmptyFolders(
                        onReset = {
                            search = ""
                            selectedFilter = "all"
                        }
                    )
                } else {
                    FolderCollection(
                        folders = filteredFolders,
                        listView = listView,
                        onOpenFolder = onOpenFolder,
                        onNotice = onNotice,
                        onArchive = { folder ->
                            onArchiveChanged(archivedCount + folder.count)
                            onNotice("'${folder.title}' arşivlendi")
                        },
                        onDelete = { folder -> onNotice("'${folder.title}' çöp kutusuna taşındı") }
                    )
                }
                Spacer(Modifier.height(20.dp))
                BottomLibraryCards(
                    tags = tags,
                    archivedCount = archivedCount,
                    trashCount = trashCount,
                    onNewTag = onNewTag,
                    onTagClick = { tag ->
                        search = tag
                        selectedFilter = "all"
                    },
                    onManageTags = { onNotice("Etiketlerinizi buradan düzenleyebilirsiniz") },
                    onOpenArchive = { onNotice("Arşivdeki notlar açılıyor…") },
                    onClearTrash = onShowTrash
                )
            }
        }
    }
}

@Composable
private fun LibraryTopBar(
    onOpenDrawer: () -> Unit,
    onOpenNotebook: () -> Unit,
    onShare: () -> Unit,
    onShowStats: () -> Unit,
    onShowShortcuts: () -> Unit,
    onShowTrash: () -> Unit,
    onSync: () -> Unit,
    sortExpanded: Boolean,
    onSortExpanded: (Boolean) -> Unit,
    moreExpanded: Boolean,
    onMoreExpanded: (Boolean) -> Unit,
    sortMode: String,
    onSortMode: (String) -> Unit,
    splitMode: Boolean,
    onSplit: () -> Unit,
    onZen: () -> Unit
) {
    Column(Modifier.fillMaxWidth().background(MevaPalette.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, contentDescription = "Menüyü aç", tint = MevaPalette.primary)
            }
            Column(Modifier.weight(1f).padding(start = 2.dp)) {
                Text("Çalışma Alanı", color = MevaPalette.onSurface, style = MaterialTheme.typography.labelMedium)
                Text("Düşünceler", color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
            }
            Surface(color = MevaPalette.surface, shape = CircleShape, modifier = Modifier.clickable(onClick = onSync)) {
                Row(Modifier.padding(horizontal = 9.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(MevaPalette.primary))
                    Spacer(Modifier.width(6.dp))
                    Text("Senkron", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(onClick = onOpenNotebook) {
                Icon(Icons.Default.Edit, contentDescription = "Çizim tuvalini aç", tint = MevaPalette.primary)
            }
            IconButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = "Paylaş", tint = MevaPalette.onSurfaceVariant)
            }
            Box {
                IconButton(onClick = { onMoreExpanded(true) }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Daha fazla seçenek", tint = MevaPalette.onSurfaceVariant)
                }
                DropdownMenu(expanded = moreExpanded, onDismissRequest = { onMoreExpanded(false) }) {
                    DropdownMenuItem(
                        text = { Text("Şimdi senkronize et") },
                        leadingIcon = { Icon(Icons.Default.Sync, null, tint = MevaPalette.primary) },
                        onClick = { onMoreExpanded(false); onSync() }
                    )
                    DropdownMenuItem(
                        text = { Text("Çalışma istatistikleri") },
                        leadingIcon = { Icon(Icons.Default.BarChart, null, tint = MevaPalette.secondary) },
                        onClick = { onMoreExpanded(false); onShowStats() }
                    )
                    DropdownMenuItem(
                        text = { Text("Klavye kısayolları") },
                        leadingIcon = { Icon(Icons.Default.Keyboard, null, tint = MevaPalette.outline) },
                        onClick = { onMoreExpanded(false); onShowShortcuts() }
                    )
                    DropdownMenuItem(
                        text = { Text("Çöp kutusunu aç") },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = MevaPalette.error) },
                        onClick = { onMoreExpanded(false); onShowTrash() }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactTopAction(Icons.Default.ViewWeek, "Bölünmüş görünüm", splitMode, onSplit)
            CompactTopAction(Icons.Default.Fullscreen, "Zen odak modu", false, onZen)
            Box {
                CompactTopAction(Icons.Default.Sort, "Sıralama seçenekleri", false) { onSortExpanded(true) }
                DropdownMenu(expanded = sortExpanded, onDismissRequest = { onSortExpanded(false) }) {
                    Text(
                        "SIRALAMA ÖLÇÜTÜ",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MevaPalette.outline,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 0.5.sp
                    )
                    listOf("recent" to "Son değiştirilme", "alpha" to "İsim (A–Z)", "notes" to "Not sayısı").forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            leadingIcon = {
                                if (sortMode == key) Icon(Icons.Default.Check, null, tint = MevaPalette.primary)
                                else Spacer(Modifier.size(24.dp))
                            },
                            onClick = { onSortMode(key); onSortExpanded(false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactTopAction(icon: ImageVector, description: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(9.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected) MevaPalette.primary else MevaPalette.onSurfaceVariant
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Icon(icon, contentDescription = description, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(5.dp))
        Text(description, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@Composable
private fun LibraryDrawerContent(
    folders: List<CollectionFolder>,
    tags: List<MevaTag>,
    sidebarSearch: String,
    onSidebarSearch: (String) -> Unit,
    onNewNote: () -> Unit,
    onNewTag: () -> Unit,
    onTagClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 18.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            MevaBrand(Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Text("Meva", style = MaterialTheme.typography.titleLarge, color = MevaPalette.onSurface)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Menu, contentDescription = null, tint = MevaPalette.outline)
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
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = sidebarSearch,
            onValueChange = onSidebarSearch,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Notlarda ve etiketlerde ara…", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MevaPalette.outline) },
            trailingIcon = {
                if (sidebarSearch.isNotBlank()) {
                    IconButton(onClick = { onSidebarSearch("") }) { Icon(Icons.Default.Close, "Temizle") }
                }
            },
            singleLine = true,
            shape = CircleShape
        )
        Spacer(Modifier.height(12.dp))
        DrawerNavItem(Icons.Default.Description, "Tüm Notlar", onClick = { onNavigate("Tüm Notlar") })
        DrawerNavItem(Icons.Default.Folder, "Klasörler & Koleksiyonlar", active = true, onClick = { onNavigate("Klasörler") })
        DrawerNavItem(Icons.Default.CheckCircle, "Hızlı Görevler & Odak", onClick = { onNavigate("Hızlı Görevler & Odak") })
        DrawerNavItem(Icons.Default.Archive, "Arşiv", onClick = { onNavigate("Arşiv") })
        DrawerNavItem(Icons.Default.Delete, "Çöp Kutusu", onClick = { onNavigate("Çöp Kutusu") })
        Spacer(Modifier.height(17.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ETİKETLER", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall, letterSpacing = 0.9.sp)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onNewTag, modifier = Modifier.size(30.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Hızlı etiket ekle", tint = MevaPalette.primary, modifier = Modifier.size(18.dp))
            }
        }
        tags.take(7).forEach { tag ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .clickable { onTagClick(tag.name) }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(tag.tone.background()))
                Spacer(Modifier.width(10.dp))
                Text("#${tag.name}", modifier = Modifier.weight(1f), color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Text(tag.count.toString(), color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.weight(1f, fill = false))
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            shape = RoundedCornerShape(14.dp),
            color = MevaPalette.surface
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(34.dp).clip(CircleShape).background(MevaPalette.primaryFixed), contentAlignment = Alignment.Center) {
                    Text("EK", color = MevaPalette.primary, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    Text("Elif Kaya", color = MevaPalette.onSurface, style = MaterialTheme.typography.labelLarge)
                    Text("Kişisel Alan", color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onSettings, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Ayarlar", tint = MevaPalette.onSurfaceVariant, modifier = Modifier.size(19.dp))
                }
            }
        }
    }
}

@Composable
private fun DrawerNavItem(
    icon: ImageVector,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit
) {
    val background = if (active) MevaPalette.surfaceHigh else Color.Transparent
    val foreground = if (active) MevaPalette.onSurface else MevaPalette.onSurfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (active) MevaPalette.primary else foreground, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, color = foreground, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun LibraryIntro(
    onNewFolder: () -> Unit,
    listView: Boolean,
    onGrid: () -> Unit,
    onList: () -> Unit
) {
    Column {
        Surface(color = MevaPalette.surface, shape = CircleShape) {
            Row(
                Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(MevaPalette.primaryContainer))
                Text("Dizin & Kütüphane", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "Klasörler &\nKoleksiyonlar",
            color = MevaPalette.onSurface,
            style = MaterialTheme.typography.headlineLarge,
            lineHeight = 36.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Düşünceleriniz ve projeleriniz için düzenli, huzurlu ve zihni dinlendiren dijital alanlar.",
            color = MevaPalette.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MevaPalette.surfaceHigh)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ViewToggle("Izgara", Icons.Default.GridView, active = !listView, onClick = onGrid)
                ViewToggle("Liste", Icons.Default.FormatListBulleted, active = listView, onClick = onList)
            }
            Button(
                onClick = onNewFolder,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MevaPalette.primaryContainer),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 13.dp, vertical = 11.dp)
            ) {
                Icon(Icons.Default.CreateNewFolder, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Text("Yeni Klasör", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ViewToggle(label: String, icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    val color = if (active) MevaPalette.onSurface else MevaPalette.onSurfaceVariant
    Row(
        Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (active) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Text(label, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun StatsGrid(
    totalNotes: Int,
    folderCount: Int,
    tagCount: Int,
    archivedCount: Int,
    onAllNotes: () -> Unit,
    onFolders: () -> Unit,
    onTags: () -> Unit,
    onArchived: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val items = listOf(
            StatItem(totalNotes.toString(), "TOPLAM NOT", Icons.Default.StickyNote2, MevaPalette.primaryFixed, MevaPalette.primary, onAllNotes),
            StatItem(folderCount.toString(), "ANA KLASÖR", Icons.Default.Folder, MevaPalette.surfaceHigh, MevaPalette.onSurface, onFolders),
            StatItem(tagCount.toString(), "CANLI ETİKET", Icons.Default.Sell, MevaPalette.tertiaryFixed, MevaPalette.tertiary, onTags),
            StatItem(archivedCount.toString(), "ARŞİVLENMİŞ", Icons.Default.Inventory2, MevaPalette.secondaryFixed, MevaPalette.secondary, onArchived)
        )
        val columns = if (maxWidth >= 690.dp) 4 else 2
        Column(Modifier.fillMaxWidth()) {
            items.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        StatCard(item, Modifier.weight(1f))
                    }
                    repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

private data class StatItem(
    val value: String,
    val label: String,
    val icon: ImageVector,
    val background: Color,
    val foreground: Color,
    val onClick: () -> Unit
)

@Composable
private fun StatCard(item: StatItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable(onClick = item.onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MevaPalette.surfaceLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(item.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, null, tint = item.foreground, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(item.value, color = MevaPalette.onSurface, style = MaterialTheme.typography.titleLarge, lineHeight = 28.sp)
                Text(item.label, color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall, letterSpacing = 0.4.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SearchAndFilters(
    search: String,
    onSearch: (String) -> Unit,
    selected: String,
    onSelectFilter: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(Color.White)
            .padding(13.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = onSearch,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MevaPalette.outline) },
            trailingIcon = {
                if (search.isNotBlank()) {
                    IconButton(onClick = { onSearch("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Aramayı temizle", tint = MevaPalette.outline)
                    }
                }
            },
            placeholder = { Text("Klasör veya etiketlerde ara…", style = MaterialTheme.typography.bodySmall) },
            singleLine = true,
            shape = CircleShape,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MevaPalette.surfaceLow,
                focusedContainerColor = MevaPalette.surfaceLow,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MevaPalette.primary
            )
        )
        Spacer(Modifier.height(9.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text("Filtrele:", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
            listOf(
                "all" to "Tümü",
                "aktif" to "Aktif Odaklar",
                "ortak" to "Ortak Çalışılanlar",
                "favori" to "Favoriler"
            ).forEach { (key, label) ->
                val active = selected == key
                Surface(
                    modifier = Modifier.clickable { onSelectFilter(key) },
                    color = if (active) MevaPalette.primary else MevaPalette.surface,
                    shape = CircleShape
                ) {
                    Text(
                        label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = if (active) Color.White else MevaPalette.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFolders(onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(vertical = 36.dp, horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.SentimentDissatisfied, null, tint = MevaPalette.outline, modifier = Modifier.size(38.dp))
        Spacer(Modifier.height(8.dp))
        Text("Eşleşen Klasör Bulunamadı", color = MevaPalette.onSurface, style = MaterialTheme.typography.titleMedium)
        Text("Farklı bir kelime veya etiket arayabilirsiniz.", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onReset) { Text("Tüm Klasörleri Göster", color = MevaPalette.primary) }
    }
}

@Composable
private fun FolderCollection(
    folders: List<CollectionFolder>,
    listView: Boolean,
    onOpenFolder: (CollectionFolder) -> Unit,
    onNotice: (String) -> Unit,
    onArchive: (CollectionFolder) -> Unit,
    onDelete: (CollectionFolder) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns = if (listView) 1 else when {
            maxWidth >= 880.dp -> 3
            maxWidth >= 580.dp -> 2
            else -> 1
        }
        Column(Modifier.fillMaxWidth()) {
            folders.chunked(columns).forEach { rowFolders ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    rowFolders.forEach { folder ->
                        FolderCard(
                            folder = folder,
                            modifier = Modifier.weight(1f),
                            onOpen = { onOpenFolder(folder) },
                            onAction = { action ->
                                when (action) {
                                    "rename" -> onNotice("'${folder.title}' yeniden adlandırma açıldı")
                                    "color" -> onNotice("'${folder.title}' rengi güncellendi")
                                    "archive" -> onArchive(folder)
                                    "delete" -> onDelete(folder)
                                }
                            }
                        )
                    }
                    repeat(columns - rowFolders.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FolderCard(
    folder: CollectionFolder,
    modifier: Modifier,
    onOpen: () -> Unit,
    onAction: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(15.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(folder.tone.background()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(folderIcon(folder.icon), null, tint = folder.tone.foreground(), modifier = Modifier.size(23.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f).padding(top = 1.dp)) {
                    Text(
                        folder.title,
                        color = MevaPalette.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("${folder.count} Not Belgesi", color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
                }
                Box {
                    IconButton(onClick = { expanded = true }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "Klasör seçenekleri", tint = MevaPalette.onSurfaceVariant)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Yeniden adlandır") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { expanded = false; onAction("rename") })
                        DropdownMenuItem(text = { Text("Rengi değiştir") }, leadingIcon = { Icon(Icons.Default.Palette, null) }, onClick = { expanded = false; onAction("color") })
                        DropdownMenuItem(text = { Text("Arşivle") }, leadingIcon = { Icon(Icons.Default.Archive, null) }, onClick = { expanded = false; onAction("archive") })
                        DropdownMenuItem(text = { Text("Klasörü sil") }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MevaPalette.error) }, onClick = { expanded = false; onAction("delete") })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            FolderBanner(folder)
            Spacer(Modifier.height(11.dp))
            Text(
                folder.description,
                color = MevaPalette.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (folder.tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    folder.tags.forEach { tag ->
                        Surface(color = MevaPalette.surface, shape = CircleShape) {
                            Text("#$tag", modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    if (folder.updated.startsWith("Bugün")) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(MevaPalette.primary))
                        Text("Senkron", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
                    } else {
                        Text(folder.updated, color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
                TextButton(onClick = onOpen, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)) {
                    Text("Klasörü Aç", color = folder.tone.foreground(), style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(3.dp))
                    Icon(Icons.Default.ArrowForward, null, tint = folder.tone.foreground(), modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

@Composable
private fun FolderBanner(folder: CollectionFolder) {
    val colors = when (folder.tone) {
        FolderTone.Sage, FolderTone.Forest -> listOf(Color(0xFFDCEBE1), Color(0xFFC5D9CC))
        FolderTone.Terracotta -> listOf(Color(0xFFFFE6DC), Color(0xFFF1CEC1))
        FolderTone.Peach -> listOf(Color(0xFFFDE7D9), Color(0xFFEACDC0))
        FolderTone.Stone -> listOf(Color(0xFFE8EBE8), Color(0xFFD7DDD8))
        FolderTone.Mint -> listOf(Color(0xFFDDEDE7), Color(0xFFC8DFD6))
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(106.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(colors))
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(13.dp)
                .size(54.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
        )
        when (folder.tone) {
            FolderTone.Terracotta -> {
                Column(Modifier.align(Alignment.CenterStart).padding(horizontal = 13.dp).fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.BarChart, null, tint = MevaPalette.secondary, modifier = Modifier.size(16.dp))
                            Text(folder.bannerTitle, color = MevaPalette.onSurface, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text("74%", color = MevaPalette.secondary, style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.height(9.dp))
                    LinearProgressIndicator(
                        progress = { 0.74f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = MevaPalette.secondary,
                        trackColor = Color.White.copy(alpha = 0.65f)
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(folder.bannerSubtitle, color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            FolderTone.Stone -> {
                Column(Modifier.align(Alignment.CenterStart).padding(horizontal = 14.dp).fillMaxWidth(0.92f)) {
                    Text("“${folder.bannerTitle}”", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(7.dp))
                    Text(folder.bannerSubtitle, color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
                }
            }
            FolderTone.Mint -> {
                Row(
                    Modifier.align(Alignment.CenterStart).padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = MevaPalette.primary, modifier = Modifier.size(20.dp))
                    Column {
                        Text(folder.bannerTitle, color = MevaPalette.onSurface, style = MaterialTheme.typography.labelLarge)
                        Text(folder.bannerSubtitle, color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            FolderTone.Forest -> {
                Column(Modifier.align(Alignment.CenterStart).padding(horizontal = 14.dp).fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Icon(Icons.Default.SelfImprovement, null, tint = MevaPalette.primary, modifier = Modifier.size(18.dp))
                        Text(folder.bannerTitle, color = MevaPalette.onSurface, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.height(9.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        repeat(7) { index ->
                            Box(Modifier.weight(1f).height(6.dp).clip(CircleShape).background(if (index < 5) MevaPalette.primary else Color.White.copy(alpha = 0.65f)))
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(folder.bannerSubtitle, color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
                }
            }
            else -> {
                Column(Modifier.align(Alignment.BottomStart).padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Text(folder.bannerTitle, color = MevaPalette.onSurface, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(folder.bannerSubtitle, color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        if (folder.tone == FolderTone.Sage) {
            Icon(Icons.Default.Spa, null, tint = MevaPalette.primary.copy(alpha = 0.48f), modifier = Modifier.align(Alignment.CenterEnd).padding(end = 19.dp).size(36.dp))
        }
        if (folder.tone == FolderTone.Peach) {
            Icon(Icons.Default.Palette, null, tint = MevaPalette.secondary.copy(alpha = 0.48f), modifier = Modifier.align(Alignment.CenterEnd).padding(end = 18.dp).size(35.dp))
        }
    }
}

private fun folderIcon(icon: FolderIcon): ImageVector = when (icon) {
    FolderIcon.Journal -> Icons.Default.Spa
    FolderIcon.Work -> Icons.Default.LaptopMac
    FolderIcon.Creative -> Icons.Default.Palette
    FolderIcon.Books -> Icons.Default.AutoStories
    FolderIcon.Travel -> Icons.Default.Explore
    FolderIcon.Wellness -> Icons.Default.SelfImprovement
    FolderIcon.New -> Icons.Default.Folder
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomLibraryCards(
    tags: List<MevaTag>,
    archivedCount: Int,
    trashCount: Int,
    onNewTag: () -> Unit,
    onTagClick: (String) -> Unit,
    onManageTags: () -> Unit,
    onOpenArchive: () -> Unit,
    onClearTrash: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(19.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(MevaPalette.surface), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Tag, null, tint = MevaPalette.onSurface, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(9.dp))
                    Text("Tüm Etiketler", modifier = Modifier.weight(1f), color = MevaPalette.onSurface, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onNewTag, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Yeni Etiket")
                    }
                }
                Text(
                    "Notlarınızı çapraz bağlamak ve anında filtrelemek için kullanılan etiket bulutu.",
                    modifier = Modifier.padding(top = 7.dp, bottom = 12.dp),
                    color = MevaPalette.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    tags.forEachIndexed { index, tag ->
                        val featured = index == 0 || index == 2
                        Surface(
                            modifier = Modifier.clickable { onTagClick(tag.name) },
                            color = if (featured) tag.tone.background() else MevaPalette.surface,
                            shape = CircleShape
                        ) {
                            Row(
                                Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Text("#${tag.name}", color = if (featured) tag.tone.foreground() else MevaPalette.onSurface, style = MaterialTheme.typography.bodySmall)
                                Surface(color = Color.White.copy(alpha = 0.74f), shape = CircleShape) {
                                    Text(tag.count.toString(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().padding(top = 13.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Toplam ${tags.size} etiket kullanımda", color = MevaPalette.outline, style = MaterialTheme.typography.labelSmall)
                    TextButton(onClick = onManageTags, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)) {
                        Text("Tümünü Yönet", color = MevaPalette.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(39.dp).clip(RoundedCornerShape(12.dp)).background(MevaPalette.tertiaryFixed), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Inventory2, null, tint = MevaPalette.tertiary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Arşivlenen Notlar", color = MevaPalette.onSurface, style = MaterialTheme.typography.titleMedium)
                        Text("$archivedCount not güvenle saklanıyor", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                    Button(
                        onClick = onOpenArchive,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MevaPalette.surface)
                    ) { Text("Aç") }
                }
                Text("Geri yükleme veya kalıcı arama yapılabilir", modifier = Modifier.padding(top = 12.dp), color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(39.dp).clip(RoundedCornerShape(12.dp)).background(MevaPalette.errorContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Delete, null, tint = MevaPalette.error, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Çöp Kutusu", color = MevaPalette.onSurface, style = MaterialTheme.typography.titleMedium)
                        Text("$trashCount not silinmeyi bekliyor", color = MevaPalette.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                    Button(
                        onClick = onClearTrash,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MevaPalette.error)
                    ) { Text("Şimdi Temizle") }
                }
                Text("30 gün sonra otomatik silinir", modifier = Modifier.padding(top = 12.dp), color = MevaPalette.outline, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
