package com.example.playlistmaker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.example.playlistmaker.search.ui.SearchFragment

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (savedInstanceState == null) {
//            // Добавляем фрагмент в контейнер
//            supportFragmentManager.commit {
//                this.add(R.id.rootFragmentContainerView, SearchFragment())
//            }
//        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.detailsFragment, R.id.moviesCastFragment -> {
//                    binding.bottomNavigationView.visibility = View.GONE
//                }
//                else -> {
//                    binding.bottomNavigationView.visibility = View.VISIBLE
//                }
//            }
//        }

    }

}