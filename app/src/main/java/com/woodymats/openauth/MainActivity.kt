package com.woodymats.openauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.databinding.ActivityMainBinding
import com.woodymats.openauth.databinding.NavHeaderMainBinding
import com.woodymats.openauth.ui.login.LoginActivity
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.PREFERENCES
import com.woodymats.openauth.utils.USER_TOKEN

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navHeaderMainBinding = NavHeaderMainBinding.inflate(layoutInflater, binding.navView, false)
        navView.addHeaderView(navHeaderMainBinding.root)
        navHeaderMainBinding.viewModel = viewModel
        navHeaderMainBinding.lifecycleOwner = this
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.courseDetailsFragment -> toolbar.visibility = GONE
                R.id.nav_home -> {
                    toolbar.visibility = VISIBLE
                    viewModel.refreshUser()
                }
                else -> toolbar.visibility = VISIBLE
            }
        }
        setUpObservers()
        setUpDeviceToken()
    }

    private fun setUpDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token ?: ""
            Log.d(TAG, msg)
            // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpViewModel() {
        val preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        viewModel = MainActivityViewModel(
            preferences.getString(USER_TOKEN, "")!!,
            getInstance(baseContext),
            preferences
        )
    }

    private fun setUpObservers() {
        viewModel.callStatus.observe(this, {
            when (it) {
                ApiCallStatus.UNKNOWNERROR -> Snackbar.make(
                    binding.root,
                    R.string.unknown_error,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.NOINTERNETERROR -> Snackbar.make(
                    binding.root,
                    R.string.no_internet_connection,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.AUTHERROR -> Snackbar.make(
                    binding.root,
                    R.string.wrong_credentials,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SERVERERROR -> Snackbar.make(
                    binding.root,
                    R.string.server_error,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SUCCESS -> {
                    startActivity(
                        Intent(this@MainActivity, LoginActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    finish()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun logout(item: MenuItem) {
        viewModel.logoutUser()
    }
}