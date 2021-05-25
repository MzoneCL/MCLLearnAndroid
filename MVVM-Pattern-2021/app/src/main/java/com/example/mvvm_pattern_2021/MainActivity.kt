package com.example.mvvm_pattern_2021

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm_pattern_2021.adapter.FruitAdapter
import com.example.mvvm_pattern_2021.databinding.ActivityMainBinding
import com.example.mvvm_pattern_2021.ui_state.MainActivityUIState
import com.example.mvvm_pattern_2021.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    val TAG = this.javaClass.name

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        initView()
    }

    private fun initView() {
        activityMainBinding.rvFruits.layoutManager = LinearLayoutManager(this)
        mainActivityViewModel.getMainActivityUIState().observe(this,
            Observer<MainActivityUIState> { t -> renderUIState(t) })
    }

    private fun renderUIState(state: MainActivityUIState?) {
        Log.e(TAG, "render UI")
        with(state!!) {
            activityMainBinding.progressBar.isVisible = isLoading

            activityMainBinding.tvEmpty.isVisible = !isLoading && fruits.isEmpty()

            activityMainBinding.rvFruits.adapter = FruitAdapter(fruits)
        }
    }
}