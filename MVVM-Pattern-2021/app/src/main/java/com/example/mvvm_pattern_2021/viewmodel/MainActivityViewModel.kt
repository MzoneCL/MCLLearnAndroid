package com.example.mvvm_pattern_2021.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvm_pattern_2021.beans.Fruit
import com.example.mvvm_pattern_2021.repository.FruitRepository
import com.example.mvvm_pattern_2021.ui_state.MainActivityUIState

class MainActivityViewModel : ViewModel() {

    private val fruitRepository: FruitRepository = FruitRepository()
    private val mainActivityUIState: MutableLiveData<MainActivityUIState> = MutableLiveData()

    init {
        initMainActivityUIState()
    }

    private fun initMainActivityUIState() {
        mainActivityUIState.value = MainActivityUIState(isLoading = true, fruits = emptyList(), error = null)
        Thread(Runnable { kotlin.run {

            fruitRepository.getFruitsFromRemote(object : FruitRepository.OnGetFruitsListener{
                override fun onSuccess(fruits: List<Fruit>) {
                   // mainActivityUIState.value = MainActivityUIState(isLoading = false, fruits = fruits, error = null)
                    mainActivityUIState.postValue(MainActivityUIState(isLoading = false, fruits = fruits, error = null))
                }

                override fun onFailed(error: String) {
                   // mainActivityUIState.value = MainActivityUIState(isLoading = false, fruits = emptyList(), error = error)
                    mainActivityUIState.postValue(MainActivityUIState(isLoading = false, fruits = emptyList(), error = error))
                }
            })

        } }).start()
    }

    fun getMainActivityUIState() : LiveData<MainActivityUIState> = mainActivityUIState

}