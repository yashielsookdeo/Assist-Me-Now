package com.ctrlaltdefeat.assistmenow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var circleTLAnim: Animation
    private lateinit var circleBRAnim: Animation
    private lateinit var textAnim: Animation
    private lateinit var circleTL1: View
    private lateinit var circleTL2: View
    private lateinit var circleBR1: View
    private lateinit var circleBR2: View
    private lateinit var headingText1: TextView
    private lateinit var headingText2: TextView
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignUp: Button

    private fun initButtons() {
        buttonLogin = findViewById(R.id.b_login)
        buttonSignUp = findViewById(R.id.b_signup)

        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

        buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)

            startActivity(intent)
        }
    }

    private fun initAnimations() {
        circleTLAnim = AnimationUtils.loadAnimation(this, R.anim.main_circle_tl_anim)
        circleBRAnim = AnimationUtils.loadAnimation(this, R.anim.main_circle_br_anim)
        textAnim = AnimationUtils.loadAnimation(this, R.anim.main_text_anim)

        circleTL1 = findViewById(R.id.tl_circle_1)
        circleTL2 = findViewById(R.id.tl_circle_2)
        circleBR1 = findViewById(R.id.br_circle_1)
        circleBR2 = findViewById(R.id.br_circle_2)
        headingText1 = findViewById(R.id.h_text_1)
        headingText2 = findViewById(R.id.h_text_2)

        circleTL1.animation = circleTLAnim
        circleTL2.animation = circleTLAnim
        circleBR1.animation = circleBRAnim
        circleBR2.animation = circleBRAnim
        headingText1.animation = textAnim
        headingText2.animation = textAnim
        buttonLogin.animation = textAnim
        buttonSignUp.animation = textAnim
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initButtons()
        initAnimations()
    }
}