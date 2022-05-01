package com.itsjpb13;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Objects;

public class movieActivity extends AppCompatActivity {
    ListView rows;
    String[][] movies = movieList.Companion.getMovies();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Movies");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        rows = findViewById(R.id.listedMovies);


        MovieListAdapter myAdapter = new MovieListAdapter(this, movies);
        rows.setAdapter(myAdapter);


    }

    private void detail(int i){
        Intent myIntent = new Intent(this, movie_detall.class);
        Bundle bundle = new Bundle();
        String[] send = movies[i];
        bundle.putStringArray("data", send);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }


    public class MovieListAdapter extends ArrayAdapter<String[]> {
        private final Context context;
        private final String[][] values;

        public MovieListAdapter(Context context, String[][] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myCard = inflater.inflate(R.layout.list_row, parent, false);
            TextView movieTitle = myCard.findViewById(R.id.rowTitle);
            TextView movieYear = myCard.findViewById(R.id.rowYear);
            myCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                         detail(position);
                }
            });
            movieTitle.setText(values[position][0]);
            movieYear.setText(values[position][1]);

            return myCard;
        }
}
}
