package com.example.mvvm_pattern_2021.repository

import com.example.mvvm_pattern_2021.beans.Fruit

class FruitRepository {

    fun getFruitsFromRemote(onGetFruitsListener: OnGetFruitsListener) {

        Thread.sleep(1500)

        onGetFruitsListener.onSuccess(generateFruits())
    }

    private fun generateFruits(): List<Fruit> {
        val fruits: MutableList<Fruit> = ArrayList<Fruit>()

        fruits.apply {
            add(Fruit("apple"))
            add(Fruit("orange"))
            add(Fruit("watermelon"))
            add(Fruit("banana"))
            add(Fruit("peach"))
            add(Fruit("pineapple"))
            add(Fruit("strawberry"))
            add(Fruit("pear"))
        }

        return fruits

    }

    interface OnGetFruitsListener {

        fun onSuccess(fruits: List<Fruit>)

        fun onFailed(error: String)
    }

}