package com.harjeet.letschat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.harjeet.letschat.Models.ChatFriendsModel
import com.harjeet.letschat.Models.LiveChatModel
import com.harjeet.letschat.MyConstants.FIREBASE_BASE_URL
import com.harjeet.letschat.adapters.ChatLiveAdapter
import harjeet.chitForChat.R
import harjeet.chitForChat.databinding.ActivityChatLiveBinding
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

/*
Chatting screen Where two users can chat with each others
 */
class ChatLiveActivity : AppCompatActivity() {
    private var senderId: String = ""
    private var receiverId: String = ""
    var sentImage: Bitmap? = null
    var binding: ActivityChatLiveBinding? = null
    var roomId: String? = null
    var firebaseChats =
        FirebaseDatabase.getInstance(FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_CHATS)


    var firebaseChatFriends =
        FirebaseDatabase.getInstance(FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_CHAT_FIRENDS)
    var chatsList: ArrayList<LiveChatModel> = ArrayList()
    var firebaseOnlineStatus =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_ONLINE_STATUS)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_live)

        binding = ActivityChatLiveBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.txtName.setText(intent.getStringExtra(MyConstants.OTHER_USER_NAME))

        Glide.with(this@ChatLiveActivity)
            .load(intent.getStringExtra(MyConstants.OTHER_USER_IMAGE)).placeholder(R.drawable.user)
            .into(binding!!.imgUser)


        clicks()
        senderId = MyUtils.getStringValue(this@ChatLiveActivity, MyConstants.USER_PHONE)
        receiverId = intent.getStringExtra(MyConstants.OTHER_USER_PHONE).toString()
// Getting online status of other user
        getOnlineStatus(receiverId)
        if (senderId < receiverId) {
            roomId = senderId + receiverId
        } else {
            roomId = receiverId + senderId
        }
        binding!!.imgSend.setOnClickListener {
            if (binding!!.edtMessage.text.toString().equals("")) {
                MyUtils.showToast(this@ChatLiveActivity, "Can't Send Empty Message")
            } else {
                sendMessageOnFirebase(binding!!.edtMessage.text.toString(), "text")
            }
        }

// Handling typing status
        binding!!.edtMessage.addTextChangedListener(object : TextWatcher {
            var isTyping = false
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.rcChat.scrollToPosition(chatsList.size - 1)

            }

            var timer = Timer();
            var DELAY: Int = 2000;

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {


            }

            override fun afterTextChanged(s: Editable) {

                if (!isTyping) {
                    if (!MyUtils.getStringValue(this@ChatLiveActivity, MyConstants.USER_PHONE)
                            .equals("")
                    )
                        firebaseOnlineStatus.child(
                            MyUtils.getStringValue(
                                this@ChatLiveActivity,
                                MyConstants.USER_PHONE
                            )
                        ).child(MyConstants.NODE_ONLINE_STATUS).setValue("Typing...")

                    // Send notification for start typing event
                    isTyping = true
                }
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            isTyping = false
                            if (!MyUtils.getStringValue(
                                    this@ChatLiveActivity,
                                    MyConstants.USER_PHONE
                                ).equals("")
                            )
                                firebaseOnlineStatus.child(
                                    MyUtils.getStringValue(
                                        this@ChatLiveActivity,
                                        MyConstants.USER_PHONE
                                    )
                                ).child(MyConstants.NODE_ONLINE_STATUS).setValue("Online")
                            //send notification for stopped typing event
                        }
                    },
                    3000
                )
            }
        })


        binding!!.imgCamera.setOnClickListener {

            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()

        }
        getChatsFromFirebase()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            // Use Uri object instead of File to avoid storage permissions

            MyUtils.showProgress(this@ChatLiveActivity)
            sentImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        this@ChatLiveActivity.contentResolver,
                        uri
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(this@ChatLiveActivity.contentResolver, uri)
            }


            uploadImageOnFirebase(sentImage!!)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }



    // sending message to the firebase database and to other user
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessageOnFirebase(message: String?, messageType: String) {
        var message = message

        val key = firebaseChats.push().key
        val data: LiveChatModel =
            LiveChatModel(
                senderId,
                receiverId,
                CodeAndDecode.encrypt(message.toString(), roomId.toString())!!,
                messageType,
                key.toString(),
                Calendar.getInstance().time.time.toString(),
            );
        firebaseChats.child(roomId!!).child(key.toString()).setValue(data)
            .addOnCompleteListener {
                MyUtils.stopProgress(this@ChatLiveActivity)

                if(messageType.equals("image")){
                    message="Image"
                }
                firebaseChatFriends.child(senderId).child(receiverId).setValue(
                    ChatFriendsModel(
                        receiverId,
                        intent.getStringExtra(MyConstants.OTHER_USER_NAME).toString(),
                        intent.getStringExtra(MyConstants.OTHER_USER_IMAGE).toString(),
                        CodeAndDecode.encrypt(message.toString(), roomId.toString())!!,
                        Calendar.getInstance().time.time.toString()
                    )
                )
                firebaseChatFriends.child(receiverId).child(senderId).setValue(
                    ChatFriendsModel(
                        senderId,
                        MyUtils.getStringValue(
                            this@ChatLiveActivity,
                            MyConstants.USER_NAME
                        ),
                        intent.getStringExtra(MyConstants.OTHER_USER_IMAGE).toString(),
                        CodeAndDecode.encrypt(message.toString(), roomId.toString())!!,
                                Calendar.getInstance().time.time.toString()
                    )
                )
                binding!!.edtMessage.setText("")
            }

    }

