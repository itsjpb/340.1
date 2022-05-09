package com.itsjpb13

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val textView = toolbar.setTitle("Jason's App")



        val buyButton = findViewById<Button>(R.id.buyButton)
        val sellButton = findViewById<Button>(R.id.sellButton)
        val tradeButton = findViewById<Button>(R.id.tradeButton)
        val giveButton = findViewById<Button>(R.id.giveButton)


        buyButton.setOnClickListener {
            val intent = Intent(this, movieActivity::class.java)
            startActivity(intent)
    }

        sellButton.setOnClickListener {
            val intent = Intent(this, Cam_Landing_Activity::class.java)
            startActivity(intent)
        }

        tradeButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        giveButton.setOnClickListener {
            Toast.makeText(this, "albums", Toast.LENGTH_LONG).show()
        }


    }
}