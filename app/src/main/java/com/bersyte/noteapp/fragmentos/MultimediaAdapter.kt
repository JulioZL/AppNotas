package com.bersyte.noteapp.fragmentos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bersyte.noteapp.R
import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.model.Multimedia

class MultimediaAdapter (var multimedia: List<Multimedia>): RecyclerView.Adapter<MultimediaAdapter.ViewHolder>(){

    class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
        var description: TextView
        var image: ImageView
        var card: CardView
        var delete: ImageView

        init {
            description = v.findViewById(R.id.descriptionM)
            image = v.findViewById(R.id.imageViewM)
            card = v.findViewById(R.id.cardViewM)
            delete = v.findViewById(R.id.delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.multimedia_item, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m = multimedia[position]
        holder.description.text = m.description

        if (m.type == "photo") {
            holder.image.setImageResource(R.drawable.foto)
        } else if (m.type == "video") {
            holder.image.setImageResource(R.drawable.camara)
        }

        holder.card.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id",m.id.toString())
            bundle.putString("path",m.path)
            bundle.putString("description",m.description)

            when (m.type) {
                "photo" -> {
                    it.findNavController().navigate(R.id.action_viewMultimediaFragment_to_viewPhotoFragment, bundle)
                }
                "video" -> {
                    it.findNavController().navigate(R.id.action_viewMultimediaFragment_to_viewVideoFragment, bundle)
                }
                else -> {
                    it.findNavController().navigate(R.id.action_viewMultimediaFragment_to_viewAudioFragment, bundle)
                }
            }
        }

        val id = m.idNote
        holder.delete.setOnClickListener {
            NoteDatabase.getInstance(holder.description.context).MultimediaDao().delete(m)
            val tareas  = NoteDatabase.getInstance(holder.description.context).MultimediaDao().getMultimedia(id)
            this.multimedia = tareas
            this.notifyItemRemoved(position)
        }

    }

    override fun getItemCount(): Int {
        return multimedia.size
    }

}



