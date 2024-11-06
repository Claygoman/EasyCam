package com.clayman.cameraapphabr.presentation.network_settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clayman.cameraapphabr.databinding.ChooseIpAddressBinding
import com.clayman.cameraapphabr.presentation.home.MainActivity

class NetworkSettingsActivity : AppCompatActivity() {

    private lateinit var binding : ChooseIpAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChooseIpAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btConfirmChanges.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}