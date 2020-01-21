package support

import android.util.SparseArray
import java.util.*

class NotificationCenter {

    interface NotificationCenterDelegate {
        fun didReceivedNotification(id: Int, vararg args: Any?)
    }
    companion object {
        private var totalEvents = 1

        val didStartedCall = totalEvents++
        val didEndedCall = totalEvents++
        val closeInCallActivity = totalEvents++
        val liveLocationsChanged = totalEvents++
        val cellClicked = totalEvents++
        val liveLocationsCacheChanged = totalEvents++

        private val observers = SparseArray<ArrayList<Any>>()
        private val removeAfterBroadcast = SparseArray<ArrayList<Any>>()
        private val addAfterBroadcast = SparseArray<ArrayList<Any>>()
        private val delayedPosts = ArrayList<DelayedPost>(10)

        private var broadcasting = 0
        private var animationInProgress = false

        private lateinit var allowedNotifications: IntArray


        class DelayedPost private constructor(val id: Int, val args: Array<Any>)

        @Volatile
        private var Instance: NotificationCenter? = null

        fun getInstance(): NotificationCenter? {
            var localInstance = Instance
            if (localInstance == null) {
                synchronized(NotificationCenter::class.java) {
                    localInstance = Instance
                    if (localInstance == null) {
                        localInstance = NotificationCenter()
                        Instance = localInstance
                    }
                }
            }
            return localInstance
        }




    }
    fun setAllowedNotificationsDutingAnimation(notifications: IntArray) {
        allowedNotifications = notifications
    }

    fun setAnimationInProgress(value: Boolean) {
        animationInProgress = value
        if (!animationInProgress && !delayedPosts.isEmpty()) {
            for (a in delayedPosts.indices) {
                val delayedPost = delayedPosts[a]
                postNotificationNameInternal(delayedPost.id, true, *delayedPost.args)
            }
            delayedPosts.clear()
        }
    }

    fun isAnimationInProgress(): Boolean {
        return animationInProgress
    }
    fun postNotificationName(id: Int, vararg args: Any) {
        var allowDuringAnimation = false
        if (allowedNotifications != null) {
            for (a in allowedNotifications!!.indices) {
                if (allowedNotifications!![a] == id) {
                    allowDuringAnimation = true
                    break
                }
            }
        }
        postNotificationNameInternal(id, allowDuringAnimation, *args)
    }

    fun postNotificationNameInternal(id: Int, allowDuringAnimation: Boolean, vararg args: Any) {
        if (!allowDuringAnimation && animationInProgress) {
//            val delayedPost = DelayedPost(id, args)
//            delayedPosts.add(delayedPost)
            return
        }
        broadcasting++
        val objects = observers[id]
        if (objects != null && !objects.isEmpty()) {
            for (a in objects.indices) {
                val obj = objects[a]
                (obj as NotificationCenterDelegate).didReceivedNotification(id, *args)
            }
        }
        broadcasting--
        if (broadcasting == 0) {
            if (removeAfterBroadcast.size() != 0) {
                for (a in 0 until removeAfterBroadcast.size()) {
                    val key = removeAfterBroadcast.keyAt(a)
                    val arrayList = removeAfterBroadcast[key]
                    for (b in arrayList.indices) {
                        removeObserver(arrayList[b], key)
                    }
                }
                removeAfterBroadcast.clear()
            }
            if (addAfterBroadcast.size() != 0) {
                for (a in 0 until addAfterBroadcast.size()) {
                    val key = addAfterBroadcast.keyAt(a)
                    val arrayList = addAfterBroadcast[key]
                    for (b in arrayList.indices) {
                        addObserver(arrayList[b], key)
                    }
                }
                addAfterBroadcast.clear()
            }
        }
    }

    fun addObserver(observer: Any, id: Int) {
        if (broadcasting != 0) {
            var arrayList = addAfterBroadcast[id]
            if (arrayList == null) {
                arrayList = ArrayList()
                addAfterBroadcast.put(id, arrayList)
            }
            arrayList.add(observer)
            return
        }
        var objects = observers[id]
        if (objects == null) {
            observers.put(id, ArrayList<Any>().also { objects = it })
        }
        if (objects!!.contains(observer)) {
            return
        }
        objects!!.add(observer)
    }

    fun removeObserver(observer: Any, id: Int) {
        if (broadcasting != 0) {
            var arrayList = removeAfterBroadcast[id]
            if (arrayList == null) {
                arrayList = ArrayList()
                removeAfterBroadcast.put(id, arrayList)
            }
            arrayList.add(observer)
            return
        }
        val objects = observers[id]
        objects?.remove(observer)
    }
}
