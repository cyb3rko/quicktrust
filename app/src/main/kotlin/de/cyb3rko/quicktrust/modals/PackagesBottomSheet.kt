/*
 * Copyright (c) 2023 Cyb3rKo
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

package de.cyb3rko.quicktrust.modals

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.cyb3rko.quicktrust.R
import de.cyb3rko.quicktrust.apps.ApplicationItem
import de.cyb3rko.quicktrust.apps.MainRecyclerAdapter
import de.cyb3rko.quicktrust.databinding.BottomSheetPackagesBinding
import de.cyb3rko.quicktrust.managers.AppManager
import java.text.Collator
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PackagesBottomSheet(
    private val onClick: (item: ApplicationItem) -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPackagesBinding
    private lateinit var adapter: MainRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentActivity = context as FragmentActivity

        @SuppressLint("CheckResult", "InflateParams")
        val layout = fragmentActivity.layoutInflater.inflate(
            R.layout.bottom_sheet_packages,
            null
        ) as LinearLayout
        binding = BottomSheetPackagesBinding.bind(layout)
        adapter = MainRecyclerAdapter(
            fragmentActivity,
            onClick = {
                onClick(it)
                dismiss()
            },
            onListChanged = {
                binding.searchInputLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        )
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = adapter

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val comparator = Comparator<ApplicationItem> { a1, a2 ->
            Collator.getInstance(Locale.getDefault()).compare(a1.label, a2.label)
        }

        lateinit var applications: List<ApplicationItem>
        lifecycleScope.launch(Dispatchers.IO) {
            applications = AppManager.getApplicationItems(requireContext()).sortedWith(comparator)
            withContext(Dispatchers.Main) { adapter.submitInitialList(applications) }
        }

        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            adapter.filter(text.toString())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return dialog
    }

    companion object {
        const val TAG = "Packages Bottom Sheet"
    }
}
