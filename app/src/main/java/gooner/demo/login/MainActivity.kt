package gooner.demo.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 1000
    var mLoginFaceButton: LoginButton? = null
    var mLoginGoogleButton: SignInButton? = null
    var mLogoutGoogleButton: Button? = null
    var mTypeTxt: TextView? = null
    var mNameTxt: TextView? = null
    lateinit var mAvatarImg: ImageView
    lateinit var mCallBackManager: CallbackManager
    lateinit var mProfile: Profile
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main)

        initCommonComponent()

        // Init component for facebook
        initFacebookComponent()

        // Init component for google
        initGoogleComponent()

    }

    private fun initCommonComponent() {
        mCallBackManager = CallbackManager.Factory.create()
        mLoginFaceButton = findViewById(R.id.main_btn_login_facebook) as LoginButton
        mLoginGoogleButton = findViewById(R.id.main_btn_login_google)
        mTypeTxt = findViewById(R.id.main_type)
        mNameTxt = findViewById(R.id.main_user_name)
        mAvatarImg = findViewById(R.id.main_avatar_img)

        mLogoutGoogleButton = findViewById(R.id.main_sign_out_btn)
        mLoginGoogleButton?.setSize(SignInButton.SIZE_WIDE)
    }

    private fun initGoogleComponent() {
        var gso: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mLoginGoogleButton?.setOnClickListener {
            startActivityForResult(mGoogleSignInClient.signInIntent, RC_SIGN_IN)
        }

        mLogoutGoogleButton?.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, object : OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
                    mLoginGoogleButton?.visibility = View.VISIBLE
                    mLogoutGoogleButton?.visibility = View.GONE
                    mNameTxt?.text = ""
                    mTypeTxt?.text = "You have logouted from Google"
                    mAvatarImg.setImageDrawable(null)
                    mLoginFaceButton?.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun initFacebookComponent() {
        AppEventsLogger.activateApp(this);

        mLoginFaceButton?.loginBehavior = LoginBehavior.NATIVE_ONLY

        mLoginFaceButton?.registerCallback(mCallBackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                Log.d("Face1", "Success")
                mLoginGoogleButton?.visibility = View.GONE
            }

            override fun onCancel() {
                Log.d("Face1", "Cancel")

            }

            override fun onError(error: FacebookException?) {
                Log.d("Face1", "Error")
                mTypeTxt?.text = "There is an error. Please try again later !"
            }
        })

        mLoginFaceButton?.setOnClickListener {
            when (isLoggedInFacebook()) {
                false -> {
//                    mTypeTxt?.text = "You have logouted from Facebook"
//                    mAvatarImg?.setImageDrawable(null)
//                    mNameTxt?.text = ""
                }
                true -> {
                    mLoginGoogleButton?.visibility = View.VISIBLE
                    mTypeTxt?.text = "You have logouted from Facebook"
                    mAvatarImg?.setImageDrawable(null)
                    mNameTxt?.text = ""
                }
            }
        }
    }

    fun isLoggedInFacebook(): Boolean {
        return AccessToken.getCurrentAccessToken() != null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Face1", "onActivityResult")

        if (Profile.getCurrentProfile() != null) {
            mProfile = Profile.getCurrentProfile()
            mTypeTxt?.text = "You have logined successfully using Facebook"
            Glide.with(this@MainActivity).load(
                mProfile?.getProfilePictureUri(
                    100,
                    100
                )
            ).into(mAvatarImg)
            mNameTxt?.text = mProfile?.name

        }

        if (requestCode == RC_SIGN_IN) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Google1", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }

    }

    private fun updateUI(account: GoogleSignInAccount?) {
        mNameTxt?.text = account?.displayName
        Glide.with(this@MainActivity).load(
            account?.photoUrl
        ).into(mAvatarImg)
        mLoginGoogleButton?.visibility = View.GONE
        mLogoutGoogleButton?.visibility = View.VISIBLE
        mTypeTxt?.text = "You have logined using Google successfully"
        mLoginFaceButton?.visibility = View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
