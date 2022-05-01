package com.itsjpb13

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class movie_detall : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detall)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Movie Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imports = this.intent.extras
        val info = imports?.getStringArray("data")
        val title = findViewById<TextView>(R.id.movieTitle)
        val year = findViewById<TextView>(R.id.movieYear)
        val dir = findViewById<TextView>(R.id.movieDir)
        val desc = findViewById<TextView>(R.id.movieDesc)

        if (info != null) {
            title.text = info[0]
            year.text = info[1]
            dir.text = "Directed by: " + info[2]
            desc.text = info[4]
        }




    }
}