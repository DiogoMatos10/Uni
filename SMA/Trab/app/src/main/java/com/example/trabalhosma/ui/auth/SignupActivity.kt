package com.example.trabalhosma.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.trabalhosma.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.buttonSignin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (validate(email, password, confirmPassword)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, LoginActivity::class.java)
                            val user = auth.currentUser
                            if (user != null) {
                                val userId = user.uid
                                val userDocRef = firestore.collection("users").document(userId)

                                val initialUserData = hashMapOf(
                                    "uid" to userId
                                )

                                userDocRef.set(initialUserData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "SigninActivity",
                                            "Documento do utilizador criado com sucesso com ID: $userId"
                                        )
                                        Toast.makeText(
                                            baseContext,
                                            "Utilizador registado com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(
                                            "SigninActivity",
                                            "Erro ao criar documento no Firestore: ${exception.message}"
                                        )
                                        Toast.makeText(
                                            baseContext,
                                            "Erro ao criar documento no Firestore: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }


                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    baseContext,
                                    "O endereço de email já está a ser usado por outra conta.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (exception != null) {
                                Toast.makeText(
                                    baseContext,
                                    exception.localizedMessage ?: "Erro desconhecido.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Registo falhou. Tente novamente.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }
    }


    private fun validate(email: String, password: String, confirmPassword: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.error = "Formato de email inválido"
            return false
        }

        if (password.length < 6) {
            binding.editTextPassword.error = "A password deve ter pelo menos 6 caracteres"
            return false
        }

        if (password != confirmPassword) {
            binding.editTextConfirmPassword.error = "As passwords não coincidem"
            return false
        }
        return true
    }
}