package gooner.demo.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton


class MainActivity : AppCompatActivity() {

    var mLoginButton: LoginButton? = null
    var mTypeTxt: TextView? = null
    var mNameTxt: TextView? = null
    var mAvatarImg: ImageView? = null
    lateinit var mCallBackManager: CallbackManager
    lateinit var mProfile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main)

        AppEventsLogger.activateApp(this);
        initComponent()

//        mLoginButton?.setReadPermissions("email")
        mLoginButton?.loginBehavior = LoginBehavior.NATIVE_ONLY
        mLoginButton?.registerCallback(mCallBackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                Log.d("Face1", "Success")


            }

            override fun onCancel() {
                Log.d("Face1", "Cancel")

            }

            override fun onError(error: FacebookException?) {
                Log.d("Face1", "Error")
                mTypeTxt?.text = "There is an error. Please try again later !"
            }
        })

        mLoginButton?.setOnClickListener {
            when (isLoggedInFacebook()) {
                false -> {
//                    mTypeTxt?.text = "You have logouted from Facebook"
//                    mAvatarImg?.setImageDrawable(null)
//                    mNameTxt?.text = ""
                }
                true -> {
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

    private fun initComponent() {
        mCallBackManager = CallbackManager.Factory.create()
        mLoginButton = findViewById(R.id.main_btn_login_facebook) as LoginButton
        mTypeTxt = findViewById(R.id.main_type)
        mNameTxt = findViewById(R.id.main_user_name)
        mAvatarImg = findViewById(R.id.main_avatar_img)
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
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
