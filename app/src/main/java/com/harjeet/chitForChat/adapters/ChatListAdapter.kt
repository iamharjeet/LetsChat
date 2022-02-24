package com.harjeet.chitForChat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harjeet.chitForChat.ChatLiveActivity
import com.harjeet.chitForChat.Models.ChatFriendsModel
import com.harjeet.chitForChat.MyConstants
import com.harjeet.chitForChat.MyUtils
import com.harjeet.chitForChat.R
import de.hdodenhof.circleimageview.CircleImageView

class ChatListAdapter(var context: Context, var chatFriendList: ArrayList<ChatFriendsModel>) :
    RecyclerView.Adapter<ChatListAdapter.viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.viewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_chat, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListAdapter.viewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.txtName.setText(chatFriendList.get(position).name)
            holder.txtLastMessage.setText(chatFriendList.get(position).origonalMessage)
            holder.txtTime.setText("2:00 pm")
            Glide.with(context).load(chatFriendList.get(position).image).placeholder(R.drawable.user).into(holder.imgUser)

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    ChatLiveActivity::class.java
                ).putExtra(MyConstants.OTHER_USER_NAME, chatFriendList.get(position).name)
                    .putExtra(MyConstants.OTHER_USER_PHONE, chatFriendList.get(position).userId)
                    .putExtra(MyConstants.OTHER_USER_IMAGE, chatFriendList.get(position).image)

            )
        }

    }

    override fun getItemCount(): Int {
        return chatFriendList.size;
    }

    class viewHolder(itemView: View) : ViewHolder(itemView) {
        var txtName = itemView.findViewById<TextView>(R.id.txtName)
        var txtLastMessage = itemView.findViewById<TextView>(R.id.txtLastMessage)
        var txtTime = itemView.findViewById<TextView>(R.id.txtTime)
        var imgUser = itemView.findViewById<CircleImageView>(R.id.imgUser)


    }
}