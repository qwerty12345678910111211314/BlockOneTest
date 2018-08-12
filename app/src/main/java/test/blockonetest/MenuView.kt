package test.blockonetest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MenuView : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.menu_view, container, false)

        val blockButton = view.findViewById<Button>(R.id.show_blocks)
        blockButton.setOnClickListener{
            val fm = this.fragmentManager
            val ft2 = fm!!.beginTransaction()
            ft2.replace(R.id.content, BlockListView())
            ft2.addToBackStack(null)
            ft2.commit()
        }
        return view
    }
}