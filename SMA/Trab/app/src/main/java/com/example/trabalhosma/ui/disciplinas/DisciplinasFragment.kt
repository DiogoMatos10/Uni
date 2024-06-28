package com.example.trabalhosma.ui.disciplinas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.lifecycleScope
import com.example.trabalhosma.ui.Disciplina
import kotlinx.coroutines.CoroutineScope


class DisciplinasFragment : Fragment() {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private var isLoading by mutableStateOf(true)
    private var disciplinas by mutableStateOf(mutableStateListOf<Disciplina>())


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            reloadDisciplinas(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DisciplinasScreen()
            }
        }
    }

    fun onDeleteClick(disciplina: Disciplina, coroutineScope: CoroutineScope) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        coroutineScope.launch {
            try {
                val document = firestore.collection("users")
                    .document(userId)
                    .collection("disciplinas")
                    .whereEqualTo("nome", disciplina.nome)
                    .whereEqualTo("ano", disciplina.ano)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()

                document?.reference?.delete()?.await()

                reloadDisciplinas(coroutineScope)
            } catch (e: Exception) {
                Log.e("DisciplinasFragment", "Erro ao excluir disciplina", e)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DisciplinasScreen() {

        val searchText by remember { mutableStateOf("") }
        var filterYear by remember { mutableStateOf<Int?>(null) }
        var filterState by remember { mutableStateOf("Todas") }
        val filterOptions = listOf("Todas", "Concluídas", "Não Concluídas")
        var expanded by remember { mutableStateOf(false) }
        var expandedYear by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            reloadDisciplinas(coroutineScope)
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val intent = Intent(context, AddDisciplina::class.java)
                    startActivityForResult(intent, 1)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Adicionar Disciplina")
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Row(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = expandedYear,
                        onExpandedChange = { newValue -> expandedYear = newValue }
                    ) {
                        OutlinedTextField(
                            value = if (filterYear == null) "Todos" else filterYear.toString(),onValueChange = {},
                            readOnly = true,
                            label = { Text("Ano") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear) },
                            modifier = Modifier.menuAnchor().width(125.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedYear,
                            onDismissRequest = { expandedYear = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos") },
                                onClick = {
                                    filterYear = null
                                    expandedYear = false
                                }
                            )
                            (1..3).forEach { ano ->
                                DropdownMenuItem(
                                    text = { Text(ano.toString()) },
                                    onClick = {
                                        filterYear = ano
                                        expandedYear = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { newValue -> expanded = newValue }
                    ) {
                        OutlinedTextField(
                            value = filterState,
                            onValueChange = {},
                            label = { Text("Conclusão") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            filterOptions.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao) },
                                    onClick = {
                                        filterState = opcao
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    LoadingScreen()
                } else {
                    LazyColumn {
                        items(disciplinas.filter { disciplina ->
                            (searchText.isBlank() || disciplina.nome.contains(searchText, ignoreCase = true)) &&
                                    (filterYear == null || disciplina.ano == filterYear) &&
                                    (filterState == "Todas" ||
                                            (filterState == "Concluídas" && disciplina.concluida) ||
                                            (filterState == "Não Concluídas" && !disciplina.concluida))
                        }) { disciplina ->
                            ItemDisciplina(disciplina = disciplina,
                                onDeleteClick = { onDeleteClick(it,coroutineScope) },
                                onEditClick = {onEditClick(it)})
                        }
                    }
                }
            }
        }
    }

    private fun onEditClick(disciplina: Disciplina) {
        val intent = Intent(context, AddDisciplina::class.java).apply {
            putExtra("option", "EDITAR")
            putExtra("nome", disciplina.nome)
            putExtra("sigla", disciplina.sigla)
            putExtra("ano", disciplina.ano)
            putExtra("concluida", disciplina.concluida)
            putExtra("nota", disciplina.nota)
            putExtra("creditos",disciplina.creditos)
        }
        startActivityForResult(intent, 1)
    }

    fun reloadDisciplinas(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            isLoading = true
            loadDisciplinas()
            isLoading = false
        }
    }


    @Composable
    fun LoadingScreen() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }


    @Composable
    fun ItemDisciplina(disciplina: Disciplina, onDeleteClick: (Disciplina) -> Unit, onEditClick: (Disciplina) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = disciplina.sigla, style = MaterialTheme.typography.headlineMedium)
                    Text(text = "${disciplina.ano} º Ano", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (disciplina.concluida) "Concluída" else "Não Concluída",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (disciplina.concluida && disciplina.nota != null) {
                        Text(
                            text = "Nota: ${disciplina.nota}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Row {
                    IconButton(onClick = { onEditClick(disciplina) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onDeleteClick(disciplina) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Excluir")
                    }
                }
            }
        }
    }


    private suspend fun loadDisciplinas() {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("disciplinas")
                .get()
                .await()
            disciplinas = snapshot.toObjects(Disciplina::class.java).toMutableStateList()
        } catch (e: Exception) {
            Log.e("DisciplinasFragment", "Erro ao carregar disciplinas", e)
        }
    }
}



