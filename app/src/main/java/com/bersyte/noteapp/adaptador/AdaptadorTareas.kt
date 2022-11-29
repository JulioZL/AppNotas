package com.bersyte.noteapp.adaptador

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bersyte.noteapp.databinding.AdaptadorNotasBinding
import com.bersyte.noteapp.databinding.AdaptadorTareasBinding
import com.bersyte.noteapp.fragmentos.FGInicioDirections
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.model.Tarea
import java.util.*


class AdaptadorTareas : RecyclerView.Adapter<AdaptadorTareas.TareaViewHolder>() {

    class TareaViewHolder(val itemBinding: AdaptadorTareasBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    private val differCallback =
        object : DiffUtil.ItemCallback<Tarea>() {
            override fun areItemsTheSame(oldItem: Tarea, newItem: Tarea): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.tareaBody == newItem.tareaBody &&
                        oldItem.tareatvDate == newItem.tareatvDate &&
                        oldItem.tareaSubTitle == newItem.tareaSubTitle &&
                        oldItem.tareaTitle == newItem.tareaTitle
            }

            override fun areContentsTheSame(oldItem: Tarea, newItem: Tarea): Boolean {
                return oldItem == newItem
            }

        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        return TareaViewHolder(
            AdaptadorTareasBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val currentTarea = differ.currentList[position]

        holder.itemBinding.tvTareaTitle.text = currentTarea.tareaTitle
        holder.itemBinding.tvTareaSubTitle.text = currentTarea.tareaSubTitle
        holder.itemBinding.tvTareaDate.text = currentTarea.tareatvDate
        holder.itemBinding.tvTareaBody.text = currentTarea.tareaBody
        val random = Random()
        /*val color =
            Color.argb(
                255, random.nextInt(256),
                random.nextInt(256), random.nextInt(256)
            )*/
        val color = Color.RED
        holder.itemBinding.ibColor.setBackgroundColor(color)

        holder.itemView.setOnClickListener { view ->

            val direction = FGInicioDirections
                .actionHomeFragmentToUpdateTareaFragment(currentTarea)
            view.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}