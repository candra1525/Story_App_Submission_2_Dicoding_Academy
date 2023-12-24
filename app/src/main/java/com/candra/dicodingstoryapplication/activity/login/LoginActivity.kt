package com.candra.dicodingstoryapplication.activity.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.main.MainActivity
import com.candra.dicodingstoryapplication.activity.register.RegisterActivity
import com.candra.dicodingstoryapplication.data.result.Result
import com.candra.dicodingstoryapplication.databinding.ActivityLoginBinding
import com.candra.dicodingstoryapplication.model.UserModel
import com.candra.dicodingstoryapplication.viewModelFactory.StoryViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        StoryViewModelFactory.getInstance(this@LoginActivity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        runBlocking {
            launch {
                delay(3000L)
                playAnimation()
            }
        }

        binding.edLoginEmail.requestFocus()

        binding.apply {
            btnLogin.setOnClickListener {
                val email = edLoginEmail.text?.trim().toString()
                val password = edLoginPassword.text?.trim().toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    setLogin(email, password)
                }
            }

            btnToRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun getAlpha(view: View, duration: Long): ObjectAnimator =
        ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(duration)

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoadingLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun buttonEnable(isEnable: Boolean) {
        binding.btnLogin.isEnabled = isEnable
    }

    private fun setLogin(email: String, password: String) {
        loginViewModel.getLogin(email, password).observe(this)
        { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        buttonEnable(false)
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val dataLogin = result.data
                        val token = dataLogin.loginResult?.token
                        val name = dataLogin.loginResult?.name.toString()

                        if (token != null) {
                            runBlocking {
                                loginViewModel.setSessionToken(
                                    UserModel(
                                        token = token,
                                        name = name,
                                        isUserLogin = true
                                    )
                                )
                            }
                            val sweetAlertDialog =
                                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getString(R.string.login_success))
                                    .setContentText(getString(R.string.login_success_text))
                                    .setConfirmButton("OK") {
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        buttonEnable(true)
                                        startActivity(intent)
                                    }
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.show()
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        val sweetAlertDialog =
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.login_fail))
                                .setContentText("${result.error.toString()}")
                                .setConfirmButton("OK") {
                                    it.dismissWithAnimation()
                                    buttonEnable(true)
                                }
                        sweetAlertDialog.show()
                    }

                    else -> {}
                }
            }
        }
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageLogin, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleLogin = getAlpha(binding.titleLogin, 1000)
        val descLogin = getAlpha(binding.descriptionLogin, 300)
        val loginEmailTextInput = getAlpha(binding.textInputLayoutEmail, 300)
        val loginEmail = getAlpha(binding.edLoginEmail, 300)
        val loginPasswordTextInput = getAlpha(binding.textInputLayoutPassword, 300)
        val loginPassword = getAlpha(binding.edLoginPassword, 300)
        val btnLogin = getAlpha(binding.btnLogin, 300)
        val noAccount = getAlpha(binding.tvNoAccount, 300)
        val noAccountToRegister = getAlpha(binding.tvNoAccountToRegister, 300)
        val btnToRegister = getAlpha(binding.btnToRegister, 300)


        AnimatorSet().apply {
            playSequentially(
                titleLogin,
                descLogin,
                loginEmailTextInput,
                loginEmail,
                loginPasswordTextInput,
                loginPassword,
                btnLogin,
                noAccount,
                noAccountToRegister,
                btnToRegister
            )
            start()
        }
    }
}