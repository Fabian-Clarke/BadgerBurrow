package com.cs407.myapplication.ui

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs407.myapplication.R
import com.cs407.myapplication.auth.*
import com.cs407.myapplication.data.UserState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.cs407.myapplication.data.NoteDatabase
import com.cs407.myapplication.data.User

//Create composables for ErrorText, userEmail, userPassword, and LogInSignUpButton
//Handle onclick function for LogInSignUpButton
@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (error != null) {
        Text(
            text = error,
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun userEmail(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.email_hint)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun userPassword(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.password_hint)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun LogInSignUpButton(
    email: String,
    password: String,
    onSetError: (String?) -> Unit,
    onAuthComplete: (Boolean) -> Unit
) {

    val emptyEmail= stringResource(R.string.empty_email)
    val invalidEmail = stringResource(R.string.invalid_email)
    val emptyPassword = stringResource(R.string.empty_password)
    val shortPassword = stringResource(R.string.short_password)
    val invalidPassword= stringResource(R.string.invalid_password)

    Button(
        onClick = {
            // TODO: 1. Validate email using validateEmail()
// TODO: 2. If email error, update ui with error message
// TODO: 3. Validate password using validatePassword()
// TODO: 4. If password error, update ui with error message
// TODO: 5. If both valid, call signIn()
            when (checkEmail(email)) {
                EmailResult.Empty-> { onSetError(emptyEmail); return@Button }
                EmailResult.Invalid-> { onSetError(invalidEmail); return@Button }
                EmailResult.Valid-> {}
            }
            when (checkPassword(password)) {
                PasswordResult.Empty-> { onSetError(emptyPassword); return@Button }
                PasswordResult.Short-> { onSetError(shortPassword); return@Button }
                PasswordResult.Invalid-> { onSetError(invalidPassword); return@Button }
                PasswordResult.Valid-> {}
            }
            onSetError(null)
            signIn(email, password, onAuthComplete)
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(stringResource(R.string.login_button))
    }
}

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    loginButtonClick: (UserState) -> Unit = {},   // <-- default
    onNeedsName: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //TODO Callback for authentication completion

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: Add UI components - ErrorText, email field,
            // password field, button
            ErrorText(error = error)
            userEmail(email = email, onEmailChange = { email = it })
            Spacer(modifier = Modifier.height(8.dp))
            userPassword(password = password, onPasswordChange = { password = it })
            Spacer(modifier = Modifier.height(12.dp))

            LogInSignUpButton(
                email = email,
                password = password,
                onSetError = { error = it },
                onAuthComplete = { success ->
                    if (!success) {
                        error = "authentication failed. Please try again."
                        return@LogInSignUpButton
                    }

                    val user = Firebase.auth.currentUser
                    if (user == null) {
                        error = "No user session."
                        return@LogInSignUpButton
                    }

                    val name = user.displayName?.trim().orEmpty()
                    val uid = user.uid

                    if (name.isEmpty()) {
                        onNeedsName()
                    } else {
                        loginButtonClick(
                            com.cs407.myapplication.data.UserState(
                                name = name,
                                uid = uid
                            )
                        )
                    }
                }
            )

        }
    }
}