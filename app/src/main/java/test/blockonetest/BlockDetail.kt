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
import android.widget.TextView
import org.json.JSONObject
import org.json.JSONTokener



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
            val textView = TextView(view.context)
            textView.text = "${key}: ${this.block?.getString(key)}"
            rawDetailsArea.addView(textView)
        }

        val rawDetails = view.findViewById(R.id.show_raw_content) as Button
        rawDetails.setOnClickListener {
            if(rawDetailsArea.visibility == View.GONE) {
                rawDetailsArea.visibility = View.VISIBLE
            }else{
                rawDetailsArea.visibility = View.GONE
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
