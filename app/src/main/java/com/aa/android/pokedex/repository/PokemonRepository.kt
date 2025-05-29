package com.aa.android.pokedex.repository

import com.aa.android.pokedex.api.PokemonApi
import com.aa.android.pokedex.api.entity.PokemonDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class PokemonRepository @Inject constructor(val api: PokemonApi) {

    //private val api: PokemonApi = retrofit.create(PokemonApi::class.java)

    suspend fun getAllPokemon(): List<String> {
        val response = api.getAllPokemon()
        if (response.isSuccessful) {
            response.body()?.let {
                return it.results.map { result ->
                    result.name
                }
            }
        }
        return listOf()
    }


    suspend fun getPokemon(name: String): PokemonDTO? {
        val response = api.getPokemon(name)
        if (response.isSuccessful) {
            response.body()?.let {
                return it
            }
        }
        return null
    }

}