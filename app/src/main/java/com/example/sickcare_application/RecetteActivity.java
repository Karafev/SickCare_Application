package com.example.sickcare_application;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecetteActivity extends AppCompatActivity {

    TextView txtNom, txtDescription, txtEtapes, txtAliments;
    Button btnRetour;
    ImageView imageRecette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recette);

        txtNom = findViewById(R.id.txtNom);
        txtDescription = findViewById(R.id.txtDescription);
        txtEtapes = findViewById(R.id.txtEtapes);
        txtAliments = findViewById(R.id.txtAliments);
        btnRetour = findViewById(R.id.btnRetour);
        imageRecette = findViewById(R.id.imageRecette);

        String nom = getIntent().getStringExtra("nom");
        String description = getIntent().getStringExtra("description");
        String etapes = getIntent().getStringExtra("etapes");
        String image = getIntent().getStringExtra("image");
        List<String> aliments = getIntent().getStringArrayListExtra("aliments");

        txtNom.setText(nom);
        txtDescription.setText(description);
        txtEtapes.setText(etapes);

        if (aliments != null && !aliments.isEmpty()) {
            txtAliments.setText(String.join(", ", aliments));
        } else {
            txtAliments.setText("Aucun aliment pour cette recette");
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = "http://10.0.2.2/~fevzican.karamercan/SickCare/public/" + image;

            // Affiche l'URL dans le log
            Log.d("RecetteActivity", "Image URL: " + imageUrl);

            // Charge l'image avec Picasso
            Picasso.get().load(imageUrl).into(imageRecette);
        }

        btnRetour.setOnClickListener(v -> finish());
    }

}
