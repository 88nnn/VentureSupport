package com.example.myactivity.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myactivity.R
import com.example.myactivity.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var loginViewModel: LoginViewModel? = null
    private var binding: FragmentLoginBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        val usernameEditText = binding!!.username
        val passwordEditText = binding!!.password
        val loginButton = binding!!.login
        val loadingProgressBar = binding!!.loading
        loginViewModel!!.loginFormState.observe(
            getViewLifecycleOwner(),
            Observer<LoginFormState?> { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.setEnabled(loginFormState.isDataValid)
                if (loginFormState.usernameError != null) {
                    usernameEditText.error = getString(loginFormState.usernameError!!)
                }
                if (loginFormState.passwordError != null) {
                    passwordEditText.error = getString(loginFormState.passwordError!!)
                }
            })
        loginViewModel!!.loginResult.observe(
            getViewLifecycleOwner(),
            Observer<LoginResult?> { loginResult ->
                if (loginResult == null) {
                    return@Observer
                }
                loadingProgressBar.visibility = View.GONE
                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                if (loginResult.success != null) {
                    updateUiWithUser(loginResult.success)
                }
            })
        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel!!.loginDataChanged(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel!!.login(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString()
                )
            }
            false
        }
        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel!!.login(
                usernameEditText.getText().toString(),
                passwordEditText.getText().toString()
            )
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView?) {
        val welcome = getString(R.string.welcome) + model!!.displayName
        // TODO : initiate successful logged in experience
        if (context != null && context!!.applicationContext != null) {
            Toast.makeText(context!!.applicationContext, welcome, Toast.LENGTH_LONG).show()
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int?) {
        if (context != null && context!!.applicationContext != null) {
            Toast.makeText(
                context!!.applicationContext,
                errorString!!,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /*
    //* btnLogin.setOnClickListener {
            // 로그인 로직: 상부에 이미 완성?
      //  } 아래 코드 코틀린식으로 작성하디

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignPageActivity::class.java)
            startActivity(intent)
        }

        btnNaverLogin.setOnClickListener {
            val intent = Intent(this, NaverLoginPageActivity::class.java)
            startActivity(intent)
        }
    *
    * */

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

