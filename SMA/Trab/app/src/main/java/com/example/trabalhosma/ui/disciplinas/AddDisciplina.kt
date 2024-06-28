package com.example.trabalhosma.ui.disciplinas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.trabalhosma.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.UUID
import com.example.trabalhosma.ui.LightColorScheme
import com.example.trabalhosma.ui.Typography
import com.example.trabalhosma.ui.Shapes
import java.io.File

class AddDisciplina : ComponentActivity() {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private lateinit var composeView: ComposeView
    private var fileUri by mutableStateOf<Uri?>(null)
    private var isCompleted by mutableStateOf(false)
    val storageRef = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            FormAdd()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fileUri = data?.data
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FormAdd() {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            shapes = Shapes
        ) {
            val context = LocalContext.current

            var disciplinaId by remember { mutableStateOf<String?>(null) }

            var disciplinaName by remember { mutableStateOf("") }
            var siglaDisciplina by remember { mutableStateOf("") }

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            val years = listOf(1, 2, 3)
            var selectedYear by remember { mutableStateOf(years[0]) }
            var expanded by remember { mutableStateOf(false) }

            val grades = (10..20).toList()
            var selectedGrade by remember { mutableStateOf(grades[0]) }
            var expandedGrade by remember { mutableStateOf(false) }

            var isLoading by remember { mutableStateOf(true) }
            var files by remember { mutableStateOf<List<String>>(emptyList()) }

            val availableCredits = listOf(3, 6, 9, 12)
            var selectedCredits by remember { mutableStateOf(availableCredits[0]) }
            var expandedCredits by remember { mutableStateOf(false) }

            var name by remember { mutableStateOf("") }
            var sigla by remember { mutableStateOf("") }
            var year by remember { mutableStateOf(0) }
            var credits by remember { mutableStateOf(0) }

            val option = intent.getStringExtra("option")
            if (option == "EDITAR") {
                name = intent.getStringExtra("nome") ?: ""
                sigla = intent.getStringExtra("sigla") ?: ""
                year = intent.getIntExtra("ano", 0)
                val completed = intent.getBooleanExtra("concluida", false)
                val grade = intent.getIntExtra("nota", 0)
                credits = intent.getIntExtra("creditos", 0)

                disciplinaName = name
                siglaDisciplina = sigla
                selectedYear = year
                this@AddDisciplina.isCompleted = completed
                selectedGrade = if (completed) grade else grades[0]

                LaunchedEffect(sigla) {
                    fetchDisciplinaId(sigla) { fetchedId ->
                        disciplinaId = fetchedId
                    }
                }

                LaunchedEffect(disciplinaId) {
                    if (disciplinaId != null) {
                        loadFileList(disciplinaId!!) { loadedFiles ->
                            files = loadedFiles
                            isLoading = false
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator()
                } else if (disciplinaId != null) {
                    DisplayFiles(disciplinaId!!, files)
                }
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Adicionar/Editar Disciplina") },
                        colors = TopAppBarDefaults.smallTopAppBarColors(),
                        navigationIcon = {
                            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Voltar"
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    OutlinedTextField(
                        value = disciplinaName,
                        onValueChange = { disciplinaName = it },
                        label = { Text("Nome da Disciplina") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = siglaDisciplina,
                        onValueChange = { siglaDisciplina = it },
                        label = { Text("Sigla da Disciplina") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedCredits,
                        onExpandedChange = { expandedCredits = !expandedCredits }
                    ) {
                        TextField(
                            value = selectedCredits.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Créditos da Disciplina") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCredits) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCredits,
                            onDismissRequest = { expandedCredits = false }
                        ) {
                            availableCredits.forEach { credits ->
                                DropdownMenuItem(
                                    text = { Text(credits.toString()) },
                                    onClick = {
                                        selectedCredits = credits
                                        expandedCredits = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedYear.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ano da Disciplina") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year.toString()) },
                                    onClick = {
                                        selectedYear = year
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { this@AddDisciplina.isCompleted = it }
                        )
                        Text("Concluída")
                    }
                    if (isCompleted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedGrade,
                            onExpandedChange = { expandedGrade = !expandedGrade }
                        ) {
                            TextField(
                                value = selectedGrade.toString(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Nota") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrade) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedGrade,
                                onDismissRequest = { expandedGrade = false }
                            ) {
                                grades.forEach { grade ->
                                    DropdownMenuItem(
                                        text = { Text(grade.toString()) },
                                        onClick = {
                                            selectedGrade = grade
                                            expandedGrade = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "*/*"
                            }
                            startActivityForResult(intent, 1)
                        },
                        modifier = Modifier.wrapContentHeight(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Anexar Ficheiro")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else if (disciplinaId != null) {
                        DisplayFiles(disciplinaId!!, files)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (userId != null) {
                                if (disciplinaName.isBlank() || siglaDisciplina.isBlank()) {
                                    Toast.makeText(
                                        baseContext, "Nome e Sigla são obrigatórios.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                if (isCompleted && selectedGrade == 0) {
                                    Toast.makeText(
                                        baseContext,
                                        "Nota é obrigatória para disciplinas concluidas.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                val disciplinaData = hashMapOf(
                                    "nome" to disciplinaName,
                                    "sigla" to siglaDisciplina,
                                    "ano" to selectedYear,
                                    "concluida" to isCompleted,
                                    "nota" to if (isCompleted) selectedGrade else null,
                                    "creditos" to selectedCredits
                                )


                                if (option.equals("EDITAR")) {
                                    firestore.collection("users")
                                        .document(userId)
                                        .collection("disciplinas")
                                        .whereEqualTo("sigla", sigla)
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            if (!querySnapshot.isEmpty) {
                                                val documentId = querySnapshot.documents[0].id
                                                val siglaOriginal =
                                                    querySnapshot.documents[0].getString("sigla")
                                                        ?: ""

                                                if (siglaDisciplina != siglaOriginal) {
                                                    firestore.collection("users")
                                                        .document(userId)
                                                        .collection("disciplinas")
                                                        .whereEqualTo("sigla", siglaDisciplina)
                                                        .get()
                                                        .addOnSuccessListener { query ->
                                                            if (query.isEmpty) {
                                                                firestore.collection("users")
                                                                    .document(userId)
                                                                    .collection("disciplinas")
                                                                    .document(documentId)
                                                                    .update(disciplinaData as Map<String, Any>)
                                                                    .addOnSuccessListener {
                                                                        if (fileUri != null) {
                                                                            val fileName =
                                                                                UUID.randomUUID()
                                                                                    .toString()
                                                                            val fileRef =
                                                                                storageRef.child("users/$userId/disciplinas/$documentId/$fileName")
                                                                            val uploadTask =
                                                                                fileRef.putFile(
                                                                                    fileUri!!
                                                                                )
                                                                            uploadTask.addOnSuccessListener {
                                                                                Toast.makeText(
                                                                                    context,
                                                                                    "Ficheiro enviado com sucesso!",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                                setResult(RESULT_OK)
                                                                            }
                                                                                .addOnFailureListener { exception ->
                                                                                    Log.e(
                                                                                        "AddDisciplina",
                                                                                        "Erro ao enviar ficheiro: ${exception.message}"
                                                                                    )
                                                                                    Toast.makeText(
                                                                                        context,
                                                                                        "Erro ao enviar ficheiro: ${exception.message}",
                                                                                        Toast.LENGTH_SHORT
                                                                                    ).show()
                                                                                }
                                                                        }
                                                                        setResult(RESULT_OK)
                                                                        finish()
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Disciplina Atualizada com sucesso.",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                    .addOnFailureListener { e ->
                                                                        Log.e(
                                                                            "AddDisciplina",
                                                                            "Erro ao atualizar: ${e.message}"
                                                                        )
                                                                        Toast.makeText(
                                                                            baseContext,
                                                                            "Erro ao salvar.",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Já existe uma disciplina com essa sigla.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                Log.d(
                                                                    "AddDisciplina",
                                                                    "Sigla duplicada, não salvando."
                                                                )
                                                            }
                                                        }
                                                } else {
                                                    firestore.collection("users")
                                                        .document(userId)
                                                        .collection("disciplinas")
                                                        .document(documentId)
                                                        .update(disciplinaData as Map<String, Any>)
                                                        .addOnSuccessListener {
                                                            if (fileUri != null) {
                                                                val fileName =
                                                                    UUID.randomUUID().toString()
                                                                val fileRef =
                                                                    storageRef.child("users/$userId/disciplinas/$documentId/$fileName")
                                                                val uploadTask =
                                                                    fileRef.putFile(fileUri!!)
                                                                uploadTask.addOnSuccessListener {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Ficheiro enviado com sucesso!",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    setResult(RESULT_OK)
                                                                }
                                                                    .addOnFailureListener { exception ->
                                                                        Log.e(
                                                                            "AddDisciplina",
                                                                            "Erro ao enviar ficheiro: ${exception.message}"
                                                                        )
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Erro ao enviar ficheiro: ${exception.message}",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                            }
                                                            setResult(RESULT_OK)
                                                            finish()
                                                            Toast.makeText(
                                                                context,
                                                                "Disciplina Atualizada com sucesso.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            finish()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.e(
                                                                "AddDisciplina",
                                                                "Erro ao atualizar: ${e.message}"
                                                            )
                                                            Toast.makeText(
                                                                baseContext,
                                                                "Erro ao salvar.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            } else {
                                                Log.e("AddDisciplina", "Disciplina não encontrada.")
                                                Toast.makeText(
                                                    baseContext,
                                                    "Disciplina não encontrada.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                finish()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("AddDisciplina", "Erro na consulta: ${e.message}")
                                            Toast.makeText(
                                                baseContext,
                                                "Erro ao buscar a disciplina.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                } else {
                                    firestore.collection("users")
                                        .document(userId)
                                        .collection("disciplinas")
                                        .whereEqualTo("sigla", siglaDisciplina)
                                        .get()
                                        .addOnSuccessListener { checkDuplicated ->
                                            if (checkDuplicated.isEmpty) {
                                                firestore.collection("users")
                                                    .document(userId)
                                                    .collection("disciplinas")
                                                    .add(disciplinaData)
                                                    .addOnSuccessListener { documentReference ->
                                                        val disciplinaId = documentReference.id
                                                        if (fileUri != null) {
                                                            val fileName =
                                                                UUID.randomUUID().toString()
                                                            val fileRef =
                                                                storageRef.child("users/$userId/disciplinas/$disciplinaId/$fileName")
                                                            val uploadTask =
                                                                fileRef.putFile(fileUri!!)
                                                            uploadTask.addOnSuccessListener {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Ficheiro enviado com sucesso!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                setResult(RESULT_OK)

                                                            }.addOnFailureListener { exception ->
                                                                Log.e(
                                                                    "AddDisciplina",
                                                                    "Erro ao enviar ficheiro: ${exception.message}"
                                                                )
                                                                Toast.makeText(
                                                                    context,
                                                                    "Erro ao enviar ficheiro: ${exception.message}",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                        finish()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.e(
                                                            "AddDisciplina",
                                                            "Erro ao adicionar: ${e.message}"
                                                        )
                                                        Toast.makeText(
                                                            baseContext,
                                                            "Erro ao salvar.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Já existe uma disciplina com essa sigla.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.d(
                                                    "AddDisciplina",
                                                    "Sigla duplicada, não salva."
                                                )
                                            }

                                        }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Salvar Disciplina")
                    }
                }
            }
        }
    }

    @Composable
    fun DisplayFiles(disciplinaId: String, files: List<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val context = LocalContext.current
        val filesState = remember { mutableStateOf<List<String>>(emptyList()) }

        LaunchedEffect(disciplinaId) {
            loadFileList(disciplinaId) { files ->
                filesState.value = files
            }
        }


        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Ficheiros Anexados",
            color = Color.Black,
            fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(4.dp)){
                LazyColumn(modifier = Modifier
                    .fillMaxHeight()
                    .height(200.dp)
                    .background(LightColorScheme.secondary)
                    .padding(16.dp)
                ) {
                    items(filesState.value) { file ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = file,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = {
                                        if (userId != null) {
                                            downloadFile(context, userId, disciplinaId, file)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Download", color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (userId != null) {
                                            deleteFile(context, userId, disciplinaId, file, filesState)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Delete", color = Color.White)
                                }
                            }
                            Spacer(modifier= Modifier.height(16.dp))
                        }
                    }
                }
            }
            }

    }

    private fun deleteFile(context: Context, userId: String, disciplinaId: String, fileName: String, filesState: MutableState<List<String>>) {
        val fileRef = storageRef.child("users/${userId}/disciplinas/${disciplinaId}/${fileName}")

        fileRef.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Ficheiro excluído com sucesso.", Toast.LENGTH_SHORT).show()

                loadFileList(disciplinaId) { files ->
                    filesState.value = files
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao excluir o ficheiro: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("DeleteFile", "Erro ao excluir o ficheiro: ${e.message}")
            }
    }

    private fun downloadFile(context: Context, userId: String, disciplinaId: String, fileName: String) {
        val fileRef = storageRef.child("users/${userId}/disciplinas/${disciplinaId}/${fileName}")
        val localFile = File.createTempFile("temp", "")

        fileRef.getFile(localFile)
            .addOnSuccessListener {
                Log.d("DownloadFile", "Download concluído com sucesso!")
                Log.d("DownloadFile", "Caminho do ficheiro local: ${localFile.absolutePath}")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${applicationContext.packageName}.provider",
                    localFile
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    baseContext,
                    "Erro no download do ficheiro: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("DownloadFile", "Erro no download do ficheiro: ${e.message}")
            }
    }


    private fun loadFileList(disciplinaId: String, onFilesLoaded: (List<String>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef =
            Firebase.storage.reference.child("users/${userId}/disciplinas/${disciplinaId}")
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                onFilesLoaded(listResult.items.map { it.name })
                Log.d("AddDisciplina", "Ficheiros carregados: ${listResult.items.map { it.name }}")
                onFilesLoaded(listResult.items.map { it.name })
            }
            .addOnFailureListener {
                Log.e("AddDisciplina", "Erro no carregamento de ficheiros: ${it.message}")
            }
    }

    private fun fetchDisciplinaId(sigla: String, onIdFetched: (String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore
        db.collection("users")
            .document(userId)
            .collection("disciplinas")
            .whereEqualTo("sigla", sigla)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val disciplinaId = querySnapshot.documents[0].id
                    onIdFetched(disciplinaId)
                } else {
                    onIdFetched(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddDisciplina", "Erro a pesquisar ID da disciplina: ${exception.message}")
                onIdFetched(null)
            }
    }
}

