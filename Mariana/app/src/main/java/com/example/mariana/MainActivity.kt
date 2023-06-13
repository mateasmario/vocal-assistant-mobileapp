package com.example.mariana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val questionText = findViewById<TextInputEditText>(R.id.questionText)
        val askQuestion = findViewById<Button>(R.id.askQuestion)
        val answerText = findViewById<TextView>(R.id.answer)
        val exit = findViewById<Button>(R.id.exitButton)

        askQuestion.setOnClickListener {
            val enteredQuestion = questionText.text.toString()

            if (enteredQuestion == "What is your name") {
                readAnswerFromFirebase { answer ->
                    answerText.text = answer
                    sendQuestionAndAnswerToFirebase(enteredQuestion, answer)
                }
            } else {
                val answer = ""
                answerText.text = "I don't know"
                sendQuestionAndAnswerToFirebase(enteredQuestion, answer)
            }
        }
    }

    private fun questionName(): String {
        return "Hello, my name is Mariana"
    }

    private fun sendQuestionAndAnswerToFirebase(question: String, answer: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val questionRef = databaseReference.child("questions").push()

        questionRef.child("question").setValue(question)
        questionRef.child("answer").setValue(answer)
    }

    private fun readAnswerFromFirebase(callback: (String) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("questions")
        val query = databaseReference.orderByChild("question").equalTo("What is your name")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val answer = snapshot.child("answer").getValue(String::class.java)
                        if (answer != null) {
                            callback(answer)
                            return
                        }
                    }
                }

                // If answer is not found or null, provide a default value
                callback("Unknown")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error case, if needed
                callback("Error")
            }
        })
    }




}