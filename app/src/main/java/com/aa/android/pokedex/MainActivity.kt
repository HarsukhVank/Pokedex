package com.aa.android.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.aa.android.pokedex.api.entity.PokemonDTO
import com.aa.android.pokedex.model.UiState
import com.aa.android.pokedex.nav.NavDestination
import com.aa.android.pokedex.ui.theme.PokedexTheme
import com.aa.android.pokedex.viewmodel.DetailViewModel
import com.aa.android.pokedex.viewmodel.MainViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                Screen(mainViewModel.pokemonLiveData, detailViewModel)
            }
        }
    }
}

@Composable
fun Screen(pokemon: LiveData<UiState<List<String>>>, detailViewModel: DetailViewModel) {
    Scaffold(topBar = {
        TopAppBar(backgroundColor = MaterialTheme.colors.primary, title = {
            Image(painter = painterResource(id = R.drawable.pokemon_logo), null)
        })
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()

            NavHost(navController, NavDestination.Main) {
                composable<NavDestination.Main> {
                    PokemonList(pokemon) { name ->
                        navController.navigate(NavDestination.Detail(name))
                    }
                }

                composable<NavDestination.Detail> { backStackEntry ->
                    val detail: NavDestination.Detail = backStackEntry.toRoute()
                    PokemonDetail(detail.name, detailViewModel)
                }
            }
        }
    }
}

@Composable
fun PokemonList(pokemon: LiveData<UiState<List<String>>>, navigate: ((String) -> Unit)? = null) {
    val uiState: UiState<List<String>>? by pokemon.observeAsState()
    LazyColumn(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        uiState?.let {
            when (it) {
                is UiState.Loading -> {
                    items(20) {
                        PokemonItem(pokemon = "", isLoading = true)
                    }
                }
                is UiState.Ready -> {
                    items(it.data) { pkmn ->
                        PokemonItem(pokemon = pkmn, isLoading = false, navigate)
                    }
                }
                is UiState.Error -> {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            textAlign = TextAlign.Center,
                            text = "Error loading list. Please try again later.",
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PokemonItem(pokemon: String, isLoading: Boolean, navigate: ((String) -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            navigate?.invoke(pokemon)
        }) {
        Text(text = pokemon.capitalize(Locale.current), modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
    }
}

@Composable
fun PokemonDetail(name: String,  detailViewModel : DetailViewModel) {

    LaunchedEffect(name) {

        detailViewModel.getData(name)

    }

    val uiState: UiState<PokemonDTO?>? by detailViewModel.pokemonDetailLiveData.observeAsState()

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight() ){
        uiState?.let {
            when (it) {
                is UiState.Loading -> {
                    Text("is Loading")
                }
                is UiState.Ready -> {

                    it.data?.let { it1 ->

                        Column(
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        ) {

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                text = it1.name.capitalize(Locale.current), fontSize = 30.sp
                            )

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                text = "Height: " + it1.height + " decimeters", fontSize = 20.sp
                            )

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                text = "Weight: " + it1.weight + " hectograms", fontSize = 20.sp
                            )

                            LoadingImageFromInternetCoil(it1.sprites.defaultFront)

                        }
                    }

                }
                is UiState.Error -> {

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            textAlign = TextAlign.Center,
                            text = "Error loading list. Please try again later.",
                            color = MaterialTheme.colors.onBackground
                        )
                }
            }
        }
    }

}

@Composable
fun LoadingImageFromInternetCoil(url : String) {
    AsyncImage(
        modifier = Modifier
            .height(height = 400.dp)
            .width(400.dp),
        model = url,
        contentDescription = "Translated description of what the image contains"
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PokedexTheme {
        //Screen(MutableLiveData(UiState.Ready(listOf("one", "two", "three"))))
    }
}