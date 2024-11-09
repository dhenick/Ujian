package id.littlequery.ujian
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class State(
    val State: String,
    val Population: Int,
    val Year : String

)

data class StateResponse(
    val data: List<State>
)

interface StateApiService {
    @GET("data?drilldowns=State&measures=Population&year=latest")
    suspend fun getStates(): StateResponse
}

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://datausa.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: StateApiService = retrofit.create(StateApiService::class.java)
}