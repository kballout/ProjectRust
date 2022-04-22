package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentBottomBinding


class BottomFragment : Fragment() {
    private var _binding : FragmentBottomBinding? = null
    private val binding get() = _binding!!
    private val viewModel : Model by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomBinding.inflate(inflater, container, false)

        viewModel.item.observe(viewLifecycleOwner) { item ->
            binding.itemName.text = item.toString()
            resetTextViews()
            (activity as MainActivity?)!!.getItemData(
                changeSpacesToSlashes(),
                binding.itemImage,
                binding.itemDescription,
                binding.itemID,
                binding.craftDataTitle,
                binding.reqWorkLevel,
                binding.craftingTime,
                binding.craftingYield,
                binding.ingredientsTitle,
                binding.ingredientsList
            )
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun resetTextViews(){
        binding.craftDataTitle.text = ""
        binding.reqWorkLevel.text = ""
        binding.craftingTime.text = ""
        binding.craftingYield.text = ""
        binding.ingredientsTitle.text = ""
        binding.ingredientsList.text = ""
    }

    private fun changeSpacesToSlashes() : String{
        val split = viewModel.item.value.toString().split(" ")
        var result = ""
        for (i in 0 until split.size){
            result += split[i]
            if(i != split.size - 1){
                result+= "-"
            }
        }
        return result
    }
}