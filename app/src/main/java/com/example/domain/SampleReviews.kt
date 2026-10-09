package com.example.domain

data class SampleReview(
    val id: String,
    val title: String,
    val category: String,
    val categoryIcon: String,
    val reviewText: String,
    val tag: String,
    val isFeatured: Boolean = false
)

object SampleReviewsData {
    val USER_FEATURED_REVIEW = SampleReview(
        id = "user_shoe_review",
        title = "Men's Dress Casual Shoe",
        category = "Footwear & Apparel",
        categoryIcon = "👞",
        reviewText = "This is a shoe I will wear with black dress pants or jeans when I need comfort and a little style, but I am not impressed. This is a very flimsy shoe with little support at all. Unlike any other shoes I've purchased in the past. It looks nice, but it's not comfortable.",
        tag = "User Inquiry Question",
        isFeatured = true
    )

    val ALL_SAMPLES: List<SampleReview> = listOf(
        USER_FEATURED_REVIEW,
        SampleReview(
            id = "headphones_review",
            title = "Noise-Cancelling Pro Headphones",
            category = "Consumer Electronics",
            categoryIcon = "🎧",
            reviewText = "The active noise cancellation on these headphones is truly phenomenal! The soundstage is rich and immersive, battery life easily lasts through long international flights, and the ear cushions are super comfortable. A stellar investment.",
            tag = "Glowing Praise"
        ),
        SampleReview(
            id = "smartwatch_review",
            title = "Ultra Fitness Smartwatch",
            category = "Wearables & Tech",
            categoryIcon = "⌚",
            reviewText = "Total disaster. The battery drains in less than 4 hours, step tracking is completely inaccurate, and the touch screen froze twice on the first day. Customer support was unhelpful and refused a return. Waste of money.",
            tag = "Critical Failure"
        ),
        SampleReview(
            id = "bistro_review",
            title = "Luigi's Italian Trattoria",
            category = "Dining & Food",
            categoryIcon = "🍝",
            reviewText = "The handmade pasta and truffle sauce were delicious and authentic, and the wine pairing was lovely. However, the dining room was extremely noisy and the service was quite slow during peak rush hour. Good food, but be prepared to wait.",
            tag = "Mixed Experience"
        ),
        SampleReview(
            id = "hotel_review",
            title = "Oceanview Boutique Resort",
            category = "Hospitality & Travel",
            categoryIcon = "🏖️",
            reviewText = "Flawless stay from start to finish! The room was spotless with breathtaking views of the coastline. The staff treated us like royalty, complimentary breakfast was fresh and organic, and the infinity pool was pure bliss.",
            tag = "5-Star Experience"
        ),
        SampleReview(
            id = "budget_airline_review",
            title = "Cross-Country Budget Flight",
            category = "Aviation & Travel",
            categoryIcon = "✈️",
            reviewText = "Flight was delayed by four hours without explanation. The seats don't recline at all, legroom is nonexistent, and they charged twenty dollars for a tiny bottle of water. Awful, stressful journey.",
            tag = "Severe Dissatisfaction"
        )
    )
}
