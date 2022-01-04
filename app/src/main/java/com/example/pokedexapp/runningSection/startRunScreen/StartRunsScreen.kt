package com.example.pokedexapp.runningSection.startRunScreen

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokedexapp.R
import com.example.pokedexapp.favPokemons.FavPokemonsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun StartRunScreen(
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
            navController = navController
        )
    }

}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var textWeight by rememberSaveable { mutableStateOf("Text") }
    var textName by rememberSaveable { mutableStateOf("Text") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {

        Image(
            painter = painterResource(R.drawable.poke_ball_pin),
            contentDescription = null,
            modifier = Modifier.requiredSize(60.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            onClick = {
                navController.navigate(
                    "runs_screen"
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(255,203,8))){
            Text(
                text = "Run!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0,103,180)
            )
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