// Uploading image to firebase and send to other user
    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImageOnFirebase(bitmap: Bitmap) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        val mountainImagesRef: StorageReference =
            storageRef.child("images/" + "harjeet" + Calendar.getInstance().time + ".jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data = baos.toByteArray()
        val uploadTask: UploadTask = mountainImagesRef.putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener {
            // Handle unsuccessful uploads
            MyUtils.stopProgress(this@ChatLiveActivity)
        })
            .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { it -> // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                val result: Task<Uri> = it.getStorage().getDownloadUrl()
                result.addOnSuccessListener { uri ->
                    val imageUri: String = uri.toString()
                    sendMessageOnFirebase(imageUri, "image")
                }
            })
    }

    private fun clicks() {
        binding!!.imgBack.setOnClickListener { finish() }
    }

    // getting online status of other user
    private fun getOnlineStatus(receiverId: String) {
        firebaseOnlineStatus.child(receiverId).child(MyConstants.NODE_ONLINE_STATUS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        var onlineStatus = snapshot.getValue(String::class.java)
                        binding!!.txtOnlineStatus.setText(onlineStatus)

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    // getting recent or live  chat from firebase and set on screen
    private fun getChatsFromFirebase() {
        firebaseChats.child(roomId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatsList.clear()
                    for (postSnapshot in snapshot.children) {
                        val chat: LiveChatModel? = postSnapshot.getValue(LiveChatModel::class.java)
                        chatsList.add(chat!!)
                        // here you can access to name property like university.name

                    }
                    MyConstants.DATE = ""
                    binding!!.rcChat.adapter =
                        ChatLiveAdapter(this@ChatLiveActivity, chatsList, roomId.toString())

                    binding!!.rcChat.setItemViewCacheSize(chatsList.size)
                    binding!!.rcChat.scrollToPosition(chatsList.size - 1)

                }
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!MyUtils.getStringValue(this@ChatLiveActivity, MyConstants.USER_PHONE).equals(""))
            firebaseOnlineStatus.child(
                MyUtils.getStringValue(
                    this@ChatLiveActivity,
                    MyConstants.USER_PHONE
                )
            ).child(MyConstants.NODE_ONLINE_STATUS).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        if (!MyUtils.getStringValue(this@ChatLiveActivity, MyConstants.USER_PHONE).equals(""))
            firebaseOnlineStatus.child(
                MyUtils.getStringValue(
                    this@ChatLiveActivity,
                    MyConstants.USER_PHONE
                )
            ).child(MyConstants.NODE_ONLINE_STATUS).setValue(MyUtils.convertIntoTime(Calendar.getInstance().timeInMillis.toString()))

    }
}