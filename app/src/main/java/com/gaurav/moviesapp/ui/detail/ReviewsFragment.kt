package com.gaurav.moviesapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gaurav.moviesapp.databinding.FragmentReviewsBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ReviewsFragment : Fragment() {
    private lateinit var binding: FragmentReviewsBinding
    private val viewModel: DetailFragmentViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewsBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        initRecyclerView()
        return binding.root
    }
    private fun initRecyclerView(){
        val recyclerView = binding.listReviews
        recyclerView.apply {
            adapter = ReviewAdapter()
        }
    }
}
