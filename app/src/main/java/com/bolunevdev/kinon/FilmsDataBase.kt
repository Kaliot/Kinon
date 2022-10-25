package com.bolunevdev.kinon

import android.content.Context

class FilmsDataBase(context: Context) {

    private var films = mutableListOf<Film>()

    init {
        films = arrayListOf(
            Film(
                context.getString(R.string.memento_title),
                R.drawable.memento,
                context.getString(R.string.memento_description)
            ),
            Film(
                context.getString(R.string.inception_title),
                R.drawable.inception,
                context.getString(R.string.inception_description)
            ),
            Film(
                context.getString(R.string.the_dark_knight_title),
                R.drawable.the_dark_knight,
                context.getString(R.string.the_dark_knight_description)
            ),
            Film(
                context.getString(R.string.forrest_gump_title),
                R.drawable.forrest_gump,
                context.getString(R.string.forrest_gump_description)
            ),
            Film(
                context.getString(R.string.pulp_fiction_title),
                R.drawable.pulp_fiction,
                context.getString(R.string.pulp_fiction_description)
            ),
            Film(
                context.getString(R.string.the_matrix_title),
                R.drawable.the_matrix,
                context.getString(R.string.the_matrix_description)
            ),
            Film(
                context.getString(R.string.interstellar_title),
                R.drawable.interstellar,
                context.getString(R.string.interstellar_description)
            ),
            Film(
                context.getString(R.string.the_wolf_of_wall_street_title),
                R.drawable.the_wolf_of_wall_street,
                context.getString(R.string.the_wolf_of_wall_street_description)
            ),
            Film(
                context.getString(R.string.american_beauty_title),
                R.drawable.american_beauty,
                context.getString(R.string.american_beauty_description)
            ),
            Film(
                context.getString(R.string.fight_club_title),
                R.drawable.fight_club,
                context.getString(R.string.fight_club_description)
            )
        )
    }

    fun getFilms(): MutableList<Film> = films
}