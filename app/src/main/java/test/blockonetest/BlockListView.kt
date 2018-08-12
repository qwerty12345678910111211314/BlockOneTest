package test.blockonetest

import android.support.v4.app.Fragment

import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import test.blockonetest.BlockchainService
import org.json.JSONObject
import org.json.JSONTokener


class BlockListView : Fragment() {
    var listOfStrings = mutableListOf<String>()
    var list : ListView? = null
    var adapter : ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun add_block(block: JSONObject){
        Log.d(TAG,"Added Block $block")
        this.listOfStrings.add("Block: ${block.getInt("block_num")}")
        this.adapter!!.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.block_list_view, container, false)

        var BlockchainService = BlockchainService(activity!!.baseContext)
        this.list = view.findViewById<ListView>(R.id.list_view)
        this.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, this.listOfStrings)
        this.list!!.adapter = adapter

        BlockchainService.getInfo(object: NetworkCallback<Any>{
            override fun success(result: Any) {
                var json = JSONTokener(result as String).nextValue() as JSONObject
                var head = json.getInt("head_block_num")

                for (index in 0..20) {
                    BlockchainService.getBlock((head - index), object : NetworkCallback<Any> {
                        override fun success(result: Any) {
                            val block = JSONTokener(result as String).nextValue() as JSONObject
                            add_block(block)
                        }

                        override fun error(code: Int) {
                            Log.d(TAG, "error $code")
                        }
                    })

                }
            }

            override fun error(code: Int) {
                Log.d(TAG, "error $code")
            }
        }
        )
            return view
        }

    companion object {
        //Static Variables
        private val TAG = "BlockListView"
        private val BlockchainService = null
        var blockList: ArrayList<Any>? = null
        //Default Initializer
        fun newInstance(): BlockListView {
            return BlockListView()
        }
    }
}