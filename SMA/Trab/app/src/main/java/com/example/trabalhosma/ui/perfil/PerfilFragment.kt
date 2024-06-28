package com.example.trabalhosma.ui.perfil

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trabalhosma.R
import com.example.trabalhosma.databinding.FragmentPerfilBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class PerfilFragment : Fragment() {

    private lateinit var binding: FragmentPerfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("images/")

        loadUserData()

        binding.buttonGuardar.setOnClickListener {
            saveChanges()
        }
    }


    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = auth.currentUser?.uid ?: return
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("nome") ?: ""
                        val age = document.getLong("idade")?.toInt() ?: 0
                        val location = document.getString("localidade") ?: ""
                        val university = document.getString("universidade") ?: ""
                        val course = document.getString("curso") ?: ""

                        binding.editTextNome.setText(name)
                        binding.editTextIdade.setText(age.toString())
                        binding.editTextLocalidade.setText(location)
                        binding.editTextUniversidade.setText(university)
                        binding.editTextCurso.setText(course)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Erro ao carregar dados do utilizador",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar dados do utilizador: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }



    private fun saveChanges() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val name = binding.editTextNome.text.toString()
            val age = binding.editTextIdade.text.toString().toIntOrNull() ?: 0
            val location = binding.editTextLocalidade.text.toString()
            val university = binding.editTextUniversidade.text.toString()
            val course = binding.editTextCurso.text.toString()

            val dados = hashMapOf(
                "nome" to name,
                "idade" to age,
                "localidade" to location,
                "universidade" to university,
                "curso" to course
            )

            firestore.collection("users").document(userId)
                .update(dados as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireActivity(),
                        "Dados do utilizador atualizados com sucesso.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateInDrawer(requireActivity(), name, university, course)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Erro ao atualizar dados do utilizador: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


    private fun updateInDrawer(context: Context, name: String, university: String, course: String) {
        val navigationView = (context as Activity).findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val textViewName = headerView.findViewById<TextView>(R.id.textViewNome)
        val textViewUniversity = headerView.findViewById<TextView>(R.id.textViewUniversidade)
        val textViewCourse = headerView.findViewById<TextView>(R.id.textViewCurso)

        textViewName.text = name
        textViewUniversity.text = university
        textViewCourse.text = course

        Log.d("PerfilFragment", "Nome no Drawer atualizado para: $name")
    }
}