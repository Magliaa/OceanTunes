package com.tunagold.oceantunes.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.tunagold.oceantunes.databinding.ActivityLoginBinding
import com.tunagold.oceantunes.utils.GoogleAuthHelper
import com.tunagold.oceantunes.utils.ToastHelper
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authHelper: GoogleAuthHelper
    private lateinit var toastHelper: ToastHelper

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        result.data?.let { data ->
            lifecycleScope.launch {
                handleGoogleSignInResult(data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        _binding = ActivityLoginBinding.inflate(inflater, container, false)
        authHelper = GoogleAuthHelper(requireContext())
        toastHelper = ToastHelper(requireContext())

        setupGoogleSignInButton()

        return binding.root
    }

    private fun setupGoogleSignInButton() {
        binding.signInWithGoogle.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val signInResult = authHelper.beginSignIn()
                    val intentSenderRequest = IntentSenderRequest.Builder(
                        signInResult.pendingIntent.intentSender
                    ).build()
                    googleSignInLauncher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    toastHelper.showShort("Errore: ${e.message}")
                }
            }
        }
    }

    private suspend fun handleGoogleSignInResult(data: Intent) {
        when (val result = authHelper.handleSignInResult(data)) {
            is GoogleAuthHelper.AuthResult.Success -> {
                toastHelper.showShort("Accesso effettuato con successo")
                // Naviga alla schermata principale
                // findNavController().navigate(R.id.action_login_to_home)
            }
            is GoogleAuthHelper.AuthResult.Failure -> {
                toastHelper.showShort("Accesso fallito: ${result.exception.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}