package com.meva.notes

import androidx.compose.ui.graphics.Color

internal data class CollectionFolder(
    val title: String,
    val count: Int,
    val description: String,
    val tags: List<String>,
    val categories: Set<String>,
    val icon: FolderIcon,
    val tone: FolderTone,
    val updated: String,
    val bannerTitle: String,
    val bannerSubtitle: String
)

internal enum class FolderIcon {
    Journal, Work, Creative, Books, Travel, Wellness, New
}

internal enum class FolderTone {
    Sage, Terracotta, Peach, Stone, Mint, Forest
}

internal data class MevaTag(
    val name: String,
    val count: Int,
    val tone: FolderTone = FolderTone.Sage
)

internal data class LocalNote(
    val title: String,
    val body: String,
    val folder: String = "Kişisel & Günlük"
)

internal data class InkStroke(
    val points: List<androidx.compose.ui.geometry.Offset>,
    val color: Color,
    val width: Float,
    val opacity: Float = 1f
)

internal val InitialFolders = listOf(
    CollectionFolder(
        title = "Kişisel & Günlük",
        count = 14,
        description = "Haftalık yansımalar, dingin sabah rutini gözlemleri ve kişisel gelişim hedefleri günlüğü.",
        tags = listOf("huzur", "düşünceler", "sabah"),
        categories = setOf("aktif", "favori"),
        icon = FolderIcon.Journal,
        tone = FolderTone.Sage,
        updated = "Bugün düzenlendi",
        bannerTitle = "Bugün kendine alan aç",
        bannerSubtitle = "Haftalık yansımalar"
    ),
    CollectionFolder(
        title = "İş & Projeler",
        count = 19,
        description = "Arayüz bileşenleri belgeleri, haftalık yol haritası ve kodlama dokümantasyonu.",
        tags = listOf("tasarım", "kodlama", "sprint"),
        categories = setOf("aktif", "ortak"),
        icon = FolderIcon.Work,
        tone = FolderTone.Terracotta,
        updated = "Dün 16:40",
        bannerTitle = "Sprint Verimliliği",
        bannerSubtitle = "Sprint #24 Tasarım Sistemi · 3 görev bekliyor"
    ),
    CollectionFolder(
        title = "Yaratıcı Fikirler & Eskizler",
        count = 12,
        description = "Ürün fikirleri, mimari eskizler, tipografi denemeleri ve görsel ilham panoları.",
        tags = listOf("konsept", "ilham", "eskiz"),
        categories = setOf("favori"),
        icon = FolderIcon.Creative,
        tone = FolderTone.Peach,
        updated = "3 gün önce",
        bannerTitle = "Fikirlerini serbest bırak",
        bannerSubtitle = "4 yeni eskiz"
    ),
    CollectionFolder(
        title = "Kitap Alıntıları & Felsefe",
        count = 8,
        description = "Stoacılık prensipleri, doğu felsefesi kesitleri ve edebi metinlerden derin pasajlar.",
        tags = listOf("okuma", "wabisabi", "stoa"),
        categories = setOf("ortak"),
        icon = FolderIcon.Books,
        tone = FolderTone.Stone,
        updated = "5 gün önce",
        bannerTitle = "Kusurlu olanda saklı zarafeti fark ettiğinde…",
        bannerSubtitle = "— Wabi-Sabi Notları"
    ),
    CollectionFolder(
        title = "Seyahat & Rotalar",
        count = 5,
        description = "Müze gezi rotaları, tren seferleri çizelgeleri ve yerel kahve kavurucuları listesi.",
        tags = listOf("gezi", "harita", "rota"),
        categories = setOf("favori"),
        icon = FolderIcon.Travel,
        tone = FolderTone.Mint,
        updated = "Geçen hafta",
        bannerTitle = "Kyoto & İskandinavya",
        bannerSubtitle = "Bir sonraki durak: ilham"
    ),
    CollectionFolder(
        title = "Sağlık & Rutinler",
        count = 7,
        description = "Nefes egzersizleri, yoga sekansları, beslenme takibi ve uyku kalitesi günlüğü.",
        tags = listOf("meditasyon", "spor", "zindelik"),
        categories = setOf("aktif"),
        icon = FolderIcon.Wellness,
        tone = FolderTone.Forest,
        updated = "2 gün önce",
        bannerTitle = "Sabah Meditasyonu · 18 gün seri",
        bannerSubtitle = "Bugünkü rutinin tamamlandı"
    )
)

internal val InitialTags = listOf(
    MevaTag("tasarım", 24, FolderTone.Sage),
    MevaTag("minimalizm", 18, FolderTone.Stone),
    MevaTag("felsefe", 12, FolderTone.Terracotta),
    MevaTag("kitap", 10, FolderTone.Stone),
    MevaTag("huzur", 9, FolderTone.Mint),
    MevaTag("düşünceler", 8, FolderTone.Sage),
    MevaTag("kodlama", 7, FolderTone.Stone),
    MevaTag("meditasyon", 7, FolderTone.Forest),
    MevaTag("gezi", 5, FolderTone.Mint),
    MevaTag("wabisabi", 4, FolderTone.Terracotta),
    MevaTag("eskiz", 4, FolderTone.Peach),
    MevaTag("sabah", 3, FolderTone.Sage),
    MevaTag("spor", 3, FolderTone.Forest),
    MevaTag("rota", 2, FolderTone.Mint)
)

internal fun FolderTone.background(): Color = when (this) {
    FolderTone.Sage -> Color(0xFFC6EBD9)
    FolderTone.Terracotta -> Color(0xFFFFDBD0)
    FolderTone.Peach -> Color(0xFFFD9D80)
    FolderTone.Stone -> Color(0xFFDDE4DF)
    FolderTone.Mint -> Color(0xFFABCEBE)
    FolderTone.Forest -> Color(0xFF4A6B5D)
}

internal fun FolderTone.foreground(): Color = when (this) {
    FolderTone.Sage -> Color(0xFF2D4D40)
    FolderTone.Terracotta -> Color(0xFF76321D)
    FolderTone.Peach -> Color(0xFF77331D)
    FolderTone.Stone -> Color(0xFF414844)
    FolderTone.Mint -> Color(0xFF244E3E)
    FolderTone.Forest -> Color.White
}
