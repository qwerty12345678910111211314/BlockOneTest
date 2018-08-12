package test.blockonetest
import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.json.JSONObject
import org.json.JSONTokener

interface NetworkCallback<T>{
    fun success(result: T)
    fun error(code: Int)
}

class ERROR_CODES {
    val INVALID_DATA = 1
    val NETWORK_CONNECTION = 2
}

class BlockchainService(context: Context){
    private val TAG = "BlockchainService"
    private val INFO_ENDPOINT = "https://api.eosnewyork.io/v1/chain/get_info"
    private val BLOCK_ENDPOINT = "https://api.eosnewyork.io/v1/chain/get_block"
    private var mRequestQueue: RequestQueue
    private var numRequests = 0

    init {
       this.mRequestQueue = Volley.newRequestQueue(context.applicationContext)
    }

    fun isFinished() : Boolean{
        return (numRequests == 0)
    }

    fun getInfo(callback: NetworkCallback<Any>){
        val stringRequest = object : StringRequest(Request.Method.POST, this.INFO_ENDPOINT,
                Response.Listener { response ->
                    Log.d(TAG,response)
                    try {
                        callback.success(result = response)

                    } catch (e: Exception) {
                        Log.d(TAG,e.toString())
                        callback.error(code = 1)
                    }
                },
                Response.ErrorListener {
                    Log.d("BlockchainService", "Network request failed..")
                    callback.error(code = 2)
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }
        this.mRequestQueue.add(stringRequest)
    }

    fun getBlock(blockID : Int, callback: NetworkCallback<Any>){

        val stringRequest = object : StringRequest(Request.Method.POST, this.BLOCK_ENDPOINT,
                Response.Listener { response ->
                    try {
                        callback.success(result = response)

                    } catch (e: Exception) {
                        Log.d(TAG,e.toString())
                        Log.d(TAG,"Block Num $blockID")
                        callback.error(code = 1)
                    }
                },
                Response.ErrorListener {
                    Log.d("BlockchainService", "Network request failed..")
                    callback.error(code = 2)
                }
        )
        {
            override fun getBody(): ByteArray {
                return "{\"block_num_or_id\":\"$blockID\"}".toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }
        this.mRequestQueue.add(stringRequest)
    }
}