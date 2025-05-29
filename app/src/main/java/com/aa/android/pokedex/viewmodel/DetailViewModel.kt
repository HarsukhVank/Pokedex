package com.aa.android.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.aa.android.pokedex.api.PokemonApi
import com.aa.android.pokedex.api.entity.PokemonDTO
import com.aa.android.pokedex.model.UiState
import com.aa.android.pokedex.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(api: PokemonApi): ViewModel() {

    val repository = PokemonRepository(api)

    private val liveData = MutableLiveData<UiState<PokemonDTO?>>()
    val pokemonDetailLiveData: LiveData<UiState<PokemonDTO?>> =  liveData

    fun getData(name: String){

        viewModelScope.launch(Dispatchers.IO) {

            liveData.postValue(UiState.Loading())
            try {
                val data = repository.getPokemon(name)
                liveData.postValue(UiState.Ready(data))
            } catch (e: Exception) {
                liveData.postValue(UiState.Error(e))
            }
        }
    }
}