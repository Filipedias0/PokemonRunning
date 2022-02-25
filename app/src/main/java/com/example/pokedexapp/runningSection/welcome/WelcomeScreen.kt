package com.example.pokedexapp.runningSection.welcome

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokedexapp.R
import com.example.pokedexapp.favPokemons.FavPokemonsViewModel
import com.example.pokedexapp.util.PermissionsHandler
import com.example.pokedexapp.util.PokemonText
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: WelcomeScreenViewModel = hiltViewModel()
) {
    val showAlert = remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column{
            PermissionsHandler(showAlert = showAlert, false)

            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
            )

            ContentWrapper(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 20.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .shadow(10.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.secondary)
                    .padding(16.dp),
                navController = navController,
                viewModel = viewModel
            )
        }
    }

}

@Composable
fun ContentWrapper(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: WelcomeScreenViewModel
) {
    var textWeight by rememberSaveable { mutableStateOf(0F) }
    var textName by rememberSaveable { mutableStateOf("Text") }
    var context = LocalContext.current

    fun btnContinueOnClick(){
        val success = viewModel.writePersonalDataToSharedPref(textName, textWeight, context)
        if (success){
            navController.navigate(
                "runs_screen"
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        PokemonText(
            text = "Welcome",
            modifier = Modifier
                .fillMaxWidth( 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(R.drawable.poke_ball_pin),
            contentDescription = null,
            modifier = Modifier
                .requiredSize(60.dp)
        )

        TextInput(
            hint = "Name",
            keyboardType = KeyboardType.Text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            textName = it
        }

        TextInput(
            hint = "Weight",
            keyboardType = KeyboardType.Number,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            textWeight = it.toFloat()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            onClick = {
                btnContinueOnClick()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(255,203,8))){
            Text(
                text = "Continue",
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
    keyboardType: KeyboardType = KeyboardType.Text,
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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