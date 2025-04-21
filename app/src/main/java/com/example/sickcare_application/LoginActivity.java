package com.example.sickcare_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

public class LoginActivity extends AppCompatActivity {
    private RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rq = Volley.newRequestQueue(this);
    }

    public void doLogIn(View v) {
        EditText etLogin = findViewById(R.id.editTextEmail);
        EditText etPass = findViewById(R.id.editTextPassword);
        String mail = etLogin.getText().toString().trim();
        String pw = etPass.getText().toString().trim();

        if (mail.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
            return;
        }


        String url = "http://10.0.2.2/~fevzican.karamercan/SickCare/public/index.php/api/login";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                this::processLoginRequest,
                this::handleErrors
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hm = new HashMap<>();
                hm.put("email", mail);
                hm.put("mot_de_passe", pw); // ✅ Correct field name
                return hm;
            }
        };

        rq.add(req);
    }

    public void processLoginRequest(String response) {
        try {
            JSONObject jo = new JSONObject(response);
            if (!jo.getBoolean("error")) {
                JSONObject joData = jo.getJSONObject("data");
                String token = joData.getString("token");

                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("token", token);
                startActivity(i);
                finish(); // Optional: prevent going back to login
            } else {
                String message = jo.optString("messages", "Échec de la connexion.");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Erreur lors du traitement de la réponse", Toast.LENGTH_LONG).show();
            Log.e("LoginError", "JSON parse error: " + e.getMessage());
            Log.e("LoginError", "Response: " + response);
        }
    }

    public void handleErrors(Throwable t) {
        Toast.makeText(this, "Erreur de connexion au serveur", Toast.LENGTH_LONG).show();
        Log.e("VolleyError", "Erreur serveur ou réseau", t);
    }

    public void ouvrirInscription(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
