package com.example.trabalhosma.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.trabalhosma.R
import com.example.trabalhosma.ui.Disciplina
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import androidx.compose.ui.Alignment

class HomeFragment : Fragment() {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private var isLoading by mutableStateOf(true)
    private var userName by mutableStateOf("Utilizador")
    private var completedDisciplinas by mutableStateOf(emptyList<Disciplina>())
    private var uncompletedDisciplinas by mutableStateOf(emptyList<Disciplina>())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HomeScreen(completedDisciplinas, uncompletedDisciplinas)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            loadUserData()
            loadDisciplinas()
        }
    }

    private fun loadUserData() {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("nome") ?: "Utilizador"
                        userName = name
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeFragment", "Erro ao carregar nome do utilizador", exception)
                }
        }
    }

    private suspend fun loadDisciplinas() {
        try {
            isLoading = true
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val completedSnapshot = firestore.collection("users")
                .document(userId)
                .collection("disciplinas")
                .whereEqualTo("concluida", true)
                .get()
                .await()
            completedDisciplinas = completedSnapshot.toObjects(Disciplina::class.java)

            val uncompletedSnapshot = firestore.collection("users")
                .document(userId)
                .collection("disciplinas")
                .whereEqualTo("concluida", false)
                .get()
                .await()
            uncompletedDisciplinas = uncompletedSnapshot.toObjects(Disciplina::class.java)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Erro ao carregar disciplinas", e)
        } finally {
            isLoading = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HomeScreen(
        completedDisciplinas: List<Disciplina>,
        uncompletedDisciplinas: List<Disciplina>
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.university_hat_icon),
                        contentDescription = "Ícone da Aplicação",
                        modifier = Modifier
                            .width(256.dp)
                            .height(256.dp)
                            .padding(bottom = 16.dp)
                    )
                }
            }
            item { Greeting() }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Progresso do Curso",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item { ProgressSummary(completedDisciplinas, uncompletedDisciplinas) }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Criado por: Henrique Rosa & Diogo Matos",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Greeting() {
        val currentHour = LocalTime.now().hour
        val greeting = when (currentHour) {
            in 0..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
        Text(
            text = "$greeting, $userName!",
            style = MaterialTheme.typography.headlineMedium
        )
    }

    @Composable
    fun ProgressSummary(
        completedDisciplinas: List<Disciplina>,
        uncompletedDisciplinas: List<Disciplina>
    ) {
        val totalCompletedCredits = completedDisciplinas.sumOf { it.creditos ?: 0 }
        val totalCreditsRequired = 180
        val progress = (totalCompletedCredits.toFloat() / totalCreditsRequired) * 100
        val totalDisciplinas = completedDisciplinas + uncompletedDisciplinas

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Disciplinas Concluídas: ${completedDisciplinas.size}/${totalDisciplinas.size}")
                Text("Disciplinas Não Concluídas: ${uncompletedDisciplinas.size}/${totalDisciplinas.size}")
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress / 100,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = Color.Blue,
                    trackColor = Color.LightGray
                )
                Text(
                    text = "Progresso: ${String.format("%.1f", progress)}%",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}