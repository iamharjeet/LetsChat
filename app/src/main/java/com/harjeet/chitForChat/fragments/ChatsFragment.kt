package com.harjeet.chitForChat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harjeet.chitForChat.Models.ChatFriendsModel
import com.harjeet.chitForChat.Models.LiveChatModel
import com.harjeet.chitForChat.MyConstants
import com.harjeet.chitForChat.MyUtils
import com.harjeet.chitForChat.adapters.ChatListAdapter
import com.harjeet.chitForChat.adapters.ChatLiveAdapter
import com.harjeet.chitForChat.databinding.FragmentChatsBinding
import android.app.Activity

class ChatsFragment : Fragment() {
    var firebaseChatFriends =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_CHAT_FIRENDS)

    lateinit var binding: FragmentChatsBinding;
    var chatFriendList: ArrayList<ChatFriendsModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getChatsFromFirebase()
    }

    private fun getChatsFromFirebase() {
        firebaseChatFriends.child(MyUtils.getStringValue(requireActivity(), MyConstants.USER_PHONE))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        chatFriendList.clear()
                        for (postSnapshot in snapshot.children) {
                            val user: ChatFriendsModel? =
                                postSnapshot.getValue(ChatFriendsModel::class.java)
                            chatFriendList.add(user!!)
                            // here you can access to name property like university.name
                        }

                        val activity: Activity? = activity
                        if (activity != null) {
                            binding!!.recyclerChatList.adapter =
                                ChatListAdapter(requireActivity(), chatFriendList!!)
                            // etc ...
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

}