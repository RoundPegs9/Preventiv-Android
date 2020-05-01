package ml.preventiv.location

import android.content.Context
import android.util.Log
import ml.preventiv.AppPreference
import ml.preventiv.Utils
import ml.preventiv.bluetooth.foreground.NotificationHelper
import ml.preventiv.firebase.Firestore

class NetworkManager constructor(context: Context) {
    private val context = context
    private val firestore = Firestore.newInstance()
    private val TAG = "NETWORK_MANAGER"

    fun getAlerts()
    {
        val appPreference = AppPreference(context)

        firestore.get("hashes").addOnCompleteListener { it ->
            for (documents in it.result!!)
            {
                val documentTime = documents.toString().split("seconds=")[1].split(",")[0].toLong()//Take out the time it was created and check if it's more than 48 hours of nowTime(). If so, delete the document

                for (data in documents.data)
                {
                    val key = data.key as String
                    val value = data.value as String
                    Log.i(TAG, "Key: $key")
                    Log.i(TAG, "Value: $value")
                    val uuid = value.split("-")[0]
                    val firebaseTime = value.split("-")[1].toLong()

                    val amInfected = (appPreference.isConnection(uuid))
                    Log.i(TAG, "Is this person one of my connections: $amInfected")
                    val meNegative = (appPreference.getConfidence() == -1F || appPreference.getConfidence() == 0.25F)
                    Log.i(TAG, "Am I Positive: $meNegative")
                    if(amInfected && meNegative && !(appPreference.isOnlineConnection(uuid)))
                    {
                        //Okay, so this tells us that the ID that we were looking for exists within
                        //this persons tree. So now, we just find the node and see if the time
                        //that these 2 met is greater or equal to the firebase sent time
                        val timeOfInteraction = appPreference.findTimeOfInteraction(uuid)
                        Log.i(TAG, "Time of Interaction (38): $timeOfInteraction")
                        Log.i(TAG, "Firebase time: $firebaseTime")
                        Log.i(TAG, "Get all Connections: ${appPreference.getConnections()}")
                        Log.i(TAG, "Get online Connections: ${appPreference.getOnlineConnections()}")
                        if (timeOfInteraction != (-1).toLong() && timeOfInteraction > firebaseTime)//found time of interaction & is Greater than Firebase Time
                        {
                            if(key.toInt() < 2)
                            {
                                val code = appPreference.addOnlineConnection(uuid)
                                if(code)
                                {
                                    if(key.toInt() == 0)
                                    {
                                        NotificationHelper.onHandleEvent("Connection Infected!",
                                            "Someone you came under contact with in the past 14 days has been tested positive of COVID-19/reported symptoms of COVID-19. " +
                                                    "Take care and contact the nearest hospital immediately! Stay alert, not anxious!",
                                            context)
                                    }
                                    else if(key.toInt() == 1)
                                    {
                                        NotificationHelper.onHandleEvent("Connection Infected!",
                                            "Someone you came under contact with in the past 14 days came under contact with someone who was tested positive/showed symptoms of COVID-19. " +
                                                    "Monitor yourself closely and contact the nearest hospital if you feel symptoms of COVID-19. Stay alert, not anxious!",
                                            context)
                                    }
                                    appPreference.addInfectedIterator()
                                    this.sendAlert(key.toInt() + 1, timeOfInteraction)
                                }
                            }
                        }
                    }
                    /**
                     * Auto delete the Connection UUID after 48 hours.
                     */
                }
                Log.i(TAG, "Doc: $documentTime, nowTime = ${Utils.getTimeStamp()}, Delta: ${Utils.getTimeStamp() - documentTime}")
                if(Utils.getTimeStamp() - documentTime > 172800) //more than 2 days, delete the document.
                {
                    firestore.delete("hashes", documents.id).addOnCompleteListener { del ->
                        Log.i(TAG, "Deleted with Document id: ${del.result!!}")
                    }
                        .addOnFailureListener { exp ->
                            Log.e(TAG, "Error Deleting document of collection = HASHES and ID = ${documents.id}, with exception: ${exp.message}")
                        }
                }
            }
        }
    }

    fun sendAlert(index:Int, time:Long)
    {
        if(index <= 1)
        {
            val uuid = AppPreference(context).getUUID()!!
                .substring(AppPreference(context).getUUID()!!.length - 10, AppPreference(context).getUUID()!!.length)

            val data:HashMap<String, String> = hashMapOf()
            Log.i(TAG, AppPreference(context).getConnections().toString())
            data["$index"] = "${uuid}-${time}"
            firestore.add("hashes", data).addOnCompleteListener {
                Log.i(TAG, "Added with Document id: ${it.result!!}")
            }
                .addOnFailureListener {
                    Log.e(TAG, "Error Writing to collection = HASHES, with exception: ${it.message}")
                }

            if(index == 0)
            {
                LocationClientManager.getLocation(context, true) //this takes care of GPS sending to Firebase
                // as it'll get updated every 1-minute after turning on FService
            }
        }
    }


    companion object
    {
        fun newInstance(context: Context) = NetworkManager(context)
    }
}