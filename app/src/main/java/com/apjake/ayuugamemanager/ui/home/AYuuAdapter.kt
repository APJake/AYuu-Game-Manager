package com.apjake.ayuugamemanager.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apjake.ayuugamemanager.R
import com.apjake.ayuugamemanager.model.User

class AYuuAdapter: ListAdapter<User, AYuuAdapter.MyViewHolder>(DiffCallback()) {

    private lateinit var context: Context
    var onItemClick: ((User) -> Unit)? = null

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvNickname: TextView = itemView.findViewById(R.id.tvNickname)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvNo: TextView = itemView.findViewById(R.id.tvNo)
        val tvDinger: TextView = itemView.findViewById(R.id.tvDinger)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem._id == newItem._id
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.updatedAt == newItem.updatedAt
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.single_ayuu_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.tvName.text = user.name
        holder.tvNickname.text = user.nickname
        holder.tvUsername.text = user.username
        holder.tvDinger.text = user.dinger.toString()
        holder.tvNo.text = (position+1).toString()
    }
}