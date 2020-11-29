package com.gaurav.moviesapp.ui.list

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.gaurav.moviesapp.R
import com.gaurav.moviesapp.databinding.FragmentMovieListBinding
import com.gaurav.moviesapp.db.PreferencesStorage
import com.gaurav.moviesapp.models.Movie
import com.gaurav.moviesapp.util.Category
import com.gaurav.moviesapp.util.RecyclerViewDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieListFragment : Fragment() {
    companion object {
        private const val TAG = "MovieListFragment"
    }

    private val movieListViewModel by viewModel<MovieListViewModel>()
    private lateinit var adapter: MoviesRecyclerAdapter
    private lateinit var binding: FragmentMovieListBinding

    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onMovieClick(movie: Movie)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieListBinding.inflate(inflater, container, false)
        binding.viewModel = movieListViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initRecyclerView()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun initRecyclerView() {
        val viewPreloader = ViewPreloadSizeProvider<String>()
        binding.recyclerview.apply {
            this@MovieListFragment.adapter =
                MoviesRecyclerAdapter(
                    onMovieClickListener,viewPreloader,initGlide()
                )
            addItemDecoration(RecyclerViewDecoration())
            adapter = this@MovieListFragment.adapter
        }
        val preloader = RecyclerViewPreloader<String>(Glide.with(this),adapter,viewPreloader,20)
        binding.recyclerview.addOnScrollListener(scrollListener)
        binding.recyclerview.addOnScrollListener(preloader)
    }

    //scroll to get Next Page of result
    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    movieListViewModel.getNextPage()
                }
            }
        }

    private fun initGlide(): RequestManager? {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_broken_image)
        return Glide.with(this).setDefaultRequestOptions(options)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getMovieList()
    }

    //method to get the first page of result
    fun getMovieList() {
        movieListViewModel.resetPageNumber()
        movieListViewModel.getList()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_movie_list_menu, menu)
        setUpSearchViewListener(menu)
    }

    private fun setUpSearchViewListener(menu: Menu) {
        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.apply {
            queryHint = context.getString(R.string.query_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        movieListViewModel.query = query
                        getMovieList()
                    }
                    onActionViewCollapsed()
                    searchItem.collapseActionView()
                    hideSoftKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_up_coming -> {
                movieListViewModel.apply {
                    query = null
                    category = Category.UPCOMING
                }
                getMovieList()
                true
            }
            R.id.menu_item_popular_movies -> {
                movieListViewModel.apply {
                    query = null
                    category = Category.POPULAR
                }
                getMovieList()
                true
            }
            R.id.menu_item_top_rated -> {
                movieListViewModel.apply {
                    query = null
                    category = Category.TOPRATED
                }
                getMovieList()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun hideSoftKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()

            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private val onMovieClickListener: (movie: Movie) -> Unit = { movie->
        callbacks?.onMovieClick(movie)
    }

    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    private fun savePreferences() {
        PreferencesStorage.setStoredQuery(this.requireContext(), movieListViewModel.query)
        PreferencesStorage.setStoredCategory(this.requireContext(), movieListViewModel.category)
    }
    fun onBackPressed(){
        movieListViewModel.cancelRequest()
    }
}


