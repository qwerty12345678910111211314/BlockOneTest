package test.blockonetest

import android.support.v4.app.Fragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.JsonReader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_main.*
import test.blockonetest.BlockchainService
import org.json.JSONObject
import org.json.JSONTokener


class BlockListView : Fragment() {
    var listOfStrings = mutableListOf<String>()
    var list : ListView? = null
    var adapter : ArrayAdapter<String>? = null
    var blocks = mutableListOf<String>()
    var headNode : Int = 0
    var running : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun add_block(block: JSONObject){
        Log.d(TAG,"Added Block $block")
        this.listOfStrings.add("Block: ${block.getInt("block_num")}")
        this.adapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if(blocks.size < 20){
            val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)
            toolbar.findViewById<ProgressBar>(R.id.loading_bar).visibility = View.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()

        val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)
        toolbar.findViewById<ProgressBar>(R.id.loading_bar).visibility = View.GONE
    }
    override fun onStop() {
        super.onStop()

        val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)
        toolbar.findViewById<ProgressBar>(R.id.loading_bar).visibility = View.GONE
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.block_list_view, container, false)

        var BlockchainService = BlockchainService(activity!!.baseContext)
        list = view.findViewById(R.id.list_view)
        adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, this.listOfStrings)
        list!!.adapter = adapter
        list!!.setOnItemClickListener { adapterView, view, i, l ->
            val ft2 = fragmentManager!!.beginTransaction()
            ft2.replace(R.id.content, BlockDetail.newInstance(blocks.get(i)))
            ft2.addToBackStack(null)
            ft2.commit()
        }

        val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)

        toolbar.findViewById<ProgressBar>(R.id.loading_bar).visibility = View.VISIBLE
        if(blocks.isEmpty()) {
            BlockchainService.getInfo(object : NetworkCallback<Any> {
                override fun success(result: Any) {
                    var json = JSONTokener(result as String).nextValue() as JSONObject
                    var head = json.getInt("head_block_num")
                    if (headNode == 0 || (headNode != 0 && headNode != head)) {
                        headNode = head
                        running = 20
                        for (index in 0..20) {
                            BlockchainService.getBlock((head - index), object : NetworkCallback<Any> {
                                override fun success(result: Any) {
                                    blocks.add(result as String)
                                    val block = JSONTokener(result).nextValue() as JSONObject
                                    add_block(block)
                                    running--
                                    if (running == 0) {
                                        toolbar.findViewById<ProgressBar>(R.id.loading_bar).visibility = View.GONE
                                    }
                                }

                                override fun error(code: Int) {
                                    Log.d(TAG, "error $code")
                                    running--
                                    if (running == 0) {
                                        toolbar.findViewById<ProgressBar>(R.id.loading_bar).visibility = View.GONE
                                    }
                                }
                            })
                        }
                    }
                }

                override fun error(code: Int) {
                    Log.d(TAG, "error $code")
                }
            }

            )
        }

            return view
        }

    companion object {
        //Static Variables
        private val TAG = "BlockListView"
        private val BlockchainService = null
        //Default Initializer
        fun newInstance(): BlockListView {
            return BlockListView()
        }
    }
}