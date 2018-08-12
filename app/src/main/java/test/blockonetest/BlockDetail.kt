package test.blockonetest

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.util.regex.Matcher
import java.util.regex.Pattern


private const val ARG_PARAM1 = "block"

class BlockDetail : Fragment() {
    private var block: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            block = JSONTokener(it.getString(ARG_PARAM1)).nextValue() as JSONObject
        }
        Log.d(TAG, "Selected $block")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_block_detail, container, false)

        val layout = view.findViewById(R.id.text_area) as LinearLayout

        val producer = TextView(view.context)
        producer.text = "Producer: ${this.block?.getString("producer")}"
        layout.addView(producer)


        val transactions = TextView(view.context)
        transactions.text = "Transactions: ${this.block?.getJSONArray("transactions")?.length()}"
        layout.addView(transactions)

        val signature = TextView(view.context)
        signature.text = "Signature: ${this.block?.getString("producer_signature")}"
        layout.addView(signature)

        val rawDetailsArea = view.findViewById(R.id.raw_content) as LinearLayout
        val keys = block!!.keys()
        for(key in keys) {
            val json = this.block?.get(key)
            if(json is JSONArray){
                val len = this.block!!.getJSONArray(key).length()
                val textView = TextView(view.context)
                textView.text = "${key}: $len"
                rawDetailsArea.addView(textView)
            }else{
                val tmp = TextView(view.context)
                tmp.text = "${key}: ${this.block?.getString(key)}"
                rawDetailsArea.addView(tmp)
            }
        }

        val rawDetails = view.findViewById(R.id.show_raw_content) as Button
        rawDetails.setOnClickListener {
            Log.d(TAG,"clicked")
            if(rawDetailsArea.visibility == View.GONE) {
                rawDetailsArea.visibility = View.VISIBLE
                rawDetails.text = "Hide Raw Content"
            }else{
                rawDetailsArea.visibility = View.GONE
                rawDetails.text = "Show Raw Content"
            }
        }

        val contractArea = view.findViewById(R.id.transactions_area) as LinearLayout
        val transactionDetails = view.findViewById(R.id.show_transactions) as Button
        if(block!!.getJSONArray("transactions").length() == 0){
            transactionDetails.visibility = View.GONE
            contractArea.visibility = View.GONE
        }
        var loaded = false
        transactionDetails.setOnClickListener {
            if(contractArea.visibility == View.GONE){
                contractArea.visibility = View.VISIBLE
                transactionDetails.text = "Hide Transactions"
            }else{
                contractArea.visibility = View.GONE
                transactionDetails.text = "Show Transactions"
            }
            if(loaded == false) {
                transactionDetails.text = "Loading Transactions"
                loaded = true
                val trans = block!!.getJSONArray("transactions")
                val BlockchainService = BlockchainService(activity!!.baseContext)
                for (index in 0..(trans.length() - 1)) {
                    val transaction = trans.getJSONObject(0).getJSONObject("trx").getJSONObject("transaction")
                    Log.d(TAG, "Transaction $transaction")
                    val actions = transaction.getJSONArray("actions")
                    var running = actions.length()
                    for (actionIndex in 0..(actions.length() - 1)) {
                        val action = actions.getJSONObject(actionIndex)
                        val data = action.getJSONObject("data")
                        data.put("transaction.delay", transaction.getString("delay_sec"))
                        BlockchainService.getABI(action.getString("account"),
                                object : NetworkCallback<Any> {
                                    override fun success(result: Any) {
                                        running--
                                        Log.d(TAG, "ABI RESUST: $result")
                                        val contract = (JSONTokener(result as String).nextValue() as JSONObject).getJSONObject("abi").getJSONArray("actions").get(0) as JSONObject
                                        val dataKeys = data.keys()
                                        var resultText = "${action.getString("account")}: ${action.getString("name")} " +
                                                "${(if (contract.getString("ricardian_contract").isEmpty()) {
                                                    ""
                                                } else {
                                                    "\n"
                                                })} " +
                                                "${contract.getString("ricardian_contract")}"
                                        for (key in dataKeys) {
                                            val regex = "\\{\\{${Pattern.quote(key)}\\}\\}"
                                            val pattern: Pattern = Pattern.compile(regex, Pattern.MULTILINE)
                                            val matcher: Matcher = pattern.matcher(resultText)
                                            resultText = matcher.replaceAll(data.getString(key))
                                        }
                                        val textView = TextView(view.context)
                                        textView.text = resultText
                                        contractArea.addView(textView)
                                        if(running == 0){
                                            transactionDetails.text = "Hide Transactions"
                                        }
                                    }

                                    override fun error(code: Int) {
                                        running--
                                        if(running == 0){
                                            transactionDetails.text = "Hide Transactions"
                                        }
                                        Log.d(TAG, "code: $code")
                                    }
                                })
                    }


                }
            }
        }
        return view
    }

    companion object {
        var TAG = "BlockDetail"
        fun newInstance(block: String) =
            BlockDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, block)
                }
            }
    }
}
