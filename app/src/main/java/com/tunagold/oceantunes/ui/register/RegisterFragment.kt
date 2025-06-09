package com.tunagold.oceantunes.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentRegisterBinding
import com.tunagold.oceantunes.utils.ToastHelper
import dagger.hilt.android.AndroidEntryPoint

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
            result.onSuccess {
                toastHelper.showShort("Registrazione effettuata con successo")
                findNavController().navigate(R.id.action_registerFragment_to_mainActivityDestination)
            }.onFailure {
                toastHelper.showShort("Registrazione fallita: ${it.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
