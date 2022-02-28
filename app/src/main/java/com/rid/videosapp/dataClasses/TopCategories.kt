package com.rid.videosapp.dataClasses

data class TopCategories(val ImgTittle:String, val ImgQuery:String, val ImgeUrl:String)
data class TopCategoriesArray(val TopCategories: List<TopCategories>)