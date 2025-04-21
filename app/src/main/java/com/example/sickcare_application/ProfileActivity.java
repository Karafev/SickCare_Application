package com.example.sickcare_application;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewNom, textViewPrenom, textViewMaladies, textViewAliments;
    private Button btnRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewNom = findViewById(R.id.nom);
        textViewPrenom = findViewById(R.id.prenom);
        textViewMaladies = findViewById(R.id.maladies);
        textViewAliments = findViewById(R.id.aliments);
        btnRetour = findViewById(R.id.btn_retour);

        btnRetour.setOnClickListener(v -> onBackPressed());

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        Log.d("TOKEN", "Token récupéré : " + token);

        if (!token.isEmpty()) {
            getProfileDetails(token);
        } else {
            Toast.makeText(this, "Token manquant", Toast.LENGTH_LONG).show();
        }
    }

    private void getProfileDetails(String token) {
        String url = "http://10.0.2.2/SickCare/public/api/details";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject userData = json.getJSONObject("data").getJSONObject("user_data");

                        String nom = userData.optString("nom", "N/A");
                        String prenom = userData.optString("prenom", "N/A");

                        JSONArray maladiesArray = userData.optJSONArray("maladies");
                        StringBuilder maladiesText = new StringBuilder();
                        StringBuilder alimentsText = new StringBuilder();

                        if (maladiesArray != null) {
                            for (int i = 0; i < maladiesArray.length(); i++) {
                                JSONObject maladie = maladiesArray.getJSONObject(i);
                                String nomMaladie = maladie.optString("nom", "Inconnue");

                                if (i > 0) maladiesText.append("\n");
                                maladiesText.append(nomMaladie);

                                JSONArray alimentsArray = maladie.optJSONArray("aliments");
                                if (alimentsArray != null) {
                                    for (int j = 0; j < alimentsArray.length(); j++) {
                                        JSONObject aliment = alimentsArray.getJSONObject(j);
                                        String nomAliment = aliment.optString("nom_aliment", "Aliment inconnu");
                                        if (!alimentsText.toString().contains(nomAliment)) {
                                            if (alimentsText.length() > 0) alimentsText.append("\n");
                                            alimentsText.append(nomAliment);
                                        }
                                    }
                                }
                            }
                        }

                        runOnUiThread(() -> {
                            textViewNom.setText(nom);
                            textViewPrenom.setText(prenom);
                            textViewMaladies.setText(maladiesText.length() > 0 ? maladiesText.toString() : "Aucune maladie");
                            textViewAliments.setText(alimentsText.length() > 0 ? alimentsText.toString() : "Aucun aliment interdit");
                        });

                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Erreur JSON : " + e.getMessage());
                        showError("Erreur de traitement des données");
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.e("NETWORK_RESPONSE", new String(error.networkResponse.data));
                        Log.e("NETWORK_CODE", String.valueOf(error.networkResponse.statusCode));
                    }
                    showError("Erreur réseau ou autorisation : " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showError(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
}
