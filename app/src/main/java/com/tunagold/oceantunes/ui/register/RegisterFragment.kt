package com.tunagold.oceantunes.ui.register

import android.content.Intent // Importa per Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tunagold.oceantunes.MainActivity // Importa la tua MainActivity
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentRegisterBinding
import com.tunagold.oceantunes.utils.ToastHelper
import dagger.hilt.android.AndroidEntryPoint
import com.tunagold.oceantunes.utils.Result

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var toastHelper: ToastHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toastHelper = ToastHelper(requireContext())


        if (viewModel.isUserLoggedIn()) {
            navigateToMainAndClearBackStack()
            return
        }


        setupRegisterButton()
        observeSignUpResult()

        binding.registerText.setOnClickListener {

            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            val email = binding.mailInput.editText?.text?.toString()?.trim().orEmpty()
            val password = binding.passwordInput.editText?.text?.toString()?.trim().orEmpty()
            val confirmPassword = binding.passwordConfirmInput.editText?.text?.toString()?.trim().orEmpty()
            val username = binding.usernameInput.editText?.text?.toString()?.trim().orEmpty()

            if (confirmPassword != password) {
                toastHelper.showShort("Le password non coincidono")
            } else if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && confirmPassword.isNotEmpty()) {
                viewModel.signUp(email, password, username)
            } else {
                toastHelper.showShort("Tutti i campi sono richiesti")
            }
        }
    }

    private fun observeSignUpResult() {
        viewModel.signUpResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    toastHelper.showShort("Registrazione effettuata con successo")
                    navigateToMainAndClearBackStack()
                }
                is Result.Error -> {
                    toastHelper.showShort("Registrazione fallita: ${result.exception.message}")
                }
            }
        }
    }


    private fun navigateToMainAndClearBackStack() {
        val intent = Intent(requireActivity(), MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

        requireActivity().finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}