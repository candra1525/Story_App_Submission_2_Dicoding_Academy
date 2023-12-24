package com.candra.dicodingstoryapplication

import com.candra.dicodingstoryapplication.model.StoryModel

object DummyData {
    fun generateDummyStoryResponse(): List<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()
        for (i in 0..100) {
            val story = StoryModel(
                i.toString(),
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-ssnq8NfEhbE4XaEXIgUvq9ruJyYnIkLYLQ&usqp=CAU",
                "2023-10-18",
                "Testing",
                "Ini adalah testing",
                10.0,
                10.0
            )
            items.add(story)
        }
        return items
    }

}