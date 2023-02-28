package com.example.diseasedetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var potato : LinearLayout = findViewById(R.id.potato)
        var tomato : LinearLayout = findViewById(R.id.tomato)
        var banana : LinearLayout  = findViewById(R.id.banana)
        var chilli : LinearLayout  = findViewById(R.id.chilli)
        var cotton : LinearLayout  = findViewById(R.id.cotton)

        var name : TextView = findViewById(R.id.name)
        var fAuth : FirebaseAuth = FirebaseAuth.getInstance()
        var fStore : FirebaseFirestore = FirebaseFirestore.getInstance()


        var userID : String = fAuth.currentUser?.uid.toString()
        fStore.collection("users").document(userID).get().addOnSuccessListener{
            if(it.exists())
            {
                name.setText(it.getString("fName"))
            }
        }
//        var tex:TextView = findViewById(R.id.uploadbartext)
        potato.setOnClickListener {
            startActivity(Intent(this,upload::class.java))
//            tex.text = "Tomato Leaf"
        }
//        banana.setOnClickListener {
//            startActivity(Intent(this,upload::class.java))
//        }
//        chilli.setOnClickListener {
//            startActivity(Intent(this,upload::class.java))
//        }
//        cotton.setOnClickListener {
//            startActivity(Intent(this,upload::class.java))
//        }
    }
}