package com.owlsoft.turntoroll.androidApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.owlsoft.turntoroll.androidApp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.encounter_fragment) {

    private lateinit var binding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }
}
