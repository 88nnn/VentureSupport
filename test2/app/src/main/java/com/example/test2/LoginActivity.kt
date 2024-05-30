package com.example.test2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.RetrofitService.authService
import com.example.test2.databinding.LoginBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: AppCompatActivity() {
    private val binding: LoginBinding by lazy {
        LoginBinding.inflate(layoutInflater)
    }
    private var selectedRole: UserRole? = null
    private var userEmail: String = ""
    private var password: String = ""
    private var intentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedRole = when (checkedId) {
                binding.radioButtonCompany.id -> UserRole.COMPANY
                binding.radioButtonDriver.id -> UserRole.DRIVER
                else -> null
            }

            selectedRole?.let {
                Log.d("LoginActivity", "Selected role: $it")
            }
        }

        binding.btnlogin.setOnClickListener {
            userEmail = binding.email.text.toString()
            password = binding.password.text.toString()

            val user = User(
                userId = null,
                username = null.toString(),
                email = userEmail,
                lat = null,
                lng = null,
                phone = null.toString(),
                role = selectedRole!!,
                password = password
            )

            fetchAllUsers(object : FetchAllUsersCallback {
                override fun onFetchComplete() {
                    loginUser(user)
                }
            })
        }

        binding.btnregister.setOnClickListener {
            val intent = Intent(this, LogintoResisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun loginUser(user: User) {
        val call = authService.loginUser(user)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    println("Login successful")
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    when (selectedRole) {
                        UserRole.COMPANY -> {
                            val intent = Intent(this@LoginActivity, CreateOrderActivity::class.java)
                            intent.putExtra("intentUser", intentUser)
                            startActivity(intent)
                            finish()
                        }
                        UserRole.DRIVER -> {
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.putExtra("intentUser", intentUser)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Role not selected", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    println("Login failed: ${response.errorBody()?.string()}")
                    binding.passwordError.text = "이메일 또는 비밀번호를 다시 확인하세요."
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Error: ${t.message}")
                binding.passwordError.text = "이메일 또는 비밀번호를 다시 확인하세요."
            }
        })
    }

    private fun fetchAllUsers(callback: FetchAllUsersCallback) {
        val call = RetrofitService.userService.getAllUsers()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    if (userList != null) {
                        for (user in userList) {
                            if (user.email == userEmail) { // 이메일이 일치하는 사용자를 찾습니다.
                                // 사용자 정보를 저장하거나 필요한 작업을 수행합니다.
                                saveUser(user)
                                break // 이메일이 일치하는 사용자를 찾았으면 반복문 종료
                            }
                        }
                        // userList를 사용하여 원하는 작업을 수행합니다.
                        callback.onFetchComplete() // fetchAllUsers가 완료되었음을 알립니다.
                    } else {
                        Log.e("MainActivity", "No user data found in response")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with status: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("MainActivity", "Network request failed", t)
            }
        })
    }

    interface FetchAllUsersCallback {
        fun onFetchComplete()
    }

    private fun saveUser(user: User) {
        intentUser = user
    }
}

