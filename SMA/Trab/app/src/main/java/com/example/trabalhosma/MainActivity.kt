package com.example.trabalhosma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhosma.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.trabalhosma.ui.disciplinas.DisciplinasFragment
import androidx.lifecycle.lifecycleScope
import com.example.trabalhosma.ui.auth.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_perfil, R.id.nav_disciplinas, R.id.nav_media
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    true
                }
                else -> {
                    lifecycleScope.launch {
                        navController.navigate(menuItem.itemId)
                        delay(75)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
            }
        }

        if (auth.currentUser != null) {
            loadNavHeaderData()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as? NavHostFragment
            val navController = navHostFragment?.navController

            val disciplinasFragment = navController?.currentDestination?.id?.let { destId ->
                navController.getBackStackEntry(destId).destination as? DisciplinasFragment
            }

            lifecycleScope.launch {
                disciplinasFragment?.reloadDisciplinas(this)
            }
        }
    }

    private fun loadNavHeaderData() {
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
                        val university =
                            document.getString("universidade") ?: "para adicionar informação "
                        val course = document.getString("curso") ?: "Selecione \"Perfil\""

                        updateNavHeader(name, university, course)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "MainActivity",
                        "Erro ao carregar dados do utilizador: ${exception.message}"
                    )
                }
        }
    }

    private fun updateNavHeader(name: String, university: String, course: String) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val textViewName = headerView.findViewById<TextView>(R.id.textViewNome)
        val textViewUniversity = headerView.findViewById<TextView>(R.id.textViewUniversidade)
        val textViewCourse = headerView.findViewById<TextView>(R.id.textViewCurso)

        textViewName.text = name
        textViewUniversity.text = university
        textViewCourse.text = course
    }

}