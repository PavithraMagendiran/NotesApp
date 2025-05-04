package com.example.notesapp



import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAnimeBinding


class AnimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start animation
        binding.splashImage.animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.top_anim)
        binding.splashImagetext.animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        // Delay 5 seconds then go to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000) // 5000 milliseconds = 5 seconds
    }
}