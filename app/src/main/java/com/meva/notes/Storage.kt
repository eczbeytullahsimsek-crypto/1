package com.meva.notes

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

internal fun loadLocalNotes(context: Context): List<LocalNote> {
    return runCatching {
        val raw = context.getSharedPreferences("meva_local", Context.MODE_PRIVATE)
            .getString("notes", "[]") ?: "[]"
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    LocalNote(
                        title = item.optString("title", "Başlıksız Not"),
                        body = item.optString("body", ""),
                        folder = item.optString("folder", "Kişisel & Günlük")
                    )
                )
            }
        }
    }.getOrDefault(emptyList())
}

internal fun saveLocalNotes(context: Context, notes: List<LocalNote>) {
    val array = JSONArray()
    notes.forEach { note ->
        array.put(
            JSONObject()
                .put("title", note.title)
                .put("body", note.body)
                .put("folder", note.folder)
        )
    }
    context.getSharedPreferences("meva_local", Context.MODE_PRIVATE)
        .edit()
        .putString("notes", array.toString())
        .apply()
}

internal fun shareMarkdown(context: Context, notes: List<LocalNote>, folders: List<CollectionFolder>) {
    val markdown = buildString {
        appendLine("# Meva Notes — Yerel arşiv")
        appendLine()
        folders.forEach { folder ->
            appendLine("## ${folder.title}")
            appendLine()
            val folderNotes = notes.filter { it.folder == folder.title }
            if (folderNotes.isEmpty()) {
                appendLine("_${folder.description}_")
                appendLine()
            } else {
                folderNotes.forEach { note ->
                    appendLine("### ${note.title}")
                    appendLine(note.body)
                    appendLine()
                }
            }
        }
        val uncategorized = notes.filter { note -> folders.none { it.title == note.folder } }
        if (uncategorized.isNotEmpty()) {
            appendLine("## Diğer notlar")
            uncategorized.forEach { note ->
                appendLine("### ${note.title}")
                appendLine(note.body)
                appendLine()
            }
        }
    }
    val file = File(context.cacheDir, "meva-notlar.md")
    file.writeText(markdown)
    shareFile(context, file, "text/markdown", "Meva notlarını paylaş")
}

internal fun shareNotesPdf(context: Context, notes: List<LocalNote>, title: String = "Meva Notes") {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(50, 83, 70)
        textSize = 28f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(55, 62, 58)
        textSize = 13f
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }
    val mutedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(114, 121, 116)
        textSize = 11f
    }

    canvas.drawText(title.take(38), 48f, 68f, headingPaint)
    canvas.drawText("Meva Notes · Yerel not arşivi", 48f, 91f, mutedPaint)
    var y = 132f
    notes.ifEmpty { listOf(LocalNote("Layers of Skin", "Notlarınıza ve çizimlerinize Meva'dan devam edin.")) }
        .forEach { note ->
            if (y > 780f) return@forEach
            headingPaint.textSize = 17f
            canvas.drawText(note.title.take(52), 48f, y, headingPaint)
            y += 22f
            canvas.drawText(note.folder, 48f, y, mutedPaint)
            y += 20f
            val text = note.body.ifBlank { "Bu not için henüz bir açıklama eklenmedi." }
            text.split('\n').forEach { paragraph ->
                var remaining = paragraph
                if (remaining.isBlank()) {
                    y += 9f
                } else {
                    while (remaining.isNotEmpty() && y < 790f) {
                        val count = bodyPaint.breakText(remaining, true, 495f, null).coerceAtLeast(1)
                        canvas.drawText(remaining.take(count), 48f, y, bodyPaint)
                        remaining = remaining.drop(count).trimStart()
                        y += 19f
                    }
                }
            }
            y += 26f
        }
    document.finishPage(page)

    val file = File(context.cacheDir, "meva-notlar.pdf")
    file.outputStream().use(document::writeTo)
    document.close()
    shareFile(context, file, "application/pdf", "Meva PDF notlarını paylaş")
}

private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, chooserTitle))
}
