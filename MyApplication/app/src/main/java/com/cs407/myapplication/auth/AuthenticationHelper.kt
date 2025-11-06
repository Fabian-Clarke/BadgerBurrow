package com.cs407.myapplication.auth
/**
 * MILESTONE 2: Authentication Helper
 * Contains all business logic for Firebase authentication
 */

import com.google.android.play.core.integrity.r
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
// ============================================
// Email Validation
// ============================================

enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun checkEmail(email: String): EmailResult {
    if (email.isEmpty()){
        // handle the case when email is empty
        return EmailResult.Empty
    }

    // 1. username of email should only contain "0-9, a-z, _, A-Z, ."
    // 2. there is one and only one "@" between username and server address
    // 3. there are multiple domain names with at least one top-level domain
    // 4. domain name "0-9, a-z, -, A-Z" (could not have "_" but "-" is valid)
    // 5. multiple domain separate with '.'
    // 6. top level domain should only contain letters and at lest 2 letters
    // this email check only valid for this course
    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
    //TODO logic to handle if email matches the pattern or not.
    return if (pattern.matches(email)) {
        EmailResult.Valid
    } else {
        EmailResult.Invalid
    }
}

// ============================================
// Password Validation
// ============================================

enum class PasswordResult {
    Valid,
    Empty,
    Short,
    Invalid
}

fun checkPassword(password: String) : PasswordResult {
    // 1. password should contain at least one uppercase letter, lowercase letter, one digit
    // 2. minimum length: 5
    if (password.isEmpty()) {
        //TODO when password is empty
        return PasswordResult.Empty
    }
    if (password.length < 5) {
        //TODO when password is short
        return PasswordResult.Short
    }
    if (Regex("\\d+").containsMatchIn(password) &&
        Regex("[a-z]+").containsMatchIn(password) &&
        Regex("[A-Z]+").containsMatchIn(password)
    ) {
        //TODO when password is valid
        return PasswordResult.Valid
    }
    //TODO when password is invalid
    return PasswordResult.Invalid

}

fun checkCurrentUser() { //this is what has been done for 2.4 basicslly
    val currentUser = Firebase.auth.currentUser
    if (currentUser != null) {
        // TODO User is signed in
        println("User signed in: UID=${currentUser.uid}, Email=${currentUser.email}") // Unique Firebase ID and // User's email
    } else {
        // TODO User is signed out
        println("No user signed in.")
    }
}


// ============================================
// Firebase Authentication Functions
// ============================================

    /**
     * Sign in existing user with email and password
     * If sign-in fails, automatically attempts to create new account
     */
    fun signIn(
        email: String,
        password: String,
        onAuthComplete: (Boolean) -> Unit
        //any other callback function or parameters if you want
    ) {
        val auth = Firebase.auth

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    // TODO: Get current user from the response and propagate it
                    val user = auth.currentUser
                    println("Sign-in has been successful for ${user?.email}")
                    onAuthComplete(true)
                } else {
                    // Sign in failed, try creating account
                    // TODO: Call createAccount function
                    println("Sign-in failed — trying to create new account")
                    createAccount(email, password, onAuthComplete)
                }
            }
    }

/**
 * Create new Firebase account with email and password
 */
fun createAccount(
    email: String,
    password: String,
    onAuthComplete: (Boolean) -> Unit
    //any other callback function or parameters if you want
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = auth.currentUser
            // TODO: Logic to propagate success response
            println("account created successfully for ${user?.email}")
            onAuthComplete(true)
        }
        .addOnFailureListener { exception ->
            // TODO: error in creation of account
            println("failed to create the account: ${exception.message}")
            onAuthComplete(false)
        }
}

/**
 * Update Firebase Auth displayName
 * Used in Milestone 3 for username collection
 */
fun updateName(name: String) {
    val user = Firebase.auth.currentUser
    if (user == null) {
        println("updateName: No user is signed in.")
        return
    }

    val request = com.google.firebase.auth.UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .build()

    user.updateProfile(request)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("updateName: Display name updated to \"$name\".")
            } else {
                println("updateName: Failed to update name — ${task.exception?.message}")
            }
        }
}
