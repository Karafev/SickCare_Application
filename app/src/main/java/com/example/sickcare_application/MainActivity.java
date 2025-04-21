package com.example.sickcare_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer le token de l'intent
        String token = getIntent().getStringExtra("token");
        if (token != null && !token.isEmpty()) {
            // Sauvegarder le token dans SharedPreferences si nécessaire
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("auth_token", token);
            editor.apply();
        } else {
            Toast.makeText(this, "Token non valide.", Toast.LENGTH_LONG).show();
        }

        // Initialiser les boutons
        Button profileButton = findViewById(R.id.buttonProfile);
        Button rechercheButton = findViewById(R.id.buttonRecherche);
        Button logoutButton = findViewById(R.id.buttonLogout); // Ajouter le bouton de déconnexion

        // Ajouter des actions pour les boutons
        profileButton.setOnClickListener(v -> {
            // Lancer ProfileActivity
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });

        rechercheButton.setOnClickListener(v -> {
            // Lancer RechercheActivity
            Intent rechercheIntent = new Intent(MainActivity.this, RechercheActivity.class);
            startActivity(rechercheIntent);
        });

        logoutButton.setOnClickListener(v -> {
            // Supprimer le token des SharedPreferences (déconnexion)
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("auth_token"); // Supprimer le token
            editor.apply();

            // Afficher un message de déconnexion
            Toast.makeText(MainActivity.this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

            // Lancer l'écran de connexion ou un autre écran approprié
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class); // Remplace "LoginActivity" par le nom de ton activité de connexion
            startActivity(loginIntent);
            finish(); // Ferme l'activité actuelle pour éviter que l'utilisateur revienne en arrière
        });
    }
}
