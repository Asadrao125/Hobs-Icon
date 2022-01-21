package com.technado.hobsicon.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.technado.hobsicon.R
import com.technado.hobsicon.adapter.AppsAdapter
import com.technado.hobsicon.helper.Dialog_CustomProgress
import com.technado.hobsicon.model.AppModel
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var installedAppsList: ArrayList<AppModel>
    lateinit var appList: ArrayList<AppModel>
    lateinit var dialog: Dialog_CustomProgress
    var onSwipeTouchListener: OnSwipeTouchListener? = null
    lateinit var imageUp: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appList = ArrayList()
        installedAppsList = ArrayList()
        dialog = Dialog_CustomProgress(this)
        imageUp = findViewById(R.id.imageUp)

        val recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.setHasFixedSize(true)

        dialog.showProgressDialog()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismissProgressDialog()
            appList.clear()
            appList = getInstalledApps()
            setAdapter(recyclerView, appList)
        }, 500)

        //onSwipeTouchListener = OnSwipeTouchListener(this, findViewById(R.id.parentLayout))

        imageUp.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AllAppsActivity::class.java)
            startActivity(intent)
        })
    }

    fun setAdapter(recyclerView: RecyclerView, appList: ArrayList<AppModel>) {
        recyclerView.adapter = AppsAdapter(this, appList)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledApps(): ArrayList<AppModel> {
        val packs = this.packageManager?.getInstalledPackages(0)
        for (i in packs?.indices!!) {
            val p = packs[i]
            if (!isSystemPackage(p)) {
                val appName = p.applicationInfo.loadLabel(this.packageManager!!).toString()
                val icon = p.applicationInfo.loadIcon(this.packageManager!!)
                val packages = p.applicationInfo.packageName
                Log.d("packages", "getInstalledApps: " + packages)
                installedAppsList.add(AppModel(appName, icon, packages))
            }
        }
        return installedAppsList
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    class OnSwipeTouchListener internal constructor(ctx: Context, mainView: View) :
        View.OnTouchListener {
        private val gestureDetector: GestureDetector
        private var context: Context
        private lateinit var onSwipe: OnSwipeListener

        init {
            gestureDetector = GestureDetector(ctx, GestureListener())
            mainView.setOnTouchListener(this)
            context = ctx
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return gestureDetector.onTouchEvent(event)
        }

        private companion object {
            private const val swipeThreshold = 100
            private const val swipeVelocityThreshold = 100
        }

        inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                var result = false
                try {
                    val diffY = e2.y - e1.y
                    val diffX = e2.x - e1.x
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > swipeThreshold && Math.abs(velocityX) > swipeVelocityThreshold) {
                            if (diffX > 0) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }
                            result = true
                        }
                    } else if (Math.abs(diffY) > swipeThreshold && Math.abs(velocityY) > swipeVelocityThreshold) {
                        if (diffY > 0) {
                            onSwipeBottom()
                        } else {
                            onSwipeTop()
                        }
                        result = true
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                return result
            }
        }

        internal fun onSwipeRight() {
            this.onSwipe.swipeRight()
        }

        internal fun onSwipeLeft() {
            this.onSwipe.swipeLeft()
        }

        internal fun onSwipeTop() {
            val intent = Intent(context, AllAppsActivity::class.java)
            context.startActivity(intent)
            this.onSwipe.swipeTop()
        }

        internal fun onSwipeBottom() {
            this.onSwipe.swipeBottom()
        }

        internal interface OnSwipeListener {
            fun swipeRight()
            fun swipeTop()
            fun swipeBottom()
            fun swipeLeft()
        }
    }
}