package com.meva.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.rgb(249, 249, 247)
        window.navigationBarColor = android.graphics.Color.rgb(249, 249, 247)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            MevaTheme {
                MevaApp()
            }
        }
    }
}

private enum class AppPage { Welcome, Library, Notebook }

@Composable
private fun MevaApp() {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("meva_local", android.content.Context.MODE_PRIVATE)
    }
    val savedNotes = remember(context) {
        mutableStateListOf<LocalNote>().apply { addAll(loadLocalNotes(context)) }
    }
    val folders = remember(context) {
        mutableStateListOf<CollectionFolder>().apply {
            addAll(InitialFolders)
            val names = preferences.getStringSet("custom_folder_names", emptySet()).orEmpty().toList().sorted()
            addAll(names.map { customFolder(it) })
        }
    }
    val tags = remember(context) {
        mutableStateListOf<MevaTag>().apply {
            addAll(InitialTags)
            val names = preferences.getStringSet("custom_tag_names", emptySet()).orEmpty().toList().sorted()
            addAll(names.filterNot { name -> any { it.name.equals(name, ignoreCase = true) } }
                .map { MevaTag(it, 0, FolderTone.Sage) })
        }
    }

    var pageName by rememberSaveable { mutableStateOf(AppPage.Welcome.name) }
    var activeDialog by rememberSaveable { mutableStateOf("") }
    var documentTitle by rememberSaveable { mutableStateOf("Layers of Skin") }
    var selectedFolder by rememberSaveable { mutableStateOf("Kişisel & Günlük") }
    var totalNotes by rememberSaveable { mutableIntStateOf(65 + savedNotes.size) }
    var archivedCount by rememberSaveable { mutableIntStateOf(preferences.getInt("archived_count", 18)) }
    var trashCount by rememberSaveable { mutableIntStateOf(preferences.getInt("trash_count", 3)) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showNotice: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    val currentPage = runCatching { AppPage.valueOf(pageName) }.getOrDefault(AppPage.Welcome)
    BackHandler(enabled = currentPage != AppPage.Welcome || activeDialog.isNotEmpty()) {
        if (activeDialog.isNotEmpty()) {
            activeDialog = ""
        } else if (currentPage == AppPage.Notebook) {
            pageName = AppPage.Library.name
        } else {
            pageName = AppPage.Welcome.name
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MevaPalette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        when (currentPage) {
            AppPage.Welcome -> WelcomeScreen(
                onStart = { pageName = AppPage.Library.name },
                onQuickNote = {
                    pageName = AppPage.Library.name
                    activeDialog = "quick_note"
                }
            )

            AppPage.Library -> LibraryScreen(
                folders = folders,
                tags = tags,
                totalNotes = totalNotes,
                archivedCount = archivedCount,
                trashCount = trashCount,
                onNotice = showNotice,
                onNewFolder = { activeDialog = "new_folder" },
                onNewTag = { activeDialog = "new_tag" },
                onNewNote = {
                    selectedFolder = "Kişisel & Günlük"
                    activeDialog = "quick_note"
                },
                onOpenNotebook = {
                    documentTitle = "Layers of Skin"
                    pageName = AppPage.Notebook.name
                },
                onOpenFolder = { folder ->
                    selectedFolder = folder.title
                    showNotice("'${folder.title}' koleksiyonu açıldı")
                },
                onShare = { activeDialog = "export_share" },
                onShowStats = { activeDialog = "workspace_stats" },
                onShowShortcuts = { activeDialog = "shortcuts" },
                onShowTrash = { activeDialog = "trash_confirm" },
                onSync = { showNotice("Tüm notlar başarıyla senkronize edildi ✨") },
                onArchiveChanged = {
                    archivedCount = it
                    preferences.edit().putInt("archived_count", it).apply()
                }
            )

            AppPage.Notebook -> NotebookScreen(
                title = documentTitle,
                onTitleChange = { documentTitle = it },
                onBack = { pageName = AppPage.Library.name },
                onNotice = showNotice,
                onNewNote = { activeDialog = "quick_note" },
                onShowStats = { activeDialog = "workspace_stats" },
                onExportPdf = { activeDialog = "export_share" }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        )

        if (activeDialog.isNotEmpty()) {
            DialogHost(
                type = activeDialog,
                selectedFolder = selectedFolder,
                noteCount = savedNotes.size,
                onDismiss = { activeDialog = "" },
                onCreateFolder = { name, description, tone ->
                    val cleanName = name.trim()
                    if (cleanName.isNotEmpty() && folders.none { it.title.equals(cleanName, true) }) {
                        folders.add(0, customFolder(cleanName, description.trim(), tone))
                        val customNames = preferences.getStringSet("custom_folder_names", emptySet())
                            .orEmpty().toMutableSet().apply { add(cleanName) }
                        preferences.edit().putStringSet("custom_folder_names", customNames).apply()
                        showNotice("'$cleanName' koleksiyonu oluşturuldu 🌿")
                    } else if (cleanName.isNotEmpty()) {
                        showNotice("Bu isimde bir koleksiyon zaten var")
                    }
                    activeDialog = ""
                },
                onCreateTag = { name, tone ->
                    val cleanName = name.trim().removePrefix("#")
                    if (cleanName.isNotEmpty() && tags.none { it.name.equals(cleanName, true) }) {
                        tags.add(MevaTag(cleanName, 0, tone))
                        val customNames = preferences.getStringSet("custom_tag_names", emptySet())
                            .orEmpty().toMutableSet().apply { add(cleanName) }
                        preferences.edit().putStringSet("custom_tag_names", customNames).apply()
                        showNotice("#$cleanName etiketi eklendi 🏷️")
                    } else if (cleanName.isNotEmpty()) {
                        showNotice("Bu etiket zaten mevcut")
                    }
                    activeDialog = ""
                },
                onSaveNote = { title, body ->
                    val note = LocalNote(
                        title = title.trim().ifBlank { "Başlıksız Düşünce" },
                        body = body.trim(),
                        folder = selectedFolder
                    )
                    savedNotes.add(note)
                    saveLocalNotes(context, savedNotes)
                    totalNotes += 1
                    preferences.edit().putInt("total_notes", totalNotes).apply()
                    activeDialog = ""
                    showNotice("Not başarıyla kaydedildi ✍️")
                },
                onTrashCleared = {
                    trashCount = 0
                    preferences.edit().putInt("trash_count", 0).apply()
                    activeDialog = ""
                    showNotice("Çöp kutusu tamamen boşaltıldı 🧹")
                },
                onShareMarkdown = {
                    runCatching { shareMarkdown(context, savedNotes, folders) }
                        .onFailure { showNotice("Markdown dışa aktarılamadı") }
                    activeDialog = ""
                },
                onSharePdf = {
                    runCatching { shareNotesPdf(context, savedNotes, documentTitle) }
                        .onFailure { showNotice("PDF oluşturulamadı") }
                    activeDialog = ""
                },
                onCopyLink = {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Meva koleksiyon bağlantısı", "https://meva.app/c/dusunceler-82f"))
                    showNotice("Bağlantı panoya kopyalandı 🔗")
                }
            )
        }
    }
}

private fun customFolder(
    title: String,
    description: String = "Yeni düşünceleriniz ve notlarınız için sakin bir alan.",
    tone: FolderTone = FolderTone.Sage
) = CollectionFolder(
    title = title,
    count = 0,
    description = description.ifBlank { "Yeni düşünceleriniz ve notlarınız için sakin bir alan." },
    tags = emptyList(),
    categories = emptySet(),
    icon = FolderIcon.New,
    tone = tone,
    updated = "Az önce oluşturuldu",
    bannerTitle = "Bu koleksiyon hazır",
    bannerSubtitle = "Yeni notlarını ekle"
)
