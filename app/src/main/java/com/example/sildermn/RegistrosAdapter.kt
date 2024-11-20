package com.example.sildermn

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegistrosAdapter : RecyclerView.Adapter<RegistrosAdapter.RegistroViewHolder>() {

    private val registrosList = mutableListOf<Registro>()

    fun setRegistros(registros: List<Registro>) {
        registrosList.clear()
        registrosList.addAll(registros)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        holder.bind(registrosList[position])
    }

    override fun getItemCount(): Int = registrosList.size

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val edadTextView: TextView = itemView.findViewById(R.id.edadTextView)
        private val sexoTextView: TextView = itemView.findViewById(R.id.sexoTextView)
        private val anioNacimientoTextView: TextView = itemView.findViewById(R.id.anioNacimientoTextView)
        private val correoTextView: TextView = itemView.findViewById(R.id.correoTextView)
        private val fotoImageView: ImageView = itemView.findViewById(R.id.fotoImageView)

        fun bind(registro: Registro) {
            nombreTextView.text = "Nombre: ${registro.Nombre}"
            edadTextView.text = "Edad: ${registro.Edad}"
            sexoTextView.text = "Sexo: ${registro.Sexo}"
            anioNacimientoTextView.text = "Año Nacimiento: ${registro.AñoNacimiento}"
            correoTextView.text = "Correo: ${registro.Correo}"

            // Decodificar la imagen Base64
            val decodedBytes = Base64.decode(registro.FotoBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            fotoImageView.setImageBitmap(bitmap)
        }
    }
}
