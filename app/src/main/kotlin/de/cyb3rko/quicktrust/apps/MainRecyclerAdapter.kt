/*
 * Copyright (c) 2023 Cyb3rKo
 * Inspired by MuntashirAkon/AppManager
 * https://github.com/MuntashirAkon/AppManager/blob/3ba5a0472c32b05d507b4390665e03e0929e7619/app/src/main/java/io/github/muntashirakon/AppManager/main/MainRecyclerAdapter.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cyb3rko.quicktrust.apps

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.cyb3rko.quicktrust.R
import java.util.Locale

internal class MainRecyclerAdapter(
    activity: Activity,
    private val onClick: (item: ApplicationItem) -> Unit,
    private val onListChanged: () -> Unit
) : ListAdapter<ApplicationItem, MainRecyclerAdapter.ViewHolder>(DiffCallback) {
    private lateinit var fullList: List<ApplicationItem>
    private val mPackageManager: PackageManager = activity.packageManager
    private val mColorSecondary = ContextCompat.getColor(activity, R.color.md_theme_dark_secondary)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.item.setOnClickListener {
            onClick(item)
        }

        holder.debugIcon.visibility = if (item.debuggable) View.VISIBLE else View.INVISIBLE
        holder.version.text = item.versionName
        item.sha?.let {
            holder.issuer.visibility = View.VISIBLE
            holder.issuer.text = it.first

            holder.sha.visibility = View.VISIBLE
            holder.sha.text = it.second
        }
        holder.icon.setImageDrawable(item.loadIcon(mPackageManager))
        holder.label.text = item.label
        holder.packageName.text = item.packageName
        holder.packageName.setTextColor(mColorSecondary)
        holder.version.text = holder.version.text
        holder.isSystemApp.text = if (item.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            "System"
        } else {
            "User"
        }
        holder.sdk.text = java.lang.String.format(Locale.getDefault(), "SDK %d", item.sdk)
    }

    fun submitInitialList(list: List<ApplicationItem>) {
        fullList = list
        submitList(fullList)
    }

    override fun onCurrentListChanged(
        previousList: MutableList<ApplicationItem>,
        currentList: MutableList<ApplicationItem>
    ) {
        onListChanged()
    }

    fun filter(name: String) {
        if (name.isNotBlank()) {
            submitList(fullList.filter { it.label.contains(name, true) })
        } else {
            submitList(fullList)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: LinearLayout = itemView as LinearLayout
        var icon: AppCompatImageView = itemView.findViewById(R.id.icon)
        var debugIcon: AppCompatImageView = itemView.findViewById(R.id.favorite_icon)
        var label: TextView = itemView.findViewById(R.id.label)
        var packageName: TextView = itemView.findViewById(R.id.packageName)
        var version: TextView = itemView.findViewById(R.id.version)
        var isSystemApp: TextView = itemView.findViewById(R.id.isSystem)
        var sdk: TextView = itemView.findViewById(R.id.sdk)
        var issuer: TextView = itemView.findViewById(R.id.issuer)
        var sha: TextView = itemView.findViewById(R.id.sha)
    }

    object DiffCallback : DiffUtil.ItemCallback<ApplicationItem>() {
        override fun areItemsTheSame(
            oldItem: ApplicationItem,
            newItem: ApplicationItem
        ): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: ApplicationItem, newItem: ApplicationItem) = true
    }
}
