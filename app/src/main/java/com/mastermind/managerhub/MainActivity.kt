
package com.mastermind.managerhub

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val backgroundColor = Color.rgb(15, 18, 32)
    private val cardColor = Color.rgb(29, 34, 55)
    private val accentColor = Color.rgb(100, 115, 255)
    private val textColor = Color.WHITE
    private val mutedColor = Color.rgb(175, 183, 207)

    private lateinit var rootLayout: LinearLayout
    private lateinit var contentLayout: LinearLayout
    private var currentSection = "Home"

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val granted = results.values.any { it }
            if (granted) {
                showSection(currentSection)
            } else {
                Toast.makeText(
                    this,
                    "মিডিয়া দেখার জন্য প্রয়োজনীয় অনুমতি দিন।",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(
                    this,
                    "ক্যামেরা থেকে ছবি পাওয়া গেছে।",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSection("Home")
    }

    private fun hasMediaPermission(): Boolean {
        val permissions = requiredMediaPermissions()
        return permissions.any { permission ->
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requiredMediaPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= 34 -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )

            Build.VERSION.SDK_INT >= 33 -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )

            else -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun requestMediaPermissions() {
        permissionLauncher.launch(requiredMediaPermissions())
    }

    private fun showSection(section: String) {
        currentSection = section

        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(18), dp(18), dp(18), dp(10))
        }

        val header = TextView(this).apply {
            text = "MASTERMIND"
            textSize = 13f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.15f
        }
        rootLayout.addView(header)

        val title = TextView(this).apply {
            text = when (section) {
                "Home" -> "Manager Hub"
                "Gallery" -> "Photo Gallery"
                "Videos" -> "Video Library"
                "Music" -> "Music Library"
                "Files" -> "File Manager"
                "Settings" -> "Settings"
                else -> section
            }
            textSize = 27f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(8), 0, dp(14))
        }
        rootLayout.addView(title)

        val scrollView = ScrollView(this)
        contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scrollView.addView(contentLayout)

        rootLayout.addView(
            scrollView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(4))
        }

        listOf("Home", "Gallery", "Videos", "Music", "Files", "Settings")
            .forEach { item ->
                val button = Button(this).apply {
                    text = when (item) {
                        "Home" -> "Home"
                        "Gallery" -> "Photos"
                        "Videos" -> "Video"
                        "Music" -> "Music"
                        "Files" -> "Files"
                        else -> "Settings"
                    }
                    textSize = 9f
                    isAllCaps = false
                    setTextColor(textColor)
                    setBackgroundColor(
                        if (item == section) accentColor else cardColor
                    )
                    setPadding(dp(2), 0, dp(2), 0)
                    setOnClickListener { showSection(item) }
                }

                navigation.addView(
                    button,
                    LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                        marginEnd = dp(3)
                    }
                )
            }

        rootLayout.addView(navigation)
        setContentView(rootLayout)

        when (section) {
            "Home" -> showHome()
            "Gallery" -> showMediaSection("Gallery")
            "Videos" -> showMediaSection("Videos")
            "Music" -> showMediaSection("Music")
            "Files" -> showFilesSection()
            "Settings" -> showSettings()
        }
    }

    private fun showHome() {
        addText(
            "আপনার সব মিডিয়া এক জায়গায়",
            17f,
            textColor,
            true
        )
        addText(
            "ছবি, ভিডিও, মিউজিক ও ফাইল পরিচালনা করুন।",
            14f,
            mutedColor
        )

        addSpacer(18)

        addDashboardCard(
            "🖼  Photo Gallery",
            "ফোনের ছবি দেখুন",
            "Gallery"
        )
        addDashboardCard(
            "🎬  Video Library",
            "আপনার ভিডিও ব্রাউজ করুন",
            "Videos"
        )
        addDashboardCard(
            "🎵  Music Library",
            "মিউজিক ফাইল খুঁজুন",
            "Music"
        )
        addDashboardCard(
            "📁  File Manager",
            "ফোনের ফাইল ব্রাউজ করুন",
            "Files"
        )

        addSpacer(12)

        addActionButton("📷  Open Camera") {
            openCamera()
        }

        addSpacer(8)

        addActionButton("🔐  Grant Media Access") {
            requestMediaPermissions()
        }

        addSpacer(16)

        addText(
            "MASTERMIND Manager Hub • Offline-first",
            12f,
            mutedColor
        )
    }

    private fun addDashboardCard(
        heading: String,
        description: String,
        section: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(cardColor, 18)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (
                    section == "Gallery" ||
                    section == "Videos" ||
                    section == "Music"
                ) {
                    if (!hasMediaPermission()) {
                        requestMediaPermissions()
                    } else {
                        showSection(section)
                    }
                } else {
                    showSection(section)
                }
            }
        }

        val title = TextView(this).apply {
            text = heading
            textSize = 17f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
        }

        val subtitle = TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(mutedColor)
            setPadding(0, dp(6), 0, 0)
        }

        card.addView(title)
        card.addView(subtitle)

        contentLayout.addView(
            card,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        )
    }

    private fun showMediaSection(section: String) {
        if (!hasMediaPermission()) {
            addText(
                "মিডিয়া দেখার অনুমতি প্রয়োজন।",
                16f,
                textColor,
                true
            )
            addSpacer(10)
            addActionButton("Allow Media Access") {
                requestMediaPermissions()
            }
            return
        }

        addText(
            "ফোনের মিডিয়া খোঁজা হচ্ছে...",
            14f,
            mutedColor
        )

        val items = queryMedia(section)

        contentLayout.removeAllViews()

        if (items.isEmpty()) {
            addText(
                "এখানে কোনো ফাইল পাওয়া যায়নি।",
                15f,
                textColor,
                true
            )
            addText(
                "ফোনে ফাইল থাকলে অনুমতি ও ফাইলের অবস্থান পরীক্ষা করুন।",
                13f,
                mutedColor
            )
            return
        }

        addText(
            "পাওয়া গেছে: ${items.size}টি ফাইল",
            15f,
            accentColor,
            true
        )

        items.forEach { item ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(14), dp(12), dp(14), dp(12))
                background = roundedBackground(cardColor, 14)
            }

            val nameView = TextView(this).apply {
                text = item.first
                textSize = 15f
                setTextColor(textColor)
                typeface = Typeface.DEFAULT_BOLD
            }

            val pathView = TextView(this).apply {
                text = item.second
                textSize = 11f
                setTextColor(mutedColor)
                setPadding(0, dp(5), 0, 0)
            }

            card.addView(nameView)
            card.addView(pathView)

            card.setOnClickListener {
                openMedia(item.third, section)
            }

            contentLayout.addView(
                card,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(8)
                }
            )
        }
    }

    private fun queryMedia(
        section: String
    ): List<Triple<String, String, Uri>> {
        val result = mutableListOf<Triple<String, String, Uri>>()

        val collection: Uri
        val projection: Array<String>
        val sortOrder: String

        when (section) {
            "Gallery" -> {
                collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.RELATIVE_PATH
                )
                sortOrder =
                    "${MediaStore.Images.Media.DATE_ADDED} DESC"
            }

            "Videos" -> {
                collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                projection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.RELATIVE_PATH
                )
                sortOrder =
                    "${MediaStore.Video.Media.DATE_ADDED} DESC"
            }

            "Music" -> {
                collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.RELATIVE_PATH
                )
                sortOrder =
                    "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            }

            else -> return result
        }

        try {
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn =
                    cursor.getColumnIndexOrThrow(
                        MediaStore.MediaColumns._ID
                    )
                val nameColumn =
                    cursor.getColumnIndexOrThrow(
                        MediaStore.MediaColumns.DISPLAY_NAME
                    )
                val pathColumn =
                    cursor.getColumnIndex(
                        MediaStore.MediaColumns.RELATIVE_PATH
                    )

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                        ?: "Unnamed media"
                    val path = if (pathColumn >= 0) {
                        cursor.getString(pathColumn) ?: "Media"
                    } else {
                        "Media"
                    }

                    val uri = ContentUris.withAppendedId(
                        collection,
                        id
                    )

                    result.add(Triple(name, path, uri))
                }
            }
        } catch (exception: SecurityException) {
            Toast.makeText(
                this,
                "মিডিয়া অনুমতি পাওয়া যায়নি।",
                Toast.LENGTH_SHORT
            ).show()
        } catch (exception: Exception) {
            Toast.makeText(
                this,
                "ফাইল লোড করা যায়নি।",
                Toast.LENGTH_SHORT
            ).show()
        }

        return result
    }

    private fun openMedia(uri: Uri, section: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                uri,
                when (section) {
                    "Gallery" -> "image/*"
                    "Videos" -> "video/*"
                    else -> "audio/*"
                }
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (exception: Exception) {
            Toast.makeText(
                this,
                "এই ফাইল খোলার অ্যাপ পাওয়া যায়নি।",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showFilesSection() {
        addText(
            "ফোনের সাধারণ ফাইল ব্রাউজ করতে নিচের বাটন চাপুন।",
            14f,
            mutedColor
        )

        addSpacer(12)

        addActionButton("📂  Browse Phone Files") {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }

            try {
                startActivity(intent)
            } catch (exception: Exception) {
                Toast.makeText(
                    this,
                    "ফাইল ব্রাউজার খোলা যায়নি।",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        addSpacer(12)

        addText(
            "এই প্রাথমিক সংস্করণে Android-এর ফাইল পিকার ব্যবহার করা হচ্ছে।",
            13f,
            mutedColor
        )
    }

    private fun showSettings() {
        addText(
            "Appearance",
            17f,
            textColor,
            true
        )

        addText(
            "বর্তমান থিম: Dark",
            14f,
            mutedColor
        )

        addSpacer(12)

        addActionButton("🌙  Dark Theme") {
            Toast.makeText(
                this,
                "Dark Theme সক্রিয় আছে।",
                Toast.LENGTH_SHORT
            ).show()
        }

        addSpacer(8)

        addActionButton("🔐  Media Permissions") {
            requestMediaPermissions()
        }

        addSpacer(16)

        addText(
            "App: MASTERMIND Manager Hub",
            13f,
            mutedColor
        )
        addText(
            "Developer: MASTERMIND",
            13f,
            mutedColor
        )
        addText(
            "Version: 1.0.0",
            13f,
            mutedColor
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            cameraLauncher.launch(intent)
        } catch (exception: Exception) {
            Toast.makeText(
                this,
                "ক্যামেরা অ্যাপ পাওয়া যায়নি।",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addText(
        text: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) {
        val view = TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)

            if (bold) {
                typeface = Typeface.DEFAULT_BOLD
            }

            setPadding(0, dp(3), 0, dp(5))
        }

        contentLayout.addView(view)
    }

    private fun addActionButton(
        label: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 14f
            setTextColor(textColor)
            background = roundedBackground(accentColor, 14)
            setOnClickListener { action() }
        }

        contentLayout.addView(
            button,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )
    }

    private fun addSpacer(size: Int) {
        contentLayout.addView(
            View(this),
            LinearLayout.LayoutParams(
                1,
                dp(size)
            )
        )
    }

    private fun roundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
