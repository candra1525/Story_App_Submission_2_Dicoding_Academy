package com.candra.dicodingstoryapplication.activity.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.data.result.Result
import com.candra.dicodingstoryapplication.databinding.ActivityRegisterBinding
import com.candra.dicodingstoryapplication.viewModelFactory.StoryViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel> {
        StoryViewModelFactory.getInstance(this@RegisterActivity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnRegister.setOnClickListener {
                val name = edRegisterName.text?.trim().toString()
                val email = edRegisterEmail.text?.trim().toString()
                val password = edRegisterPassword.text?.trim().toString()
                val konfirmasiPassword = edRegisterKonfirmasiPassword.text?.trim().toString()
                setRegister(name, email, password, konfirmasiPassword)
            }
            tvBackToLogin.setOnClickListener {
                onBackPressed()
            }
        }

        runBlocking {
            launch {
                delay(2000L)
                playAnimation()
            }
        }
    }

    private fun buttonEnable(isEnable: Boolean) {
        binding.btnRegister.isEnabled = isEnable
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoadingRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getAlpha(view: View, duration: Long): ObjectAnimator =
        ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(duration)

    private fun checkPassword(password: String, konfirmasiPassword: String): Boolean {
        if (password.equals(konfirmasiPassword)) {
            return true
        }
        return false
    }

    private fun setRegister(
        name: String,
        email: String,
        password: String,
        konfirmasiPassword: String
    ) {
        if (name.isEmpty()) {
            popUpWarning(
                getString(R.string.name_empty),
                getString(R.string.name_empty_text),
                editText = binding.edRegisterName
            )
        } else if (email.isEmpty()) {
            popUpWarning(
                getString(R.string.email_empty),
                getString(R.string.email_empty_text),
                editText = binding.edRegisterEmail
            )
        } else if (password.isEmpty()) {
            popUpWarning(
                getString(R.string.password_empty),
                getString(R.string.password_empty_text),
                editText = binding.edRegisterPassword
            )
        } else if (konfirmasiPassword.isEmpty()) {
            popUpWarning(
                getString(R.string.konfirmasi_password_empty),
                getString(R.string.konfirmasi_password_text),
                editText = binding.edRegisterKonfirmasiPassword
            )
        } else {
            if (checkPassword(password, konfirmasiPassword)) {
                registerViewModel.getRegister(name, email, password).observe(this)
                { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                buttonEnable(false)
                                showLoading(true)
                            }

                            is Result.Success -> {
                                showLoading(false)
                                val dataRegister = result.data
                                val messageResponse = dataRegister.message
                                popUpSuccess(
                                    getString(R.string.register_success),
                                    messageResponse.toString()
                                )

                            }

                            is Result.Error -> {
                                showLoading(false)
                                val sweetAlertDialog =
                                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getString(R.string.register_fail))
                                        .setContentText("${result.error.toString()}")
                                        .setConfirmButton("OK") {
                                            it.dismissWithAnimation()
                                        }
                                sweetAlertDialog.show()
                                buttonEnable(true)
                            }

                            else -> {}
                        }
                    }
                }
            } else {
                popUpWarning(
                    getString(R.string.konfirmasi_password_not_same),
                    getString(R.string.konfirmasi_password_not_same_text),
                    binding.edRegisterPassword
                )
            }
        }
    }

    private fun popUpWarning(title: String, content: String, editText: EditText) {
        val sweetAlertDialog =
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmButton("OK") {
                    it.dismissWithAnimation()
                    editText.isFocusable = true
                }
        sweetAlertDialog.show()
    }

    private fun popUpSuccess(title: String, content: String) {
        val sweetAlertDialog =
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmButton("OK") {
                    buttonEnable(true)
                    onBackPressed()
                }
        sweetAlertDialog.show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageRegister, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleRegister = getAlpha(binding.titleRegister, 1000)
        val descRegister = getAlpha(binding.descriptionRegister, 300)
        val textNameLayout = getAlpha(binding.textInputLayoutName, 300)
        val registerName = getAlpha(binding.edRegisterName, 300)
        val textEmailLayout = getAlpha(binding.textInputLayoutEmail, 300)
        val registerEmail = getAlpha(binding.edRegisterEmail, 300)
        val textPasswordLayout = getAlpha(binding.textInputLayoutPassword, 300)
        val registerPassword = getAlpha(binding.edRegisterPassword, 300)
        val textKonfirmasiPasswordLayout = getAlpha(binding.textInputLayoutKonfirmasiPassword, 300)
        val registerKonfirmasiPassword = getAlpha(binding.edRegisterKonfirmasiPassword, 300)
        val btnRegister = getAlpha(binding.btnRegister, 300)
        val backToLogin = getAlpha(binding.tvBackToLogin, 300)

        AnimatorSet().apply {
            playSequentially(
                titleRegister,
                descRegister,
                textNameLayout,
                registerName,
                textEmailLayout,
                registerEmail,
                textPasswordLayout,
                registerPassword,
                textKonfirmasiPasswordLayout,
                registerKonfirmasiPassword,
                btnRegister,
                backToLogin
            )
            start()
        }
    }
}