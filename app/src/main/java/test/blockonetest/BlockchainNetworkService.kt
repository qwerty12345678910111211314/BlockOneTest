package test.blockonetest

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.util.*

class BlockchainNetworkService : IntentService("BlockchainNetworkService") {

    private var mBlockchainService : BlockchainService? = null

    fun Log(msg: String){
        Log.d(TAG,msg)
    }


    override fun onHandleIntent(intent: Intent?) {
//        Log("Intent ${TAG} started!")
//        mTicketService = TicketService(baseContext)
//        if(intent != null && mTicketService != null) {
//            when (intent.dataString){
//                TicketConstants.TICKET_DOWNLOAD -> {
//                    Log("Downloading Tickets.")
//                    mTicketService!!.getTickets(this)
//                }
//                TicketConstants.TICKET_DATABASE -> {
//                    Log("Retrieving Database Tickets")
//                    mTicketService!!.getDatabaseTickets(this)
//                }
//                TicketConstants.TICKET_ERROR -> {
//                    Log("An Error Occurred")
//                }
//                TicketConstants.TICKET_DOWNLOAD_FINISHED -> {
//                    Log("Retrieving tickets download finished")
//                }
//                TicketConstants.TICKET_DATABASE_FINISHED -> {
//                    Log("Retrieving tickets database finished")
//                }
//                else -> Log("Error: ${intent.dataString} does not exist.")
//            }
//            val dataString = intent.dataString
//            Log("Data String: $dataString")
//        }else{
//            Log("intent or context null!")
//        }
    }

    companion object {
        private val TAG : String = "TicketIntentService"
        var downloadFinished = false
    }

}