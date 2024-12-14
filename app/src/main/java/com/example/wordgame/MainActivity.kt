package com.example.wordgame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var textInputEditText1: TextInputEditText
    private lateinit var textInputEditText2: TextInputEditText
    private lateinit var textInputEditText3: TextInputEditText
    private lateinit var scoreTextView: TextView
    private lateinit var wordSet: Set<String>
    private var score: Int = 0
    private var focusedEditText: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize UI elements
        textInputEditText1 = findViewById(R.id.textInputEditText1)
        textInputEditText2 = findViewById(R.id.textInputEditText2)
        textInputEditText3 = findViewById(R.id.textInputEditText3)
        scoreTextView = findViewById(R.id.textViewScore)

        // set focus change listeners for EditTexts
        setFocusChangeListeners()

        // load words from the dictionary
        //wordSet = loadWordsFromAssets()

        val wordUrl = "https://www.eecis.udel.edu/~lliao/cis320f05/dictionary.txt" // Replace with actual URL
        loadWordsFromUrl(wordUrl)

        // Randomize letters for buttons
        randomizeButtonLetters()

        // Submit button logic
        findViewById<Button>(R.id.submit).setOnClickListener {
            validateWords()
        }
    }

    private fun setFocusChangeListeners() {
        val focusListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && v is TextInputEditText) {
                focusedEditText = v
            }
        }

        textInputEditText1.onFocusChangeListener = focusListener
        textInputEditText2.onFocusChangeListener = focusListener
        textInputEditText3.onFocusChangeListener = focusListener
    }

    private fun loadWordsFromUrl(url: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showToast("Failed to fetch words!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val wordList = response.body?.string()
                    if (!wordList.isNullOrEmpty()) {
                        wordSet = wordList.lines()
                            .map { it.trim().lowercase() }
                            .filter { it.isNotEmpty() }
                            .toSet()
                        runOnUiThread {
                            showToast("Words loaded successfully!")
                        }
                    }
                } else {
                    runOnUiThread {
                        showToast("Error fetching words!")
                    }
                }
            }
        })
    }


    Ro

    private fun validateWords() {
        val word1 = textInputEditText1.text.toString().trim().lowercase()
        val word2 = textInputEditText2.text.toString().trim().lowercase()
        val word3 = textInputEditText3.text.toString().trim().lowercase()

        var validCount = 0

        if (wordSet.contains(word1)) validCount++
        if (wordSet.contains(word2)) validCount++
        if (wordSet.contains(word3)) validCount++

        score += validCount
        scoreTextView.text = "Score: $score"

        if (validCount > 0) {
            showToast("Correct! $validCount word(s) valid.")
        } else {
            showToast("No valid words. Try again!")
        }

        // Clear inputs for the next round
        textInputEditText1.text?.clear()
        textInputEditText2.text?.clear()
        textInputEditText3.text?.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}