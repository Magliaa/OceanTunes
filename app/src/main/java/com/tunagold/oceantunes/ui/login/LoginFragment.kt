package com.tunagold.oceantunes.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tunagold.oceantunes.databinding.FragmentLoginBinding
import com.tunagold.oceantunes.utils.ToastHelper
import dagger.hilt.android.AndroidEntryPoint
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.utils.Result

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var toastHelper: ToastHelper

    private val loginViewModel: LoginViewModel by viewModels()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        result.data?.let { data ->
            loginViewModel.handleGoogleSignInIntentResult(data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        toastHelper = ToastHelper(requireContext())

        setupLoginButton()
        setupGoogleSignInButton()

        binding.registerText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.usernameInput.editText?.text?.toString()?.trim().orEmpty()
            val password = binding.passwordInput.editText?.text?.toString()?.trim().orEmpty()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.signIn(email, password)
            } else {
                toastHelper.showShort("Email e password sono richieste")
            }
        }
    }

    private fun setupGoogleSignInButton() {
        binding.signInWithGoogle.setOnClickListener {
            loginViewModel.initiateGoogleSignIn()
        }
    }

    private fun observeViewModel() {
        loginViewModel.signInResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    toastHelper.showShort("Accesso effettuato con successo")
                    findNavController().navigate(R.id.action_loginFragment_to_mainActivityDestination)
                }
                is Result.Error -> {
                    Log.e("LoginFragment", "Accesso fallito: ${result.exception.message}")
                    toastHelper.showShort("Accesso fallito: ${result.exception.message}")
                }
            }
        }

        loginViewModel.googleSignInResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    toastHelper.showShort("Accesso effettuato con successo")
                    findNavController().navigate(R.id.action_loginFragment_to_mainActivityDestination)
                }
                is Result.Error -> {
                    Log.e("LoginFragment", "Accesso fallito: ${result.exception.message}")
                    toastHelper.showShort("Accesso fallito: ${result.exception.message}")
                }
            }
        }

        loginViewModel.googleSignInIntentSenderRequest.observe(viewLifecycleOwner) { pendingIntent ->
            val intentSender = pendingIntent.intentSender
            val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
            googleSignInLauncher.launch(intentSenderRequest)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
