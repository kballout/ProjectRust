package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentTopBinding

class TopFragment : Fragment() {
    private var _binding : FragmentTopBinding? = null
    private val binding get() = _binding!!
    private val viewModel : Model by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTopBinding.inflate(inflater, container, false)

        //get list of items under category
        (activity as MainActivity?)!!.getListOfItems(binding.spinner, viewModel.category.value.toString())


        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(dropdown: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel!!.setItem(dropdown?.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}