package com.harjeet.letschat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harjeet.letschat.Models.ChatFriendsModel
import com.harjeet.letschat.MyConstants
import com.harjeet.letschat.MyUtils
import com.harjeet.letschat.adapters.ChatListAdapter
import android.app.Activity
import harjeet.chitForChat.databinding.FragmentChatsBinding


/* Showing the friends list with last message */
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


    //getting chats from firebase database
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