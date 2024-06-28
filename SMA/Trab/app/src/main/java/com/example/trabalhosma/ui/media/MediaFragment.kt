package com.example.trabalhosma.ui.media

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.trabalhosma.ui.Disciplina
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.trabalhosma.ui.LightColorScheme
import com.example.trabalhosma.ui.Shapes
import com.example.trabalhosma.ui.Typography

class MediaFragment : Fragment() {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private var isLoading by mutableStateOf(true)
    private var completedDisciplinas by mutableStateOf(emptyList<Disciplina>())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen()
            }
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            loadCompletedDisciplinas()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MediaScreen() {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            shapes = Shapes
        ) {
            var media by remember { mutableStateOf<Double?>(null) }

            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = "Nome",
                            modifier = Modifier.width(80.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Ano",
                            modifier = Modifier.width(80.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Créditos",
                            modifier = Modifier.width(80.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Nota",
                            modifier = Modifier.width(80.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Divider(thickness = 2.dp)


                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        items(completedDisciplinas) { disciplina ->
                            ItemDisciplinaMedia(disciplina)
                        }


                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { media = calculateMedia() },
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Calcular Média")
                            }
                        }
                        if (media != null) {
                            item {
                                Text(
                                    text = "Média: ${String.format("%.2f", media)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                        .wrapContentHeight()
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private suspend fun loadCompletedDisciplinas() {
        try {
            isLoading = true
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("disciplinas")
                .whereEqualTo("concluida", true)
                .get()
                .await()
            completedDisciplinas = snapshot.toObjects(Disciplina::class.java)
        } catch (e: Exception) {
            Log.e("MediaFragment", "Erro ao carregar disciplinas concluídas", e)
        } finally {
            isLoading = false
        }
    }

    private fun calculateMedia(): Double {
        if (completedDisciplinas.isEmpty()) return 0.0

        val somaPonderada = completedDisciplinas.sumOf { (it.creditos ?: 0) *(it.nota ?: 0) }
        val totalCredits = completedDisciplinas.sumOf { it.creditos ?: 0 }

        return if (totalCredits > 0) somaPonderada.toDouble() / totalCredits else 0.0
    }

    @Composable
    fun ItemDisciplinaMedia(disciplina: Disciplina) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = disciplina.sigla,
                modifier = Modifier.width(80.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${disciplina.ano}º",
                modifier = Modifier.width(80.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${disciplina.creditos}",
                modifier = Modifier.width(80.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = (disciplina.nota ?: 0).toString(),
                modifier = Modifier.width(80.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

