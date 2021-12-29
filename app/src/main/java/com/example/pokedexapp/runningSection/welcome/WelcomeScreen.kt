package com.example.pokedexapp.runningSection.welcome

import android.Manifest
import com.example.pokedexapp.favPokemons.FavPokemonsViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.pokedexapp.R
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.pokemonList.SearchBar
import com.example.pokedexapp.pokemondetail.PokemonDetailSection
import com.example.pokedexapp.pokemondetail.PokemonDetailStateWrapper
import com.example.pokedexapp.pokemondetail.updateFavPokemon
import com.example.pokedexapp.util.Resource
import com.example.pokedexapp.util.isPermanentlyDenied
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.plcoding.jetpackcomposepokedex.ui.theme.RobotoCondensed
import java.security.Permissions

@ExperimentalPermissionsApi
@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: FavPokemonsViewModel = hiltViewModel()
) {
    val showAlert = remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        handlePermissions(showAlert = showAlert, false)
        RunningWrapper(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 48.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.secondary)
                .padding(16.dp),
        )
    }

}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
) {
    var textWeight by rememberSaveable { mutableStateOf("Text") }
    var textName by rememberSaveable { mutableStateOf("Text") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Text(
            text = "Welcome!",
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        Image(
            painter = painterResource(R.drawable.poke_ball_pin),
            contentDescription = null,
            modifier = Modifier.requiredSize(60.dp)
        )

        Text(
            text = "Please enter your name and weight",
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        TextInput(
            hint = "Name",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            textName = it
        }

        TextInput(
            hint = "Weight",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            textWeight = it
        }
    }
}

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    hint: String = "",
    onValueChange: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(text == "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onValueChange(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun PermissionsDialog(
    title: String?,
    state: MutableState<Boolean>,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = {
            state.value = false
        },
        title = title?.let {
            {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = title)
                    Divider(modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        },
        text = content,

        confirmButton = {
            Button(onClick = onClick) {
                Text(text = "Ok")
            }
        },
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun handlePermissions(showAlert: MutableState<Boolean>, background: Boolean) {

    fun onCLickDialog(){
        showAlert.value = false
    }

    val permissions =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || !background) {
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }


    val permissionsBack =
            listOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )


    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissions
    )

    val permissionsBackState = rememberMultiplePermissionsState(
        permissions = permissionsBack
    )

    PermissionsRequired(
        multiplePermissionsState = permissionsState,
        permissionsNotGrantedContent = {
            showAlert.value = true
            PermissionsDialog(
                state = showAlert,
                onClick = { permissionsState.launchMultiplePermissionRequest() },
                title = "Welcome!"
            ) {
                Text(
                    "This app uses permissions for location in background" +
                            " to track your runs, please enable it to use the app!"
                )
            }
        },
        permissionsNotAvailableContent = {
            showAlert.value = true
            PermissionsDialog(
                state = showAlert,
                onClick = { permissionsState.launchMultiplePermissionRequest() },
                title = "Welcome!"
            ) {
                Text(
                    "This app uses permissions for location in background" +
                            " to track your runs, please enable \"all the time\" in the settings to use the app!"
                )
            }

        }

    ) {
        if(showAlert.value){
            PermissionsDialog(
                state = showAlert,
                onClick = {
                    showAlert.value = false
                    permissionsBackState.launchMultiplePermissionRequest()
                          },
                title = "Welcome!"
            ) {
                Text(
                    "This app uses permissions for location in background" +
                            " to track your runs, please enable \"all the time\" in the settings to use the app!"
                )
            }
        }
    }

    if(!permissionsBackState.allPermissionsGranted && permissionsState.allPermissionsGranted){
        showAlert.value = true

        PermissionsDialog(
            state = showAlert,
            onClick = {
                showAlert.value = false
                permissionsBackState.launchMultiplePermissionRequest()
            },
            title = "Welcome!"
        ) {
            Text(
                "This app uses permissions for location in background" +
                        " to track your runs, please enable \"all the time\" in the settings to use the app!"
            )
        }
    }
}

