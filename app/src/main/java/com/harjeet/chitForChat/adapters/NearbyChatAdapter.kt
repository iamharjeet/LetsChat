package com.harjeet.chitForChat.adapters

import android.content.Context
import android.content.Intent
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
import com.harjeet.chitForChat.Models.Users
import com.harjeet.chitForChat.MyConstants
import com.harjeet.chitForChat.MyUtils
import com.harjeet.chitForChat.R
import de.hdodenhof.circleimageview.CircleImageView

class NearbyChatAdapter(var context: Context, var chatNearbyList: ArrayList<Users>) :
    RecyclerView.Adapter<NearbyChatAdapter.viewHolder>() {

    var firebaseUsers =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_USERS)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NearbyChatAdapter.viewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_nearby, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: NearbyChatAdapter.viewHolder, position: Int) {
        holder.txtName.text=chatNearbyList.get(position).name
        if(!chatNearbyList.get(position).image.equals("")) {
            Glide.with(context).load(chatNearbyList.get(position).image).into(holder.imgUser)
        }

        holder.txtStatus.setText(chatNearbyList.get(position).captions)
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, ChatLiveActivity::class.java).putExtra(MyConstants.OTHER_USER_NAME,chatNearbyList.get(position).name)
                    .putExtra(MyConstants.OTHER_USER_PHONE,chatNearbyList.get(position).phone)
                    .putExtra(MyConstants.OTHER_USER_IMAGE,chatNearbyList.get(position).image)
            )
        }


    }

    override fun getItemCount(): Int {
        return chatNearbyList.size
    }

    class viewHolder(itemView: View) : ViewHolder(itemView) {
        var txtName = itemView.findViewById<TextView>(R.id.txtName)
        var txtStatus = itemView.findViewById<TextView>(R.id.txtStatus)
        var imgUser = itemView.findViewById<CircleImageView>(R.id.imgUser)


    }

}