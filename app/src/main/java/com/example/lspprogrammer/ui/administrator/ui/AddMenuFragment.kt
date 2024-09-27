//package com.example.lspprogrammer.ui.administrator.ui
//
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.result.launch
//import androidx.fragment.app.Fragment
//import com.example.lspprogrammer.databinding.FragmentAddMenuBinding
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import java.io.ByteArrayOutputStream
//import java.util.UUID
//
//
//class AddMenuFragment : Fragment() {
//    private lateinit var binding: FragmentAddMenuBinding
//    private val firestore = FirebaseFirestore.getInstance()
//    private val storage = FirebaseStorage.getInstance()
//    private var imageUri: Uri? = null
//    private var imageBitmap: Bitmap? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        binding = FragmentAddMenuBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.btnGaleri.setOnClickListener {
//            startGaleri()
//        }
//
//        binding.btnCamera.setOnClickListener {
//            startCamera()
//        }
//
//        binding.btnTambah.setOnClickListener {
//            if (imageUri != null) {
//                uploadImageAndSaveData()
//            } else if (imageBitmap != null) {
//                uploadBitmapAndSaveData(imageBitmap!!)
//            } else {
//                showToast("Pilih atau ambil gambar terlebih dahulu")
//            }
//        }
//    }
//
//    private fun uploadImageAndSaveData() {
//        val imageName = "menu_images/${UUID.randomUUID()}.jpg"
//        val storageRef = storage.reference.child(imageName)
//
//        imageUri?.let { uri ->
//            storageRef.putFile(uri)
//                .addOnSuccessListener {
//                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                        saveDataToFirestore(downloadUri.toString())
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    showToast("Gagal mengunggah gambar")
//                }
//        }
//    }
//
//    private fun uploadBitmapAndSaveData(bitmap: Bitmap) {
//        val imageName = "menu_images/${UUID.randomUUID()}.jpg"
//        val storageRef = storage.reference.child(imageName)
//
//        // Convert Bitmap to ByteArray
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val imageData = baos.toByteArray()
//
//        // Upload byte array to Firebase Storage
//        storageRef.putBytes(imageData)
//            .addOnSuccessListener {
//                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    saveDataToFirestore(downloadUri.toString())
//                }
//            }
//            .addOnFailureListener { exception ->
//                showToast("Gagal mengunggah gambar")
//            }
//    }
//
//    private fun saveDataToFirestore(imageUri: String) {
//        val namaMenu = binding.edtNamaMenu.text.toString()
//        val hargaMenu = binding.edtHargaMenu.text.toString()
//        val stokMenu = binding.edtStokMenu.text.toString()
//        val kategoriMenu = binding.spinnerRoleMenu.selectedItem.toString()
//
//        if (namaMenu.isEmpty() || hargaMenu.isEmpty() || stokMenu.isEmpty() || kategoriMenu.isEmpty()) {
//            showToast("Semua field harus diisi")
//            return
//        }
//
//        val menuData = hashMapOf(
//            "nama" to namaMenu,
//            "harga" to hargaMenu.toInt(),
//            "stok" to stokMenu.toInt(),
//            "kategori" to kategoriMenu,
//            "imageUri" to imageUri
//        )
//
//        firestore.collection("menu")
//            .add(menuData)
//            .addOnSuccessListener {
//                showToast("Menu berhasil ditambahkan")
//            }
//            .addOnFailureListener {
//                showToast("Gagal menambahkan menu")
//            }
//
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//    }
//
//    // Launcher galeri
//    private val launcherGaleri = registerForActivityResult(
//        ActivityResultContracts.PickVisualMedia()
//    ) { uri: Uri? ->
//        if (uri != null) {
//            imageUri = uri
//            imageBitmap = null // Reset bitmap
//            showImageFromUri()
//        } else {
//            Log.d("Photo Picker", "No media selected")
//        }
//    }
//
//    private fun showImageFromUri() {
//        imageUri?.let {
//            binding.ivImageDetail.setImageURI(it)
//        }
//    }
//
//    // Launcher kamera
//    private val launcherCamera = registerForActivityResult(
//        ActivityResultContracts.TakePicturePreview()
//    ) { bitmap: Bitmap? ->
//        if (bitmap != null) {
//            imageBitmap = bitmap
//            imageUri = null // Reset URI
//            showImageFromBitmap()
//        } else {
//            Log.d("Camera", "No image captured")
//        }
//    }
//
//    private fun showImageFromBitmap() {
//        imageBitmap?.let {
//            binding.ivImageDetail.setImageBitmap(it)
//        }
//    }
//
//    private fun startGaleri() {
//        launcherGaleri.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }
//
//    private fun startCamera() {
//        launcherCamera.launch()
//    }
//}
