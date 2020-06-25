package com.drugstore.domain.entity

enum class Gender(val id: Int) {
    MALE(1),
    FEMALE(2);

    companion object {
        val values = values()
        val ids = IntArray(values.size) { i -> values[i].id }

        fun getById(id: Int) = values.find { it.id == id }
    }
}