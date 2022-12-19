package com.bolunevdev.kinon.data

import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.domain.Film

class MainRepository {

    val filmsDataBase = listOf(
        Film(
            "Memento",
            R.drawable.memento,
            "A man with short-term memory loss attempts to track down his wife\\'s murderer.",
            8.4f
        ),
        Film(
            "Inception",
            R.drawable.inception,
            "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.",
            8.8f
        ),
        Film(
            "The Dark Knight",
            R.drawable.the_dark_knight,
            "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            9.0f
        ),
        Film(
            "Forrest Gump",
            R.drawable.forrest_gump,
            "The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.",
            8.8f
        ),
        Film(
            "Pulp Fiction",
            R.drawable.pulp_fiction,
           "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
            8.9f
        ),
        Film(
            "The Matrix",
            R.drawable.the_matrix,
            "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth-the life he knows is the elaborate deception of an evil cyber-intelligence.",
            8.7f
        ),
        Film(
            "Interstellar",
            R.drawable.interstellar,
            "A team of explorers travel through a wormhole in space in an attempt to ensure humanity\\'s survival.",
            8.6f
        ),
        Film(
            "The Wolf of Wall Street",
            R.drawable.the_wolf_of_wall_street,
            "Based on the true story of Jordan Belfort, from his rise to a wealthy stock-broker living the high life to his fall involving crime, corruption and the federal government.",
            8.2f
        ),
        Film(
            "American Beauty",
            R.drawable.american_beauty,
            "A sexually frustrated suburban father has a mid-life crisis after becoming infatuated with his daughter\\'s best friend.",
            8.4f
        ),
        Film(
            "Fight Club",
            R.drawable.fight_club,
            "An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more.",
            8.8f
        )
    )
}