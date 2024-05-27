package com.example.myactivity.data.repository;

import com.example.myactivity.data.network.ApiService
import com.example.myactivity.data.model.Order

class OrderRepository(private val apiService: ApiService) {

    suspend fun getOrders(): List<Order> {
        return apiService.getOrders()
    }

    suspend fun sendOrder(order: Order): Boolean {
        return apiService.sendOrder(order)
    }

    suspend fun getOrderById(orderId: Int): Order {
        return apiService.getOrderById(orderId)
    }
}

private fun ApiService.getOrders(): List<Order> {

}
private fun ApiService.sendOrder(order): Boolean {

}
private suspend fun ApiService.getOrders(): List<Order> {
    // 여기에 네트워크 요청을 수행하여 주문 목록을 가져오는 코드를 작성합니다.
    // 실제로는 ApiService에서 제공하는 Retrofit을 사용하여 네트워크 요청을 수행해야 합니다.
    // 이 예제에서는 더미 데이터를 반환하겠습니다.
    return listOf(
        Order(1, "Customer 1", "Address 1", emptyList()),
        Order(2, "Customer 2", "Address 2", emptyList()),
        Order(3, "Customer 3", "Address 3", emptyList()),
        Order(4, "Customer 4", "Address 4", emptyList()),
        Order(5, "Customer 5", "Address 5", emptyList()),
        Order(6, "Customer 6", "Address 6", emptyList()),
        Order(7, "Customer 7", "Address 7", emptyList()),
        Order(8, "Customer 8", "Address 8", emptyList()),
        Order(9, "Customer 9", "Address 9", emptyList()),
        Order(10, "Customer 10", "Address 10", emptyList())
    )
}

// 실제 데이터 로드 로직을 여기에 추가합니다 (예: 네트워크 요청)



