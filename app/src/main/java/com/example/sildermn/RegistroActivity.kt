package com.example.sildermn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class RegistrosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var registrosAdapter: RegistrosAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        registrosAdapter = RegistrosAdapter()
        recyclerView.adapter = registrosAdapter

        database = FirebaseDatabase.getInstance().getReference("usuarios")

        // Leer datos de Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registrosList = mutableListOf<Registro>()
                for (data in snapshot.children) {
                    val registro = data.getValue(Registro::class.java)
                    if (registro != null) {
                        registrosList.add(registro)
                    }
                }
                registrosAdapter.setRegistros(registrosList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })

        // Botón para volver al formulario
        findViewById<Button>(R.id.buttonVolverFormulario).setOnClickListener {
            val intent = Intent(this, FormularioActivity::class.java)
            startActivity(intent)
        }

        // Botón para ir al inicio
        findViewById<Button>(R.id.buttonIrInicio).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Asegúrate de que MainActivity sea el inicio
            startActivity(intent)
        }
    }
}
