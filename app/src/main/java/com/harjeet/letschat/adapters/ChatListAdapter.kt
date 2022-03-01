package com.harjeet.letschat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.harjeet.letschat.*
import com.harjeet.letschat.Models.ChatFriendsModel
import com.harjeet.letschat.MyUtils.showProfileDialog
import de.hdodenhof.circleimageview.CircleImageView
import harjeet.chitForChat.R


/*
* Handling users list
* */
class ChatListAdapter(var context: Context, var chatFriendList: ArrayList<ChatFriendsModel>) :
    RecyclerView.Adapter<ChatListAdapter.viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.viewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_chat, parent, false)
        return viewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: ChatListAdapter.viewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        var roomId = ""
        var senderId = MyUtils.getStringValue(context, MyConstants.USER_PHONE)
        var receiverId = chatFriendList.get(position).userId.toString()
        if (senderId < receiverId) {
            roomId = senderId + receiverId
        } else {
            roomId = receiverId + senderId
        }

        holder.txtName.setText(chatFriendList.get(position).name)
        holder.txtLastMessage.setText(
            CodeAndDecode.decrypt(
                chatFriendList.get(position).origonalMessage,
                roomId
            )
        )
        holder.txtTime.setText("")
        Glide.with(context).load(chatFriendList.get(position).image).placeholder(R.drawable.user)
            .into(holder.imgUser)

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
        holder.imgUser.setOnClickListener {
            showProfileDialog(context, chatFriendList.get(position).image.toString())
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