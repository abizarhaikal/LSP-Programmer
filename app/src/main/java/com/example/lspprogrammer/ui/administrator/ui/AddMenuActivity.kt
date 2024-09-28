package com.example.lspprogrammer.ui.administrator.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.lspprogrammer.R
import com.example.lspprogrammer.databinding.ActivityAddMenuBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMenuBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private var previousImageUri: String? = null
    private var menuId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        when (intent.getIntExtra(ADD_MENU, 0)) {
            100 -> {
                clearFields()
                binding.btnTambah.text = "Tambah Menu"
                binding.btnDelete.visibility = View.GONE
            }
            200 -> {
                menuId = intent.getStringExtra("menuId")
                loadMenuData(menuId)
                binding.btnTambah.text = "Perbarui Menu"
                binding.btnDelete.visibility = View.VISIBLE
            }
        }

        if (imageUri == null) {
            binding.ivImageDetail.visibility = View.GONE
            binding.ivLottie.visibility = View.VISIBLE
        }
        binding.btnDelete.setOnClickListener { deleteMenu() }
        binding.btnGaleri.setOnClickListener { startGaleri() }
        binding.btnCamera.setOnClickListener { checkCameraPermissionAndStart() }
        binding.btnTambah.setOnClickListener { saveOrUpdateMenu() }
    }

    private fun deleteMenu() {
        firestore.collection("menu").document(menuId!!).delete()
            .addOnSuccessListener {
                showToast("Menu berhasil dihapus")
                finish()
            }
            .addOnFailureListener {
                showToast("Gagal menghapus menu")
            }
    }

    private fun clearFields() {
        binding.edtNamaMenu.text?.clear()
        binding.edtHargaMenu.text?.clear()
        binding.edtStokMenu.text?.clear()
        binding.spinnerRoleMenu.setSelection(0)
        imageUri = null
        imageBitmap = null
        previousImageUri = null
        binding.ivImageDetail.setImageDrawable(null)
    }

    private fun loadMenuData(menuId: String?) {
        menuId?.let {
            firestore.collection("menu").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        binding.edtNamaMenu.setText(document.getString("nama"))
                        binding.edtHargaMenu.setText(document.getLong("harga")?.toString())
                        binding.edtStokMenu.setText(document.getLong("stok")?.toString())
                        binding.spinnerRoleMenu.setSelection(getCategoryPosition(document.getString("kategori")))
                        previousImageUri = document.getString("imageUri")
                        showImageFromUrl(previousImageUri)
                    } else {
                        showToast("Menu tidak ditemukan")
                    }
                }
                .addOnFailureListener {
                    showToast("Gagal memuat data menu")
                }
        }
    }

    private fun saveOrUpdateMenu() {
        when {
            imageUri != null -> uploadImageAndSaveData()
            imageBitmap != null -> uploadBitmapAndSaveData(imageBitmap!!)
            else -> saveDataToFirestore(previousImageUri ?: "")
        }
    }

    private fun uploadImageAndSaveData() {
        val imageName = "menu_images/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(imageName)

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveDataToFirestore(downloadUri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Gagal mengunggah gambar: ${exception.message}")
                }
        }
    }

    private fun uploadBitmapAndSaveData(bitmap: Bitmap) {
        val imageName = "menu_images/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(imageName)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        storageRef.putBytes(imageData)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveDataToFirestore(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception ->
                showToast("Gagal mengunggah gambar: ${exception.message}")
            }
    }

    private fun saveDataToFirestore(imageUri: String) {
        val namaMenu = binding.edtNamaMenu.text.toString()
        val hargaMenu = binding.edtHargaMenu.text.toString()
        val stokMenu = binding.edtStokMenu.text.toString()
        val kategoriMenu = binding.spinnerRoleMenu.selectedItem.toString()

        if (namaMenu.isEmpty() || hargaMenu.isEmpty() || stokMenu.isEmpty() || kategoriMenu.isEmpty()) {
            showToast("Semua field harus diisi")
            return
        }

        val menuData = hashMapOf(
            "nama" to namaMenu,
            "harga" to hargaMenu.toInt(),
            "stok" to stokMenu.toInt(),
            "kategori" to kategoriMenu,
            "imageUri" to imageUri
        )

        if (menuId != null) {
            // Update existing menu
            firestore.collection("menu").document(menuId!!).set(menuData)
                .addOnSuccessListener {
                    showToast("Menu berhasil diperbarui")
                    finish()
                }
                .addOnFailureListener {
                    showToast("Gagal memperbarui menu")
                }
        } else {
            // Add new menu
            firestore.collection("menu").add(menuData)
                .addOnSuccessListener {
                    showToast("Menu berhasil ditambahkan")
                    finish()
                }
                .addOnFailureListener {
                    showToast("Gagal menambahkan menu")
                }
        }
    }

    private fun showImageFromUrl(url: String?) {
        url?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivImageDetail)
            // Jika gambar ada, tampilkan gambar dan sembunyikan Lottie
            binding.ivImageDetail.visibility = View.VISIBLE
            binding.ivLottie.visibility = View.GONE
        } ?: run {
            // Jika url kosong, sembunyikan gambar dan tampilkan Lottie
            binding.ivImageDetail.visibility = View.GONE
            binding.ivLottie.visibility = View.VISIBLE
        }
    }


    private val launcherGaleri = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            imageBitmap = null
            showImageFromUri()
        } else {
            Log.d("Photo Picker", "Tidak ada media yang dipilih")
        }
    }

    private fun showImageFromUri() {
        imageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.ivImageDetail)
            // Jika gambar ada, tampilkan gambar dan sembunyikan Lottie
            binding.ivImageDetail.visibility = View.VISIBLE
            binding.ivLottie.visibility = View.GONE
        } ?: run {
            // Jika uri kosong, sembunyikan gambar dan tampilkan Lottie
            binding.ivImageDetail.visibility = View.GONE
            binding.ivLottie.visibility = View.VISIBLE
        }
    }


    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            imageBitmap = bitmap
            imageUri = null
            showImageFromBitmap()
        } else {
            Log.d("Camera", "Tidak ada gambar yang diambil")
        }
    }

    private fun showImageFromBitmap() {
        imageBitmap?.let {
            binding.ivImageDetail.setImageBitmap(it)
            // Jika bitmap ada, tampilkan gambar dan sembunyikan Lottie
            binding.ivImageDetail.visibility = View.VISIBLE
            binding.ivLottie.visibility = View.GONE
        } ?: run {
            // Jika bitmap kosong, sembunyikan gambar dan tampilkan Lottie
            binding.ivImageDetail.visibility = View.GONE
            binding.ivLottie.visibility = View.VISIBLE
        }
    }


    private fun startGaleri() {
        launcherGaleri.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        launcherCamera.launch()
    }

    private fun checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1001)
        } else {
            startCamera()
        }
    }

    private fun getCategoryPosition(category: String?): Int {
        val categories = resources.getStringArray(R.array.menu_options)
        return categories.indexOf(category)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ADD_MENU = "add_menu"
    }
}
