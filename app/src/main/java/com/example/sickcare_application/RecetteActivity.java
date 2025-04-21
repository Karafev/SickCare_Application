package com.example.sickcare_application;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecetteActivity extends AppCompatActivity {

    TextView txtNom, txtDescription, txtEtapes, txtAliments;
    Button btnRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recette);

        txtNom = findViewById(R.id.txtNom);
        txtDescription = findViewById(R.id.txtDescription);
        txtEtapes = findViewById(R.id.txtEtapes);
        txtAliments = findViewById(R.id.txtAliments);
        btnRetour = findViewById(R.id.btnRetour);

        String nom = getIntent().getStringExtra("nom");
        String description = getIntent().getStringExtra("description");
        String etapes = getIntent().getStringExtra("etapes");
        List<String> aliments = getIntent().getStringArrayListExtra("aliments");

        txtNom.setText(nom);
        txtDescription.setText(description);
        txtEtapes.setText(etapes);

        if (aliments != null && !aliments.isEmpty()) {
            txtAliments.setText(String.join(", ", aliments));
        } else {
            txtAliments.setText("Aucun aliment pour cette recette");
        }

        btnRetour.setOnClickListener(v -> finish());
    }
}
