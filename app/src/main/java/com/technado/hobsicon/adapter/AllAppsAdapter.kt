package com.technado.hobsicon.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.technado.hobsicon.R
import com.technado.hobsicon.helper.RecyclerItemClickListener
import com.technado.hobsicon.model.AppModel

class AllAppsAdapter(var context: Context, var list: ArrayList<AppModel>) :
    RecyclerView.Adapter<AllAppsAdapter.MyViewHolder>() {
    lateinit var iconNew: Drawable
    lateinit var adapter: ImageAdapter
    lateinit var imageList: ArrayList<Drawable>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_apps, parent, false)

        imageList = ArrayList()
        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_share)!!)
        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_uninstall)!!)
        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_edit)!!)

        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_share)!!)
        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_uninstall)!!)
        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_edit)!!)

        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_share)!!)
        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_uninstall)!!)
        imageList.add(ContextCompat.getDrawable(context, R.drawable.ic_edit)!!)
        adapter = ImageAdapter(context, imageList)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.appName.text = list.get(position).name
        holder.image.setImageDrawable(list.get(position).icon)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent =
                context.packageManager.getLaunchIntentForPackage(list.get(position).packages)
            context.startActivity(intent)
        })

        holder.itemView.setOnLongClickListener {
            optionsDialog(list.get(position).name, position)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appName: TextView
        var image: ImageView

        init {
            appName = itemView.findViewById(R.id.appName)
            image = itemView.findViewById(R.id.image)
        }
    }

    private fun optionsDialog(title: String, position: Int) {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_options)
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        val btnClose = dialog.findViewById(R.id.btnClose) as Button
        val unInstall = dialog.findViewById(R.id.unInstall) as LinearLayout
        val share = dialog.findViewById(R.id.share) as LinearLayout
        val edit = dialog.findViewById(R.id.edit) as LinearLayout
        tvTitle.text = title

        btnClose.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        unInstall.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:" + list.get(position).packages)
            context.startActivity(intent)
            list.removeAt(position)
            adapter.notifyItemChanged(position)
            dialog.dismiss()
        })

        share.setOnClickListener(View.OnClickListener {

        })

        edit.setOnClickListener(View.OnClickListener {
            editDialog(title, position)
            dialog.dismiss()
        })

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun editDialog(title: String, position: Int) {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_edit)
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        val imgApp = dialog.findViewById(R.id.imgApp) as ImageView
        val edtTitle = dialog.findViewById(R.id.edtTitle) as EditText
        val btnUpdate = dialog.findViewById(R.id.btnUpdate) as Button
        val btnClose = dialog.findViewById(R.id.btnClose) as Button
        val imageRecyclerView = dialog.findViewById(R.id.imageRecyclerView) as RecyclerView

        btnClose.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        imageRecyclerView.layoutManager = GridLayoutManager(context, 4)
        imageRecyclerView.setHasFixedSize(true)
        imageRecyclerView.adapter = adapter

        tvTitle.text = "Edit - " + title
        imgApp.setImageDrawable(list.get(position).icon)
        edtTitle.setText(title)

        imageRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(context, imageRecyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        iconNew = imageList.get(position)
                        adapter.selectedPos = position
                        adapter.notifyDataSetChanged()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )

        btnUpdate.setOnClickListener(View.OnClickListener {
            if (edtTitle.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Title Required", Toast.LENGTH_SHORT).show()
            } else {
                list.get(position).name = edtTitle.text.toString().trim()
                list.get(position).icon = iconNew
                notifyItemChanged(position)
                dialog.dismiss()
            }
        })

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}