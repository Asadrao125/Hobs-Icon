package com.technado.hobsicon.activities

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.technado.hobsicon.R
import com.technado.hobsicon.adapter.AllAppsAdapter
import com.technado.hobsicon.helper.Dialog_CustomProgress
import com.technado.hobsicon.model.AppModel

class AllAppsActivity : AppCompatActivity() {
    private lateinit var installedAppsList: ArrayList<AppModel>
    lateinit var appList: ArrayList<AppModel>
    lateinit var dialog: Dialog_CustomProgress
    lateinit var adapter: AllAppsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_apps)

        appList = ArrayList()
        installedAppsList = ArrayList()
        dialog = Dialog_CustomProgress(this)

        val recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.setHasFixedSize(true)

        //dialog.showProgressDialog()
        Handler(Looper.getMainLooper()).postDelayed({
            //dialog.dismissProgressDialog()
            appList.clear()
            appList = getInstalledApps()
            setAdapter(recyclerView, appList)
            adapter = AllAppsAdapter(this, appList)
        }, 500)
    }

    fun setAdapter(recyclerView: RecyclerView, appList: ArrayList<AppModel>) {
        recyclerView.adapter = AllAppsAdapter(this, appList)
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
}