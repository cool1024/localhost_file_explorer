package com.example.explorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.example.explorer.databinding.FragmentExplorerBinding

class ExplorerFragment : BaseFragment() {

    private val args: ExplorerFragmentArgs by navArgs()
    private lateinit var binding: FragmentExplorerBinding
    private val adapter = ExplorerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExplorerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // update actionbar title as dir path
        val title = args.path.ifEmpty { resources.getString(R.string.app_name) }
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar!!.title = title


        val refreshListener = {
            binding.refresh.isRefreshing = true
            adapter.refreshData(args.path) { binding.refresh.isRefreshing = false }
        }
        binding.recyclerView.adapter = adapter
        binding.refresh.setOnRefreshListener(refreshListener)
        if (adapter.empty) refreshListener()
    }
}