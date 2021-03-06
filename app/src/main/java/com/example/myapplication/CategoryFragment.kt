package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel : Model by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = "Item"
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        viewModel.category.observe(viewLifecycleOwner) { choice ->
            binding.category.text = choice.replaceFirstChar { it.uppercase() }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}