package com.example.venturesupport

import User
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.venturesupport.databinding.MyprofileBinding
import com.example.venturesupport.databinding.MychartItemBinding
import com.example.venturesupport.databinding.MyprofileItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileActivity: Fragment() {
    private val binding: MyprofileBinding by lazy {
        MyprofileBinding.inflate(layoutInflater)
    }
    private lateinit var naverPayApiService: NaverPayApiService
    private lateinit var myprofileAdapter: MyProfileAdapter
    private lateinit var mychartAdapter: MyChartAdapter
    private var myprofileLists = ArrayList<User>()
    private var chartLists = ArrayList<MyChart>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = binding.root

        // 현재 로그인 한 user 정보 가져 오기
        fetchUserById(1)

        // 유저 조회
        myprofileAdapter = MyProfileAdapter(myprofileLists)
        myprofileAdapter.setOnItemClickListener(object : MyProfileAdapter.OnItemClickListeners {
            override fun onItemClick(binding: MyprofileItemBinding, user: User, position: Int) {
                val intentUser = User(
                    userId = user.userId,
                    username = user.username,
                    email = user.email,
                    lat = user.lat,
                    lng = user.lng,
                    phone = user.phone,
                    role = user.role,
                    password = user.password
                )

                val intent = Intent(requireContext(), EditProfileActivity::class.java).apply {
                    putExtra("user", intentUser)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })

        binding.MyProfileRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.MyProfileRecyclerView.adapter = myprofileAdapter
        myprofileAdapter.notifyDataSetChanged()

        // 결제 수단 조회 (수정 필요)
        naverPayApiService = NaverPayApiService()
        binding.NaverPayButton.setOnClickListener {
            val orderId = "order1234"
            val price = 10000
            naverPayApiService.makePayment(orderId, price) { success, message ->
                if (success) {
                    Toast.makeText(requireActivity(), "Payment successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), "Payment failed: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.WarehouseButton.setOnClickListener {
            val intentUser = User(
                userId = myprofileLists[0].userId,
                username = myprofileLists[0].username,
                email = myprofileLists[0].email,
                lat = myprofileLists[0].lat,
                lng = myprofileLists[0].lng,
                phone = myprofileLists[0].phone,
                role = myprofileLists[0].role,
                password = myprofileLists[0].password
            )

            val intent = Intent(requireContext(), WarehouseActivity::class.java).apply {
                putExtra("user", intentUser)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        chartLists = ArrayList<MyChart>().apply {
            add(MyChart(R.drawable.sales, "내 매출 내역"))
            add(MyChart(R.drawable.income, "내 수익 내역"))
            add(MyChart(R.drawable.expense, "내 지출 내역"))
        }

        mychartAdapter = MyChartAdapter(chartLists)
        mychartAdapter.setOnItemClickListener(object : MyChartAdapter.OnItemClickListeners {
            override fun onItemClick(binding: MychartItemBinding, myChart: MyChart, position: Int) {
                val intentUser = User(
                    userId = myprofileLists[0].userId,
                    username = myprofileLists[0].username,
                    email = myprofileLists[0].email,
                    lat = myprofileLists[0].lat,
                    lng = myprofileLists[0].lng,
                    phone = myprofileLists[0].phone,
                    role = myprofileLists[0].role,
                    password = myprofileLists[0].password
                )

                val intent: Intent = when (position) {
                    0 -> {
                        Intent(requireContext(), SalesChartActivity::class.java).apply {
                            putExtra("user", intentUser)
                        }
                    }
                    1 -> {
                        Intent(requireContext(), IncomeChartActivity::class.java).apply {
                            putExtra("user", intentUser)
                        }
                    }
                    2 -> {
                        Intent(requireContext(), ExpenseChartActivity::class.java).apply {
                            putExtra("user", intentUser)
                        }
                    }
                    else -> return
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })

        binding.ChartRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.ChartRecyclerView.adapter = mychartAdapter

        return view
    }

    // 현재 로그인 한 user 정보 가져 오기
    private fun fetchUserById(userId: Int) {
        val call = RetrofitService.userService.getUserById(userId)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        myprofileLists.add(user)
                        myprofileAdapter.updateData(myprofileLists)
                        Log.d("MainActivity", "User added to list: $user")
                    } else {
                        Log.e("MainActivity", "No user data found in response")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with status: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Network request failed", t)
            }
        })
    }
}