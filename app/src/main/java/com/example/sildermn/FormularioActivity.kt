package com.example.sildermn

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.util.*

class FormularioActivity : AppCompatActivity() {

    private lateinit var imageViewFoto: ImageView
    private lateinit var inputNombre: EditText
    private lateinit var inputApellido: EditText
    private lateinit var inputAnioNacimiento: EditText
    private lateinit var inputCorreo: EditText
    private lateinit var inputEdad: EditText
    private lateinit var inputSexo: Spinner
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        val buttonSeleccionarFoto: Button = findViewById(R.id.buttonSeleccionarFoto)
        val buttonGuardar: Button = findViewById(R.id.buttonGuardar)
        val buttonVerRegistros: Button = findViewById(R.id.buttonVerRegistros)
        val buttonVolver: Button = findViewById(R.id.buttonVolver)
        imageViewFoto = findViewById(R.id.imageViewFoto)
        inputNombre = findViewById(R.id.inputNombre)
        inputApellido = findViewById(R.id.inputApellido)
        inputAnioNacimiento = findViewById(R.id.inputAnioNacimiento)
        inputCorreo = findViewById(R.id.inputCorreo)
        inputEdad = findViewById(R.id.inputEdad)
        inputSexo = findViewById(R.id.inputSexo)

        // Configurar opciones para el Spinner (campo Sexo)
        val sexoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sexo_opciones,
            android.R.layout.simple_spinner_item
        )
        sexoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputSexo.adapter = sexoAdapter

        // Registra el launcher para seleccionar imágenes
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    selectedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(contentResolver, it)
                    }
                    imageViewFoto.setImageBitmap(selectedBitmap)
                }
            }
        }

        // Botón para seleccionar una foto
        buttonSeleccionarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        // Botón para guardar los datos en Firebase
        buttonGuardar.setOnClickListener {
            saveDataToFirebase()
        }

        // Botón para ver los registros
        buttonVerRegistros.setOnClickListener {
            val intent = Intent(this, RegistrosActivity::class.java)
            startActivity(intent)
        }

        // Botón para volver a la actividad principal
        buttonVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Cambia 'MainActivity' al nombre de tu actividad principal
            startActivity(intent)
            finish() // Finaliza la actividad actual para evitar volver a ella con el botón 'Atrás'
        }

        // Evento para mostrar el DatePickerDialog
        inputAnioNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    inputAnioNacimiento.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    private fun saveDataToFirebase() {
        val nombre = inputNombre.text.toString()
        val apellido = inputApellido.text.toString()
        val anioNacimiento = inputAnioNacimiento.text.toString()
        val correo = inputCorreo.text.toString()
        val edad = inputEdad.text.toString()
        val sexo = inputSexo.selectedItem.toString()

        if (nombre.isEmpty() || apellido.isEmpty() || anioNacimiento.isEmpty() || correo.isEmpty() || edad.isEmpty() || selectedBitmap == null) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir la imagen a Base64
        val base64Image = convertBitmapToBase64(selectedBitmap!!)
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("usuarios")
        val userId = usersRef.push().key ?: return
        val userMap = mapOf(
            "Nombre" to nombre,
            "Apellido" to apellido,
            "AñoNacimiento" to anioNacimiento,
            "Correo" to correo,
            "Edad" to edad,
            "Sexo" to sexo,
            "FotoBase64" to base64Image // Guardar la imagen como cadena Base64
        )

        // Guardar datos en Firebase Realtime Database
        usersRef.child(userId).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                // Limpiar los campos
                clearFields()

                // Redirigir a RegistrosActivity
                val intent = Intent(this, RegistrosActivity::class.java)
                startActivity(intent)
                finish() // Finalizar la actividad actual
            } else {
                Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearFields() {
        inputNombre.text.clear()
        inputApellido.text.clear()
        inputAnioNacimiento.text.clear()
        inputCorreo.text.clear()
        inputEdad.text.clear()
        inputSexo.setSelection(0) // Volver a la primera opción del Spinner
        imageViewFoto.setImageResource(0) // Limpiar la imagen
        selectedBitmap = null // Resetea el bitmap seleccionado
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}

