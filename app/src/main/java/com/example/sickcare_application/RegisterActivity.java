package com.example.sickcare_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rq = Volley.newRequestQueue(this);
    }

    public void faireEnregistrer(View v) {
        EditText champEmail = findViewById(R.id.champEmail);
        EditText champNom = findViewById(R.id.champNom);
        EditText champPrenom = findViewById(R.id.champPrenom);
        EditText champMaladie = findViewById(R.id.champMaladie);
        EditText champMotDePasse = findViewById(R.id.champMotDePasse);
        EditText champConfirmerMotDePasse = findViewById(R.id.champConfirmerMotDePasse);

        String email = champEmail.getText().toString().trim();
        String nom = champNom.getText().toString().trim();
        String prenom = champPrenom.getText().toString().trim();
        String maladie = champMaladie.getText().toString().trim();
        String mot_de_passe = champMotDePasse.getText().toString().trim();
        String confirmpassword = champConfirmerMotDePasse.getText().toString().trim();

        if (email.isEmpty() || prenom.isEmpty() || nom.isEmpty() || maladie.isEmpty() || mot_de_passe.isEmpty() || confirmpassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
            return;
        }

        String url = "http://10.0.2.2/SickCare/public/index.php/api/register";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                this::traiterReponseEnregistrement,
                this::gererErreurs
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("maladie", maladie);
                params.put("mot_de_passe", mot_de_passe);
                params.put("confirmpassword", confirmpassword);
                return params;
            }
        };

        rq.add(req);
    }

    public void traiterReponseEnregistrement(String reponse) {
        try {
            JSONObject jo = new JSONObject(reponse);
            if (!jo.getBoolean("error")) {
                Toast.makeText(this, "Enregistrement réussi", Toast.LENGTH_LONG).show();
                // Rediriger vers la page de connexion ou une autre page
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // Ferme l'activité actuelle
            } else {
                String message = jo.optString("messages", "Échec de l'enregistrement.");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Erreur lors du traitement de la réponse", Toast.LENGTH_LONG).show();
            Log.e("EnregistrementError", "Erreur de parsing JSON: " + e.getMessage());
        }
    }

    public void gererErreurs(Throwable t) {
        Toast.makeText(this, "Erreur de connexion au serveur", Toast.LENGTH_LONG).show();
        Log.e("VolleyError", "Erreur serveur ou réseau", t);
    }


}
