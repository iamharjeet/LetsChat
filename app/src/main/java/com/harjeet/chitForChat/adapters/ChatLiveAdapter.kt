package com.harjeet.chitForChat.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.harjeet.chitForChat.Models.LiveChatModel
import com.harjeet.chitForChat.MyConstants
import com.harjeet.chitForChat.MyUtils
import com.harjeet.chitForChat.R
import de.hdodenhof.circleimageview.CircleImageView

class ChatLiveAdapter(
    var context: Context,
    var chatsList: ArrayList<LiveChatModel>,
    var roomId: String
) :
    RecyclerView.Adapter<ChatLiveAdapter.viewHolder>() {
    var firebaseChats =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_CHATS)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        if (viewType == 1) {
            return viewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_chat_right, parent, false)!!
            )
        } else {
            return viewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_chat_left, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ChatLiveAdapter.viewHolder, position: Int) {

        if (chatsList.get(position).messageType.equals("text")) {
            holder.imgMessage.visibility = View.GONE
            holder.txtMessage.visibility = View.VISIBLE
            holder.txtMessage.text = chatsList[position].message
        } else if (chatsList.get(position).messageType.equals("image")) {
            holder.txtMessage.visibility = View.GONE
            holder.imgMessage.visibility = View.VISIBLE
            Glide.with(context)
                .load(chatsList.get(position).message)
                .into(holder.imgMessage)
        }

        if (!MyConstants.DATE.equals(MyUtils.convertIntoDate(chatsList.get(position).time.toString()))) {

            holder.txtDate.setText(MyUtils.convertIntoDate(chatsList.get(position).time.toString()))
            MyConstants.DATE = MyUtils.convertIntoDate(chatsList.get(position).time.toString())
        } else {
            holder.txtDate.visibility = View.GONE
        }
        holder.txtTime.setText(MyUtils.convertIntoTime((chatsList.get(position).time).toString()))



    }



    override fun getItemCount(): Int {
        return chatsList.size;
    }

    override fun getItemViewType(position: Int): Int {

        if (chatsList.get(position).sender.equals(
                MyUtils.getStringValue(
                    context,
                    MyConstants.USER_PHONE
                )
            )
        ) {
            return 1
        } else {
            return 2
        }
    }

    class viewHolder(itemView: View) : ViewHolder(itemView) {
        var txtMessage = itemView.findViewById<TextView>(R.id.txtMessage)
        var imgMessage = itemView.findViewById<ImageView>(R.id.imgMessage)
        var txtTime = itemView.findViewById<TextView>(R.id.txtTime)
        var txtDate = itemView.findViewById<TextView>(R.id.txtdate)

    }
}