package com.example.ricky.friendsactivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_friends.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN=123
    private var mAuth: FirebaseAuth?=null
    private var mUser: FirebaseUser?= null

    public override fun onStart() {
        super.onStart()
        val currentUser=mAuth?.getCurrentUser()
        updateUI(currentUser)
    }
    public fun updateUI(currentUser: FirebaseUser?){
        mUser  = currentUser
        if (currentUser != null){
            buttonLogout.visibility = View.VISIBLE
            buttonLogin.visibility = View.GONE
            textUserName.text = currentUser?.displayName
            textUserEMail.text = currentUser?.email
            textUserId.text = currentUser?.uid

            Picasso.with(this@MainActivity).load(currentUser?.photoUrl).into(imageProfilee)
        } else{
            buttonLogout.visibility = View.GONE
            buttonLogin.visibility = View.VISIBLE
            textUserName.text="NOT LOGGED IN"
            textUserEMail.text=""
            textUserId.text =""
            imageProfilee.setImageResource(R.drawable.common_google_signin_btn_text_disabled)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth=FirebaseAuth.getInstance()


        buttonLogin.setOnClickListener{
            _ -> doLoginStuff()
        }
        buttonLogout.setOnClickListener{
            _-> doLogoutStuff()
        }
              buttonFriends.setOnClickListener({view ->
            val intent = Intent(this,FriendsActivity::class.java)
            intent.putExtra("userEmail",mUser?.email.toString())
            intent.putExtra("userImageUrl",mUser?.photoUrl.toString())
            startActivity(intent)
        })
        buttonChat.setOnClickListener({view ->
            val intent = Intent(this,ChatActivity::class.java)
            intent.putExtra("userEmail",mUser?.email.toString())
            intent.putExtra("userImageUrl",mUser?.photoUrl.toString())
            startActivity(intent)
        })

    }


    fun doLoginStuff(){
        val providers = Arrays.asList(
                AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        )
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),RC_SIGN_IN
        )
    }

    fun doLogoutStuff(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener{this.updateUI(null)}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGN_IN){
            val response= IdpResponse.fromResultIntent(data)
            if(resultCode==Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                this.updateUI(user)

            }else{
                this.updateUI(null)
            }
        }
    }
}