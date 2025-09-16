package com.example.finalproject.data

import com.example.finalproject.ApiService
import com.example.finalproject.LoginRequest
import com.example.finalproject.LoginResponse
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var service: ApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        // Retrofit pointed at the fake server
        service = Retrofit.Builder()
            .baseUrl(server.url("/")) // important: ends with "/"
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun login_postsToSydneyAuth_andReturnsKeypass() {
        // Arrange a fake success response
        val body = """{"status":"ok","keypass":"abc123"}"""
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        // Act
        val request = LoginRequest(username = "Manish", password = "12345678")
        val resp = service.login(request).execute()

        // Assert HTTP + parsing
        assertThat(resp.isSuccessful).isTrue()
        val parsed: LoginResponse? = resp.body()
        assertThat(parsed).isNotNull()
        assertThat(parsed?.keypass).isEqualTo("abc123")

        // Verify the exact path hit
        val recorded = server.takeRequest()
        // Expecting POST /sydney/auth (your interface uses "sydney/auth")
        assertThat(recorded.method).isEqualTo("POST")
        assertThat(recorded.path).isEqualTo("/sydney/auth")
    }

    @Test
    fun dashboard_getsByPath_andReturnsBody() {
        // Arrange
        val body = """{"status":"ok","data":[{"title":"Alpha"},{"name":"Bravo"}]}"""
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        // Act
        val resp = service.getDashboard("abc123").execute()

        // Assert HTTP success and non-null body
        assertThat(resp.isSuccessful).isTrue()
        val responseBody = resp.body()
        assertThat(responseBody).isNotNull()
        // Optional: check content text
        assertThat(responseBody!!.string()).contains("Alpha")

        // Verify we called GET /dashboard/abc123
        val recorded = server.takeRequest()
        assertThat(recorded.method).isEqualTo("GET")
        assertThat(recorded.path).isEqualTo("/dashboard/abc123")
    }
}
