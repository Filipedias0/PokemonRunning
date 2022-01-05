package com.example.pokedexapp.util

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

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
fun PermissionsHandler(showAlert: MutableState<Boolean>, background: Boolean) {

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