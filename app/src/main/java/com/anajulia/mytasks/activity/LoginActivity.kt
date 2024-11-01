package com.anajulia.mytasks.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anajulia.mytasks.databinding.ActivityLoginBinding
import com.anajulia.mytasks.extension.value
import com.anajulia.mytasks.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.anajulia.mytasks.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 9001;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("544136956228-5isv825hig2j1hbvihadfijjeuucqn9s.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //setIntent()
    }

    private fun initComponents() {
        binding.tvOr.setText(R.string.or)

        binding.btLogin.setOnClickListener {
            login()
        }

        binding.btCreateAccount.setOnClickListener {
            createAccount()
        }

        binding.btLoginWithGoogle.setOnClickListener {
            loginWithGoogle()
        }
    }

    private fun login() {
        Firebase.auth.signInWithEmailAndPassword(binding.etEmail.value(), binding.etPassword.value())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Log.e("auth", "createUserWithEmail:failure", task.exception)

                        task.exception?.message?.let { errorMessage ->
                            binding.tilEmail.error = errorMessage
                        }

                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

    }

    private fun createAccount() {
        Firebase.auth.createUserWithEmailAndPassword(binding.etEmail.value(), binding.etPassword.value())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        login()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("auth", "createUserWithEmail:failure", task.exception)

                        task.exception?.message?.let { errorMessage ->
                            binding.tilEmail.error = errorMessage
                        }

                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
    }

    private fun loginWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuth(account.idToken.toString())
            } catch (e: ApiException) {
                Log.w("Google Sign-In", "Erro ao fazer login: ${e.statusCode}")
            }
        }
    }

    private fun firebaseAuth(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Log.w("auth", "signInWithCredential:failure", task.exception)
                }
            }
    }

    // Funções utilizadas para login com link de e-mail (não funcionou)

    private fun setIntent() {
        intent?.data?.let { uri ->
            if (uri.host == "finishSignUp") {
                val emailLink = uri.toString()
                if (Firebase.auth.isSignInWithEmailLink(emailLink)) {
                    val email = Utils.getEmail(this)

                    if (email != null) {
                        Firebase.auth.signInWithEmailLink(email, emailLink)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("auth", "Successfully signed in with email link!")
                                } else {
                                    Log.e("auth", "Error signing in with email link", task.exception)
                                }
                            }
                    }
                }
            }
        }
    }

    private fun sendLink() {
        val actionCodeSettings = actionCodeSettings {
            // URL you want to redirect back to. The domain (www.example.com) for this
            // URL must be whitelisted in the Firebase Console.
            url = "https://anajulia.page.link/finishSignUp"
            // This must be true
            handleCodeInApp = true
            setAndroidPackageName(
                "com.anajulia.mytasks",
                true,
                ""
            )
        }

        Firebase.auth.sendSignInLinkToEmail(binding.etEmail.value(), actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    finish()
                    Utils.saveEmail(this, binding.tilEmail.toString())
                } else {
                    // If send link fails, display a message to the user.
                    Log.e("auth", "sendLink:failure", task.exception)

                    task.exception?.message?.let { errorMessage ->
                        binding.tilEmail.error = errorMessage
                    }

                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}