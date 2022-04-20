package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Model : ViewModel() {
    private var choice : MutableLiveData<String> = MutableLiveData()
    val category: LiveData<String> = choice

    private var _item : MutableLiveData<String> = MutableLiveData()
    val item: LiveData<String> = _item

    fun setChoice(choice: String){
        this.choice.value = choice
    }

    fun setItem(item: String){
        this._item.value = item
    }
}