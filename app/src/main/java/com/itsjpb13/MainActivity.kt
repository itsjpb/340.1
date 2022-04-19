package com.itsjpb13

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buyButton = findViewById<Button>(R.id.buyButton)
        val sellButton = findViewById<Button>(R.id.sellButton)
        val tradeButton = findViewById<Button>(R.id.tradeButton)
        val giveButton = findViewById<Button>(R.id.giveButton)


            // i wanted to use the strings resource for the text in these, but it did not
            // recognize it. am i missing something?
        buyButton.setOnClickListener {
        Toast.makeText(this, "buy", Toast.LENGTH_LONG).show()
    }

        sellButton.setOnClickListener {
            Toast.makeText(this, "sell", Toast.LENGTH_LONG).show()
        }

        tradeButton.setOnClickListener {
            Toast.makeText(this, "trade", Toast.LENGTH_LONG).show()
        }

        giveButton.setOnClickListener {
            Toast.makeText(this, "give", Toast.LENGTH_LONG).show()
        }


    }
}