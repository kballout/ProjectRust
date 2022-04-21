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
    ): View? {
        _binding = FragmentBottomBinding.inflate(inflater, container, false)

        viewModel!!.item.observe(viewLifecycleOwner, { item ->
            binding.itemName.text = item.toString()
            (activity as MainActivity?)!!.getItemData(changeSpacesToSlashes(),binding.itemImage, binding.itemDescription, binding.itemID)
        })

        return binding.root
    }

    fun changeSpacesToSlashes() : String{
        val split = viewModel.item.value.toString().split(" ")
        var result: String = ""
        for (i in 0 until split.size){
            result += split[i]
            if(i != split.size - 1){
                result+= "-"
            }
        }
        return result
    }
}